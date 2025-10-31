package mx.gob.imss.dpes.bitacorafront.exception;

import mx.gob.imss.dpes.common.exception.BusinessException;

public class CronTareasException extends BusinessException {

    public static final String ERROR_SERVICIO_BITACORA = "err401";
    public static final String ERROR_DESCONOCIDO = "err402";
    public static final String ERROR_SERVICIO_SOLICITUD = "err403";
    public static final String ERROR_SERVICIO_ENTIDAD_FINANCIERA = "err404";
    public static final String ERROR_SERVICIO_PROMOTOR = "err405";
    public static final String ERROR_SERVICIO_PERSONA = "err406";
    public static final String ERROR_SERVICIO_CRONTAREA = "err407";
    public static final String ERROR_SERVICIO_CORREO = "err408";
    public static final String ERROR_SERVICIO_DIAS_INHABILES = "err409";
    public static final String ERROR_SERVICIO_TAREAS_CATALOGO = "err410";

    public CronTareasException(String messageKey) {
        super(messageKey);
    }
}
