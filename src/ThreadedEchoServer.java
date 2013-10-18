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
            String fileName = in.nextLine();
            System.out.println("File name: " + fileName + ".txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName.trim() + ".txt"));
            
            boolean done = false;
            boolean first = true;
            while (!done && in.hasNextLine())
              {  
                 String line = in.nextLine();   
                 
                 // Trim off the first character of subsequent lines:
//                 if (!first){
//                	 line = line.substring(1);
//                 }else{
//                	 first = false;
//                 }
                 System.out.println("Received: " + line);   
                 out.write(line);
                 out.newLine();
                 if (line.trim().equals("BYE"))
                    done = true;
              }
            out.close();
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

