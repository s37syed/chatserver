import java.io.*;
import java.util.*;
import java.net.*;

/**
 * Name: Shahnawaz Syed
 * CPS706 Computer Networking Final Project - Chatroom Client
 * Date: 01/12/2013
 */

public class P2PClient implements Runnable 
{
  private static Socket clientSocket = null;
  private static PrintStream inFromServer = null;
  private static BufferedReader outToServer = null;
  private static BufferedReader inputLine = null;
  public static String directoryServerIp = null;
  public static String name = null;
  private static boolean closed = false;

  public static void main(String[] args) throws Exception 
  {
    int portNumber = 1025;
    String host;
    Scanner in = new Scanner(System.in);
    String[] clients;
    String s;
    
    System.out.println("Please enter a host IP: ");
    host = in.nextLine();    
    try 
    {
      clientSocket = new Socket(host, portNumber);
      inputLine = new BufferedReader(new InputStreamReader(System.in));
      inFromServer = new PrintStream(clientSocket.getOutputStream());
      outToServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    } 
    catch (IOException e) 
    {
      System.err.println("Could not connect to " + host);
    }
    if (clientSocket != null && inFromServer != null && outToServer != null) 
    {
      try 
      {
        new Thread(new P2PClient()).start();
        while (!closed) 
        {
          inFromServer.println(inputLine.readLine().trim());
        }
        inFromServer.close();
        outToServer.close();
        clientSocket.close();
      } 
      catch (IOException e) 
      {
        System.err.println("IOException:  " + e);
      }
    }
  }
  public void run() 
  {
    String serverMessage;
    try 
    {
      while ((serverMessage = outToServer.readLine()) != null) 
      {
        System.out.println(serverMessage);
      }
      closed = true;
    } 
    catch (IOException e) 
    {
      System.err.println("IOException:  " + e);
    }
  }
}
