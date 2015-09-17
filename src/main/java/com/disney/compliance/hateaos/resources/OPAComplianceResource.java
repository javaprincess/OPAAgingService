package com.disney.compliance.hateaos.resources;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import com.disney.compliance.aging.POC.queue.entry.AgedUserEntry;
import com.disney.compliance.aging.POC.transactions.ReactivationTx;

public class OPAComplianceResource extends ResourceSupport {
	private final ReactivationTx reactivationTx;
	private final List<AgedUserEntry> deactivatedUsersList;

	public OPAComplianceResource(List<AgedUserEntry> deactivatedUsersList) {
		this.deactivatedUsersList = deactivatedUsersList;
		this.reactivationTx = new ReactivationTx();
	}
	
   /* public OPAComplianceResource(ReactivationTx reactivate) {
        String username = reactivate.getUser().getUsername();
        this.reactivationTx = reactivationTx;
        this.add(new Link(reactivate.getUri(), "reactivateUser-uri"));
        this.add(linkTo(OPAComplianceRestController.class, username).withRel("reactivationTx"));
        this.add(linkTo(methodOn(OPAComplianceRestController.class, username).readBookmark(username, reactivationTx.getId())).withSelfRel());
    }*/

    public ReactivationTx getReativationTx() {
        return reactivationTx;
    }

}
