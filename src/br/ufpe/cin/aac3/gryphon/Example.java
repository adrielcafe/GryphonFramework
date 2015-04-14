package br.ufpe.cin.aac3.gryphon;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.apache.log4j.PropertyConfigurator;

import br.ufpe.cin.aac3.gryphon.model.Database;
import br.ufpe.cin.aac3.gryphon.model.Ontology;
import br.ufpe.cin.aac3.gryphon.model.impl.MySQLDatabase;
import br.ufpe.cin.aac3.gryphon.model.impl.OWLOntology;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;

public class Example {
	private static final URI currentURI = new File("").toURI();

	static {
		// Required
		PropertyConfigurator.configure("log4j.properties");
	}
	
	public static void main(String[] args) {
		// 1. Configure
		GryphonConfig.setWorkingDirectory(Paths.get("examples/alignments"));
		GryphonConfig.setLogEnabled(true); 
		
		loadExample1();
		// or
		//loadExample2();
		
		// 2. Align (and Map) the Sources
		Gryphon.align();

		// 3. Query Using SPARQL
		String strQuery = 
				 "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+"SELECT ?x ?y "
				+"WHERE { ?x rdf:type ?y }";
		Query query = Gryphon.createQuery(strQuery);
		OntModel result = Gryphon.query(query);
		
		// 4. Save Result
		GryphonUtil.saveModel(result, Gryphon.Format.RDFXML, new File("result.rdf"));
		GryphonUtil.saveModel(result, Gryphon.Format.JSON_LD, new File("result.json"));
		GryphonUtil.saveModel(result, Gryphon.Format.TTL, new File("result.ttl"));
	} 
	
	// 2 Ontologies, 1 Database
	private static void loadExample1() {
		try {
			Ontology globalOntBibtex = new OWLOntology(new URI(currentURI + "examples/ex1/global_bibtex.owl"));
			Ontology localOnt1 = new OWLOntology(new URI(currentURI + "examples/ex1/bibtex.owl"));
			Ontology localOnt2 = new OWLOntology(new URI(currentURI + "examples/ex1/publication.owl"));
			Database localDB1 = new MySQLDatabase("localhost", 3306, "root", "", "bibtex");
			
			Gryphon.setGlobalOntology(globalOntBibtex);
			Gryphon.addLocalOntology("bibtex", localOnt1);
			Gryphon.addLocalOntology("publication", localOnt2);
			Gryphon.addLocalDatabase("bibsql", localDB1);
		} catch(URISyntaxException e){
			e.printStackTrace();
		}
	}
	
	// 3 Ontologies
	private static void loadExample2() {
		try {
			Ontology global = new OWLOntology(new URI(currentURI + "examples/ex2/human.owl"));
			Ontology localOnt1 = new OWLOntology(new URI(currentURI + "examples/ex2/fly.owl"));
			Ontology localOnt2 = new OWLOntology(new URI(currentURI + "examples/ex2/mouse.owl"));
			Ontology localOnt3 = new OWLOntology(new URI(currentURI + "examples/ex2/zebrafish.owl"));
			
			Gryphon.setGlobalOntology(global);
			Gryphon.addLocalOntology("fly", localOnt1);
			Gryphon.addLocalOntology("mouse", localOnt2);
			Gryphon.addLocalOntology("zebrafish", localOnt3);
		} catch(URISyntaxException e){ 
			e.printStackTrace(); 
		}
	}
}