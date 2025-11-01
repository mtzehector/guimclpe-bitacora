/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.bitacorafront.service;

/**
 *
 * @author gabriel.rios
 */
//import java.util.Date;
//import java.util.logging.Level;
//import javax.annotation.Resource;
//import javax.ejb.Schedule;
//import javax.ejb.Singleton;
//import javax.ejb.Startup;
//import javax.ejb.Timeout;
//import javax.ejb.Timer;
//import javax.ejb.TimerService;
//import javax.inject.Inject;
//import mx.gob.imss.dpes.bitacorafront.model.CronTareasRq;
//import mx.gob.imss.dpes.common.model.Message;
import mx.gob.imss.dpes.common.service.BaseService;

//@Singleton
//@Startup()
public class CronScheduleService extends BaseService{
    //@Inject
    //CronJobService cronJobService;
    
  // Here we inject a reference to the timerService
  //@Resource
  //private TimerService timerService;

  // Schedule timer will fire every hour, laboral hours and days
  //@Schedule(minute="*", hour="8-23",dayOfWeek="Mon, Tue, Wed, Thu, Fri")
  //@Schedule(minute="*/10",hour="7-23",dayOfWeek="Mon, Tue, Wed, Thu, Fri")
  //@Schedule(hour="7-23",dayOfWeek="Mon, Tue, Wed, Thu, Fri")
/*
  public void scheduledTimeout()
  {
      log.log(Level.INFO,">>>TimerScheduleBean execute");
      
    System.out.println("Schedule Timer fired - " + new Date());
    //log.log(Level.INFO,">>>TimerScheduleBean execute");
    CronTareasRq cronTareasRq = new CronTareasRq();
        Message<CronTareasRq> request = new Message(cronTareasRq);
        Message<CronTareasRq> response;
        try{
            response = cronJobService.execute(request);
            log.log(Level.INFO,"        >>><<<CronJobService response="+response);
        }
        catch(Exception e){
            e.printStackTrace();
            log.log(Level.SEVERE,">>>CronJobService ERROR! ="+e);
        }
        
  }
*/
}
