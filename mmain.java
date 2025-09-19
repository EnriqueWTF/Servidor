import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.UUID;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;  

public class mmain {
   
    public static void UsuarioMensajes(BufferedReader entrada, String usuario) throws IOException {
        while (true) {
            System.out.println("Elige la opcion que deseas (1) Enviar mensaje (2) Leer mis mensajes (3) Cerrar sesion");
            String opcion = entrada.readLine();

            if (opcion == null || opcion.equals("3")) {
                System.out.println("Usuario '" + usuario + "' ha cerrado sesión.");
                break; 
            }

            if (opcion.equals("1")) {
                System.out.println("Escribe el nombre del destinatario:");
                String destinatario = entrada.readLine();

                if (!userExists(destinatario)) {
                    System.out.println("Error: El destinatario '" + destinatario + "' no existe.");
                    continue;
                } else {
                    System.out.println("Escribe tu mensaje:");
                    String mensaje = entrada.readLine();

                    String Key = UUID.randomUUID().toString();
                    String nombreArchivo = "messages/" + destinatario.trim() + "/" + Key + ".txt";

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
                        writer.write("De: " + usuario.trim() + "\nMensaje: " + mensaje);
                    }
                    System.out.println("Mensaje enviado.");
                }
            } else if (opcion.equals("2")) {
                System.out.println("--- Tus Mensajes ---");
                File buzonUsuario = new File("messages/" + usuario.trim());
                File[] mensajes = buzonUsuario.listFiles();

                if (mensajes == null || mensajes.length == 0) {
                    System.out.println("No tienes mensajes.");
                } else {
                    for (File archivoMensaje : mensajes) {
                        String contenido = new String(Files.readAllBytes(archivoMensaje.toPath()));
                        System.out.println("--------------------");
                        System.out.println(contenido);
                    }
                }
            }
        }
    }

    
    public static boolean registerUser(String nombreusuario, String password) throws IOException {
        if (userExists(nombreusuario) || password == null || password.trim().isEmpty()) {
            return false;
        }
       
        String userLine = nombreusuario.trim() + "," + password.trim() + System.lineSeparator();
        Files.write(Paths.get("nombre.txt"), userLine.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        
        new File("messages/" + nombreusuario.trim()).mkdirs();
        return true;
    }

   
    public static boolean userExists(String nombreusario) throws IOException {
        File usuarios = new File("nombre.txt");    
        if (!usuarios.exists() || nombreusario == null) {
            return false;
        }
        List<String> lineas = Files.readAllLines(Paths.get("nombre.txt"));
        for (String linea : lineas) {
            String[] parts = linea.split(",");
            if (parts.length > 0 && parts[0].trim().equalsIgnoreCase(nombreusario.trim())) {
                return true;
            }
        }
        return false;
    }

    
    public static boolean checkCredentials(String nombreusuario, String password) throws IOException {
        File usuarios = new File("nombre.txt");
        if (!usuarios.exists() || nombreusuario == null || password == null) {
            return false;
        }
        List<String> lineas = Files.readAllLines(Paths.get("nombre.txt"));
        for (String linea : lineas) {
            String[] parts = linea.split(",");
            if (parts.length == 2) {
                String storedUser = parts[0].trim();
                String storedPass = parts[1].trim();
               
                if (storedUser.equalsIgnoreCase(nombreusuario.trim()) && storedPass.equals(password.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        new File("messages").mkdirs();
        ServerSocket servidor = new ServerSocket(8080);
        System.out.println("Esperando al cliente...");

        while (true) {
            Socket cliente = servidor.accept();
            System.out.println("Cliente conectado");
            System.out.println("Menu: (1)Registrarte (2)Iniciar sesion");
  
            try (BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()))) {
                String opcion = entrada.readLine();
                if (opcion == null) {
                    continue;
                }

        
                if (opcion.equals("1")) {
                    System.out.println("Nombre:");
                    String nuevoUsuario = entrada.readLine();
                    System.out.println("Crea una contraseña:");
                    String nuevaPassword = entrada.readLine();
    
                    if (registerUser(nuevoUsuario, nuevaPassword)) {
                        System.out.println("Usuario " + nuevoUsuario + " registrado correctamente.");
                        System.out.println("Iniciando sesion...");
                        UsuarioMensajes(entrada, nuevoUsuario);
                    } else {
                        System.out.println("El usuario ya esta registrado o los datos son inválidos.");
                    }
                
                } else if (opcion.equals("2")) {
                    System.out.println("Nombre de usuario para iniciar sesion: ");
                    String loginUser = entrada.readLine();
                    System.out.println("Contraseña:");
                    String loginPassword = entrada.readLine();

                    if (checkCredentials(loginUser, loginPassword)) {
                        System.out.println("Sesion iniciada.");
                        UsuarioMensajes(entrada, loginUser);
                    } else {
                        System.out.println("Usuario o contraseña incorrectos.");
                    }
                }
            } catch (Exception e) {
                System.out.println("Error con el cliente: " + e.getMessage());
            }
            System.out.println("Cliente desconectado");
        }
    }
}