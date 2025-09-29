import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter; 
import java.net.ServerSocket;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;  

public class mmain {
   public static void main(String[] args) throws Exception {
        new File("messages").mkdirs();
        ServerSocket servidor = new ServerSocket(8080);
        System.out.println("Servidor iniciado. Esperando al cliente...");

        while (true) {
            Socket cliente = servidor.accept();
            System.out.println("Cliente conectado: " + cliente.getInetAddress());
  
            
            try (BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                 PrintWriter salida = new PrintWriter(cliente.getOutputStream(), true)) {
                
                salida.println("Menu: (1)Registrarte (2)Iniciar sesion");
                String opcion = entrada.readLine();
                
                if (opcion == null) continue;

                if (opcion.equals("1")) {
                    salida.println("Nombre:");
                    String nuevoUsuario = entrada.readLine();
                    salida.println("Crea una contraseña:");
                    String nuevaPassword = entrada.readLine();
    
                    if (registerUser(nuevoUsuario, nuevaPassword)) {
                        salida.println("Usuario " + nuevoUsuario + " registrado correctamente. Iniciando sesion...");
                        UsuarioMensajes(entrada, salida, nuevoUsuario);
                    } else {
                        salida.println("El usuario ya esta registrado o los datos son inválidos.");
                        continue;
                    }
                
                } else if (opcion.equals("2")) {
                    salida.println("Nombre de usuario para iniciar sesion: ");
                    String loginUser = entrada.readLine();
                    salida.println("Contraseña:");
                    String loginPassword = entrada.readLine();

                    if (checkCredentials(loginUser, loginPassword)) {
                        salida.println("Sesion iniciada.");
                        UsuarioMensajes(entrada, salida, loginUser);
                    } else {
                        salida.println("Usuario o contraseña incorrectos.");
                        continue;
                    }
                }
            } catch (Exception e) {
                System.out.println("Error con el cliente: " + e.getMessage());
            }
            System.out.println("Cliente desconectado");
        }
    }
    public static void UsuarioMensajes(BufferedReader entrada, PrintWriter salida, String usuario) throws IOException {
        while (true) {
            salida.println("Elige la opcion que deseas (1) Enviar mensaje (2) Leer mis mensajes (3) Eliminar mensaje (4) Cerrar sesion (5) Eliminar cuenta");
            String opcion = entrada.readLine();

            if (opcion == null || opcion.equals("4")) { 
                salida.println("Cerrando sesion...");
                break; 
            }

            if (opcion.equals("1")) {
                salida.println("Escribe el nombre del destinatario:");
                String destinatario = entrada.readLine();

                if (!userExists(destinatario)) {
                    salida.println("Error: El destinatario '" + destinatario + "' no existe.");
                    continue;
                } else {
                    salida.println("Escribe tu mensaje:");
                    String mensaje = entrada.readLine();

                    String Key = UUID.randomUUID().toString();
                    String nombreArchivo = "messages/" + destinatario.trim() + "/" + Key + ".txt";

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
                        writer.write("De: " + usuario.trim() + "\nMensaje: " + mensaje);
                    }
                    salida.println("Mensaje enviado.");
                }
            } if (opcion.equals("2")) {
                salida.println("--- Tus Mensajes ---");
                File buzonUsuario = new File("messages/" + usuario.trim());
                File[] mensajes = buzonUsuario.listFiles();

                if (mensajes == null || mensajes.length == 0) {
                    salida.println("No tienes mensajes.");
                } else {
                    for (File archivoMensaje : mensajes) {
                        String contenido = new String(Files.readAllBytes(archivoMensaje.toPath()));
                        salida.println("--------------------");
                        salida.println(contenido);
                    }
                }
            } if (opcion.equals("3")) {
                salida.println("--- Eliminar Mensaje ---");
                File buzonUsuario = new File("messages/" + usuario.trim());
                File[] mensajes = buzonUsuario.listFiles();

                if (mensajes == null || mensajes.length == 0) {
                    salida.println("No tienes mensajes para eliminar.");
                    continue;
                }
                if(opcion.equals("5")){
                salida.print("Estas seguro de que quieres elimar la cuenta? (SI) (NO)");
                String Cuenta = entrada.readLine();
                if(Cuenta.equalsIgnoreCase("SI") ){
                 if(deleteUser(usuario)){
                 salida.println("Tu cuenta ha sido eliminada permanentemente.");
                 break;
                 }else{
                    salida.print("No se pudo eleminar la cuenta.");
                 }
               
                  
                }if(Cuenta.equalsIgnoreCase("NO") || Cuenta == null){
                 continue;

                }

                
                }

                
                List<File> messageFiles = new ArrayList<>();
                salida.println("Tus mensajes:");
                for (int i = 0; i < mensajes.length; i++) {
                    messageFiles.add(mensajes[i]);
                    String contentPreview = new String(Files.readAllBytes(mensajes[i].toPath())).split("\n")[0]; 
                    salida.println("[" + (i + 1) + "] " + contentPreview);
                }

                salida.println("Elige el numero del mensaje a eliminar (o 'cancelar'):");
                String numStr = entrada.readLine();

                try {
                    int numToDelete = Integer.parseInt(numStr);
                    if (numToDelete > 0 && numToDelete <= messageFiles.size()) {
                        File fileToDelete = messageFiles.get(numToDelete - 1); 
                        if (fileToDelete.delete()) {
                            salida.println("Mensaje eliminado correctamente.");
                        } else {
                            salida.println("Error: No se pudo eliminar el mensaje.");
                        }
                    } else {
                        salida.println("Numero invalido.");
                    }
                } catch (NumberFormatException e) {
                    salida.println("Entrada invalida. Operacion cancelada.");
                }
            }
        }
    }

    private static boolean deleteUser(String usuario) {
       
      // 1. Eliminar usuario de nombre.txt
        File userFile = new File("nombre.txt");
        if (!userFile.exists()) return false;

        List<String> lines = Files.readAllLines(Paths.get("nombre.txt"));
        List<String> updatedLines = new ArrayList<>();
        boolean userFound = false;

        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length > 0 && parts[0].trim().equalsIgnoreCase(usuario.trim())) {
                userFound = true; 
            } else {
                updatedLines.add(line); 
            }
        }

        if (!userFound) return false; // El usuario no estaba en el archivo

        // Sobrescribir el archivo con la lista de usuarios actualizada
        Files.write(Paths.get("nombre.txt"), updatedLines);

        // 2. Eliminar el directorio de mensajes del usuario
        File userMessageDir = new File("messages/" + usuario.trim());
        if (userMessageDir.exists()) {
            return deleteDirectory(userMessageDir);
        }

        return true;
    }
 
    
    private static boolean deleteDirectory(File userMessageDir) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteDirectory'");
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
        if (!usuarios.exists() || nombreusario == null) return false;
        List<String> lineas = Files.readAllLines(Paths.get("nombre.txt"));
        for (String linea : lineas) {
            String[] parts = linea.split(",");
            if (parts.length > 0 && parts[0].trim().equalsIgnoreCase(nombreusario.trim())) return true;
        }
        return false;
    }

    public static boolean checkCredentials(String nombreusuario, String password) throws IOException {
        File usuarios = new File("nombre.txt");
        if (!usuarios.exists() || nombreusuario == null || password == null) return false;
        List<String> lineas = Files.readAllLines(Paths.get("nombre.txt"));
        for (String linea : lineas) {
            String[] parts = linea.split(",");
            if (parts.length == 2) {
                if (parts[0].trim().equalsIgnoreCase(nombreusuario.trim()) && parts[1].trim().equals(password.trim())) {
                    return true;
                }
            }
        }
        return false;
    }
}