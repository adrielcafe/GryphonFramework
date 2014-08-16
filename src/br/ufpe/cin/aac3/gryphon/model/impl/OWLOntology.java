package br.ufpe.cin.aac3.gryphon.model.impl;

import java.net.URI;

import br.ufpe.cin.aac3.gryphon.model.Ontology;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public final class OWLOntology extends Ontology {
	public OWLOntology(URI uri) {
		super(uri);

		Model defaultModel = ModelFactory.createDefaultModel().read(uri.toString());
		model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, defaultModel);
	}
}