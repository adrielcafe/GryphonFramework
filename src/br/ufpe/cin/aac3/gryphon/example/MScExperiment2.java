package br.ufpe.cin.aac3.gryphon.example;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import br.ufpe.cin.aac3.gryphon.Gryphon;
import br.ufpe.cin.aac3.gryphon.Gryphon.ResultFormat;
import br.ufpe.cin.aac3.gryphon.GryphonConfig;
import br.ufpe.cin.aac3.gryphon.GryphonUtil;
import br.ufpe.cin.aac3.gryphon.model.Database;
import br.ufpe.cin.aac3.gryphon.model.Ontology;

public final class MScExperiment2 {
	public static void main(String[] args) {
		// 1. Configure
		GryphonConfig.setWorkingDirectory(new File("integrationMScExperiment2"));
		GryphonConfig.setLogEnabled(true);
		GryphonConfig.setShowLogo(true);
		Gryphon.init();

		try {
			// 2. Set the global ontology and local sources
			Ontology globalOntNews = new Ontology("news", new URI(GryphonUtil.getCurrentURI() + "mscExperiment2/news.owl"));
			Ontology localOntSioc = new Ontology("sioc", new URI(GryphonUtil.getCurrentURI() + "mscExperiment2/sioc.owl"));
			Database localDBJoomla = new Database("localhost", 3306, "root", "", "joomla", Gryphon.DBMS.MySQL);
			Database localDBWordPress = new Database("localhost", 3306, "root", "", "wordpress", Gryphon.DBMS.MySQL);
			
			Gryphon.setGlobalOntology(globalOntNews);
			Gryphon.addLocalOntology(localOntSioc);
			Gryphon.addLocalDatabase(localDBJoomla);
			Gryphon.addLocalDatabase(localDBWordPress);

			// 3. Aligns ontologies and maps databases
			//Gryphon.alignAndMap();

			// 4. Query Using SPARQL
			long startTime = System.currentTimeMillis();
			
			String query = getQuery1();
			Gryphon.query(query, ResultFormat.JSON);
			
			long endTime = System.currentTimeMillis();
			System.out.println("Query Duration: " + ((endTime - startTime) / 1000 % 60) + "s");
		} catch(URISyntaxException e){
			e.printStackTrace();
		}
		
		System.exit(0);
	} 

	private static String getTestQuery(){
		return ""
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
				+ "PREFIX news: <http://ebiquity.umbc.edu/ontology/news.owl#> "
				+ "PREFIX dc: <http://purl.org/dc/terms/> "
				+ "PREFIX sioc: <http://rdfs.org/sioc/ns#> "
				+ "PREFIX vocab: <http://localhost:2020/vocab/> "
				+ "SELECT DISTINCT * "
				+ "WHERE { "
					+ "?x a news:News ; "
					+ "news:title ?title ; "
					+ "news:publishedOn ?date . "
					+ "FILTER (?date >= '2015-08-17'^^xsd:dateTime) . "
				+ "}";
	}
	
	// Q1: Retrieve all news
	private static String getQuery1(){
		return ""
				+ "PREFIX news: <http://ebiquity.umbc.edu/ontology/news.owl#> "
				+ "SELECT DISTINCT ?title ?date ?category ?content "
				+ "WHERE { "
					+ "?x a news:News ; "
					+ "news:title ?title ; "
					+ "news:publishedOn ?date ; "
					+ "news:category ?category ; "
					+ "news:description ?content . "
				+ "}";
	}
	
	// Q2: Retrieve science news
	private static String getQuery2(){
		return ""
				+ "PREFIX news: <http://ebiquity.umbc.edu/ontology/news.owl#> "
				+ "SELECT DISTINCT ?title "
				+ "WHERE { "
					+ "?x a news:News ; "
					+ "news:title ?title ; "
					+ "news:category 'Science' . "
				+ "}";
	}
	
	// Q3: Retrieve news that are not science
	private static String getQuery3(){
		return ""
				+ "PREFIX news: <http://ebiquity.umbc.edu/ontology/news.owl#> "
				+ "SELECT DISTINCT ?title ?category "
				+ "WHERE { "
					+ "?x a news:News ; "
					+ "news:title ?title ; "
					+ "news:category ?category . "
					+ "FILTER NOT EXISTS { "
						+ "FILTER (?category = 'Tech') . "
					+ " } . "
				+ "}";
	}
	
	// Q4: Retrieve news starting on 08/17/2015
	private static String getQuery4(){
		return ""
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
				+ "PREFIX news: <http://ebiquity.umbc.edu/ontology/news.owl#> "
				+ "SELECT DISTINCT ?title ?date "
				+ "WHERE { "
					+ "?x a news:News ; "
					+ "news:title ?title ; "
					+ "news:publishedOn ?date . "
					+ "FILTER (?date >= '2015-08-17'^^xsd:dateTime) . "
				+ "} ORDER BY ?date";
	}
}