package mx.gob.imss.dpes.bitacorafront.service;

import mx.gob.imss.dpes.common.exception.BusinessException;
import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.model.ServiceStatusEnum;
import mx.gob.imss.dpes.common.service.ServiceDefinition;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import javax.ws.rs.ext.Provider;
import mx.gob.imss.dpes.bitacorafront.restclient.BitacoraClient;
import mx.gob.imss.dpes.common.exception.BadRequestException;
import mx.gob.imss.dpes.interfaces.bitacora.model.Bitacora;
@Provider
public class CreateBitacoraService extends ServiceDefinition<Bitacora, Bitacora> {

  @Inject
  @RestClient
  private BitacoraClient client;

  @Override
  public Message<Bitacora> execute(Message<Bitacora> request) throws BusinessException {
    try {
      if (request.getPayload().getTipo() == null)
        throw new BadRequestException();

      Response event = client.create(request.getPayload());
      if (event != null && event.getStatus() == 200)
        return request;

    } catch (BusinessException be) {
      throw be;
    } catch (Exception e) {
      log.log(Level.SEVERE, "CreateBitacoraService.execute() - request = [" + request.getPayload() + "]", e);
    }

    throw new BadRequestException();
  }
}
