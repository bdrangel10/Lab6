package grupo4.lab6.websocket;

import grupo4.lab6.modelo.Usuario;
import grupo4.lab6.persistence.PersistenceManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import javax.json.*;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
/**
 *
 * @author administrador
 */


@ApplicationScoped
public class AdministradorSesiones 
{
    private static final List<Usuario> usuarios = Collections.synchronizedList(new LinkedList<Usuario>()); 
    private static final Set<Session> sesiones = Collections.synchronizedSet(new HashSet<Session>()); 
    
    public final static String DIR_RAIZ_USUARIOS="/home/administrador/Escritorio/Videos_Usuarios";
    private final static int PUERTO_INICIAL=15000;
    private int puertoActual;
    private String canales;
    private JsonArrayBuilder builderCanales = Json.createArrayBuilder();
    
    
    
    public synchronized int darNuevoPuerto()
    {
        puertoActual++;
        return puertoActual;
    }
    
    public AdministradorSesiones()
    {
        //TODO Cargar el archivo serializado
        puertoActual=PUERTO_INICIAL;
    }
        
    
    /*
    @PersistenceContext(unitName = "UsuariosPU")
    EntityManager entityManager; 
    
    @PostConstruct
    public void init() {
        try {
            entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
    
    public void inicializarDB()
    {
        if (entityManager == null) 
        {
            try 
            {
                entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }
        
    }   
    */
    
    
    public void addSession(Session session) 
    {
        sesiones.add(session);
    }

    public void removeSession(Session session) 
    {
        sesiones.remove(session);
    }
    
    public Usuario buscarUsuario(String login)
    {
        for(Usuario u: usuarios)
        {
            if(u.getLogin().equals(login))
            {
                return u;
            }
        }
        return null;
        /*
        inicializarDB();
        Query q = entityManager.createQuery("select u from Usuario u where u.login='"+login+"'");
        List cuantos = q.getResultList();
        return cuantos.size()!=0;
        */
    }
    
    public boolean login(Usuario usuario, String password)
    {
        return usuario.esPassCorrecto(password);
        /*
        Query q = entityManager.createQuery("select u from Usuario u where u.login='"+login+"'");
        Usuario encontrado = (Usuario)q.getSingleResult();
        return encontrado.getPassword().equals(password);  
        */
    }

    /**
     * Pre: EL usuario no existe
     * @param login
     * @param password
     * @return 
     */
    public Usuario registrarUsuario(String login, String password)
    {
        int puerto = darNuevoPuerto();
        File directorioUsuario = new File(DIR_RAIZ_USUARIOS, ""+puerto);
        Usuario nuevo = new Usuario(login, password, puerto, directorioUsuario);        
        directorioUsuario.mkdirs();     
        //Añade a la lista de canales
        JsonObject jsonObjectActual = JsonProvider.provider().createObjectBuilder().add("usuario",login).add("puerto", puerto).build();
        builderCanales.add(jsonObjectActual);
        //Agrega a la lista de usuarios
        usuarios.add(nuevo);        
        return nuevo;
    }
    
    public void enviarMsjSesion(Session session, JsonObject message) 
    {
        try 
        {
            session.getBasicRemote().sendText(message.toString());
        } 
        catch (IOException ex) 
        {
            sesiones.remove(session);
            Logger.getLogger(AdministradorSesiones.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public JsonObject crearRespuesta(boolean logueado, boolean error, String msj, JsonArray suscripcionesUsuario)
    {
        JsonProvider provider = JsonProvider.provider();
        JsonObject respuesta;
        if (!logueado) 
        {
            respuesta = provider.createObjectBuilder()
                    .add("logueado", logueado)
                    .add("error", error)
                    .add("msj", msj)
                    .build();
        }
        else
        {
            
            respuesta = provider.createObjectBuilder()
                    .add("logueado", logueado)
                    .add("error", error)
                    .add("msj", msj)
                    .add("canales", darListaCanales())
                    .add("suscripciones", suscripcionesUsuario)
                    .build();
        }

        
        return respuesta;        
    }
    
    public JsonArray darListaCanales()
    {
        return builderCanales.build();
    }

   
    
}
