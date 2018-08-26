package cchat_server;

import java.net.*;
import java.io.*;

/**
 * Simple, lightweight server for a chat box
 * @author Nick Vocaire
 * Date Created: 11/17/17
 */
public class ChatServer extends Thread{
    public static Socket clients[]; //Create an array of Sockets to store all of the Client Sockets
    public static String names[]; //Create an array of names to store all of the names of the Clients
    public static String name; //A Variable to temporarly hold a name for the client
    public static int client; //A Variable to temporarly hold a position in the client array
    public static int portNumber; //The port to make the server on
    public static int connections; //Number of Clients
    public static final int DEFAULT_PORT = 22333; //Default Port
    public static final int DEFAULT_CONNECTIONS = 1; //Default Connections
    
    /**
     * Constructor for Making the Threads for every client, takes an int for the client
     * @param n Client name
     * @param c Client number
     */
    public ChatServer (String n, int c) {
        client = c;
        name = n;
    }
    
    /**
     * Method that is used when a chat server thread is created and run
     */
    public void run(){ //The code that the Thread runs
        int tClient = client; //A local variable for the thread that stores the client position in array
        String tName = name; //A local variable for the thread that stores the client name
        try { //Try statement to catch errors from loosing contact with client.
            BufferedReader in = new BufferedReader(new InputStreamReader(clients[tClient].getInputStream()));
            //Buffered Reader for taking input from the inputstream of the Socket connected to the client of,
            //the Thread.   

            String inputLine; //String to hold input from the client of the Thread
            while (true) {
                if((inputLine = in.readLine()) != null) { //Waits for input from client of Thread
                    System.out.println("C:" + (tClient+1) + " " + tName + ": " + inputLine);
                    //Prints Input from Client of Thread.
                    for(int i=0; i<clients.length; i++){ //Loops through all clients
                        if (clients[i] != null) { //If the client Exists
                            PrintWriter out = new PrintWriter(clients[i].getOutputStream(), true);
                            //Sets a writer to print out to all clients (because of the for loop)

                            if(i != tClient) //Sends to all clients but the client of the thread
                                out.println(tName + ": " + inputLine); //Sends input
                        }
                    }
                }
            }
        } catch (IOException e) { //Catch a error
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        } 
    }
    
    /**
     * Main method for starting the chat server
     * @param args arguments for if the program is run through the console
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        System.out.print("\u000C"); //Clears Screen
        
        InetAddress ip = InetAddress.getLocalHost(); //Create InetAddress object based on the local machine
        System.out.println("Lan IP: " + ip.getHostAddress()); //Print Ip (For people to connect through lan)
        
        BufferedReader person = new BufferedReader(new InputStreamReader(System.in)); //Get input from Person

        System.out.println("Port? (hit enter for default: " + DEFAULT_PORT +  " )"); //Ask Question
        String temp = person.readLine(); // Temp String for input
        if(!temp.equals("")) //If not enter, set port number to temp
            portNumber = Integer.parseInt(temp);
        else //If enter, set port number to deafult
            portNumber = DEFAULT_PORT;

        ServerSocket serverSocket = new ServerSocket(portNumber); //Creat Socket for Server to connect to clients

        System.out.println("How many Connections (hit enter for default: " + DEFAULT_CONNECTIONS + " )"); //Ask Question
        temp = person.readLine(); // Use same temp String for input
        if(!temp.equals("")) //If not enter, set connections to temp
            connections = Integer.parseInt(temp);
        else //If enter, set connections to deafult
            connections = DEFAULT_CONNECTIONS;

        clients = new Socket[connections]; //Set the array length of clients to the amount of connections
        Thread threads[] = new Thread[connections]; //Create a thread array with the same size as the clients so
                                                    //each client has a Thread
        names = new String[connections]; //Set the array length of names to the amount of connections

        for(int i=0; i<clients.length; i++){ //Loop through all the clients
            System.out.println("Waiting for connection...");
            clients[i] = serverSocket.accept(); //Wait for a client to connect and set a Socket in the array to
            //the socket the client connected on
            System.out.println("Connection found for Client: " + (i+1));

            PrintWriter out = new PrintWriter(clients[i].getOutputStream(), true);
            //Sets a writer to print out to the client that just connected

            BufferedReader in = new BufferedReader(new InputStreamReader(clients[i].getInputStream()));
            //Sets a reader to get input from the client that just connected

            names[i] = in.readLine(); //Sets the name at position i in the array to the name the clinet sends
            for(int j = 0; j<names.length; j++) { //Loops through all the names
                if(i != j) { //If the name is being checked is not the one that was just put in
                    if(names[i].equals(names[j])) //If the current name is the same as another name
                        names[i] = names[i] + (i+1); //Give it a the number of the client at the end
                }
            }

            out.println(i+1); //Send back the clients number

            threads[i] = new ChatServer(names[i], i); //Make a Thread using the postion in the array of the
            //client and the name of the client
            threads[i].start(); //Start that Thread
        }
    }
}