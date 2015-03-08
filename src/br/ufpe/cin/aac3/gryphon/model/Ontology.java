package br.ufpe.cin.aac3.gryphon.model;

import java.net.URI;

import com.hp.hpl.jena.ontology.OntModel;

public abstract class Ontology {
	protected URI uri = null;
	protected OntModel model = null;
	
	public Ontology(URI uri) {
		this.uri = uri;
	}	
	
	public URI getURI() {
		return uri;
	}
	
	public OntModel getModel() {
		return model;
	}
}