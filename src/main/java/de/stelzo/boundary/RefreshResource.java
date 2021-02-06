package de.stelzo.boundary;

import de.stelzo.logic.Manager;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/refresh")
public class RefreshResource {

    @Inject
    Manager manager;

    @PUT
    @Consumes(MediaType.MEDIA_TYPE_WILDCARD)
    @Produces(MediaType.TEXT_PLAIN)
    public Response refresh() {
        manager.refreshToken();
        return Response.status(Response.Status.OK).build();
    }
}