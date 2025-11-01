/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.bitacorafront.restclient;

/**
 *
 * @author antonio
 */
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import mx.gob.imss.dpes.interfaces.bitacora.model.CronTarea;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/cronTarea")
@RegisterRestClient
public interface CronTareaClient {

    @Path("/agregar")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(CronTarea request);

    @Path("/vencidas")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTareasVencidas();

    @Path("/tarea/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTareaCatalogo(@PathParam("id") Integer id);

    @Path("/actualizarPorJob")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarTarea(CronTarea request);

    @Path("/tareas")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTareas();
    
    @Path("/tarea/solicitud/{cveSol}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTareabySolicitud(@PathParam("cveSol") Long cveSol);
}
