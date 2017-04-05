/**
 * The server thread (ClientWorker) will:
	1)	Accept and process requests from the client.
	2)	Add the user’s name to the list of known users.
	3)	Provide mutual exclusion protection for the data structure that stores the messages.
	4)	Send only the minimal data needed to the client, not the menu or other UI text.

 */

import java.io.*;
import java.net.*;

class ClientWorker implements Runnable 
{
   private Socket client;
   private String name = "";
   
   ClientWorker(Socket client) {
      this.client = client;
   }

   public void run() {
      String line = "";
      BufferedReader in = null;
      PrintWriter out = null;
      try {
    	  in = new BufferedReader
    			  (new InputStreamReader(client.getInputStream()));
    	  out = new PrintWriter(client.getOutputStream(), true);
      } 
      catch (IOException e) {
    	  System.out.println("in or out failed");
    	  System.exit(-1);
      }

      try {
    	  // Receive text from client
    	  line = in.readLine();
    	  this.name = line;
    	  // Send response back to client
    	  line = "Hi " + line;
    	  out.println(line);
    	  //  update user cache
      } 
      catch (IOException e) {
    	  System.out.println("Read failed");
    	  System.exit(-1);
      }

      while (!line.equals("7") ) {
    	  try {
        	  // Receive text from client
        	  line = in.readLine();
    	 
        	  // Send response back to client
        	  //line = "Hi " + line;
        	  out.println(line);
          } 
          catch (IOException e) {
        	  System.out.println("Read failed");
        	  System.exit(-1);
          }
      }
      System.out.println("Server thread for " + name + " exiting...");
      
      try {
    	  client.close();
      } 
      catch (IOException e) {
    	  System.out.println("Close failed");
    	  System.exit(-1);
      }
   }
}