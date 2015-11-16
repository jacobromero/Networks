import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Scanner;

public class Sender implements Runnable{
	private String url = "localhost";
	private int port  = 12345;
    private Socket socket = null;
    
	public void searchForHost(){
		//need to change so the user can change port number
		while(port == -1){
			System.out.println("Please set a port to listen on.");
		}
		
		boolean flag = true;
		while(flag){
			try {
				socket = new Socket(url, port);
				
				//10 second timeout
				socket.setSoTimeout(0);
				
				System.out.println("Connected.");
				flag = false;
			} catch (ConnectException e) {
				//continue to search for server with specified ip and port open
				System.out.println("Continuing to listen...");
			}
			catch(Exception e){
				System.out.println("Something unexpected happened... \n" + e.getMessage());
			}
		}
	}
  
  public void closeConnection(){
	  try {
		System.out.println("closing connection");
		socket.close();
	} catch (IOException e) {
		System.out.println("Something happened: " + e);
	}
  }
 

  //used this method to sendData encrypted bytes to receiver.
  public void sendByte(FilePacket sendData) {
	  try{
		  //create byte array the length of the file we are sending
		  //can specify the length of the array to sendData in chunks, here 1000 bytes = 1kb chunks
		  byte[] data = new byte[1000];
		  			  
		  ByteArrayInputStream bis = new ByteArrayInputStream(sendData.fileArray);
		  ByteArrayOutputStream baos = new ByteArrayOutputStream();
		  
		  OutputStream os = socket.getOutputStream();
		  
		  //write the overall file checksum into the stream, to be read by the receiver. (this may or may not be corrupted on sender side
		  os.write(sendData.fileChecksum, 0, sendData.fileChecksum.length);
		  
		  byte[] chunkChecksum;
		  int bytes = bis.read(data, 0, data.length);
		  while(bytes != -1){
			  //chunkChecksum sum the 1kb chunk 'data'
			  chunkChecksum = saltMD5.computeMD5(data);
			  
			  //write both data and data checksum into a single array(chunk)
			  baos.write(data);
			  baos.write(chunkChecksum);
			  byte[] toSend = baos.toByteArray();
			  
			  
			  //Encrypt the chunk(data and hash)
			  toSend = XOREncoding.encode(toSend, "key.txt");
			  
			  //write the chunk to the output stream
			  os.write(toSend);
			  
			  //clear data on the byte output stream
			  baos.flush();
			  os.flush();

			  bytes = bis.read(data, 0, data.length);
		  }
		  
		  os.close();
		  bis.close();
		  return;
	    }
	  	//cannot find host
	    catch(UnknownHostException unhe){
	      System.out.println("UnknownHostException: " + unhe.getMessage());
	    }
	  	//connection interrupted/timeout
	    catch(InterruptedIOException intioe){
	      System.out.println("Timeout while attempting to establish socket connection.");
	    }
	  	//input/output error
	  	catch(IOException ioe){
	      System.out.println();
	    }
	    finally{
	      try{
	        socket.close();
	      }catch(IOException ioe){
	        System.out.println("IOException: " + ioe.getMessage());
	      }
	    }
  	}
  
  	public void sendText(){
  		Scanner kb = new Scanner(System.in);
  		
  		try{
  			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
  			
  			String userInput = "";
  			
  			while(userInput.compareToIgnoreCase("disconnect") != 0){
  				System.out.print("Message to send: ");
  				userInput = kb.nextLine();
  				
  				pw.println(userInput);
  			}
  			
//  			pw.println("");
  			pw.close();
  			kb.close();
  		}
  		catch(IOException ioe){
  			System.out.println("Input/Output Error: " + ioe.getMessage());
  		}
  	}
  	
	public void setServerUrl(String url){
		this.url = url;
	}
	
	public void setPort(int portNum){
		this.port = portNum;
	}

	public void run(){
		try{
			BufferedReader readData = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			String clientText = "";
			while(clientText.compareToIgnoreCase("disconnect") != 0 && clientText != null){
				clientText = readData.readLine();
			
				System.out.println("\nMessage from Receiver: " + clientText);
				System.out.print("Message to send: ");
			}			
		}
		catch(IOException ioe){
			ioe.getMessage();
		}
	}
} 