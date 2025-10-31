/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.bitacorafront.restclient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 *
 * @author juan.garfias
 */
@Path("/inhabiles")
@RegisterRestClient
public interface SolicitudInhabilesBackClient {

    @Path("/diasInhabiles")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiasInhabiles();
    
}
