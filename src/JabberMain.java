/*
   Simple command-line Jabber client (skeleton code).

   To compile:
   $ javac   -classpath .;commons-codec-1.8.jar   *.java

   To execute:
   $ java    -classpath .;commons-codec-1.8.jar \
      JabberMain  jabber_id  password  server_name  server_port 
                  [more Jabber ID details] ... 
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.*;

import javax.xml.stream.XMLStreamReader;

/**
   Class containing the {@link #main(String[])} method.

   <p> This class creates an XMPP connection to the server
   specified in the command-line arguments, using the
   {@link XmppConnection} class.
 */
public class JabberMain {

    /** XMPP connection. */
    private static XmppConnection connection = null;
    private static InputStreamReader streamReader;
    private static BufferedReader in;
    private static XmppSenderReceiver senderReceiver;

    /** Main method that starts off everything. */
    public static void main( String[] args ) {

        try {

            // In this assignment, you need to connect to *one*
            //  Jabber server only. For extra credit, you can
            //  extend your client to handle multiple Jabber
            //  servers (multiple Jabber IDs) simultaneously.

            // Check if number of args are ok (multiple of 4)
            if ( args.length < 4 || args.length % 4 != 0 ) {
                System.err.println( "Usage: java JabberMain " + 
                                    "jabber_id password server_name server_port " + 
                                    "[more Jabber ID details] ... " );
                return;
            }
            System.out.println();

            // Get the list of Jabber IDs
            List <JabberID> jidList = getJidList( args );

            // In this assignment, handling one server is sufficient
            // Create an XMPP connection
            JabberID jid = jidList.get( 0 );
            connection = new XmppConnection( jid );

            // Connect to the Jabber server
            connection.connect();

            // Write code here for the assignment's three tasks...
            // See the documentation of the XmppConnection class for details.
            System.out.println();
            System.out.println( "Welcome " + jid.getJabberID() + "/" + jid.getResource() + " ! " );
            
            // Start the XmppReceiver on another thread:
            senderReceiver = new XmppSenderReceiver(connection);
            Thread newThread = new Thread(senderReceiver);
            newThread.start();

            // Begin user interaction:
            
            String currentLine = "";
            String command = "";
    		System.out.println("Type '@help' to see the available commands.");
    		streamReader = new InputStreamReader(System.in);
    		in = new BufferedReader(streamReader);

    		try{
    			currentLine = in.readLine();
    			command = getWordAtIndex(0, currentLine);
    			
    			while(!command.equals("@end")){
    				
    				switch(command){
    					case "@help":
    						displayHelpInformation();
    						break;
    					case "@roster":
    						displayContactList();
    						break;
    					case "@chat":
    						String receiverEmail = getWordAtIndex(1, currentLine);
    						beginChattingSession(receiverEmail);
    						break;
    					case "@end":
    						// The while loop will terminate
    						break;
    					default:
    						System.out.println("Invalid command. Type Type '@help' to see the available commands.");
    						break;
    				}
    				
    				currentLine = in.readLine();
        			command = getWordAtIndex(0, currentLine);
    			}
    		} catch(IOException e){
                System.err.println( "Encountered exception during user interaction" );
                e.printStackTrace();
    		}
            
            System.out.println("Exited. Hope you had fun!");
            
        }
        catch ( Exception e ) {
            System.err.println( "Encountered exception in main method" );
            e.printStackTrace();

            // If there is any exception, it gets thrown up here to main()
            //  (or the run() method of your thread, if you use a thread).
            // In Task 2, you need to re-connect to the server, instead of
            //  simply quitting.
        }
        finally {
            // Close the connection
            try {
                connection.close();
            }
            catch ( Exception e ) {
                // Ignore
            }
        }
    }
    
    /** Show help */
    private static void displayHelpInformation(){
    	System.out.println("@roster - Gets the roster list");
    	System.out.println("@chat <friend_jabber_id> - This ends any ongoing chat session, and starts a new chat session with a friend with specified Jabber ID.");
    	System.out.println("@end - End any ongoing chat");
    	System.out.println("@help - Display this help menu");
    }
    
    /** Retrieve and display contact list */
    private static void displayContactList(){
    	try {
			senderReceiver.sendRoasterRequest();
		} catch (Exception e) {
			System.out.println("Error occured when sending request for contact list");
			e.printStackTrace();
		}
    }

    
    /** Chat */
    private static void beginChattingSession (String receiver)throws IOException{
    	System.out.println("Start chatting with " + receiver);
    	
		String currentLine = in.readLine();
		String command = getWordAtIndex(0, currentLine);
		while (!command.equals("@end")){
			if (command.equals("@chat")){
				String receiverEmail = getWordAtIndex(1, currentLine);
				beginChattingSession(receiverEmail);
				return;
			}
			senderReceiver.sendMessageToClient(currentLine, receiver);
			
			currentLine = in.readLine();
			command = getWordAtIndex(0, currentLine);
		}
		
		System.out.println("Ended chatting with " + receiver);
    }
    
    /** Get the first word of a string. Words are seperated by space */
    private static String getWordAtIndex(int index, String string){
    	String arr[] = string.split(" ");
    	return arr[index];    	
    }

    /** Helper method that gets the list of Jabber IDs specified as args. */
    private static List <JabberID> getJidList( String[] args ) {

        // Get the list of Jabber IDs
        List <JabberID> jidList = new ArrayList <JabberID>();
        for ( int i = 0 ; i < args.length ; i += 4 ) {

            // Try to convert the port number to int
            int port;
            try {
                port = Integer.parseInt( args[ i + 3 ] );
            }
            catch ( NumberFormatException e ) {
                throw new IllegalArgumentException( "Invalid port: Not a number" , e );
            }

            // Add the Jabber ID to the list
            jidList.add( new JabberID( args[i] ,      // Jabber ID: username@domain
                                       args[i + 1] ,  // Password
                                       args[i + 2] ,  // Server name
                                       port ) );      // Server port
        }

        return jidList;
    }

}
