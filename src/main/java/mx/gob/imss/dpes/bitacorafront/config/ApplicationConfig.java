/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.bitacorafront.config;

/**
 * @author antonio
 */

import javax.ws.rs.core.Application;
import java.util.Set;

@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

  @Override
  public Set<Class<?>> getClasses() {
    Set<Class<?>> resources = new java.util.HashSet<>();
    addRestResourceClasses(resources);
    return resources;
  }

  /**
   * Do not modify addRestResourceClasses() method.
   * It is automatically populated with
   * all resources defined in the project.
   * If required, comment out calling this method in getClasses().
   */
  private void addRestResourceClasses(Set<Class<?>> resources) {    
    resources.add(mx.gob.imss.dpes.bitacorafront.endpoint.BitacoraEndPoint.class);
    resources.add(mx.gob.imss.dpes.bitacorafront.endpoint.CronTareasEndPoint.class);
    resources.add(mx.gob.imss.dpes.bitacorafront.service.CorreoService.class);
    resources.add(mx.gob.imss.dpes.bitacorafront.service.CreateBitacoraService.class);
    resources.add(mx.gob.imss.dpes.bitacorafront.service.CronJobService.class);
    resources.add(mx.gob.imss.dpes.bitacorafront.service.CronTareaService.class);
        resources.add(mx.gob.imss.dpes.bitacorafront.service.ValidacionTareaService.class);
        resources.add(mx.gob.imss.dpes.common.exception.AlternateFlowMapper.class);
        resources.add(mx.gob.imss.dpes.common.exception.BusinessMapper.class);
        resources.add(mx.gob.imss.dpes.common.rule.MontoTotalRule.class);
        resources.add(mx.gob.imss.dpes.common.rule.PagoMensualRule.class);
  }

}