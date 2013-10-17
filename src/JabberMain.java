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
            
            // Task 1:
            Socket socket = connection.getSocket();
            BufferedReader reader = connection.getReader();
            BufferedWriter writer = connection.getWriter();
            XMLStreamReader parser = connection.getParser();
            
            writer.write("Hello! Zhixing");
            String line = reader.readLine();
            System.out.println("Read from socket: " + line);
            System.out.println("Finished Chatting. Hope you had fun!");
            
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

    /** XMPP connection. */
    private static XmppConnection connection = null;
}
