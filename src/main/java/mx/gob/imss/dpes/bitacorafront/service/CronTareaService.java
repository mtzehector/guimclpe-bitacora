package mx.gob.imss.dpes.bitacorafront.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mx.gob.imss.dpes.bitacorafront.exception.CronTareasException;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.ext.Provider;

import mx.gob.imss.dpes.bitacorafront.restclient.CronTareaClient;
import mx.gob.imss.dpes.bitacorafront.restclient.SolicitudInhabilesBackClient;
import mx.gob.imss.dpes.interfaces.bitacora.model.CronTarea;
import mx.gob.imss.dpes.interfaces.bitacora.model.DiaInhabil;
import mx.gob.imss.dpes.interfaces.bitacora.model.TareaAccion;

@Provider
public class CronTareaService extends ServiceDefinition<CronTarea, CronTarea> {

    @Inject
    @RestClient
    private CronTareaClient client;

    @Inject
    @RestClient
    private SolicitudInhabilesBackClient clientSB;

    @Override
    public Message<CronTarea> execute(Message<CronTarea> request) throws BusinessException {
        try {
            List<Date> lstFecha = this.obtenerDiasInhabiles();
            TareaAccion ta = this.obtenerTareaCatalogo(request);
            request.getPayload().setFechaLimite(
                    this.calcularFechaLimite(lstFecha, ta.getHorasVigencia())
            );
            this.creaTareaCatalogo(request);
            request.getPayload().getTareaAccion().setHorasVigencia(ta.getHorasVigencia());
            return request;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.log(Level.SEVERE, "CronTareaService.execute() - " +
                    "request = [" + request.getPayload() + "]", e);
        }
        throw new CronTareasException(CronTareasException.ERROR_SERVICIO_DIAS_INHABILES);
    }

    private List<Date> obtenerDiasInhabiles() throws BusinessException {
        try {
            Response responseDA = clientSB.getDiasInhabiles();
            if (responseDA != null && responseDA.getStatus() == 200) {
                List<DiaInhabil> lstDiasInhabiles = responseDA.readEntity(
                        new GenericType<List<DiaInhabil>>() {}
                );
                List<Date> lstFecha = new ArrayList<>();
                if (lstDiasInhabiles != null && lstDiasInhabiles.size() > 0)
                    for (DiaInhabil di : lstDiasInhabiles)
                        lstFecha.add(di.getDiaInhabil());

                return lstFecha;
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "CronTareaService.obtenerDiasInhabiles()", e);
        }
        throw new CronTareasException(CronTareasException.ERROR_SERVICIO_DIAS_INHABILES);
    }

    private TareaAccion obtenerTareaCatalogo(Message<CronTarea> request) throws BusinessException {
        try {
            Response responseTareaCat = client.getTareaCatalogo(
                    request.getPayload().getTareaAccion().getId().intValue()
            );
            if (responseTareaCat != null && responseTareaCat.getStatus() == 200) {
                return responseTareaCat.readEntity(TareaAccion.class);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "CronTareaService.obtenerTareaCatalogo() - " +
                    "request = [" + request.getPayload() + "]", e);
        }
        throw new CronTareasException(CronTareasException.ERROR_SERVICIO_TAREAS_CATALOGO);
    }

    private void creaTareaCatalogo(Message<CronTarea> request) throws BusinessException {
        boolean ejecucionExitosa = false;
        try {
            Response event = client.create(request.getPayload());
            if (event != null && event.getStatus() == 200)
                ejecucionExitosa = true;
        } catch (Exception e) {
            log.log(Level.SEVERE, "CronTareaService.creaTareaCatalogo() - " +
                    "request = [" + request.getPayload() + "]", e);
        }
        if (!ejecucionExitosa)
            throw new CronTareasException(CronTareasException.ERROR_SERVICIO_CRONTAREA);
    }

    private static Date calcularFechaLimite(List<Date> diasFestivos, Integer horasParaAniadir) {
        Integer horasAniadidas = 0;
        Calendar calAux = Calendar.getInstance();

        // Genera mapa de días inhabiles
        Map<String, Date> fechasStringMap = mapFechaUtil(diasFestivos);

        // Inicia ciclo para asignar tiempo limite
        while (horasAniadidas <= horasParaAniadir) {

            // Valida si ya se excedió el tiempo añadido
            if (horasAniadidas >= horasParaAniadir)
                break;

            // Se añade un día
            calAux.add(Calendar.HOUR, 24);

            int dow = calAux.get(Calendar.DAY_OF_WEEK);
            boolean isWeekday = ((dow >= Calendar.MONDAY) && (dow <= Calendar.FRIDAY));

            if (isWeekday) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
                String dateAux = sdf.format(calAux.getTime());

                // Valida si no es día festivo
                if (!fechasStringMap.containsKey(dateAux))
                    // se registran las horas añadidas
                    horasAniadidas += 24;

            }
        }
        return calAux.getTime();
    }

    private static Map<String, Date> mapFechaUtil(List<Date> fechas) {
        Map<String, Date> fechasMap = new HashMap<>();
        for (Date d : fechas) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
            String date = sdf.format(d);
            fechasMap.put(date, d);
        }
        return fechasMap;
    }
}
