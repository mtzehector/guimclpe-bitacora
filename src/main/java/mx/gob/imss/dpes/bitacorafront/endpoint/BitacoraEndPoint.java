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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mx.gob.imss.dpes.bitacorafront.exception.BitacoraException;
import mx.gob.imss.dpes.bitacorafront.service.CreateBitacoraService;
import mx.gob.imss.dpes.common.endpoint.BaseGUIEndPoint;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.model.ServiceStatusEnum;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.bitacora.model.Bitacora;
import org.eclipse.microprofile.openapi.annotations.Operation;

/**
 * @author antonio
 */
@Path("/bitacora")
@RequestScoped
public class BitacoraEndPoint extends BaseGUIEndPoint<Bitacora, Bitacora, Bitacora> {

    @Inject
    CreateBitacoraService createBitacoraService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Crear una bitacora",
            description = "Registrar en base de datos la bitacora de eventos")
    @Override
    public Response create(Bitacora bitacora) {
        try {
            ServiceDefinition[] steps = {createBitacoraService};
            Message<Bitacora> response = createBitacoraService.
                    executeSteps(steps, new Message<>(bitacora));

            return toResponse(response);

        } catch (BusinessException e) {
            return toResponse(new Message(null, ServiceStatusEnum.EXCEPCION, e, null));
        } catch (Exception e) {
            log.log(Level.SEVERE, "BitacoraEndPoint.create() - bitacora = [" + bitacora + "]", e);
        }

        return toResponse(new Message(
                null,
                ServiceStatusEnum.EXCEPCION,
                new BitacoraException(BitacoraException.ERROR_DESCONOCIDO),
                null
        ));
    }

}
