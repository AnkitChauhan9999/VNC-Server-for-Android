import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

public class MainActivity extends Activity {

 TextView info, infoip, msg;
 String message = "";
 ServerSocket serverSocket;

 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_main);
  info = (TextView) findViewById(R.id.info);//used to print the port number on device screen
  infoip = (TextView) findViewById(R.id.infoip);//used to print the ip address of the device
  msg = (TextView) findViewById(R.id.msg);//used to print the clinets connected to the device
  
  infoip.setText(getIpAddress());//print the ip address
//always use thread to implement socket otherwise it will give ' "app name" stooped working' error 
  Thread socketServerThread = new Thread(new SocketServerThread());
  socketServerThread.start();
 }

 //On exit from the activity close the serverSockte
 @Override
 protected void onDestroy() {
  super.onDestroy();

  if (serverSocket != null) {
   try {
    serverSocket.close();
   } catch (IOException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
   }
  }
 }

 //Class that defines the properties of the thread started above i.e. how the thread will work
 private class SocketServerThread extends Thread {

  static final int SocketServerPORT = 8080;
  int count = 0;//current number of clients connected set to zero

  @Override
  public void run() {
   try {
    serverSocket = new ServerSocket(SocketServerPORT);
    MainActivity.this.runOnUiThread(new Runnable() {
     @Override
     public void run() {
      info.setText("I'm waiting here: "+ serverSocket.getLocalPort());
     }
    });
 /*getLocalPort() Gets the local port of this server socket or -1 if the socket is not bound.
    If the socket has ever been bound this method will return the local port it was bound to,
    even after it has been closed.
    returns the local port this server is listening on.
    */
    while (true) {
     Socket socket = serverSocket.accept();
     count++;
     message += "#" + count + " from " + socket.getInetAddress()+ ":" + socket.getPort() + "\n";
     //get the ip address and port of the client
     MainActivity.this.runOnUiThread(new Runnable() {
      @Override
      public void run() {
       msg.setText(message);
      }
     });

     SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
       socket, count);
     socketServerReplyThread.run();

    }
   } catch (IOException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
   }
  }

 }

 private class SocketServerReplyThread extends Thread {

  private Socket hostThreadSocket;
  int cnt;

  SocketServerReplyThread(Socket socket, int c) {
   hostThreadSocket = socket;
   cnt = c;
  }
/*runOnUiThread() :
 *Runs the specified action on the UI thread. 
 **/
  @Override
  public void run() {
   OutputStream outputStream;
   String msgReply = "Hello from Android, you are #" + cnt;

   try {
    outputStream = hostThreadSocket.getOutputStream();
             PrintStream printStream = new PrintStream(outputStream);
             printStream.print(msgReply);
             printStream.close();

    message += "replayed: " + msgReply + "\n";

    MainActivity.this.runOnUiThread(new Runnable() {

     @Override
     public void run() {
      msg.setText(message);
     }
    });

   } catch (IOException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
    message += "Something wrong! " + e.toString() + "\n";
   }

   MainActivity.this.runOnUiThread(new Runnable() {

    @Override
    public void run() {
     msg.setText(message);
    }
   });
  }

 }
//method for getting ip address

 private String getIpAddress() {
  String ip = "";
  try {
  /* NetworkInterface : 
  This class is used to represent a network interface of the local device.
  An interface is defined by its address and a platform dependent name.
  The class provides methods to get all information about the available interfaces of the system or
  to identify the local interface of a joined multicast group. 
  */
  /*
	InetAddress : This class stores Internet Protocol (IP) address.
   	This can be either an IPv4 address or an IPv6 address
  */
   Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
	 //getNetworkInterfaces() returns all the interfaces on the device, return NuLL if no networkInterface is found
   while (enumNetworkInterfaces.hasMoreElements()) {
   //hasMoreElements does the tokenization of string (here enumNetworkInterfaces) 
   //hasMoreElements() returns true if the next token is available
    NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
	  //nextElement() return the next element of the enum
    Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
	  //getInetAddress() Returns an enumeration of the addresses bound to this network interface. 
    while (enumInetAddress.hasMoreElements()) {
     InetAddress inetAddress = enumInetAddress.nextElement();

     if (inetAddress.isSiteLocalAddress()) {//isSiteLocalAddress() Returns whether this address is a site-local address or not. 
      ip += "SiteLocalAddress: " + inetAddress.getHostAddress() + "\n";//on normal working of app this line is printed
     }
     //getHostAddress() Returns the numeric representation of this IP address (such as "127.0.0.1"). 
    }

   }

  } catch (SocketException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
   ip += "Something Wrong! " + e.toString() + "\n";
  }

  return ip;
 }
}