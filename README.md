![](https://photos-5.dropbox.com/t/2/AADiBA0IF9OuBOex3D8ZOj9BEmKarYRRjMO1bAcAi_Txmw/12/98073648/png/1024x768/3/1425592800/0/2/Gryphon.png/CLD44S4gASACIAMoAQ/pWKjWXkkFqA98JFTG7C6I29El6Mf-kVgJuiw8oX3KDE)

Gryphon is a Framework for integrating ontologies and relational databases

Supported ontologies:
* OWL
* RDF

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
OWLOntology globalOnt = new OWLOntology(uriToGlobalOnt); 
OWLOntology localOntBibtex = new OWLOntology(uriToBibtex); 
OWLOntology localOntPublication = new OWLOntology(uriToPublication);
MySQLDatabase localDBBibsql = new MySQLDatabase("localhost", 3306, "root", "root", "bibsql"); 

Gryphon.setGlobalOntology(globalOnt); 
Gryphon.getLocalOWLOntologies().put("bibtex", localOntBibtex);
Gryphon.getLocalOWLOntologies().put("publication", localOntPublication);
Gryphon.getLocalDatabases().put("bibsql", localDBBibsql);
```

#### 3. Align the the sources
```java
Gryphon.alignAndMap();
```

#### 4. Query using SPARQL
```java
String queryGlobal = 
	"PREFIX owl: <http://www.w3.org/2002/07/owl#> \n"
	+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
	+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
	+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n"
	+ "PREFIX global_bibtex: <http://aac3.cin.ufpe.br/ns/global_bibtex#> \n"
	+ "SELECT DISTINCT ?x ?y \n"
	+ "WHERE { global_bibtex:author ?x ?y . }"; 
Gryphon.query(queryGlobal);
```
