import java.io.*;
import java.util.*;
import java.net.*;

/**
 * Name: Shahnawaz Syed
 * CPS706 Computer Networking Final Project - Chatroom Server
 * Date: 01/12/2013
 */

public class P2PServer 
{
  private static ServerSocket serverSocket = null;
  private static Socket clientSocket = null;
  private static final int maxClientsCount = 3;
  private static final ChatHandler[] threads = new ChatHandler[maxClientsCount];
  private static String roomName = null;
  public static void main(String args[]) throws Exception 
  {
    int portNumber = 1025;
    try 
    {
      serverSocket = new ServerSocket(portNumber);
      System.out.println("Please enter a name for the chatroom.");
      Scanner scan = new Scanner(System.in);
      roomName = scan.nextLine();
      System.out.println(roomName + "'s socket was created on port " + portNumber);
    }
    catch (IOException e) 
    {
      System.out.println(e);
    }
    while (true) 
    {
      try 
      {
        clientSocket = serverSocket.accept();
        int currentClients = 0;
        for (currentClients = 0; currentClients < maxClientsCount; currentClients++) 
        {
          if (threads[currentClients] == null) 
          {
            (threads[currentClients] = new ChatHandler(clientSocket, threads, roomName)).start();
            break;
          }
        }
        if (currentClients == maxClientsCount) 
        {
          PrintStream os = new PrintStream(clientSocket.getOutputStream());
          os.println("Server has reached maximum number of clients. Press enter to continue.");
          os.close();
          clientSocket.close();
        }
      } 
      catch (IOException e) 
      {
        System.out.println(e);
      }
    }
  }
}
