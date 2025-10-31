/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.bitacorafront.endpoint;

import java.util.logging.Level;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mx.gob.imss.dpes.bitacorafront.exception.CronTareasException;
import mx.gob.imss.dpes.bitacorafront.model.CronTareasRq;
import mx.gob.imss.dpes.bitacorafront.service.CronJobService;
import mx.gob.imss.dpes.bitacorafront.service.CronTareaService;
import mx.gob.imss.dpes.bitacorafront.service.ValidacionTareaService;
import mx.gob.imss.dpes.common.endpoint.BaseGUIEndPoint;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.BaseModel;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.model.ServiceStatusEnum;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.bitacora.model.CronTarea;

/**
 * @author juan.garfias
 */
@Path("/cronTarea")
@RequestScoped
public class CronTareasEndPoint extends BaseGUIEndPoint<CronTarea, CronTarea, CronTarea> {

    @Inject
    CronTareaService cronTareaService;

    @Inject
    CronJobService cronJobService;

    @Inject
    ValidacionTareaService validacionTareaService;


    @Path("/agregar")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response agregarTarea(CronTarea cronTarea) throws BusinessException {

        log.log(Level.INFO, "cronTarea {0}", cronTarea);
        ServiceDefinition[] steps = {cronTareaService};
        Message<CronTarea> response = cronTareaService.
                executeSteps(steps, new Message<>(cronTarea));

        return toResponse(response);
    }

    @Path("/vencidas")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response consultarVencidas() {
        try {
            ServiceDefinition[] steps = {cronJobService};
            Message<CronTareasRq> response = cronJobService.execute(
                    new Message<CronTareasRq>()
            );
            return toResponse(response);
        } catch (BusinessException e) {
            return toResponse(new Message(null, ServiceStatusEnum.EXCEPCION, e, null));
        } catch (Exception e) {
            log.log(Level.SEVERE, "CronTareasEndPoint.consultarVencidas() ", e);
        }
        return toResponse(new Message(
                null,
                ServiceStatusEnum.EXCEPCION,
                new CronTareasException(CronTareasException.ERROR_DESCONOCIDO),
                null
        ));
    }

//    @POST
//    @Path("/cron/{id}")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getCron(@PathParam("id") Integer id) {
//        log.log(Level.INFO,">>>getCron taskBeanService.class="+taskBeanService.getClass() );
//        log.log(Level.INFO,">>>getCron id=="+id);
//        String response="";
//        switch(id){
//            case 1: log.log(Level.INFO,">>>getCron 1");response += taskBeanService.isStarted();break;
//            case 2: log.log(Level.INFO,">>>getCron 2");response += taskBeanService.standby();break;
//            case 3: log.log(Level.INFO,">>>getCron 3");response += taskBeanService.start();break;
//            case 4: log.log(Level.INFO,">>>getCron 4");response += taskBeanService.check();break;
//            case 5: log.log(Level.INFO,">>>getCron 5");response += taskBeanService.pause();break;
//            case 6: log.log(Level.INFO,">>>getCron 6");response += taskBeanService.resume();break;
//            case 80: log.log(Level.INFO,">>>getCron 80");response += taskBeanService.shutdown();break;
//            default: log.log(Level.INFO,">>>getCron default");response += taskBeanService.isStarted();break;
//        }
//        return toResponse(new Message(response));
//
//    }

    @Path("/creaTareasPendienteCargaCEP")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response creaTareasPendienteCargaCEP() {
        try {
            ServiceDefinition[] steps = {validacionTareaService};
            Message<BaseModel> response = validacionTareaService.execute(
                    new Message<BaseModel>()
            );
            return toResponse(response);
        } catch (BusinessException e) {
            return toResponse(new Message(null, ServiceStatusEnum.EXCEPCION, e, null));
        } catch (Exception e) {
            log.log(Level.SEVERE, "CronTareasEndPoint.creaTareasPendienteCargaCEP() ", e);
        }
        return toResponse(new Message(
                null,
                ServiceStatusEnum.EXCEPCION,
                new CronTareasException(CronTareasException.ERROR_DESCONOCIDO),
                null
        ));
    }
}
