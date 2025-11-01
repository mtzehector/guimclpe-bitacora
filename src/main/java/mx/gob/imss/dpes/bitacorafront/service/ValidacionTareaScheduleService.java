/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.bitacorafront.service;

//import java.util.logging.Level;
//import javax.annotation.Resource;
//import javax.ejb.Schedule;
//import javax.ejb.Singleton;
//import javax.ejb.Startup;
//import javax.ejb.TimerService;
//import javax.inject.Inject;

//import mx.gob.imss.dpes.bitacorafront.model.CronTareasRq;
//import mx.gob.imss.dpes.common.model.BaseModel;
//import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.service.BaseService;

/**
 * @author juanf.barragan
 */

//@Singleton
//@Startup()
public class ValidacionTareaScheduleService extends BaseService {

    //@Inject
    //ValidacionTareaService service;


    // Here we inject a reference to the timerService
    //@Resource
    //private TimerService timerService;

    //@Schedule(hour = "12,15,18", dayOfWeek = "Mon, Tue, Wed, Thu, Fri, Sat, Sun")
/*
    public void scheduledTimeout() {
        log.log(Level.INFO, ">>>Inicia la comprobacion de tareas de cancelacion");
        BaseModel bm = new CronTareasRq();
        Message<BaseModel> request = new Message(bm);
        Message<BaseModel> response;
        try {
            response = service.execute(request);
        } catch (Exception e) {
            log.log(Level.INFO, ">>>Error en la comprobacion de tareas de cancelacion {0}", e.getMessage());
        }
    }
*/
}
