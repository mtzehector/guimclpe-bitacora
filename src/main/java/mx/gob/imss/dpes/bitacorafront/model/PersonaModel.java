/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.imss.dpes.bitacorafront.model;

import lombok.Data;
import mx.gob.imss.dpes.common.model.BaseModel;

/**
 *
 * @author edgar.arenas
 */
@Data
public class PersonaModel extends BaseModel{
    
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private String telLocal;
    private String telCelular;
    private String correoElectronico;
}
