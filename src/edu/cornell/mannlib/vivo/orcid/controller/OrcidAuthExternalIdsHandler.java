/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vivo.orcid.controller;

import static edu.cornell.mannlib.orcidclient.actions.ApiAction.ADD_EXTERNAL_ID;
import static edu.cornell.mannlib.vivo.orcid.controller.OrcidIntegrationController.PATH_ADD_EXTERNAL_IDS;

import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.RedirectResponseValues;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.responsevalues.ResponseValues;

/**
 * We offered to add external IDs and they decided to go ahead. Get
 * authorization.
 * 
 * We can't assume that they haven't been here before, so they might already
 * have authorized, or denied authorization.
 */
public class OrcidAuthExternalIdsHandler extends OrcidAbstractHandler {
	private static final Log log = LogFactory
			.getLog(OrcidAuthExternalIdsHandler.class);

	private AuthorizationStatus status;

	public OrcidAuthExternalIdsHandler(VitroRequest vreq) {
		super(vreq);
	}

	public ResponseValues exec() throws URISyntaxException,
			OrcidClientException {
		updateState();

		if (isAddingExternalIds()) {
			status = auth.getAuthorizationStatus(ADD_EXTERNAL_ID);
			if (status.isNone()) {
				return seekAuthorizationForExternalIds();
			} else if (status.isSuccess()) {
				return redirectToAddExternalIds();
			} else if (status.isDenied()) {
				return showDeniedAuthorization(ADD_EXTERNAL_ID);
			} else {
				return showFailedAuthorization(ADD_EXTERNAL_ID);
			}
		} else {
			return show500InternalServerError("No External IDs selected.");
		}
	}

	private void updateState() {
		state.setAddVivoId(vreq.getParameterMap().keySet()
				.contains("addVivoId"));
		state.setAddCornellId(vreq.getParameterMap().keySet()
				.contains("addCornellId"));
	}

	private boolean isAddingExternalIds() {
		return state.isAddVivoId() || state.isAddCornellId();
	}

	private ResponseValues seekAuthorizationForExternalIds()
			throws OrcidClientException, URISyntaxException {
		log.debug("Seeking authorization to add external IDs");
		String returnUrl = occ.resolvePathWithWebapp(PATH_ADD_EXTERNAL_IDS);
		String seekUrl = auth.seekAuthorization(ADD_EXTERNAL_ID, returnUrl);
		return new RedirectResponseValues(seekUrl);
	}

	private ResponseValues redirectToAddExternalIds() throws URISyntaxException {
		log.debug("Already authorized to add external IDs.");
		return new RedirectResponseValues(
				occ.resolvePathWithWebapp(PATH_ADD_EXTERNAL_IDS));
	}

}