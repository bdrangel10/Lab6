/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package grupo4.lab6.websocket;

import grupo4.lab6.modelo.Usuario;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.spi.JsonProvider;

/**
 *
 * @author administrador
 */
@ApplicationScoped
@ServerEndpoint("/upload")
public class ReceptorVideosSocket {

    
    private AdministradorUsuarios administradorSesiones;
    
    @PostConstruct
    public void inicializar()
    {
       System.out.println("POSTCONSTRUCT darInstancia");
       administradorSesiones= AdministradorUsuarios.darInstancia();
        
    }
    
    

    @OnOpen
    public void open(Session session) 
    {
        administradorSesiones.addSession(session);
        System.out.println("WS:Se abrió el socket con sesión:" +session);
    }

    @OnClose
    public void close(Session session) 
    {
        administradorSesiones.removeSession(session);
        System.out.println("WS:Socket cerrado para la sesión:" +session);
    }

    @OnError
    public void onError(Throwable error) 
    {
        System.out.println("ERROR: "+error.getMessage());
        error.printStackTrace();
        Logger.getLogger(ReceptorVideosSocket.class.getName()).log(Level.SEVERE, null, error);
    }

    @OnMessage
    public void handleMessage(File video, Session session) 
    {
        System.out.println("SE RECIBIÓ UN ARCHIVO FILE");
        File carpeta = administradorSesiones.darCarpetaRaiz();
        try {
            InputStream in = new FileInputStream(video);
            OutputStream out = new FileOutputStream(new File(carpeta.getAbsolutePath(),video.getName()));

            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();

        } catch (Exception e) 
        {
            e.printStackTrace();
        }
        JsonProvider provider = JsonProvider.provider();
        JsonObjectBuilder respuesta=provider.createObjectBuilder();
        respuesta.add("msj", "El video se agregó existosamente");
        File[] archivosUsuario = carpeta.listFiles();
        JsonArrayBuilder listado=provider.createArrayBuilder();
        for(int i=0; i<archivosUsuario.length;i++)
        {
            listado.add(archivosUsuario[i].getName());
        }
        respuesta.add("videos",listado);
        administradorSesiones.enviarMsjSesion(session, respuesta.build());           
        
    }

}
