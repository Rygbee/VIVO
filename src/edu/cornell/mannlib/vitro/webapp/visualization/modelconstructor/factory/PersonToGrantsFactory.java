/* $This file is distributed under the terms of the license in /doc/license.txt$ */
package edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.factory;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;

import edu.cornell.mannlib.vitro.webapp.visualization.exceptions.MalformedQueryParametersException;
import edu.cornell.mannlib.vitro.webapp.visualization.modelconstructor.PersonToGrantsModelConstructor;
import edu.cornell.mannlib.vitro.webapp.visualization.valueobjects.ConstructedModelTracker;
import edu.cornell.mannlib.vitro.webapp.visualization.visutils.ModelConstructor;

public class PersonToGrantsFactory implements ModelFactoryInterface {

	@Override
	public Model getOrCreateModel(String uri, Dataset dataset)
			throws MalformedQueryParametersException {
		
		Model candidateModel = ConstructedModelTracker.getModel(
				ConstructedModelTracker
				.generateModelIdentifier(
						uri, 
						PersonToGrantsModelConstructor.MODEL_TYPE));
		
		if (candidateModel != null) {
			
			return candidateModel;
			
		} else {
		
			ModelConstructor model = new PersonToGrantsModelConstructor(uri, dataset);
			
			Model constructedModel = model.getConstructedModel();
			ConstructedModelTracker.trackModel(
					ConstructedModelTracker
						.generateModelIdentifier(
								uri, 
								PersonToGrantsModelConstructor.MODEL_TYPE),
								constructedModel);
			
			return constructedModel;
		}
	}
}