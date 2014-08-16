package br.ufpe.cin.aac3.gryphon;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import br.ufpe.cin.aac3.gryphon.model.Database;
import br.ufpe.cin.aac3.gryphon.model.Ontology;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public final class Gryphon {
	public static final String VERSION = "0.1a";

	private static Logger logger = Logger.getLogger(Gryphon.class);
	private static Ontology globalOntology = null;
	private static Map<String, Ontology> localOntologies = null;
	private static Map<String, Database> localDatabases = null;

	static {
		/*System.out.println(
						   "\n          _          (`-. "
						 + "\n          \\`----.    ) ^_`)    GRYPHON v" + VERSION
						 + "\n   ,__     \\__   `\\_/  ( `     A Framework for Semantic Integration"
						 + "\n    \\_\\      \\__  `|   }"
						 + "\n      \\\\  .--' \\__/    }       By Adriel Caf�, Filipe Santana, Fred Freitas"
						 + "\n       ))/   \\__,<  /_/               {aac3, fss3, fred}@cin.ufpe.br"
						 + "\n        `\\_____\\\\  )__\\_\\"
						 + "\n");
		*/

		localOntologies = new HashMap<String, Ontology>();
		localDatabases = new HashMap<String, Database>();

		PropertyConfigurator.configure("gryphon-log4j.properties");
	}

	private Gryphon() { }

	public static void alignAndMap() {
		if(!localDatabases.isEmpty()){
			Util.logInfo(logger, "Mapping and Aligning databases...");
			for (String key : localDatabases.keySet()) {
				localDatabases.get(key).mapAndAlign(globalOntology.getURI(), key);
				Util.logInfo(logger, String.format("> Database %s was mapped and aligned", key));
			}
			Util.logInfo(logger, "Done");
		}

		if(!localOntologies.isEmpty()){
			Util.logInfo(logger, "Aligning ontologies...");
			for (String key : localOntologies.keySet()) {
				localOntologies.get(key).align(globalOntology.getURI(), key);
				Util.logInfo(logger, String.format("> Ontology %s was aligned", key));
			}
			Util.logInfo(logger, "Done");
		}
	}

	public static void alignOntology(URI globalOntologyURI, URI localOntologyURI, File alignmentFile) {
		try {
			File jarFile = new File("libs\\alignment-api\\procalign.jar");
			Process proc = Runtime.getRuntime().exec(String.format("java -jar \"%s\" -t \"%s\" -o \"%s\" \"%s\" \"%s\"", jarFile.getAbsolutePath(), GryphonConfig.getAlignmentThreshold(), alignmentFile.getAbsolutePath(), globalOntologyURI.toString(), localOntologyURI.toString()));
			proc.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void mapDatabase(Database db, File mappingFile, File alignmentFile) {
		String mapping = null;
		
		try {
			File batFile = new File("libs\\d2rq\\generate-mapping" + (Util.isWindows() ? ".bat" : ""));
			Process proc = Runtime.getRuntime().exec(String.format("\"%s\" -o \"%s\" -u \"%s\" -p \"%s\" \"%s\"", batFile.getAbsolutePath(), mappingFile.getAbsolutePath(), db.getUsername(), db.getPassword(), db.getJdbcURL()));
			proc.waitFor();
			mapping = FileUtils.readWholeFileAsUTF8(mappingFile.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Files.write(Paths.get(mappingFile.toURI()), mapping.getBytes());
			String d2rqNS = "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#";
			String owlNS = "http://localhost:2020/vocab/";
			FileWriter fileWriter = new FileWriter(alignmentFile);
			Model ttlModel = FileManager.get().loadModel(mappingFile.toURI().toString());
			OntModel owlModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF);
			owlModel.createOntology(owlNS);

			for (StmtIterator i = ttlModel.listStatements(); i.hasNext();) {
				Statement s = i.nextStatement();
				if (s.getPredicate().toString().equals(d2rqNS + "class"))
					owlModel.createClass(owlNS + s.getSubject().getLocalName());
			}

			for (StmtIterator i = ttlModel.listStatements(); i.hasNext();) {
				Statement s = i.nextStatement();
				Resource r = null;
				String id = null;
				
				if (s.getPredicate().toString().equals(d2rqNS + "property")) {
					id = s.getSubject().getLocalName();
					owlModel.createDatatypeProperty(owlNS + id);					
				} else if (s.getPredicate().toString().equals(d2rqNS + "belongsToClassMap")) {
					id = s.getSubject().getLocalName();
					r = owlModel.getResource(owlNS + s.getObject().asResource().getLocalName());
					owlModel.getDatatypeProperty(owlNS + id).setDomain(r);
				}
			}

			owlModel.write(fileWriter, "RDF/XML-ABBREV");
			owlModel.close();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static OntModel query(Query queryGlobal){
		File alignmentFile = null;
		File mappingFile = null;
		OntModel modelResult = globalOntology.getModel();
		Query queryLocal = null;
		ResultSet resultSet = null;
		String result = null;
		
		for(ExtendedIterator<Individual> i = modelResult.listIndividuals(); i.hasNext();)
			i.next().remove();
		
		for(String key : getLocalOntologies().keySet()){
			alignmentFile = new File(GryphonConfig.getWorkingDirectory().toFile(), "ont_" + key + ".owl");
			
			queryLocal = queryRewrite(queryGlobal, alignmentFile);
			
			if(queryLocal != null){
				Util.logInfo(logger, "REWRITTEN QUERY FOR " + key + ":\n" + queryLocal.serialize() + "\n");
				resultSet = execSPARQLQuery(queryLocal, getLocalOntologies().get(key).getModel());
				Util.logInfo(logger, "QUERY RESULT FOR " + key + ":\n" + ResultSetFormatter.asText(resultSet) + "\n");
			}
			
			//resultRewrite(resultSet, alignmentFile);
		}
		
		for(String key : getLocalDatabases().keySet()){
			alignmentFile = new File(GryphonConfig.getWorkingDirectory().toFile(), "db_" + key + ".owl");
			mappingFile = new File(GryphonConfig.getWorkingDirectory().toFile(), "db_" + key + ".ttl");
			
			queryLocal = queryRewrite(queryGlobal, alignmentFile);
			
			if(queryLocal != null){
				Util.logInfo(logger, "REWRITTEN QUERY FOR " + key + ":\n" + queryLocal.serialize() + "\n");
				result = execSQLQuery(queryLocal, mappingFile);
				Util.logInfo(logger, "QUERY RESULT FOR " + key + ":\n" + result + "\n");
			}
			
			//resultRewrite(resultSet, alignmentFile);
		}
		
		return modelResult;
	}

	private static Query queryRewrite(Query query, File alignmentFile) {
		try {
			File jarFile = new File("libs\\mediation\\");
			Process proc = Runtime.getRuntime().exec(String.format("java -cp \"%s/mediation.jar;%s/lib/*\" uk.soton.CLT \"%s\" \"%s\"", jarFile.getAbsolutePath(), jarFile.getAbsolutePath(), alignmentFile.getAbsolutePath(), query.toString(Syntax.syntaxARQ)));
			proc.waitFor();
			
			InputStream is = proc.getInputStream();
	        byte b[] = new byte[is.available()];
	        is.read(b, 0, b.length);
	        is.close();
	        
	        return QueryFactory.create(new String(b)); 
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/*private static OntModel resultRewrite(ResultSet rs, File alignmentFile){
		Resource r = ResultSetFormatter.asRDF(rs.getResourceModel(), rs);
		for(StmtIterator i = r.listProperties(); i.hasNext();){
			Statement s = i.next();
			System.out.println(s.getPredicate());	
		}
		
		return ModelFactory.createOntologyModel();
	}*/
	
	private static ResultSet execSPARQLQuery(Query query, Model model) {
		return QueryExecutionFactory.create(query, model).execSelect();
	}
	
	private static String execSQLQuery(Query query, File mappingFile) {
		try {
			File result = File.createTempFile("query-result-", ".xml");
			File batFile = new File("libs\\d2rq\\d2r-query" + (Util.isWindows() ? ".bat" : ""));
			Process proc = Runtime.getRuntime().exec(String.format("\"%s\" -f text \"%s\" \"%s\" > \"%s\"", batFile.getAbsolutePath(), mappingFile.getAbsolutePath(), query.toString(Syntax.syntaxARQ), result.getAbsoluteFile()));
			proc.waitFor();
			
			InputStream is = proc.getInputStream();
	        byte b[] = new byte[is.available()];
	        is.read(b, 0, b.length);
	        is.close();
	        
	        return new String(FileUtils.readWholeFileAsUTF8(result.getAbsolutePath()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Ontology getGlobalOntology() {
		return globalOntology;
	}
	
	public static void setGlobalOntology(Ontology globalOntology) {
		Gryphon.globalOntology = globalOntology;
	}
	
	public static Map<String, Ontology> getLocalOntologies() {
		return localOntologies;
	}
	
	public static Map<String, Database> getLocalDatabases() {
		return localDatabases;
	}
	
	public static void addLocalOntology(String name, Ontology ont){
		localOntologies.put(name, ont);
	}
	
	public static void removeLocalOntology(String name){
		localOntologies.remove(name);
	}
	
	public static void addLocalDatabase(String name, Database db){
		localDatabases.put(name, db);
	}
	
	public static void removeLocalDatabase(String name){
		localDatabases.remove(name);
	}
}