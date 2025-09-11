import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

   
    private static final String RUTA_BASE = "data/";
    private static final String RUTA_ARCHIVO_USUARIOS = RUTA_BASE + "usuarios.txt";
    private static final Object lockArchivo = new Object();

    public static void main(String[] args) throws IOException {
       
        File dir = new File(RUTA_BASE);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File archivoUsuarios = new File("C:\\Users\\kike\\Desktop\\txt visual\\usuarios.txt");
        if (!archivoUsuarios.exists()) {
            try {
                archivoUsuarios.createNewFile();
                System.out.println("Archivo 'usuarios.txt' creado.");
            
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\kike\\Desktop\\txt visual\\usuarios.txt", true))) {
                    writer.write("admin,1234");
                    writer.newLine();
                }
            } catch (IOException e) {
                System.err.println("Error fatal al crear el archivo de usuarios: " + e.getMessage());
                return;
            }
        } else {
            System.out.println("Usando el archivo 'usuarios.txt' existente.");
        }


        System.out.println("Servidor de autenticación y mensajería iniciado en el puerto 8080...");
        ServerSocket socketServidor = new ServerSocket(8080);

        try {
            while (true) {
                Socket clienteSocket = socketServidor.accept();
                System.out.println("Cliente conectado desde " + clienteSocket.getInetAddress());
                new ManejadorCliente(clienteSocket).start();
            }
        } finally {
            socketServidor.close();
        }
    }

    private static class ManejadorCliente extends Thread {
        private final Socket clienteSocket;
        private String usuarioLogueado; 

        public ManejadorCliente(Socket socket) {
            this.clienteSocket = socket;
        }

        @Override
        public void run() {
            try (
                PrintWriter escritor = new PrintWriter(clienteSocket.getOutputStream(), true);
                BufferedReader lector = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()))
            ) {
                if (autenticarUsuario(lector, escritor)) {
                
                    manejarSesionActiva(lector, escritor);
                }
            } catch (IOException e) {
                System.out.println("Error en la comunicación con el cliente: " + e.getMessage());
            } finally {
                try {
                    clienteSocket.close();
                    System.out.println("Conexión con '" + (usuarioLogueado != null ? usuarioLogueado : "desconocido") + "' cerrada.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private boolean autenticarUsuario(BufferedReader lector, PrintWriter escritor) throws IOException {
            String opcion;
            while (true) {
                escritor.println("Bienvenido. Escribe 'login' para iniciar sesión, 'registro' para registrarte, o 'FIN' para salir.");
                opcion = lector.readLine();

                if (opcion == null || opcion.equalsIgnoreCase("FIN")) {
                    return false;
                }

                if (opcion.equalsIgnoreCase("login")) {
                    escritor.println("Introduce tu nombre de usuario:");
                    String usuario = lector.readLine();
                    escritor.println("Introduce tu contraseña:");
                    String pass = lector.readLine();

                    if (validarCredenciales(usuario, pass)) {
                        this.usuarioLogueado = usuario; 
                        escritor.println("¡Acceso concedido! Bienvenido, " + usuario + ".");
                        System.out.println("Usuario '" + usuario + "' ha iniciado sesión correctamente.");
                        return true; 
                    } else {
                        escritor.println("Usuario o contraseña incorrectos.");
                    }
                } else if (opcion.equalsIgnoreCase("registro")) {
                   
                     escritor.println("Elige un nombre de usuario para el registro:");
                    String usuario = lector.readLine();
                    if (usuario == null) return false;

                    escritor.println("Elige una contraseña:");
                    String pass = lector.readLine();
                    if (pass == null) return false;

                    synchronized (lockArchivo) {
                        if (usuarioExiste(usuario)) {
                            escritor.println("Ese nombre de usuario ya existe. Inténtalo de nuevo.");
                        } else {
                            try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\kike\\Desktop\\txt visual\\usuarios.txt", true))) {
                                writer.write(usuario + "," + pass);
                                writer.newLine();
                                escritor.println("¡Registro completado! Ahora puedes iniciar sesión.");
                                System.out.println("Nuevo usuario registrado: '" + usuario + "'");
                            }
                        }
                    }
                } else {
                    escritor.println("Opción no válida.");
                }
            }
        }

        private void manejarSesionActiva(BufferedReader lector, PrintWriter escritor) throws IOException {
            escritor.println("--- SESIÓN INICIADA ---");
            escritor.println("Comandos disponibles: 'send <usuario> <mensaje>', 'read', 'logout'");
            String comando;
            while ((comando = lector.readLine()) != null) {
                if (comando.toLowerCase().startsWith("send ")) {
                    enviarMensaje(comando, escritor);
                } else if (comando.equalsIgnoreCase("read")) {
                    leerMensajes(escritor);
                } else if (comando.equalsIgnoreCase("logout")) {
                    escritor.println("Cerrando sesión. ¡Hasta luego!");
                    break; 
                } else {
                    escritor.println("Comando desconocido. Usa 'send', 'read' o 'logout'.");
                }
            }
        }

        private void enviarMensaje(String comando, PrintWriter escritor) throws IOException {
            String[] partes = comando.split(" ", 3);
            if (partes.length < 3) {
                escritor.println("Formato incorrecto. Usa: send <usuario> <mensaje>");
                return;
            }
            String destinatario = partes[1];
            String mensaje = partes[2];

            synchronized (lockArchivo) {
                if (!usuarioExiste(destinatario)) {
                    escritor.println("El usuario '" + destinatario + "' no existe.");
                    return;
                }
                
              
                String rutaBuzon = RUTA_BASE + "mensajes_" + destinatario + ".txt";
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaBuzon, true))) {
                    writer.write("De " + this.usuarioLogueado + ": " + mensaje);
                    writer.newLine();
                    escritor.println("Mensaje enviado a '" + destinatario + "'.");
                    System.out.println("Mensaje de '" + this.usuarioLogueado + "' para '" + destinatario + "' guardado.");
                }
            }
        }

        private void leerMensajes(PrintWriter escritor) throws IOException {
            String rutaBuzon = RUTA_BASE + "mensajes_" + this.usuarioLogueado + ".txt";
            File archivoBuzon = new File(rutaBuzon);

            synchronized (lockArchivo) {
                if (!archivoBuzon.exists() || archivoBuzon.length() == 0) {
                    escritor.println("No tienes mensajes nuevos.");
                    return;
                }

                escritor.println("--- TUS MENSAJES ---");
                try (BufferedReader reader = new BufferedReader(new FileReader(archivoBuzon))) {
                    String linea;
                    while ((linea = reader.readLine()) != null) {
                        escritor.println(linea);
                    }
                }
                escritor.println("--- FIN DE LOS MENSAJES ---");

           
                new PrintWriter(archivoBuzon).close();
                System.out.println("Buzón de '" + this.usuarioLogueado + "' leído y vaciado.");
            }
        }

      
        private boolean validarCredenciales(String usuario, String pass) throws IOException {
            synchronized (lockArchivo) {
                try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\kike\\Desktop\\txt visual\\usuarios.txt"))) {
                    String linea;
                    while ((linea = reader.readLine()) != null) {
                        String[] partes = linea.split(",", 2);
                        if (partes.length == 2 && partes[0].equals(usuario) && partes[1].equals(pass)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        private boolean usuarioExiste(String usuario) throws IOException {
            synchronized (lockArchivo) {
                try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\kike\\Desktop\\txt visual\\usuarios.txt"))) {
                    String linea;
                    while ((linea = reader.readLine()) != null) {
                        String[] partes = linea.split(",", 2);
                        if (partes.length > 0 && partes[0].equals(usuario)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }}