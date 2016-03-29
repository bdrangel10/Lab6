package grupo4.lab6.websocket;

import grupo4.lab6.modelo.Usuario;
import grupo4.lab6.persistence.PersistenceManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.websocket.Session;
/**
 *
 * @author administrador
 */


@ApplicationScoped
public class AdministradorSesiones 
{
    private final Set sesiones = new HashSet<>();
    private final ArrayList<Usuario> usuarios = new ArrayList<Usuario>();
    
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
    
    public boolean usuarioExiste(String login)
    {
        for(Usuario u: usuarios)
        {
            if(u.getLogin().equals(login))
            {
                return true;
            }
        }
        return false;
        /*
        inicializarDB();
        Query q = entityManager.createQuery("select u from Usuario u where u.login='"+login+"'");
        List cuantos = q.getResultList();
        return cuantos.size()!=0;
        */
    }
    
    public boolean login(String login, String password)
    {
        for(Usuario u: usuarios)
        {
            if(u.getLogin().equals(login))
            {
                return u.esPassCorrecto(password);
            }
        }
        return false;
        /*
        Query q = entityManager.createQuery("select u from Usuario u where u.login='"+login+"'");
        Usuario encontrado = (Usuario)q.getSingleResult();
        return encontrado.getPassword().equals(password);  
        */
    }

    public String registrarUsuario(String login, String password)
    {
        Usuario nuevo = new Usuario(login, password);
        usuarios.add(nuevo);
        return login;
        /*
        inicializarDB();
        String loginRegistradoError;
        Usuario nuevo = new Usuario(login, password);
        try 
        {
            entityManager.getTransaction().begin();
            entityManager.persist(nuevo);
            entityManager.getTransaction().commit();
            entityManager.refresh(nuevo);
            loginRegistradoError=login;
        } catch (Throwable t) 
        {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) 
            {
                entityManager.getTransaction().rollback();
            }
            nuevo = null;
            loginRegistradoError="ERROR"+t.getMessage();
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return loginRegistradoError;
        */
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
    
    public JsonObject crearRespuesta(boolean logueado, boolean error, String msj)
    {
        JsonProvider provider = JsonProvider.provider();
        JsonObject respuesta = provider.createObjectBuilder()
                .add("logueado", logueado)
                .add("error", error)
                .add("msj", msj)
                .build();
        
        return respuesta;        
    }

   
    
}
