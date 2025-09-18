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
  System.out.println("Bienvenido, quieres enviar un mensaje?, SI/NO");

    BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
    String mensaje = entrada.readLine();

  if(mensaje.equals("NO")){
   
  

      System.out.println("Gracias por usar el programa");
      System.exit(0);

  }else if(mensaje.equals("SI")){

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
