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
  
   BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
    String mensaje = entrada.readLine();
    System.out.println("Cliente te dice: " + mensaje);

   BufferedWriter write = new BufferedWriter(new FileWriter("mensaje.txt"));
    write.write(mensaje);
   
   write.close();

  servidor.close();
    
  cliente.close();
    }
 

    

    
}
