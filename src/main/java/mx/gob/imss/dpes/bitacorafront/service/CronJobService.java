/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.bitacorafront.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import mx.gob.imss.dpes.bitacorafront.exception.BitacoraException;
import mx.gob.imss.dpes.bitacorafront.exception.CronTareasException;
import mx.gob.imss.dpes.bitacorafront.model.CorreoDatosEnvio;
import mx.gob.imss.dpes.bitacorafront.model.CronTareasRq;
import mx.gob.imss.dpes.bitacorafront.model.PersonaModel;
import mx.gob.imss.dpes.bitacorafront.restclient.BitacoraClient;
import mx.gob.imss.dpes.bitacorafront.restclient.CronTareaClient;
import mx.gob.imss.dpes.bitacorafront.restclient.EntidadFinancieraBackClient;
import mx.gob.imss.dpes.bitacorafront.restclient.PersonaBackClient;
import mx.gob.imss.dpes.bitacorafront.restclient.PromotorBackClient;
import mx.gob.imss.dpes.bitacorafront.restclient.SolicitudBackClient;
import mx.gob.imss.dpes.common.enums.BitacoraEnum;
import mx.gob.imss.dpes.common.enums.TipoCronTareaEnum;
import mx.gob.imss.dpes.common.enums.TipoEstadoSolicitudEnum;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.bitacora.model.Bitacora;
import mx.gob.imss.dpes.interfaces.bitacora.model.CronTarea;
import mx.gob.imss.dpes.interfaces.bitacora.model.EstadoOrigen;
import mx.gob.imss.dpes.interfaces.bitacora.model.TareaAccion;
import mx.gob.imss.dpes.interfaces.entidadfinanciera.model.EntidadFinanciera;
import mx.gob.imss.dpes.interfaces.pensionado.model.Pensionado;
import mx.gob.imss.dpes.interfaces.serviciosdigitales.model.Persona;
import mx.gob.imss.dpes.interfaces.solicitud.model.EstadoSolicitud;
import mx.gob.imss.dpes.interfaces.solicitud.model.Solicitud;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * @author juan.garfias
 */
@Provider
public class CronJobService extends ServiceDefinition<CronTareasRq, CronTareasRq> {

    @Inject
    @RestClient
    private CronTareaClient cronTareaClient;

    @Inject
    @RestClient
    private SolicitudBackClient SolicitudBackClient;

    @Inject
    @RestClient
    private PersonaBackClient personaBackClient;

    @Inject
    private CorreoService correoService;

    @Inject
    private PromotorBackClient promotorBackClient;

    @Inject
    private EntidadFinancieraBackClient entidadFinancieraBackClient;

    @Inject
    private CronTareaService cronTareaService;
    @Inject
    @RestClient
    private BitacoraClient bitacoraClient;

    protected HashMap<Long, Solicitud> mapSolicitud = new HashMap<>();

    protected HashMap<Long, TareaAccion> mapTareaAccion = new HashMap<>();

    protected HashMap<String, PersonaModel> mapPersonas = new HashMap<>();

    protected HashMap<Long, EntidadFinanciera> mapEntidadFinanciera = new HashMap<>();

    private void obtieneCatalogoTareaAccion() throws BusinessException {
        try {
            Response response = cronTareaClient.getTareas();
            if (response != null && response.getStatus() == 200) {
                List<TareaAccion> listaTareaAccion = response.readEntity(
                        new GenericType<List<TareaAccion>>() {}
                );
                if (listaTareaAccion != null && listaTareaAccion.size() > 0)
                    for (TareaAccion ta : listaTareaAccion) {
                        mapTareaAccion.put(ta.getId(), ta);
                    }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "CronJobService.obtieneCatalogoTareaAccion()", e);
            throw new CronTareasException(CronTareasException.ERROR_SERVICIO_BITACORA);
        }
    }

