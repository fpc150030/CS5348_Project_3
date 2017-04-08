/**
 * The server thread (ClientWorker) will:
	1)	Accept and process requests from the client.
	2)	Add the user’s name to the list of known users.
	3)	Provide mutual exclusion protection for the data structure that stores the messages.
	4)	Send only the minimal data needed to the client, not the menu or other UI text.

 */

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
    	  SocketServer.mutex.acquire();
    	  // if user name exists in hashmap, assume this is repeated login?
    	  // remove from disconnected list
    	  if (!SocketServer.users.containsKey(name)) {
    		  SocketServer.users.put(name, null);
    	  } else {
    		  SocketServer.disconnected.remove(name);
    	  }
    	  SocketServer.mutex.release();
      } 
      catch (IOException e) {
    	  System.out.println("Read failed");
    	  System.exit(-1);
      } catch (InterruptedException e) {
		  System.out.println("mutex acquire failed");
		  System.exit(-1);
	}

      while (!line.equals("7") ) {
    	  try {
        	  // Receive text from client
        	  line = in.readLine();
        	  switch (line) {
        	  case "1":			// return comma delimited string of usernames
        		  Set<String> userKeys = SocketServer.users.keySet();
        		  List<String> list=new ArrayList<String>(userKeys);
        		  Collections.sort(list);
        		  StringBuilder b = new StringBuilder();
        		  for (String key: list) {
        			  b.append(key);
        			  b.append(","); 
        		  }
        		  out.println(b.toString());
        		  break;
        	  case "2":			// return comma delimited string of usernames iff not in disconnected
				  Set<String> keys = SocketServer.users.keySet();
				  List<String> all=new ArrayList<String>(keys);
				  List<String> connected=new ArrayList<String>();
				  for(String elm: all){
					  if (!SocketServer.disconnected.contains(elm)) {
						  connected.add(elm);
					  }
				  }
				   Collections.sort(connected);
				   StringBuilder c = new StringBuilder();
				   for(String key: connected) {
					   c.append(key);
					   c.append(",");
				   }
				   out.println(c.toString());
				   break;
        	  case "3":
        	  case "4":
        	  case "5":
        	  case "6":
        	  case "7":
				  /* break; */
        	  }
        	  // Send response back to client
        	  //line = "Hi " + line;
        	  //out.println(line);
          } 
          catch (IOException e) {
        	  System.out.println("Read failed");
        	  System.exit(-1);
          }
      }
      System.out.println("Server thread for " + name + " exiting...");
      try {
		SocketServer.mutex.acquire();
	} catch (InterruptedException e1) {
		System.out.println("unable to acquire mutex");
	}
	  // add username to 'disconnected'
	  SocketServer.disconnected.add(name) ;
	  SocketServer.mutex.release();
      try {
    	  client.close();
      } 
      catch (IOException e) {
    	  System.out.println("Close failed");
    	  System.exit(-1);
      }
   }
}