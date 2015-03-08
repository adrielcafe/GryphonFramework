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

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;

public class Example {
	private static final URI currentURI = new File("").toURI();

	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		
		// Configuring
		GryphonConfig.setWorkingDirectory(Paths.get("alignments"));
		GryphonConfig.setAlignmentThreshold(0.5);
		GryphonConfig.setLogEnabled(true); 
		
		// Loading sources
		loadExample1();
		//loadExample2();
		
		// Aligning
		//Gryphon.align();

		// Querying the GLOBAL ontology
		String strQuery = 
				"PREFIX vocab: <http://localhost:2020/vocab/>"
				+"	PREFIX global_bibtex: <http://aac3.cin.ufpe.br/ns/global_bibtex#>"
				+"	PREFIX integrativo: <http://www.cin.ufpe.br/~fss3/integrativO#>"
				+"	PREFIX btl2: <http://purl.org/biotop/btl2.owl#>"
				+"	PREFIX biotop: <http://purl.org/biotop/biotop.owl#>"
				+"	PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+"	PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+"	SELECT ?x ?y"
				+ " WHERE { ?x rdf:type ?y }";
		Query query = QueryFactory.create(strQuery);
		Gryphon.query(query);
	} 
	
	private static void loadExample1() {
		try {
			Ontology globalOntBibtex = new OWLOntology(new URI(currentURI.toString() + "examples/ex1/global_bibtex.owl"));
			Ontology localOnt1 = new OWLOntology(new URI(currentURI.toString() + "examples/ex1/bibtex.owl"));
			Ontology localOnt2 = new OWLOntology(new URI(currentURI.toString() + "examples/ex1/publication.owl"));
			Database localDB1 = new MySQLDatabase("localhost", 3306, "root", "", "bibtex");
			
			Gryphon.setGlobalOntology(globalOntBibtex);
			
			Gryphon.addLocalOntology("bibtex", localOnt1);
			Gryphon.addLocalOntology("publication", localOnt2);
			Gryphon.addLocalDatabase("bibsql", localDB1);
		} catch(URISyntaxException e){ }
	}
	
	private static void loadExample2() {
		try {
			Ontology global = new OWLOntology(new URI(currentURI.toString() + "examples/ex2/human.owl"));
			Ontology localOnt1 = new OWLOntology(new URI(currentURI.toString() + "examples/ex2/fly.owl"));
			Ontology localOnt2 = new OWLOntology(new URI(currentURI.toString() + "examples/ex2/mouse.owl"));
			Ontology localOnt3 = new OWLOntology(new URI(currentURI.toString() + "examples/ex2/zebrafish.owl"));
			
			Gryphon.setGlobalOntology(global);

			Gryphon.addLocalOntology("fly", localOnt1);
			Gryphon.addLocalOntology("mouse", localOnt2);
			Gryphon.addLocalOntology("zebrafish", localOnt3);
		} catch(URISyntaxException e){ e.printStackTrace(); }
	}
	
	/*private static void loadExampleIntegrativO() {
		try {
			Ontology globalOntIntegrativO = new OWLOntology(new URI(currentURI.toString() + "examples/integrativOv2.owl"));
			//Ontology localOntGO = new OWLOntology(new URI(currentURI.toString() + "examples/gene-ontology.owl"));
			Ontology localOntBioTop = new OWLOntology(new URI(currentURI.toString() + "examples/biotop.owl"));
			//Ontology localOntChEBI = new OWLOntology(new URI(currentURI.toString() + "examples/chebi.owl"));
			Database localDBReactome = new MySQLDatabase("localhost", 3306, "root", "", "reactome");
			Database localDBFlyBase = new PostgreSQLDatabase("localhost", 5432, "postgres", "root", "FB2014_02");

			Gryphon.setGlobalOntology(globalOntIntegrativO);

			//Gryphon.addLocalOntology("gene-ontology", localOntGO);
			Gryphon.addLocalOntology("biotop", localOntBioTop);
			//Gryphon.addLocalOntology("chebi", localOntChEBI);
			Gryphon.addLocalDatabase("reactome", localDBReactome);
			Gryphon.addLocalDatabase("flybase", localDBFlyBase);
		} catch(URISyntaxException e){ }
	}*/
}