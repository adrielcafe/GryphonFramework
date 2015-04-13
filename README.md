![](https://github.com/adrielcafe/GryphonFramework/raw/master/gryphon.png)

Gryphon is a Framework for integrating ontologies and relational databases

Supported ontologies:
* OWL

Supported databases: 
* MySQL
* PostgreSQL

### How to use

#### 1. Configure
```java
// Where the alignments and mappings will be saved? Want to see logs?
GryphonConfig.setWorkingDirectory(Paths.get("alignmentsAndMappings"));
GryphonConfig.setLogEnabled(true); 

// Where are the sources?
OWLOntology globalOntology = new OWLOntology(uriToGlobalOntology); 
OWLOntology localOntology1 = new OWLOntology(uriToLocalOntology1); 
OWLOntology localOntology2 = new OWLOntology(uriToLocalOntology2);
MySQLDatabase localDatabase1 = new MySQLDatabase("localhost", 3306, "root", "", "localDatabase1"); 
PostgreSQLDatabase localDatabase2 = new PostgreSQLDatabase("localhost", 3306, "root", "", "localDatabase2"); 

Gryphon.setGlobalOntology(globalOntology); 
Gryphon.addLocalOntology("localOntology1", localOntology1);
Gryphon.addLocalOntology("localOntology2", localOntology2);
Gryphon.addLocalDatabase("localDatabase1", localDatabase1);
Gryphon.addLocalDatabase("localDatabase2", localDatabase2);
```

#### 2. Align (and Map) the sources
```java
// Simple like that
Gryphon.align();
```

#### 3. Query using SPARQL
```java
// Query must be based on *Global Ontology*
String strQuery = 
       "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
	+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
	+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
	+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
	+ "SELECT ... "
	+ "WHERE { ... } "; 
Query query = QueryFactory.create(strQuery);
OntModel result = Gryphon.query(query);
GryphonUtil.saveModel(result, new File("result.rdf"));
```