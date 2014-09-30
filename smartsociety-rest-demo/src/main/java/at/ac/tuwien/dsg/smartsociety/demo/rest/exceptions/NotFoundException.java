package at.ac.tuwien.dsg.smartsociety.demo.rest.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class NotFoundException extends WebApplicationException {
	
    private static final long serialVersionUID = 1902126340930034511L;

    public NotFoundException() {
		super(
			Response
				.status( Status.NOT_FOUND )
				.build()
		);
	}
}
