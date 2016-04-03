package grupo4.lab6.modelo;

import java.io.File;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
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
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    //@Id
    //@NotNull
    private String login;

    //@NotNull
    private String password;

    private int puerto;

    private File carpeta;

    private transient JsonArray suscripciones;

    public Usuario(String nLogin, String nPassword, int nPuerto, File nCarpeta) {
        login = nLogin;
        password = nPassword;
        puerto = nPuerto;
        carpeta = nCarpeta;
        suscripciones = JsonProvider.provider().createArrayBuilder().build();
    }

    private void writeObject(ObjectOutputStream oos) throws IOException 
    {
        // default serialization 
        oos.defaultWriteObject();
        // write the object
        List datos = new ArrayList();
        datos.add(login);
        datos.add(password);
        datos.add(puerto);
        datos.add(carpeta);
        datos.add(darSuscripciones().toString());
        oos.writeObject(datos);
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException 
    {
        // default deserialization
        ois.defaultReadObject();
        List datos = (List) ois.readObject(); // Replace with real deserialization
        login=(String) datos.get(0);
        password=(String) datos.get(1);
        puerto=(int)datos.get(2);
        carpeta=(File) datos.get(3);
        JsonReader reader = Json.createReader(new StringReader((String)datos.get(4)));
        suscripciones=reader.readArray();

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
    
    public JsonArray darVideosUsuario()
    {
        JsonArrayBuilder listado=JsonProvider.provider().createArrayBuilder();
        File[] archivosUsuario = carpeta.listFiles();
        for(int i=0; i<archivosUsuario.length;i++)
        {
            listado.add(archivosUsuario[i].getName());
        }
        return listado.build();
    }
}
