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
                }else{
                 System.out.println("Escribe tu mensaje:");
                String mensaje = entrada.readLine();

                String Key = UUID.randomUUID().toString();
                String nombreArchivo = "mensaje.txt" + destinatario.trim() + "/" + Key + ".txt";

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
                    writer.write("De: " + usuario.trim() + "\nMensaje: " + mensaje);
                }
              }if (opcion.equals("2")) {
         

                System.out.println("--- Tus Mensajes ---");
                File buzonUsuario = new File("mensaje.txt" + usuario.trim());
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
  }
   public static boolean  registerUser(String nombreusuario)throws IOException{
  if (userExists(nombreusuario)) {
        return false;
    }
    String userLine = nombreusuario.trim() + System.lineSeparator();
    Files.write(Paths.get("nombre.txt"), userLine.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    //mkdirs es para crear una carpeta a cada usuario, asi se evita que todos los mensajes esten en 1 sola carpeta 
    new File("mensaje.txt" + nombreusuario.trim()).mkdirs();

    return true;
}





  
    public static boolean userExists(String nombreusario)throws IOException{
    File usuarios = new File("nombre.txt");    
    if(!usuarios.exists() || nombreusario== null){
      return false;
    }
    List<String> lineas = Files.readAllLines(Paths.get("nombre.txt"));
  //for de la morma mas moderna para estos casos: for (String line : lines), no lo usare de moment
    for (int i = 0; i < lineas.size(); i++) {
    String linea = lineas.get(i); 
      if(linea.trim().equalsIgnoreCase(nombreusario.trim())){
       return true;
      }
    }
      return false;
    }







    public static void main(String[] args) throws Exception {
    
  ServerSocket servidor = new ServerSocket(8080);
  System.out.println("Esperando al cliente...");


  while(true){

  Socket cliente = servidor.accept();
  System.out.println("Cliente conectado");
  System.out.println("Menu: (1)Registrarte (2)Iniciar sesion");
  

   try (
                BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                Socket clientSocket = cliente; // Para que se cierre automáticamente
   ) {


    

    

   String opcion = entrada.readLine();
  if(opcion == null){
    System.out.println("El cliente se desconecto");
    continue;
  }

   if(opcion.equals("1")){

   System.out.println("Nombre:");
   String nuevoUsuario = entrada.readLine();
    

    if(registerUser(nuevoUsuario)){
    System.out.println("Usuario"+ nuevoUsuario+ "Usuario registrado correctamente.");

    }else{
      System.out.println("El usuario ya esta registrado");

    }
    }


   if (opcion.equals("2")){
    String loginUser = entrada.readLine();

    if(userExists(loginUser)){
    System.out.println("Sesion iniciada.");
    UsuarioMensajes(entrada, loginUser);
   

    }else{

     System.out.println("El usuario no existe.");
    }
  }
    } catch(Exception e){

      System.out.println("Error con el cliente"+ e.getMessage());

    }
  }
  }
  }

  

