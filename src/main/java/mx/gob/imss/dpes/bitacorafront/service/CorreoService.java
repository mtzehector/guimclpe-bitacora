/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.bitacorafront.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import mx.gob.imss.dpes.bitacorafront.exception.CronTareasException;
import mx.gob.imss.dpes.bitacorafront.model.CorreoDatosEnvio;
import mx.gob.imss.dpes.bitacorafront.restclient.CorreoClient;
import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import mx.gob.imss.dpes.interfaces.serviciosdigitales.model.Correo;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 *
 * @author juan.garfias
 */
@Provider
public class CorreoService extends ServiceDefinition<CorreoDatosEnvio, CorreoDatosEnvio> {

    @Inject
    @RestClient
    private CorreoClient correoClient;

    @Inject
    private Config config;

    @Override
    public Message<CorreoDatosEnvio> execute(Message<CorreoDatosEnvio> request) throws BusinessException {

        try {
            String entidadFinanciera = request.getPayload().getEntidadFinanciera();
            String folio = request.getPayload().getFolio();
            String plantilla = null;
            if (request.getPayload().getPlantillaCorreo().equals("plantillaParaPensionado-Folio-cancelado-48hrs")) {
                plantilla = String.format(
                        config.getValue(
                                request.getPayload().getPlantillaCorreo(),
                                String.class
                        ), folio, entidadFinanciera);
            } else {
                plantilla = String.format(
                        config.getValue(
                                request.getPayload().getPlantillaCorreo(),
                                String.class
                        ), entidadFinanciera, folio);
            }

            Correo correo = new Correo();

            correo.setCuerpoCorreo(new String(plantilla.getBytes(), StandardCharsets.UTF_8.name()));

            correo.setAsunto(
                    request.getPayload().getAsunto()
            );

            ArrayList<String> correos = new ArrayList<>();

            for (String c : request.getPayload().getLstCorreos()) {
                correos.add(c);
            }

            correo.setCorreoPara(correos);

            Response response = correoClient.enviaCorreo(correo);

            if (response.getStatus() == 200 || response.getStatus() == 204)
                return request;

        } catch (Exception e) {
            log.log(Level.SEVERE, "CorreoService.execute() - request = [" + request.getPayload() + "]", e);
        }

        throw new CronTareasException(CronTareasException.ERROR_SERVICIO_CORREO);
    }
}
