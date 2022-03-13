/**
 * @author Luis Oliveros [6131616]
 * @version 07/01/2020
 * 
 * A class that creates a multi-user chat room using different threads to 
 * handle each client on the server. Uses telnet to perform chat interactions.
 */
package chat.room;
import java.io.IOException; 
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server 
{
    private static final int PORT = 1337; //server port clients use to connect
    private ArrayList<ClientHandler> connections = 
    new ArrayList<ClientHandler>();
    //list of objects created to handle each client accepted    
    private String clientID = ""; //name chosen by the client
    private ExecutorService pool = Executors.newFixedThreadPool(5);
    //executes each ClientHandler object in a new thread
     
    /**
     * @return clientID
     * The string chosen by the client used as their name in the server chat
     */
    public String getClientID()
    {
        return clientID;
    }
    
    /**
     * @return connections
     * the list of ClientHandler objects to deal with each client
     */
    public ArrayList<ClientHandler> getConnections()
    {
        return connections;
    }
    
        
    /**
     * @param args the command line arguments
     * @throws java.io.IOException 
     */
    public static void main(String[] args) throws IOException 
    {
        //Instance of Server class created to access methods and variables
        Server s = new Server();  
        //Server Socket created with port of the Server computer
        ServerSocket listener = new ServerSocket(PORT);
        
        while(true)
        {
            System.out.println("Server is Waiting for client connection...");
            Socket client = listener.accept();//accepts client connection
            System.out.println("Client has connected to server!");
            //ClientHandler class instantiated to use class protocols
            ClientHandler clientThread = 
            new ClientHandler(client, s.connections);
            s.connections.add(clientThread);
            //new thread added to list of ClientHandler objects
            s.pool.execute(clientThread);
            //execution starts of specific thread 
        }
          
    }
}
       
         
              
    
 

 