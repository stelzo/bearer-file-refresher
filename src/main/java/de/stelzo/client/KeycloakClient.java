package de.stelzo.client;

import de.stelzo.dto.KeycloakAuthenticateResponse;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

@Path("/")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey="kc")
public interface KeycloakClient {

    @POST
    KeycloakAuthenticateResponse token(Form parameter, @HeaderParam("Authorization") String auth);
}
