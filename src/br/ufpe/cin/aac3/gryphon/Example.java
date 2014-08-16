package br.ufpe.cin.aac3.gryphon;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import br.ufpe.cin.aac3.gryphon.model.Database;
import br.ufpe.cin.aac3.gryphon.model.Ontology;
import br.ufpe.cin.aac3.gryphon.model.impl.MySQLDatabase;
import br.ufpe.cin.aac3.gryphon.model.impl.OWLOntology;
import br.ufpe.cin.aac3.gryphon.model.impl.PostgreSQLDatabase;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;

public class Example {
	private static final URI currentURI = new File("").toURI();
	
	public static void main(String[] args) {
		// Configuring
		GryphonConfig.setWorkingDirectory(Paths.get("alignments"));
		GryphonConfig.setAlignmentThreshold(0.5);
		GryphonConfig.setLogEnabled(true); 
		
		// Loading datasources
		//loadExampleIntegrativO();
		loadExampleBibtex();
		//loadExampleAML();
		
		// Aligning
		//if(GryphonConfig.getWorkingDirectory().toFile().listFiles().length == 0)
			Gryphon.alignAndMap();

		// Querying the GLOBAL ontology
		String strQuery = 
				"PREFIX vocab: <http://localhost:2020/vocab/>"
				+"	PREFIX global_bibtex: <http://aac3.cin.ufpe.br/ns/global_bibtex#>"
				+"	PREFIX integrativo: <http://www.cin.ufpe.br/~fss3/integrativO#>"
				+"	PREFIX btl2: <http://purl.org/biotop/btl2.owl#>"
				+"	PREFIX biotop: <http://purl.org/biotop/biotop.owl#>"
				+"	PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+"	PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+"	SELECT ?x WHERE { ?x rdf:type global_bibtex:Article }";
		
				/*+"	SELECT ?species" 
				+"	WHERE {"
				//+"		OPTIONAL {"
				//+"			?species rdfs:subClassOf btl2:TaxonValueRegion ."
				//+"		} ."
				//+"		OPTIONAL {"
				+"			?s biotop:SpeciesDrosophilaMelanogasterValueRegion ?species ."
				+"			FILTER regex(?species, \"^Drosophila\") ."
				//+"		} ."
				//+"		OPTIONAL {"
				//+"			?organism rdf:type btl2:Organism ;"
				//+"			btl2:TaxonValueRegion \"Drosophila\" ;"
				//+"			biotop:SpeciesDrosophilaMelanogasterValueRegion ?species ."
				//+"		}"
				+"	}";*/
		
				/*+"	SELECT ?organism ?genus" 
				+"	WHERE {"
				+"	?organism rdf:type vocab:organism ;"
				+"	vocab:organism_species \"larissa\" ;"
				+"	vocab:organism_genus ?genus ."
				+"	}";*/
		Query query = QueryFactory.create(strQuery);
		Gryphon.query(query);
	} 
	
	private static void loadExampleIntegrativO() {
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
	}
	
	private static void loadExampleBibtex() {
		try {
			Ontology globalOntBibtex = new OWLOntology(new URI(currentURI.toString() + "examples/bibtex/global_bibtex.owl"));
			Ontology localOntBibtex = new OWLOntology(new URI(currentURI.toString() + "examples/bibtex/bibtex.owl"));
			Ontology localOntPublication = new OWLOntology(new URI(currentURI.toString() + "examples/bibtex/publication.owl"));
			Database localDBBibsql = new MySQLDatabase("localhost", 3306, "root", "", "bibtex");
			
			Gryphon.setGlobalOntology(globalOntBibtex);
			
			Gryphon.addLocalOntology("bibtex", localOntBibtex);
			Gryphon.addLocalOntology("publication", localOntPublication);
			Gryphon.addLocalDatabase("bibsql", localDBBibsql);
		} catch(URISyntaxException e){ }
	}
	
	private static void loadExampleAML() {
		try {
			Ontology global = new OWLOntology(new URI("file:///E:/Documentos/Desktop/AML-Jar-master/store/anatomy/human.owl"));
			Ontology local = new OWLOntology(new URI("file:///E:/Documentos/Desktop/AML-Jar-master/store/anatomy/mouse.owl"));
			
			Gryphon.setGlobalOntology(global);
			
			Gryphon.addLocalOntology("mouse", local);
		} catch(URISyntaxException e){ e.printStackTrace(); }
	}
}