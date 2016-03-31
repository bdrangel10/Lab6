package grupo4.lab6.modelo;

import java.io.File;
import java.io.Serializable;
import javax.json.JsonArray;

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
    
    private String suscripciones;
    
    public Usuario(String nLogin, String nPassword, int nPuerto, File nCarpeta)
    {
        login=nLogin;
        password=nPassword;
        puerto=nPuerto;
        carpeta=nCarpeta;
        suscripciones="[";
        //Las suscripciones siempre van a llegar en JSon desde el cliente
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
    
    public void actualizarSuscripciones (String nSuscripciones)
    {
        suscripciones=nSuscripciones;
    }
    
    public String darSuscripciones()
    {
        return suscripciones+"]";
    }
    
    
    
    
}
