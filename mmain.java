import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;  

public class mmain {
   public static boolean  registerUser(String nombreusuario)throws IOException{
  if (userExists(nombreusuario)) {
        return false;
    }
    String userLine = nombreusuario.trim() + System.lineSeparator();
    Files.write(Paths.get("nombre.txt"), userLine.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
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
    System.out.println("Elige la opcion que deseas (1). Enviar mensaje (2). Cerrar sesion");
   

    }
 





    String mensaje2 = entrada.readLine();
    if(mensaje2.equals("2")){
    System.out.println("Gracias  por usar el programa");
    System.exit(0);
    

    }else if(mensaje2.equals("1")){
      
      System.out.println("Escribe el nombre de la persona que deseas enviar el mensaje");
      String NombrePersona = entrada.readLine();

      

      System.out.println("Escribe tu mensaje");

      String nombre = entrada.readLine();

     try(BufferedWriter write = new BufferedWriter(new FileWriter("nombre.txt"))){
      write.write(nombre);
     }
     try(BufferedWriter write2 = new BufferedWriter(new FileWriter("mensaje.txt"))){
      write2.write(NombrePersona);


     }
     }
    
    
    
    else {

      System.out.println("El usuario no existe.");
    }

    




    
   




      

   
    
   
  


      
     
     
      System.out.println("Mensaje y nombre guardados correctamente.");
    }
    } catch(Exception e){

      System.out.println("Error con el cliente"+ e.getMessage());

    }
  }
   

    
    
    
    



    }
  }

    
}
