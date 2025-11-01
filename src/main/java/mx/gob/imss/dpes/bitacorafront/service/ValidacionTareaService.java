/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.bitacorafront.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import mx.gob.imss.dpes.bitacorafront.exception.CronTareasException;
import mx.gob.imss.dpes.bitacorafront.model.SolicitudCep;
import mx.gob.imss.dpes.bitacorafront.restclient.CronTareaClient;
import mx.gob.imss.dpes.bitacorafront.restclient.SolicitudBackClient;
import mx.gob.imss.dpes.common.enums.TipoCronTareaEnum;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.BaseModel;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.bitacora.model.CronTarea;
import mx.gob.imss.dpes.interfaces.bitacora.model.TareaAccion;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * @author juanf.barragan
 */
@Provider
public class ValidacionTareaService extends ServiceDefinition<BaseModel, BaseModel> {

    @Inject
    @RestClient
    private SolicitudBackClient solicitudBackClient;

    @Inject
    @RestClient
    private CronTareaClient cronTareaClient;

    @Inject
    private CronTareaService cronTareaService;

    @Override
    public Message<BaseModel> execute(Message<BaseModel> request) throws BusinessException {
        try {
            List<Long> listaSolicitudesPendientesCargaCEP = new ArrayList<Long>();
            Response response = solicitudBackClient.consultaPendienteCep();
            if (response != null && response.getStatus() == 200) {
                List<SolicitudCep> solicitudes = response.readEntity(new GenericType<List<SolicitudCep>>() {
                });
                this.obtenerTareaPorSolicitud(solicitudes, listaSolicitudesPendientesCargaCEP);
                this.crearTareaPendienteCargarCEP(listaSolicitudesPendientesCargaCEP);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.log(Level.SEVERE, "ValidacionTareaService.execute() - " +
                    "request = [" + request.getPayload() + "]", e);
            throw new CronTareasException(CronTareasException.ERROR_SERVICIO_SOLICITUD);
        }
        return request;
    }

    private void obtenerTareaPorSolicitud(List<SolicitudCep> solicitudes, List<Long> listaSolicitudesPendientesCargaCEP)
            throws BusinessException {

        try {
            if (solicitudes != null && solicitudes.size() > 0) {
                for (SolicitudCep sol : solicitudes) {
                    Response tareaRes = cronTareaClient.getTareabySolicitud(sol.getId());
                    if (tareaRes != null && tareaRes.getStatus() == 204)
                        listaSolicitudesPendientesCargaCEP.add(sol.getId());
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "ValidacionTareaService.obtenerTareaPorSolicitud() - " +
                    "solicitudes = [" + solicitudes + "], listaSolicitudesPendientesCargaCEP = [" +
                    listaSolicitudesPendientesCargaCEP + "]", e);
            throw new CronTareasException(CronTareasException.ERROR_SERVICIO_BITACORA);
        }
    }

    private void crearTareaPendienteCargarCEP(List<Long> listaSolicitudesPendientesCargaCEP) throws BusinessException {
        try {
            if (listaSolicitudesPendientesCargaCEP != null && listaSolicitudesPendientesCargaCEP.size() > 0) {
                for (Long id : listaSolicitudesPendientesCargaCEP) {
                    Message<CronTarea> messageCronTarea = new Message<CronTarea>();
                    CronTarea cronTarea = new CronTarea();
                    cronTarea.setCveSolicitud(id);
                    TareaAccion tareaAccion = new TareaAccion();
                    tareaAccion.setId(TipoCronTareaEnum.PENDIENTE_CARGAR_CEP.getTipo());
                    cronTarea.setTareaAccion(tareaAccion);
                    messageCronTarea.setPayload(cronTarea);
                    cronTareaService.execute(messageCronTarea);
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "ValidacionTareaService.crearTareaPendienteCargarCEP() - " +
                    "listaSolicitudesPendientesCargaCEP = [" + listaSolicitudesPendientesCargaCEP + "]", e);
            throw new CronTareasException(CronTareasException.ERROR_SERVICIO_CRONTAREA);
        }
    }
}


