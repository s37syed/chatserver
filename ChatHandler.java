import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;

/**
 * Name: Shahnawaz Syed
 * CPS706 Computer Networking Final Project - Chat handler for Client and Server
 * Date: 01/12/2013
 */

public class ChatHandler extends Thread 
{
  private String clientName = null;
  private BufferedReader inFromClient = null;
  private PrintStream outToClient = null;
  private Socket clientSocket = null;
  private final ChatHandler[] threads;
  private static String roomName = null;
  private int maxClientsCount;
  private double rating;

  public ChatHandler(Socket clientSocket, ChatHandler[] threads, String roomName) 
  {
    this.clientSocket = clientSocket;
    this.threads = threads;
    this.roomName = roomName;
    maxClientsCount = threads.length;
  }
  public void run() 
  {
    int maxClientsCount = this.maxClientsCount;
    ChatHandler[] threads = this.threads;
    try 
    {
      inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      outToClient = new PrintStream(clientSocket.getOutputStream());
      String joinTime;
      String name;
      Calendar cal = Calendar.getInstance();
      cal.getTime();
      SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
      joinTime = sdf.format(cal.getTime());   
      while (true) 
      {
        int count = 0;
        outToClient.println("Enter a username for the chatserver.");
        name = inFromClient.readLine().trim();
        break;
      }
      System.out.println(name + " has entered the chatroom at " + joinTime);
      rating++;
      outToClient.println("**** Welcome to the \"" +roomName+"\" chatroom! To leave enter /quit in a new message.");
      synchronized (this)
      {
        for (int currentClient = 0; currentClient < maxClientsCount; currentClient++) 
        {
          if (threads[currentClient] != null && threads[currentClient] == this) 
          {
            clientName = name;
            break;
          }
        }
        for (int currentClient = 0; currentClient < maxClientsCount; currentClient++) 
        {
          if (threads[currentClient] != null && threads[currentClient] != this) 
          {
            threads[currentClient].outToClient.println("**** " + name + " has entered the chatroom.");
          }
        }
      }
      //while the server inFromClient running
      while (true)
      {
        String message = inFromClient.readLine();
        // Leave the server
	if (message.startsWith("/quit")) 
        {
          break;
        }
	// Check the time you joined the server
        else if (message.startsWith("/time"))
        {
          synchronized (this) 
          {
            if (this != null && this.clientName != null) 
            {
              outToClient.println("<" + name + "> joined at " + joinTime);
            }
          }
      }
      // Check how many users are in the server
      else if (message.startsWith("/query"))
      {
        int numberOfUsers = 0;
        synchronized (this) 
        {
          for (int currentClient = 0; currentClient < maxClientsCount; currentClient++) 
          {
            if (threads[currentClient] != null && threads[currentClient].clientName != null) 
            {
              numberOfUsers++;
            }
          }
        outToClient.println("The current number of users in the chatroom are: " + numberOfUsers);
        }
      }
      else 
      {
        synchronized (this) 
        {
          for (int currentClient = 0; currentClient < maxClientsCount; currentClient++) 
          {
            if (threads[currentClient] != null && threads[currentClient].clientName != null) 
            {
              threads[currentClient].outToClient.println("<" + name + "> " + message);
            }
          }
        }
      }
    }
    synchronized (this) 
    {
      for (int currentClient = 0; currentClient < maxClientsCount; currentClient++) 
      {
        if (threads[currentClient] != null && threads[currentClient] != this && threads[currentClient].clientName != null) 
        {
          threads[currentClient].outToClient.println("**** " + name + " has left the chatroom.");
          rating = (rating-1) * 1.03;
        }
      }
    }

    System.out.println(name + " has left the chatroom.");
    outToClient.println("<Server> You have left the chatroom. Press enter to continue.");
    synchronized (this) 
    {
      for (int currentClient = 0; currentClient < maxClientsCount; currentClient++) 
      {
        if (threads[currentClient] == this) 
        {
          threads[currentClient] = null;
        }
      }
    }
    inFromClient.close();
    outToClient.close();
    clientSocket.close();
    }
    catch (IOException e) 
    {
      System.out.println(e);
    }
  }
}
