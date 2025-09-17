import java.net.Socket;
import java.net.ServerSocket;
public class mmain {
    
    public static void main(String[] args) throws Exception {
    
  ServerSocket servidor = new ServerSocket(8080);
  System.out.println("Esperando al cliente...");
  Socket cliente = servidor.accept();
  System.out.println("Cliente conectado");
  cliente.close();

    }
    

    
}