    @Override
    public Message<CronTareasRq> execute(Message<CronTareasRq> request) throws BusinessException {
        try {
            obtieneCatalogoTareaAccion();
            List<CronTarea> listaTareasVencidas = obtieneTareasVencidas();
            actualizaEstadosJob(listaTareasVencidas);

            CronTareasRq cronTareasRq = new CronTareasRq();
            cronTareasRq.setLstCronTareas(listaTareasVencidas);
            request.setPayload(cronTareasRq);
            return request;
        } catch (BusinessException e) {
            log.log(Level.SEVERE, "CronJobService.execute() - request = [" + request.getPayload() + "]", e);
            throw e;
        } catch (Exception e) {
            log.log(Level.SEVERE, "CronJobService.execute() - request = [" + request.getPayload() + "]", e);
            throw new CronTareasException(CronTareasException.ERROR_DESCONOCIDO);
        }
    }

    private List<CronTarea> obtieneTareasVencidas() throws BusinessException {
        try {
            Response response = cronTareaClient.getTareasVencidas();
            if (response != null && response.getStatus() == 200) {
                List<CronTarea> listaCronTareasVencidas = response.readEntity(
                        new GenericType<List<CronTarea>>() {}
                );
                if (listaCronTareasVencidas != null && listaCronTareasVencidas.size() > 0) {
                    for (CronTarea cronTareaVencida : listaCronTareasVencidas) {
                        Solicitud solicitud = obtieneSolicitud(cronTareaVencida.getCveSolicitud());
                        mapSolicitud.put(cronTareaVencida.getCveSolicitud(), solicitud);
                        cronTareaVencida.setSolicitud(solicitud);
                    }

                    return listaCronTareasVencidas;
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "CronJobService.obtieneTareasVencidas()", e);
            throw new CronTareasException(CronTareasException.ERROR_DESCONOCIDO);
        }
        return new ArrayList<CronTarea>();
    }

    protected List<CronTarea> actualizaEstadosJob(List<CronTarea> listaTareasVencidas) throws BusinessException {
        try {
            if(!(listaTareasVencidas != null && listaTareasVencidas.size() > 0))
                return listaTareasVencidas;

            boolean flagEjecutado = false;

            for (CronTarea tareaVencida : listaTareasVencidas) {
                Solicitud solicitudRq = mapSolicitud.get(tareaVencida.getCveSolicitud());

                Long cveEstadoSolicitudActual = solicitudRq.getCveEstadoSolicitud().getId();

                List<EstadoOrigen> lstEstadoOrigen = tareaVencida.getLstEstadosOrigen();
                flagEjecutado = false;

                for (EstadoOrigen eo : lstEstadoOrigen) {

                    if (eo.getCveEstadoSolicitudOrigen().equals(cveEstadoSolicitudActual)) {

                        solicitudRq = this.actualizaEstadoSolicitud(solicitudRq, tareaVencida);

                        if (tareaVencida.getTareaAccion().getId() == 6 || tareaVencida.getTareaAccion().getId() == 9) {
                            this.obtenerDatosPensionado(solicitudRq);
                            this.actualizaRecuperacion(solicitudRq);
                            this.ejecutarTareaAccion(
                                    solicitudRq,
                                    tareaVencida.getTareaAccion().getId() == 6 ?
                                            TipoCronTareaEnum.SIMULACION_COMPRA_CARTERA_MIXTO_MEJOR_OPCION :
                                            TipoCronTareaEnum.PROMOTOR_COMPRA_CARTERA_MIXTO_MEJOR_OPCION
                            );
                        }

                        tareaVencida.setEjecutado(1L);
                        TareaAccion ta = this.actualizarTarea(tareaVencida);
                        this.guardarBitacora(solicitudRq);
                        flagEjecutado = true;

                        if (!ta.getPlantillaCorreo().equals("NA"))
                            this.enviarCorreo(solicitudRq, ta);
                        break;
                    }
                }

                if (!flagEjecutado) {
                    tareaVencida.setEjecutado(0L);
                    this.actualizarTarea(tareaVencida);
                }
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.log(Level.SEVERE, "CronJobService.actualizaEstadosJob() - " +
                    "listaTareasVencidas = [" + listaTareasVencidas + "]", e);
            throw new CronTareasException(CronTareasException.ERROR_DESCONOCIDO);
        }

        return listaTareasVencidas;
    }

    private PersonaModel consultaPersona(String curp, String nss) throws BusinessException {
        try {
            if (mapPersonas.containsKey(curp)) {
                return mapPersonas.get(curp);
            } else {
                Pensionado p = new Pensionado();
                p.setCurp(curp);
                p.setNss(nss);
                Response response = promotorBackClient.getDatosPensionando(p);
                if (response != null && response.getStatus() == 200) {
                    PersonaModel pm = response.readEntity(PersonaModel.class);
                    mapPersonas.put(curp, pm);
                    return pm;
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "CronJobService.consultaPersona() - " +
                    "curp = [" + curp + "], nss = [" + nss + "]", e);
        }
        throw new CronTareasException(CronTareasException.ERROR_SERVICIO_PROMOTOR);
    }

    private EntidadFinanciera consultaEntidadFinencieraById(Long id) throws BusinessException {
        try {
            if (mapEntidadFinanciera.containsKey(id)) {
                return mapEntidadFinanciera.get(id);
            } else {
                Response response = entidadFinancieraBackClient.load(id);
                if (response != null && response.getStatus() == 200) {
                    EntidadFinanciera ef = response.readEntity(EntidadFinanciera.class);
                    mapEntidadFinanciera.put(id, ef);
                    return ef;
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "CronJobService.consultaEntidadFinencieraById() - id = [" + id + "]", e);
        }
        throw new CronTareasException(CronTareasException.ERROR_SERVICIO_ENTIDAD_FINANCIERA);
    }

    private void guardarBitacora(Solicitud solicitud) throws BusinessException {
        Bitacora bitacoraRequest = null;
        try {
            TipoEstadoSolicitudEnum tipoEstadoSolicitudEnum = TipoEstadoSolicitudEnum.forValue(
                solicitud.getCveEstadoSolicitud().getId());

            bitacoraRequest = new Bitacora();
            bitacoraRequest.setCurp(solicitud.getCurp());
            bitacoraRequest.setSesion(0L);
            bitacoraRequest.setIdSolicitud(solicitud.getId());
            bitacoraRequest.setEstadoSolicitud(tipoEstadoSolicitudEnum);
            bitacoraRequest.setTipo(tipoEstadoSolicitudEnum.getTipo() == 6 ?
                    BitacoraEnum.CANCELACION_AUTOMATICA :
                    BitacoraEnum.CONFIRMACION_AUTOMATICA_MONTO_LIQUIDAR
            );
            bitacoraClient.create(bitacoraRequest);
        } catch (Exception e) {
            log.log(Level.SEVERE, "ERROR CronJobService.guardarBitacora() - solicitud = [" + solicitud + "]", e);
            throw new BitacoraException(BitacoraException.ERROR_SERVICIO_BITACORA);
        }

    }

    private Solicitud actualizaEstadoSolicitud(Solicitud solicitudRq, CronTarea cronTarea) throws BusinessException {
        try {
            Long cveEstadoDestino = cronTarea.getTareaAccion().getCveEstadoSolicitudDestino();
            solicitudRq.setEstadoSolicitud(TipoEstadoSolicitudEnum.forValue(cveEstadoDestino));
            solicitudRq.setCveEstadoSolicitud(new EstadoSolicitud(cveEstadoDestino));
            Response response = SolicitudBackClient.actualizaEstado(solicitudRq);
            if (response != null && response.getStatus() == 200) {
                Solicitud solicitudRs = response.readEntity(Solicitud.class);
                mapSolicitud.put(cronTarea.getCveSolicitud(), solicitudRs);
                return solicitudRs;
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "CronJobService.actualizaEstadoSolicitud() - " +
                    "solicitudRq = [" + solicitudRq + "], cronTarea = [" + cronTarea + "]", e);
        }
        throw new CronTareasException(CronTareasException.ERROR_SERVICIO_SOLICITUD);
    }

    private void obtenerDatosPensionado(Solicitud solicitudRq) throws BusinessException {
        boolean ejecucionExitosa = false;
        try {
            Response persResponse = personaBackClient.getPersona(solicitudRq.getCurp());
            if (persResponse != null && persResponse.getStatus() == 200) {
                solicitudRq.setPersona(
                        persResponse.readEntity(Persona.class)
                );
                ejecucionExitosa = true;
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "CronJobService.obtenerDatosPersona() - " +
                    "solicitudRq = [" + solicitudRq + "]", e);
        }
        if (!ejecucionExitosa)
            throw new CronTareasException(CronTareasException.ERROR_SERVICIO_PERSONA);
    }

    private void actualizaRecuperacion(Solicitud solicitudRq) throws BusinessException {
        boolean ejecucionExitosa = false;
        try {
            Response respuesta = SolicitudBackClient.actualizaRecuperacion(solicitudRq);
            if (respuesta != null && respuesta.getStatus() == 200)
                ejecucionExitosa = true;
        } catch (Exception e) {
            log.log(Level.SEVERE, "CronJobService.actualizaRecuperacion() - " +
                    "solicitudRq = [" + solicitudRq + "]", e);
        }
        if (!ejecucionExitosa)
            throw new CronTareasException(CronTareasException.ERROR_SERVICIO_SOLICITUD);
    }

    private void ejecutarTareaAccion(Solicitud solicitudRq, TipoCronTareaEnum tipoCronTareaEnum) throws BusinessException {
        try {
            CronTarea cronTarea = new CronTarea();
            cronTarea.setCveSolicitud(solicitudRq.getId());
            TareaAccion tareaAccion = new TareaAccion();
            tareaAccion.setId(tipoCronTareaEnum.toValue());
            cronTarea.setTareaAccion(tareaAccion);
            cronTareaService.execute(new Message<>(cronTarea));
        } catch (Exception e) {
            log.log(Level.SEVERE, "CronJobService.ejecutarTareaAccion() - " +
                    "solicitudRq = [" + solicitudRq + "], tipoCronTareaEnum = [" + tipoCronTareaEnum + "]", e);
            throw new CronTareasException(CronTareasException.ERROR_SERVICIO_CRONTAREA);
        }
    }

    private TareaAccion actualizarTarea(CronTarea tarea) throws BusinessException {
        try {
            tarea.setActivo(0L);
            Response responseTarea = cronTareaClient.actualizarTarea(tarea);
            if (responseTarea != null && responseTarea.getStatus() == 200) {
                tarea = responseTarea.readEntity(CronTarea.class);
                return mapTareaAccion.get(tarea.getTareaAccion().getId());
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "CronJobService.actualizarTarea() - " +
                    "tarea = [" + tarea + "]", e);
        }
        throw new CronTareasException(CronTareasException.ERROR_SERVICIO_BITACORA);
    }

    private void enviarCorreo(Solicitud solicitudRs, TareaAccion ta) throws BusinessException {
        try {
            PersonaModel pm = consultaPersona(
                    solicitudRs.getCurp(),
                    solicitudRs.getNss()
            );
            EntidadFinanciera ef = consultaEntidadFinencieraById(
                    solicitudRs.getCveEntidadFinanciera()
            );

            Message<CorreoDatosEnvio> mail = new Message<>();
            List<String> correos = new ArrayList<>();
            correos.add(pm.getCorreoElectronico());
            CorreoDatosEnvio cde = new CorreoDatosEnvio();
            cde.setAsunto("Cancelacion de Solicitud de Prestamo");
            cde.setEntidadFinanciera(ef.getNombreComercial());
            cde.setFolio(solicitudRs.getNumFolioSolicitud());
            cde.setPlantillaCorreo(ta.getPlantillaCorreo());
            cde.setLstCorreos(correos);
            mail.setPayload(cde);
            correoService.execute(mail);
        } catch (Exception e) {
            log.log(Level.SEVERE, "CronJobService.enviarCorreo() - " +
                    "solicitudRs = [" + solicitudRs + "], ta = [" + ta + "]", e);
            throw new CronTareasException(CronTareasException.ERROR_SERVICIO_CORREO);
        }
    }

    private Solicitud obtieneSolicitud(Long id) throws BusinessException {
        try {
            Response respuesta = SolicitudBackClient.getSolicitud(id);

            if (respuesta != null && respuesta.getStatus() == 200)
                return respuesta.readEntity(Solicitud.class);
        } catch (Exception e) {
            log.log(Level.SEVERE, "CronJobService.obtieneSolicitud() - " +
                    "id = [" + id + "]", e);
        }

        throw new CronTareasException(CronTareasException.ERROR_SERVICIO_SOLICITUD);
    }

}
