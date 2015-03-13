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
import com.hp.hpl.jena.query.QueryFactory;

public class Example {
	private static final URI currentURI = new File("").toURI();
	
	public static void main(String[] args) {
		// Required
		PropertyConfigurator.configure("log4j.properties");
		
		// 1. Configure
		GryphonConfig.setWorkingDirectory(Paths.get("alignments"));
		GryphonConfig.setLogEnabled(true); 
		
		// 2. Set the sources
		loadExample1();
		// or
		//loadExample2();
		
		// 3. Align the sources
		Gryphon.align();

		// 4. Query using SPARQL
		String strQuery = 
				 "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+"PREFIX vocab: <http://localhost:2020/vocab/> "
				+"SELECT ?x ?y "
				+"WHERE { ?x rdf:type ?y }";
		Query query = QueryFactory.create(strQuery);
		OntModel result = Gryphon.query(query);
		GryphonUtil.saveModel(result, new File("result.rdf"));
	} 
	
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
		} catch(URISyntaxException e){ }
	}
	
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
		} catch(URISyntaxException e){ e.printStackTrace(); }
	}
}