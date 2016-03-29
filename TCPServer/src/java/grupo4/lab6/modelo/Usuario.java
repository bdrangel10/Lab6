package grupo4.lab6.modelo;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

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
    
    public Usuario(String nLogin, String nPassword)
    {
        login=nLogin;
        password=nPassword;
    }
        
    public String getLogin() {
        return login;
    }
    
    /*
    public boolean isPassword(String nPassword)
    {
        return password.equals(nPassword);
    }

    

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    */

    public boolean esPassCorrecto(String nPassword) {
        return this.password.equals(nPassword);
    }
    
    
}
