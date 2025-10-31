package mx.gob.imss.dpes.bitacorafront.exception;

import mx.gob.imss.dpes.common.exception.BusinessException;

public class BitacoraException extends BusinessException {

    public static final String ERROR_SERVICIO_BITACORA = "err401";
    public static final String ERROR_DESCONOCIDO = "err402";
    public BitacoraException(String messageKey) {
        super(messageKey);
    }
}
