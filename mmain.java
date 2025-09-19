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
  Socket cliente = servidor.accept();
  System.out.println("Cliente conectado");
  System.out.println("Bienvenido, quieres enviar un mensaje? (SI/NO)");

    BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
    String mensaje = entrada.readLine();

  if(mensaje.equals("NO")){
   
  

      System.out.println("Gracias por usar el programa");
      System.exit(0);

  }else if(mensaje.equals("SI")){
    System.out.println("Elige la opcion que deseas");
    System.out.println("1. enviar un mensaje");
    System.out.println("2. salir");
    BufferedReader entrada2 = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
    String mensaje2 = entrada2.readLine();


    if(mensaje2.equals("2")){
    System.out.println("Gracias  por usar el programa");
    System.exit(0);
  


    
     }else if(mensaje2.equals("1")){
      System.out.println("Escribe el nombre de la persona que deseas enviar el mensaje");
      



    }
    
    System.out.println("Escribe tu mensaje");


    String mensajeReal = entrada.readLine();
    
    try(BufferedWriter write = new BufferedWriter(new FileWriter(new File("mensaje.txt")))){
    write.write(mensajeReal);

    }
    


  servidor.close();
    
  cliente.close();
    }
  }

    
}
