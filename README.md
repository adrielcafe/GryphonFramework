![](https://github.com/adrielcafe/GryphonFramework/raw/master/images/gryphon.png)

Gryphon is a lightweight framework for integrating **ontologies** and **relational databases** in *really* simple way.

*Paper coming soon...*

Supported ontology formats:
* OWL
* RDF

Supported databases: 
* MySQL
* PostgreSQL

## How to Use

### 1. Configure
```java
// Where the alignments and mappings will be saved? Want to see logs?
GryphonConfig.setWorkingDirectory(new File("integrationExample"));
GryphonConfig.setLogEnabled(true);
GryphonConfig.setShowGryphonLogoOnConsole(true);

// Init Gryphon
Gryphon.init();

// Where are the sources?
Ontology globalOntBibtex = new Ontology("globalOntology", uriToGlobalOntology);
Ontology localOnt1 = new Ontology("localOntology1", uriToLocalOntology1);
Ontology localOnt2 = new Ontology("localOntology2", uriToLocalOntology2);
Database localDB1 = new Database("localhost", 3306, "username", "password", "localDatabase1", Database.DBMS.MySQL);
Database localDB1 = new Database("localhost", 3306, "username", "password", "localDatabase2", Database.DBMS.PostgreSQL);

Gryphon.setGlobalOntology(globalOntBibtex);
Gryphon.addLocalOntology(localOnt1);
Gryphon.addLocalOntology(localOnt2);
Gryphon.addLocalDatabase(localDB1);
```

### 2. Align (and Map) the Sources
```java
// Simple like that (this may take a while)
Gryphon.align();
```

### 3. Query Using [SPARQL](http://www.w3.org/TR/sparql11-query/)
```java
// Query must be based on *Global Ontology*
String strQuery = 
	 "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
	+"SELECT ?x ?y "
	+"WHERE { ?x rdf:type ?y }";
Gryphon.query(strQuery);
```

### 4. Save Result
```java
// TODO
```
Supported formats:
* TODO

## Practical Example
Check out [Example.java](http://github.com/adrielcafe/GryphonFramework/blob/master/src/br/ufpe/cin/aac3/gryphon/Example.java) for a complete example.

You'll find ontologies and databases used on examples in [examples folder](http://github.com/adrielcafe/GryphonFramework/tree/master/examples).

## Developed By
* Adriel Café | <aac3@cin.ufpe.br>
* Filipe Santana | <fss3@cin.ufpe.br>
* Fred Freitas | <fred@cin.ufpe.br>

## Acknowledgements
[![CIn-UFPE](https://github.com/adrielcafe/GryphonFramework/raw/master/images/cin.png)](http://www2.cin.ufpe.br)
[![UFPE](https://github.com/adrielcafe/GryphonFramework/raw/master/images/ufpe.png)](http://www.ufpe.br)
[![FACEPE](https://github.com/adrielcafe/GryphonFramework/raw/master/images/facepe.png)](http://www.facepe.br)

Special thanks to:
* [Jena](http://jena.apache.org)
* [Sesame](http://rdf4j.org)
* [AgreementMakerLight](http://somer.fc.ul.pt/aml.php)
* [D2RQ](http://d2rq.org)
* [Mediation](http://github.com/correndo/mediation)

## License
```
The MIT License (MIT)

Copyright (c) 2015 Adriel Café, Filipe Santana, Fred Freitas

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```