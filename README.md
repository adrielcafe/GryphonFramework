![](https://photos-5.dropbox.com/t/2/AADiBA0IF9OuBOex3D8ZOj9BEmKarYRRjMO1bAcAi_Txmw/12/98073648/png/1024x768/3/1425592800/0/2/Gryphon.png/CLD44S4gASACIAMoAQ/pWKjWXkkFqA98JFTG7C6I29El6Mf-kVgJuiw8oX3KDE)

Gryphon is a Framework for integrating ontologies and relational databases

Supported ontologies:
* OWL

Supported databases: 
* MySQL
* PostgreSQL

### How to use

#### 1. Configure
```java
GryphonConfig.setWorkingDirectory(Paths.get("alignments"));
GryphonConfig.setAlignmentThreshold(0.5);
```

#### 2. Set the sources
```java
OWLOntology globalOntology = new OWLOntology(uriToGlobalOntology); 
OWLOntology localOntology1 = new OWLOntology(uriToLocalOntology1); 
OWLOntology localOntology2 = new OWLOntology(uriToLocalOntology2);
MySQLDatabase localDatabase1 = new MySQLDatabase("localhost", 3306, "root", "root", "localDatabase1"); 
MySQLDatabase localDatabase2 = new MySQLDatabase("localhost", 3306, "root", "root", "localDatabase2"); 

Gryphon.setGlobalOntology(globalOntology); 
Gryphon.getLocalOWLOntologies().put("localOntology1", localOntology1);
Gryphon.getLocalOWLOntologies().put("localOntology2", localOntology2);
Gryphon.getLocalDatabases().put("localDatabase1", localDatabase1);
Gryphon.getLocalDatabases().put("localDatabase2", localDatabase2);
```

#### 3. Align the sources
```java
Gryphon.alignAndMap();
```

#### 4. Query using SPARQL
```java
String query = 
	"PREFIX owl: <http://www.w3.org/2002/07/owl#> \n"
	+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
	+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
	+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n"
	+ "SELECT DISTINCT ... \n"
	+ "WHERE { ... }"; 
Gryphon.query(query);
```
