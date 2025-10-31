/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.bitacorafront.restclient;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import mx.gob.imss.dpes.interfaces.pensionado.model.Pensionado;
import mx.gob.imss.dpes.interfaces.serviciosdigitales.model.Correo;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 *
 * @author juan.garfias
 */
@Path("/promotor")
@RegisterRestClient
public interface PromotorBackClient {
        
    @POST
    @Path("/validar/persona")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Response getDatosPensionando(Pensionado pensionado);
    
}
