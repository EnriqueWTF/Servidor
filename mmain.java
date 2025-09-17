import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
public class mmain {
    
    public static void main(String[] args) throws Exception {
    
  ServerSocket servidor = new ServerSocket(8080);
  System.out.println("Esperando al cliente...");
  Socket cliente = servidor.accept();
  System.out.println("Cliente conectado");
  
   BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
    String mensaje = entrada.readLine();
    System.out.println("Cliente te dice: " + mensaje);



    
    servidor.close();
    
  cliente.close();
    }
 

    

    
}
