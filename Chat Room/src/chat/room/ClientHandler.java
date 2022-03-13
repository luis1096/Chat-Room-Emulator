/**
 * @author Luis Oliveros [6131616]
 * @version 07/01/2020
 * 
 * A class that handles each instance a clients socket connects to the server. 
 * Implements the Runnable interface to allow for multi-threading. 
 */
package chat.room;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientHandler implements Runnable  
{
    private Socket client;//client socket that will connect to server port
    private BufferedReader in;//reads information typed in from client
    private PrintWriter out;//sends information from server to client
    private ArrayList<ClientHandler> connections;//list of all threads
    private ArrayList<String> clientIDS = new ArrayList<String>();
    //list of all client names
    private Server s = new Server();
    //instance of Server class created
    private String id = s.getClientID();
    //sets a new string equal to the method that 
    //returns client name in Server Class
    private String msg;//client messages sent in server chat room
    private Lock lock = new ReentrantLock();
    //lock to avoid a thread interruption mid execution 
    
    /**
     * @param clientSocket, client socket that connects to server port
     * @param conn, list of all client threads
     * @throws IOException 
     */
    public ClientHandler(Socket clientSocket, ArrayList<ClientHandler> conn)
    throws IOException
    {
        client = clientSocket;//sets client socket for access to server port
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        //sets in to read client inputed information
        out = new PrintWriter(client.getOutputStream(), true);
        //set out to send client information
        connections = conn;//list of client threads
    }

    /**
     * method that contains all executions for each client in the server chat
     * room.Run every time a new thread is created to handle a client.
     */
    public void run() 
    {  
         try
         {  
            Boolean sameID = true;
            while(sameID)//while user types an ID already in use.
            {
                out.println("Please enter a name to identify you with: ");       
                id = in.readLine();//set id to string typed by client
                if(!clientIDS.contains(id))
                //if the client does not have the same name as another client
                {
                    sameID = false;//terminate loop
                    clientIDS.add(id);//update list of client names
                }
                else
                {
                    out.println("Selected ID is already in use!");
                }      
                if(id.contains("LOGOUT") || id.isEmpty())
                //if client types LOGOUT or nothing
                {
                    return;//terminate further method executions
                }       
            }  
            
            for(ClientHandler clients: connections)
            //for each ClientHandler object (thread) in the list of threads
            {
              clients.out.println(id + ": has joined the chat room, welcome!");
            }  
                        
            while(true)
            {
                msg = in.readLine();//sets msg to client typed String
                if(msg.startsWith("LOGOUT") || msg.isEmpty())
                //if client types LOGOUT or nothing.
                {
                    break;//terminate loop
                }
                outToAll(connections);
                //call to method that outputs to all clients
            }         
         }
         catch (IOException ex) 
         {
             System.err.println("IOException in ClientHandler");
             System.err.println(ex.getStackTrace());
         }         
         finally
         {
            outToAll(connections);//call to method that outputs to all clients
        
            out.close();//close connection that sends information to client
            try 
            {
                in.close();
                //close connection that receives information from client
            } 
            catch (IOException ex) 
            {
                System.err.println("IOException in ClientHandler");
                System.err.println(ex.getStackTrace());
            }
         }
        
    }
    
    /**
     * @param connections, list of all ClientHandler Objects (threads)
     * method that sends String input from a specific client to all other 
     * clients in the Server chat room.
     */
    public void outToAll(ArrayList<ClientHandler> connections)
    {
        lock.lock();//locks current client thread 
        try
        {
            if(id.contains("LOGOUT") || id.isEmpty() || 
            msg.startsWith("LOGOUT") || msg.isEmpty())
            //if client id is LOGOUT or empty, or their msg is LOGOUT or empty
            {
                for(ClientHandler clients: connections)
                //print to all client in the chat room
                {
                    clients.out.println(id + " has left the chat room.");
                }   
            }
            else if(msg.startsWith("SAY"))//if client input starts with SAY
            {
                int firstSpace = msg.indexOf(" ");
                if(firstSpace != -1)
                {
                    for(ClientHandler clients: connections)
                    //print to all clients in the chat room
                    {
                        clients.out.println(id + ": " + 
                        msg.substring(firstSpace + 1));
                    }
                }
            }
            else if(msg.startsWith("FACT"))//if client input starts with FACT
            {
                Random r = new Random();
                int random = r.nextInt(8);
                for(ClientHandler clients: connections)
                //print to all clients in the chat room
                {
                    if(random == 0)
                    {
                        clients.out.println("SERVER: Theres only "
                        + "one letter that"
                        + " doesnt appear in any U.S. state name, Q.");
                    }
                    else if(random == 1)
                    {
                        clients.out.println
                        ("SERVER: Human noses and ears keep "
                        + "getting bigger, even when the rest "
                        + "of the bodys growth "
                        + "has come to a halt.");
                    }
                    else if (random == 2)
                    {
                        clients.out.println("SERVER: Bees can fly "
                        + "higher than 29,525 "
                        + "feet above sea level, according to "
                        + "National Geographic. "
                        + "That’s higher than Mount Everest, the tallest "
                        + "mountain in the world.");
                    }
                    else if(random == 3)
                    {
                        clients.out.println("SERVER: Canadians say "
                        + "(sorry) so much "
                        + "that a law was passed in 2009 declaring that an "
                        + "apology cant be used as evidence "
                        + "of admission to guilt.");
                    }
                    else if(random == 4)
                    {
                        clients.out.println("SERVER: To leave a party "
                        + "without telling"
                        + " anyone is called in English, a “French Exit”. "
                        + "In French, its called a (partir a l anglaise), "
                        + "to leave like the English."); 
                    }
                    else if(random == 5)
                    {
                        clients.out.println("SERVER: There is an "
                        + "insurance policy issued "
                        + "against alien abduction. Around "
                        + "50,000 policies have "
                        + "been sold, mainly to residents"
                        + " of the U.S. and England."); 
                    }
                    else if(random == 6)
                    {
                        clients.out.println("SERVER: The largest "
                        + "known prime number "
                        + "has 17,425,170 digits. The new prime number is 2 "
                        + "multiplied by itself 57,885,161 times, minus 1.");
                    }
                    else if(random == 7)
                    {
                        clients.out.println("SERVER: If you "
                        + "translate (Jesus) from "
                        + "Hebrew to English, the correct translation "
                        + "is (Joshua). The name (Jesus) "
                        + "comes from translating "
                        + "the name from Hebrew, to Greek, "
                        + "to Latin, to English.");
                    }
                }
            }
            else if(msg.startsWith("COMMANDS"))
            //if client input starts with COMMANDS
            {   
                //prints to specific client list of server chatroom commands.
                out.println("List of Server commands: LOGOUT: "
                + "(removes you from "
                + "server chatroom) SAY: (outputs message to all users in "
                + "chatroom) FACT: (outputs a random "
                + "fact to all users in chatroom). "
                + "Entering nothing removes you from chatroom");
            }
        }
        finally
        {
            lock.unlock();//unlocks current client thread
        }
    }
}
 