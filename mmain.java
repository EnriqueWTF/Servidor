import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;  

public class mmain {
    
    public static void main(String[] args) throws Exception {
    
  ServerSocket servidor = new ServerSocket(8080);
  System.out.println("Esperando al cliente...");


  while(true){

  Socket cliente = servidor.accept();
  System.out.println("Cliente conectado");
  System.out.println("Bienvenido, quieres enviar un mensaje? (SI/NO)");
  

   try (
                BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                Socket clientSocket = cliente; // Para que se cierre automáticamente
            ) {


    String mensaje = entrada.readLine();
   if(mensaje == null){

  System.out.println("Sesion cerrada");
  continue;
   }
  if(mensaje.equals("NO")){
   
  

      System.out.println("El cliente decide n");
      

  }else if(mensaje.equals("SI")){
    System.out.println("Elige la opcion que deseas");
    System.out.println("1. enviar un mensaje");
    System.out.println("2. salir");
   
    String mensaje2 = entrada.readLine();


    if(mensaje2.equals("2")){
    System.out.println("Gracias  por usar el programa");
    System.exit(0);
  


    
     }else if(mensaje2.equals("1")){
      
      System.out.println("Escribe el nombre de la persona que deseas enviar el mensaje");
      String mensajeReal = entrada.readLine();
      System.out.println("Escribe tu mensaje");
      String nombre = entrada.readLine();
     try(BufferedWriter write = new BufferedWriter(new FileWriter("nombre.txt"))){
      write.write(nombre);
     }
     try(BufferedWriter write2 = new BufferedWriter(new FileWriter("mensaje.txt"))){
      write2.write(mensajeReal);


     }
     }
     
      System.out.println("Mensaje y nombre guardados correctamente.");
    }
    } catch(Exception e){

      System.out.println("Error con el cliente"+ e.getMessage());

    }
    
   

    
    
    
    



    }
  }

    
}
