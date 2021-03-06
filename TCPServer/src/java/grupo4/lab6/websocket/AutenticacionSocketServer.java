/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package grupo4.lab6.websocket;

import grupo4.lab6.modelo.Usuario;
import java.io.StringReader;
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
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 *
 * @author administrador
 */
@ApplicationScoped
@ServerEndpoint("/auth")
public class AutenticacionSocketServer {

    
    private AdministradorUsuarios administradorSesiones;
    private long inicioConexion;
    
    @PostConstruct
    public void inicializar()
    {
       System.out.println("POSTCONSTRUCT darInstancia");
       administradorSesiones= AdministradorUsuarios.darInstancia();
        
    }
    
    

    @OnOpen
    public void open(Session session) 
    {
        inicioConexion=System.currentTimeMillis();
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
        Logger.getLogger(AutenticacionSocketServer.class.getName()).log(Level.SEVERE, null, error);
    }

    @OnMessage
    public void handleMessage(String message, Session session) 
    {
        long inicioAtencion=System.currentTimeMillis();
        int tiempoCola = (int) (inicioAtencion-inicioConexion);
        System.out.println("WS: RECIBIDO: "+message);
        JsonObject respuesta=null;
        String tipoSol="registrar";
        try (JsonReader reader = Json.createReader(new StringReader(message))) 
        {
            JsonObject jsonMessage = reader.readObject();
            String usuario=jsonMessage.getString("username");
            String contrasenia=jsonMessage.getString("password");

            if ("ingresar".equals(jsonMessage.getString("accion"))) 
            {
                tipoSol="ingresar";
                Usuario encontrado=administradorSesiones.buscarUsuario(usuario);
                if (encontrado!=null) 
                {
                    if (encontrado.esPassCorrecto(contrasenia)) 
                    {
                        //Exitoso rta
                        respuesta = administradorSesiones.crearRespuesta(true, false, "Ingreso exitoso para el usuario "+usuario, encontrado.darSuscripciones(), encontrado.getPuerto(), encontrado.darVideosUsuario());
                    }
                    else
                    {
                        //Contraseña incorrecta
                        respuesta = administradorSesiones.crearRespuesta(false, true, "La contraseña es incorrecta",null,-1,null);
                        
                    }
                }
                else
                {
                    //usuario no registrado
                    respuesta = administradorSesiones.crearRespuesta(false, true, "Usuario no existe",null,-1,null);
                }
                
            }
            else 
            {
                if ("registrar".equals(jsonMessage.getString("accion"))) 
            {
                Usuario encontrado = administradorSesiones.buscarUsuario(usuario);
                if(encontrado==null)
                {
                    Usuario resultado =administradorSesiones.registrarUsuario(usuario, contrasenia);
                    if(resultado!=null)
                    {
                        //Se registró
                        respuesta = administradorSesiones.crearRespuesta(true, false, "Usuario "+usuario +" registrado exitosamente", resultado.darSuscripciones(), resultado.getPuerto(), resultado.darVideosUsuario());
                    }
                    else
                    {
                        //ERROR; ENVIAR RTA
                        respuesta = administradorSesiones.crearRespuesta(false, true, "ERROR al intentar crear el usuario",null,-1,null);
                    }
                }
                else
                {
                    //Usuario ya existe
                    respuesta = administradorSesiones.crearRespuesta(false, true, "El usuario ya está registrado",null,-1,null);
                }
            }
            else if ("guardar".equals(jsonMessage.getString("accion"))) 
            {
                Usuario encontrado = administradorSesiones.buscarUsuario(usuario);
                if(encontrado!=null)
                {
                    encontrado.actualizarSuscripciones(jsonMessage.getJsonArray("suscripciones"));
                    respuesta = administradorSesiones.crearRespuesta(true, false, "Suscripciones de "+usuario +" actualizadas exitosamente", encontrado.darSuscripciones(), encontrado.getPuerto(), encontrado.darVideosUsuario());
                }
                else
                {
                    //Usuario ya existe
                    respuesta = administradorSesiones.crearRespuesta(false, true, "No se encontró al usuario "+usuario,null,-1,null);
                }
            }
                administradorSesiones.guardarEstado();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            respuesta = administradorSesiones.crearRespuesta(false, true, "ERROR: "+e.getMessage(),null,-1,null);
        }
        administradorSesiones.enviarMsjSesion(session, respuesta);
        System.out.println("WS: ENVIADO: "+respuesta.toString());
        long finAtencion = System.currentTimeMillis();
        int tiempoAtencion = (int) (finAtencion-inicioAtencion);
        administradorSesiones.registrarEstadísticas(session.getId(), tiempoCola, tiempoAtencion, tipoSol);
    }

}
