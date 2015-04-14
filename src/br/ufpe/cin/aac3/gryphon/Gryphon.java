package br.ufpe.cin.aac3.gryphon;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

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
	public static final String VERSION = "1.0";
	private static final OWLOntologyManager owlManager = OWLManager.createOWLOntologyManager();
	private static Ontology globalOntology = null;
	private static Map<String, Ontology> localOntologies = null;
	private static Map<String, Database> localDatabases = null;

	static {
		if(GryphonConfig.isLogEnabled()){
			System.out.println(
				   "\n          _          (`-. "
				 + "\n          \\`----.    ) ^_`)    GRYPHON v" + VERSION
				 + "\n   ,__     \\__   `\\_/  ( `     A Framework for Semantic Integration"
				 + "\n    \\_\\      \\__  `|   }"
				 + "\n       \\  .--' \\__/    }       By Adriel Café, Filipe Santana, Fred Freitas"
				 + "\n       ))/   \\__,<  /_/               {aac3, fss3, fred}@cin.ufpe.br"
				 + "\n       ((|  _/_/ `\\ \\_\\_"
				 + "\n        `\\_____\\\\  )__\\_\\"
				 + "\n"
			);
		}
		localOntologies = new HashMap<String, Ontology>();
		localDatabases = new HashMap<String, Database>();
	}

	private Gryphon() { }

	public static void align() {
		if(!localDatabases.isEmpty()){
			GryphonUtil.logInfo("Mapping and Aligning databases...");
			for (String key : localDatabases.keySet()) {
				File mappingFile = new File(GryphonConfig.getWorkingDirectory().toFile(), "db_" + key + ".ttl");
				File alignmentFile = new File(GryphonConfig.getWorkingDirectory().toFile(), "db_" + key + ".rdf");
				Gryphon.mapDatabase(localDatabases.get(key), mappingFile, alignmentFile);
				Gryphon.alignOntology(globalOntology.getURI(), alignmentFile.toURI(), alignmentFile);
				GryphonUtil.logInfo(String.format("> Database %s was mapped and aligned", key));
			}
		}

		if(!localOntologies.isEmpty()){
			GryphonUtil.logInfo("Aligning ontologies...");
			for (String key : localOntologies.keySet()) {
				File alignmentFile = new File(GryphonConfig.getWorkingDirectory().toFile(), "ont_" + key + ".rdf");
				Gryphon.alignOntology(globalOntology.getURI(), localOntologies.get(key).getURI(), alignmentFile);
				GryphonUtil.logInfo(String.format("> Ontology %s was aligned", key));
			}
		}
	}

	public static void alignOntology(URI globalOntologyURI, URI localOntologyURI, File alignmentFile) {
		try {
			File jarFile = new File("libs/aml/AgreementMakerLight.jar");
			String cmd = String.format("cd \"%s\" && java -jar \"%s\" -s \"%s\" -t \"%s\" -o \"%s\" -m", jarFile.getParentFile().getAbsolutePath(), jarFile.getAbsolutePath(), new File(globalOntologyURI).getAbsolutePath(), new File(localOntologyURI).getAbsolutePath(), alignmentFile.getAbsolutePath());
			ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", cmd);
			Process process = processBuilder.start();
			process.waitFor();
		} catch (Exception e) {
			GryphonUtil.logError(e.getMessage());
		}
	}

	public static void mapDatabase(Database db, File mappingFile, File alignmentFile) {
		String mapping = null;
		
		try {
			File scriptFile = new File("libs/d2rq/generate-mapping" + (GryphonUtil.isWindows() ? ".bat" : ""));
			Process process = Runtime.getRuntime().exec(String.format("%s \"%s\" -o \"%s\" -u \"%s\" -p \"%s\" \"%s\"", (GryphonUtil.isWindows() ? "" : "bash"), scriptFile.getAbsolutePath(), mappingFile.getAbsolutePath(), db.getUsername(), db.getPassword(), db.getJdbcURL()));
			process.waitFor();
			mapping = FileUtils.readWholeFileAsUTF8(mappingFile.getAbsolutePath());
		} catch (Exception e) {
			GryphonUtil.logError(e.getMessage());
		}
		
		try {
			Files.write(Paths.get(mappingFile.toURI()), mapping.getBytes());
			String d2rqNS = "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#";
			String rdfNS = "http://localhost:2020/vocab/";
			FileWriter fileWriter = new FileWriter(alignmentFile);
			Model ttlModel = FileManager.get().loadModel(mappingFile.toURI().toString());
			OntModel owlModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF);
			owlModel.createOntology(rdfNS);

			for (StmtIterator i = ttlModel.listStatements(); i.hasNext();) {
				Statement s = i.nextStatement();
				if (s.getPredicate().toString().equals(d2rqNS + "class"))
					owlModel.createClass(rdfNS + s.getSubject().getLocalName());
			}

			for (StmtIterator i = ttlModel.listStatements(); i.hasNext();) {
				Statement s = i.nextStatement();
				Resource r = null;
				String id = null;
				
				if (s.getPredicate().toString().equals(d2rqNS + "property")) {
					id = s.getSubject().getLocalName();
					owlModel.createDatatypeProperty(rdfNS + id);					
				} else if (s.getPredicate().toString().equals(d2rqNS + "belongsToClassMap")) {
					id = s.getSubject().getLocalName();
					r = owlModel.getResource(rdfNS + s.getObject().asResource().getLocalName());
					owlModel.getDatatypeProperty(rdfNS + id).setDomain(r);
				}
			}

			owlModel.write(fileWriter, Gryphon.Format.RDFXML.toString());
			owlModel.close();
		} catch (IOException e) {
			GryphonUtil.logError(e.getMessage());
		}
	}
	
	public static Query createQuery(String strQuery){
		return QueryFactory.create(strQuery, Syntax.syntaxARQ);
	}

	public static OntModel query(Query queryGlobal){
		File alignmentFile = null;
		File mappingFile = null;
		OntModel modelResult = ModelFactory.createOntologyModel();		
		Query queryLocal = null;
		ResultSet ontResult = null;
		OntModel dbResult = null;
		
		for(ExtendedIterator<Individual> i = modelResult.listIndividuals(); i.hasNext();)
			i.next().remove();
		
		for(String key : getLocalOntologies().keySet()){
			alignmentFile = new File(GryphonConfig.getWorkingDirectory().toFile(), "ont_" + key + ".rdf");
			
			queryLocal = queryRewrite(queryGlobal, alignmentFile);
			
			if(queryLocal != null){
				GryphonUtil.logInfo("REWRITTEN QUERY FOR " + key + ":\n" + queryLocal.serialize() + "\n");
				ontResult = execSPARQLQuery(queryLocal, getLocalOntologies().get(key).getModel());
				modelResult.add(ontResult.getResourceModel());
				GryphonUtil.logInfo("QUERY RESULT FOR " + key + ":\n" + ResultSetFormatter.asText(ontResult) + "\n");
			}
		}
		
		for(String key : getLocalDatabases().keySet()){
			alignmentFile = new File(GryphonConfig.getWorkingDirectory().toFile(), "db_" + key + ".rdf");
			mappingFile = new File(GryphonConfig.getWorkingDirectory().toFile(), "db_" + key + ".ttl");
			
			queryLocal = queryRewrite(queryGlobal, alignmentFile);
			
			if(queryLocal != null){
				GryphonUtil.logInfo("REWRITTEN QUERY FOR " + key + ":\n" + queryLocal.serialize() + "\n");
				dbResult = execSQLQuery(queryLocal, mappingFile);
				modelResult.add(dbResult);
				GryphonUtil.logInfo("QUERY RESULT FOR " + key + ":\n" + dbResult + "\n");
			}
		}

		List<Statement> sr = new ArrayList<Statement>();
		for (StmtIterator i = modelResult.listStatements(); i.hasNext();) {
			try {
				Statement s = i.nextStatement();
				Resource r = s.getResource();
				if(r.getURI().toString().contains("www.w3.org")){
					sr.add(s);
				}
			} catch(Exception e) { }
		}
		modelResult.remove(sr);
		return modelResult;
	}
	
	private static ResultSet execSPARQLQuery(Query query, Model model) {
		return QueryExecutionFactory.create(query, model).execSelect();
	}
	
	private static OntModel execSQLQuery(Query query, File mappingFile) {
		try {
			OntModel resultModel = ModelFactory.createOntologyModel();
			File ttlResultFile = File.createTempFile("gryphon-sqlquery-result-", ".ttl");
			File rdfResultFile = File.createTempFile("gryphon-sqlquery-result-", ".rdf");
			File batFile = new File("libs\\d2rq\\d2r-query" + (GryphonUtil.isWindows() ? ".bat" : ""));
			Process process = Runtime.getRuntime().exec(String.format("\"%s\" -f ttl \"%s\" \"%s\" > \"%s\"", batFile.getAbsolutePath(), mappingFile.getAbsolutePath(), query.toString(Syntax.syntaxARQ), ttlResultFile.getAbsoluteFile()));
			process.waitFor();

			OWLOntology ttlOntology = owlManager.loadOntologyFromOntologyDocument(ttlResultFile);
			RDFXMLDocumentFormat rdfXmlFormat = new RDFXMLDocumentFormat();
			owlManager.saveOntology(ttlOntology, rdfXmlFormat, IRI.create(rdfResultFile));
			
			Model m = FileManager.get().loadModel(rdfResultFile.toURI().toString());
			resultModel.add(m);
	        return resultModel;
		} catch (Exception e) {
			GryphonUtil.logError(e.getMessage());
			return null;
		}
	}

	private static Query queryRewrite(Query query, File alignmentFile) {
		try {
			File jarFile = new File("libs/mediation/Mediation.jar");
			String cmd = String.format("cd \"%s\" && java -jar \"%s\" \"%s\" \"%s\"", jarFile.getParentFile().getAbsolutePath(), jarFile.getAbsolutePath(), alignmentFile.getAbsolutePath(), query.toString(Syntax.syntaxARQ).replace("\n", " "));
			ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", cmd);
			Process process = processBuilder.start();
			process.waitFor();
			InputStream is = process.getInputStream();
	        byte b[] = new byte[is.available()];
	        is.read(b, 0, b.length);
	        is.close();
	        return QueryFactory.create(new String(b)); 
		} catch (Exception e) {
			GryphonUtil.logError(e.getMessage());
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
	
	public enum Format {
		RDFXML("RDF/XML-ABBREV"),
		JSON_LD("JSON-LD"),
		TTL("TTL");
		
		private String name;
		 
	    private Format(String s) {
	        name = s;
	    } 
	    
	    @Override
	    public String toString() {
	    	return name;
	    }
	}
}