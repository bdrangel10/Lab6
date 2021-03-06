package grupo4.lab6.websocket;

import grupo4.lab6.modelo.Usuario;
import java.io.*;
import java.util.*;
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
public class AdministradorUsuarios implements Serializable {

    private static final long serialVersionUID = 1L;
    private static AdministradorUsuarios instancia = null;
    private static List<Usuario> usuarios;
    private static Set<Session> sesiones = Collections.synchronizedSet(new HashSet<Session>());

    public final static String DIR_RAIZ_USUARIOS = "/home/administrador/Escritorio/Videos_Usuarios";
    private final static int PUERTO_INICIAL = 15000;
    private int puertoActual;
    private JsonArrayBuilder builderCanales;

    private File archivoEstadisticas = new File(DIR_RAIZ_USUARIOS + "/estadisticas.csv");
    PrintWriter csvOutput;

    public AdministradorUsuarios() {
        darInstancia();
    }

    public AdministradorUsuarios(boolean n) {
        boolean recuperado = false;
        File archivo = new File(DIR_RAIZ_USUARIOS + "/persistencia.ddr");
        if (archivo.exists()) {

            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo));
                puertoActual = (int) ois.readObject();
                usuarios = (List<Usuario>) ois.readObject();
                ois.close();
                builderCanales = Json.createArrayBuilder();
                for (Usuario u : usuarios) {
                    String login = u.getLogin();
                    int puerto = u.getPuerto();
                    JsonObject jsonObjectActual = JsonProvider.provider().createObjectBuilder().add("usuario", login).add("puerto", puerto).build();
                    builderCanales.add(jsonObjectActual);
                    System.out.println(login + "-" + u.esPassCorrecto(login));
                }
                System.out.println("Usuarios recuperados desde la persistencia " + usuarios.size() + "-" + darListaCanales().size());
                recuperado = true;
            } catch (Exception e) {
                System.out.println("EXCEPCIÓN AL RECUPERAR DE PERSISTENCIA");
                e.printStackTrace();
                recuperado = false;
            }
        }
        if (!recuperado) {
            puertoActual = PUERTO_INICIAL;
            usuarios = Collections.synchronizedList(new LinkedList<Usuario>());
            String linea = "id usuario,tiempo en cola,tiempo de procesamiento,tiempo de solicitud";
            builderCanales = Json.createArrayBuilder();
            System.out.println("INSTANCIA CREADA DESDE CERO");
            try {
                csvOutput = new PrintWriter(new FileWriter(archivoEstadisticas, true));
                csvOutput.println(linea);
                csvOutput.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static AdministradorUsuarios darInstancia() {
        if (instancia == null) {
            instancia = new AdministradorUsuarios(true);
        }
        return instancia;
    }

    public void guardarEstado() {

        try {
            File existente = new File(DIR_RAIZ_USUARIOS, "persistencia.ddr");
            if (existente.exists()) {
                existente.delete();
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(DIR_RAIZ_USUARIOS, "persistencia.ddr")));
            oos.writeObject(puertoActual);
            oos.writeObject(usuarios);
            oos.close();
            System.out.println("Usuarios actualmente:" + usuarios.size());
            System.out.println("Guardado exitosamente");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public synchronized int darNuevoPuerto() {
        puertoActual++;
        return puertoActual;
    }

    public void addSession(Session session) {
        sesiones.add(session);
    }

    public void removeSession(Session session) {
        sesiones.remove(session);
    }

    public Usuario buscarUsuario(String login) {
        for (Usuario u : usuarios) {
            if (u.getLogin().equals(login)) {
                return u;
            }
        }
        return null;
    }

    public boolean login(Usuario usuario, String password) {
        return usuario.esPassCorrecto(password);
    }

    /**
     * Pre: EL usuario no existe
     *
     * @param login
     * @param password
     * @return
     */
    public Usuario registrarUsuario(String login, String password) {
        int puerto = darNuevoPuerto();
        File directorioUsuario = new File(DIR_RAIZ_USUARIOS, "" + puerto);
        Usuario nuevo = new Usuario(login, password, puerto, directorioUsuario);
        directorioUsuario.mkdirs();
        //Añade a la lista de canales
        JsonObject jsonObjectActual = JsonProvider.provider().createObjectBuilder().add("usuario", login).add("puerto", puerto).build();
        builderCanales.add(jsonObjectActual);
        //Agrega a la lista de usuarios
        usuarios.add(nuevo);
        return nuevo;
    }

    public void enviarMsjSesion(Session session, JsonObject message) {
        try {
            session.getBasicRemote().sendText(message.toString());
        } catch (IOException ex) {
            sesiones.remove(session);
            Logger.getLogger(AdministradorUsuarios.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JsonObject crearRespuesta(boolean logueado, boolean error, String msj, JsonArray suscripcionesUsuario, int canal, JsonArray listado) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject respuesta;
        if (!logueado) {
            respuesta = provider.createObjectBuilder()
                    .add("logueado", logueado)
                    .add("error", error)
                    .add("msj", msj)
                    .build();
        } else {

            respuesta = provider.createObjectBuilder()
                    .add("logueado", logueado)
                    .add("error", error)
                    .add("msj", msj)
                    .add("canales", darListaCanales())
                    .add("suscripciones", suscripcionesUsuario)
                    .add("canal", canal)
                    .add("videos", listado)
                    .build();
        }

        return respuesta;
    }

    public JsonArray darListaCanales() {
        return builderCanales.build();
    }

    public File darCarpetaRaiz() {
        return new File(DIR_RAIZ_USUARIOS);
    }


    public void registrarEstadísticas(String idUsuario, int tiempoCola, int tiempoProcesamiento, String tipoSolicitud) {

        String linea = idUsuario + "," + tiempoCola + "," + tiempoProcesamiento + "," + tipoSolicitud;
        /**
         * if(contador<20) { escribir+=linea+"\n"; } else {
         * csvOutput.write(escribir); csvOutput.println(linea);
         * System.out.println("ESCRITO************"+escribir); contador=0;
         * escribir=""; csvOutput.close();
         */
        try {
            csvOutput = new PrintWriter(new FileWriter(archivoEstadisticas, true));
            csvOutput.println(linea);
            csvOutput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
