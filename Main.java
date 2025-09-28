import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    
    private static final String RUTA_ARCHIVO_USUARIOS = "usuarios.txt";
    private static final Object lockArchivo = new Object();

    public static void main(String[] args)throws IOException {


           try {
            prepararArchivoUsuarios();
            iniciarBucleServidor(8080);
        } catch (IOException e) {
            System.err.println("Error fatal del servidor: " + e.getMessage());
        }
    }




         private static void iniciarBucleServidor(int i) {
       
        throw new UnsupportedOperationException("Unimplemented method 'iniciarBucleServidor'");
    }




         private static void prepararArchivoUsuarios() {


        }
        throw new UnsupportedOperationException("Unimplemented method 'prepararArchivoUsuarios'");
    }




        {
            File archivo = new File("C:\\Users\\Enrique\\Desktop\\Archivos de texto\\usuarios.txt");
            if (archivo.createNewFile()) {
                System.out.println("Archivo 'usuarios.txt' creado.");
             
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\Enrique\\Desktop\\Archivos de texto\\usuarios.txt", true))) {
                    writer.write("admin,1234");
                    writer.newLine();
                }
            } else {
                System.out.println("Usando el archivo 'usuarios.txt' existente.");
            }
        } catch (IOException e) {
            System.err.println("Error al crear el archivo de usuarios: " + e.getMessage());
            return;
        }

        System.out.println("Servidor de autenticación iniciado en el puerto 8080...");
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

        public ManejadorCliente(Socket socket) {
            this.clienteSocket = socket;
        }

        @Override
        public void run() {
            try (
                PrintWriter escritor = new PrintWriter(clienteSocket.getOutputStream(), true);
                BufferedReader lector = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()))
            ) {
                autenticarUsuario(lector, escritor);
            } catch (IOException e) {
                System.out.println("Error en la comunicación con el cliente: " + e.getMessage());
            } finally {
                try {
                    clienteSocket.close();
                    System.out.println("Conexión con el cliente cerrada.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void autenticarUsuario(BufferedReader lector, PrintWriter escritor) throws IOException {
            String opcion;
            while (true) {
                escritor.println("Bienvenido. Escribe 'login' para iniciar sesión o 'registro' para registrarte.");
                opcion = lector.readLine();

                if (opcion == null) return;

                if (opcion.equalsIgnoreCase("login")) {
                    escritor.println("Introduce tu nombre de usuario:");
                    String usuario = lector.readLine();
                    escritor.println("Introduce tu contraseña:");
                    String pass = lector.readLine();

                    if (validarCredenciales(usuario, pass)) {
                        escritor.println("¡Acceso concedido! Bienvenido, " + usuario + ".");
                        System.out.println("Usuario '" + usuario + "' ha iniciado sesión correctamente.");
                        break; 
                    } else {
                        escritor.println("Usuario o contraseña incorrectos. Inténtalo de nuevo.");
                    }

                } else if (opcion.equalsIgnoreCase("registro")) {
                    escritor.println("Elige un nombre de usuario para el registro:");
                    String usuario = lector.readLine();
                    if (usuario == null) return;

                    escritor.println("Elige una contraseña:");
                    String pass = lector.readLine();
                    if (pass == null) return;

                    synchronized (lockArchivo) {
                        if (usuarioExiste(usuario)) {
                            escritor.println("Ese nombre de usuario ya existe. Inténtalo de nuevo.");
                        } else {
                            try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\Enrique\\Desktop\\Archivos de texto\\usuarios.txt", true))) {
                                writer.write(usuario + "," + pass);
                                writer.newLine();
                                escritor.println("¡Registro completado! Ahora puedes iniciar sesión.");
                                System.out.println("Nuevo usuario registrado: '" + usuario + "'");
                            }
                        }
                    }
                } else {
                    escritor.println("Opción no válida. Por favor, escribe 'login' o 'registro'.");
                }
            }
        }

        private boolean validarCredenciales(String usuario, String pass) throws IOException {
            synchronized (lockArchivo) {
                try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Enrique\\Desktop\\Archivos de texto\\usuarios.txt"))) {
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
            try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Enrique\\Desktop\\Archivos de texto\\usuarios.txt"))) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    String[] partes = linea.split(",", 2);
                    if (partes.length > 0 && partes[0].equals(usuario)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
