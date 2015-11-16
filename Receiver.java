import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Receiver implements Runnable{
	private int port = 12345;
	private ServerSocket server = null;
	private Socket socket = null;
	boolean flag = true;
	
	//set up connection to the server
	public void listen(){		
		//need to change so the user can change port number
		while(port == -1){
			System.out.println("Please enter a valid port");
		}
		try {
			server = new ServerSocket(port);
			socket = server.accept();
		} catch (ConnectException e) {
			//continue to search for server with specified ip and port open
			System.out.println("Continuing to listen...");
		}
		catch(Exception e){
			System.out.println("Something unexpected happened... \n" + e.getMessage());
		}
		
		System.out.println("Connected.");
	}
	
	//use this method to received encrypted/ascii armored bytes, then pass them up to be decrypted
	public FilePacket recieveByteArray(){
		//entire file checksum
		byte[] fileSum = new byte[16];
		
		byte[] rawData = new byte[1016];
		byte[] data = new byte[1000];
		byte[] checksum = new byte[16];

	    try{
	      //input stream for reading in from the network
	      InputStream in = socket.getInputStream();
	      
	      //output stream for writing to the end result
	      ByteArrayOutputStream baos = new ByteArrayOutputStream();
	      
	      //read the entire checksum of the file and save it for later.
	      in.read(fileSum, 0, fileSum.length);
	      
	      //read one packet from the network
	      int bytesRead = in.read(rawData, 0, rawData.length);
	      while(bytesRead != -1){	    	  
	    		  rawData = XOREncoding.decodeByte(rawData, "key.txt");
	    		  
	    		  ByteArrayInputStream bis = new ByteArrayInputStream(rawData);
		    	  
	    		  
	    		  bis.read(data, 0, data.length);
	    		  bis.read(checksum, 0, checksum.length);
		    	  
	    		  //compute the md5 of the data portion of the packet to verify integrity
		    	  byte[] compare = saltMD5.computeMD5(data);	
		    	  
		    	  //compare data checksum with packet checksum
		    	  for(int i = 0; i < checksum.length; i++){
		    		  if(checksum[i] != compare[i]){
		    			  System.out.println("Failed");
		    			  return null;
		    		  }
		    	  }
		    	 
		    	  baos.write(data);
		    	  bytesRead = in.read(rawData, 0, rawData.length);
	      }
	      
	      byte[] fileData = baos.toByteArray();
	      
	      baos.close();
	      
	      FilePacket receievedData = new FilePacket(fileData, fileSum);
	      System.out.println("Reveived file --- Closing connection.");
	      return receievedData; 
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
	      System.out.println("IOException: " + ioe.getMessage());
	    }
	    finally{
	      try{
	        socket.close();
	      }catch(IOException ioe){
	        System.out.println("IOException: " + ioe.getMessage());
	      }
	    }
	    
	    return null;
	}
	
	public void closeConnection(){
		try {
			socket.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void readText(){
		try{
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			String clientText = "";
			while(clientText.compareToIgnoreCase("disconnect") != 0 && clientText != null){
				clientText = input.readLine();
			
				System.out.println(clientText);
			}			
			
			shutDown();
		}
		catch(IOException ioe){
			ioe.getMessage();
		}
		catch(NullPointerException npe){
			System.out.println("Client Disconnected");
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
	
	public void shutDown(){		
		try {
			System.out.println("Disconnecting...");
			server.close();
			System.out.println("Done.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run(){
		try{
			BufferedReader readData = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			String clientText = "";
			while(clientText.compareToIgnoreCase("disconnect") != 0 && clientText != null){
				clientText = readData.readLine();
			
				System.out.println("\nMessage from Sender: " + clientText);
				System.out.print("Message to send: ");
			}			
			
			shutDown();
		}
		catch(IOException ioe){
			ioe.getMessage();
		}
	}
}