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
import java.nio.ByteBuffer;
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
public class ReceptorVideosSocket 
{
    
    private String nombre_video;
    private String puerto_carpeta;
    private long inicioCola;
    private long cola;

    
    private AdministradorUsuarios administradorSesiones;
    
    @PostConstruct
    public void inicializar()
    {
       administradorSesiones= AdministradorUsuarios.darInstancia();
    }
    
    

    @OnOpen
    public void open(Session session) 
    {
        inicioCola=System.currentTimeMillis();
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
    public void prepararCanal(String message, Session session)
    {
        cola=System.currentTimeMillis()-inicioCola;
        String msj;
        try (JsonReader reader = Json.createReader(new StringReader(message))) 
        {
            JsonObject jsonMessage = reader.readObject();
            nombre_video=jsonMessage.getString("nombre");
            puerto_carpeta=""+jsonMessage.getInt("puerto");
            msj="Socket preparado para recibir el video "+nombre_video+ "que será transmitido en el puerto "+puerto_carpeta;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            msj="ERROR EN EL SOCKET: "+e.getMessage();
        }
        administradorSesiones.enviarMsjSesion(session, JsonProvider.provider().createObjectBuilder().add("msj", msj).build());           
        
        
    }

    @OnMessage
    public void handleMessage(ByteBuffer video, Session session) 
    {
        long inicioTransmision =System.currentTimeMillis();
        System.out.println("Video recibido");
        File carpeta = new File(administradorSesiones.darCarpetaRaiz().getAbsoluteFile()+"/"+puerto_carpeta);
        try 
        {
            OutputStream out = new FileOutputStream(new File(carpeta.getAbsolutePath(), nombre_video));
            byte[] buf = video.array();
	    out.write(buf);
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
        int transmision=(int)(System.currentTimeMillis()-inicioTransmision);
        administradorSesiones.registrarEstadísticas(session.getId(), (int)cola, transmision, "subir video");
        
    }

}
