/**
   @author Cay Horstmann
   @version 1.20 2004-08-03
*/

import java.io.*;
import java.net.*;
import java.util.*;

/**
   This program implements a multithreaded server that listens to port 8189 and echoes back 
   all client input.
   
   Use telnet to test the echo server:
    <br> $ javac ThreadedEchoServer.java
    <br> $ java ThreadedEchoServer
    <br> $ telnet localhost 8189
*/
public class ThreadedEchoServer
{  
   public static void main(String[] args )
   {  
      try
      {  
         int i = 1;
         ServerSocket s = new ServerSocket(9119);

         while (true)
         {  
            Socket incoming = s.accept();
            System.out.println("Spawning " + i);
            Runnable r = new ThreadedEchoHandler(incoming, i);
            Thread t = new Thread(r);
            t.start();
            i++;
         }
      }
      catch (IOException e)
      {  
         e.printStackTrace();
      }
   }
}

/**
   This class handles the client input for one server socket connection. 
*/
class ThreadedEchoHandler implements Runnable
{ 
   /**
      Constructs a handler.
      @param i the incoming socket
      @param c the counter for the handlers (used in prompts)
   */
   public ThreadedEchoHandler(Socket i, int c)
   { 
      incoming = i; counter = c; 
   }

   public void run()
   {  
      try
      {  
         try
         {
            InputStream inStream = incoming.getInputStream();
            OutputStream outStream = incoming.getOutputStream();
            
            Scanner in = new Scanner(inStream);         
            PrintWriter out = new PrintWriter(outStream, true /* autoFlush */);
            
            out.println( "Hello! Enter BYE to exit." );
            
            // echo client input
            boolean done = false;
            while (!done && in.hasNextLine())
            {  
               String line = in.nextLine();            
               out.println("Echo: " + line);            
               if (line.trim().equals("BYE"))
                  done = true;
            }
         }
         finally
         {
            incoming.close();
         }
      }
      catch (IOException e)
      {  
         e.printStackTrace();
      }
   }

   private Socket incoming;
   private int counter;
}

