package grupo4.lab6.modelo;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.*;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;

/**
 *
 * @author administrador
 */
//@Entity
public class Usuario implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    //@Id
    //@NotNull
    private String login;
    
    //@NotNull
    private String password;
    
    private int puerto;
    
    private File carpeta;
    
    private JsonArray suscripciones;

    
    public Usuario(String nLogin, String nPassword, int nPuerto, File nCarpeta)
    {
        login=nLogin;
        password=nPassword;
        puerto=nPuerto;
        carpeta=nCarpeta;
        suscripciones=JsonProvider.provider().createArrayBuilder().build();
    }
    
    public File darDirectorioUsuario()
    {
        return carpeta;
    }
        
    public String getLogin() {
        return login;
    }
    
    public int getPuerto()
    {
        return puerto;
    }

    public boolean esPassCorrecto(String nPassword) {
        return this.password.equals(nPassword);
    }
    
    public void actualizarSuscripciones (JsonArray nSuscripciones)
    {
        suscripciones=nSuscripciones;
    }
    
    public JsonArray darSuscripciones()
    {
        return suscripciones;
    }
    
    
    
    
}
