import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Queue;

public class Receiver implements Runnable{
	protected int port;
	private ServerSocket server = null;
	private Socket socket = null;
	private PrintWriter pw = null;
	
	public Queue<String> messages = new LinkedList<String>();
	public boolean read = true;
	
	//Constructors
	public Receiver(){
		port = 12345;
	}
	public Receiver(int p){
		this.port = p;
	}
	
	public void setPort(int portNum){
		this.port = portNum;
	}
	
	//set up connection to the server
	public void listen(){		
		//need to change so the user can change port number
		while(port == -1){
			System.out.println("Please enter a valid port");
		}
		
		try {
			server = new ServerSocket(port);
			socket = server.accept();
			
			//open printwriter for writing to socket
			pw = new PrintWriter(socket.getOutputStream(), true);
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
	public FilePacket receiveData(){
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
//	      in.read(fileSum, 0, fileSum.length);
	      
	      //read one packet from the network
	      int bytesRead = in.read(rawData, 0, rawData.length);
	      while(bytesRead != -1){	    	  
	    		  rawData = XOREncoding.decodeByte(rawData, "key.txt");
	    		  
	    		  ByteArrayInputStream bis = new ByteArrayInputStream(rawData);
		    	  
	    		  
	    		  bis.read(data, 0, bytesRead - 16);
	    		  bis.read(checksum, 0, 16);
		    	 
	    		  //compute the md5 of the data portion of the packet to verify integrity
		    	  byte[] compare = saltMD5.computeMD5(data);	
		    	  
		    	  //compare data checksum with packet checksum
		    	  for(int i = 0; i < checksum.length; i++){
		    		  if(checksum[i] != compare[i]){
		    			  System.out.println("Failed");
		    			  pw.println("Transmission = Failed");
		    			  return null;
		    		  }
		    	  }
		    	 
		    	  baos.write(data);
		    	  bytesRead = in.read(rawData, 0, rawData.length);
	      }
	      
	      byte[] fileData = baos.toByteArray();
	      
	      baos.close();
	      
	      FilePacket receievedData = new FilePacket(fileData, fileSum);
	      System.out.println("Received file");
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
	
	public void sendText(String toSend){
			pw.println(toSend);
  	}
	
	//async socket reader
	public void run(){
		try{
			BufferedReader readText = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			String clientText = "";

			while(read){
				clientText = readText.readLine();
				
				//change this preform an action if a certian string is read
				//ie if it reads "Encryption = True" set some boolean Encryption to true
				if(clientText != null)
//					System.out.println(clientText);
					messages.add(clientText);
				else
					return;
			}			
		}
		catch(IOException ioe){
			ioe.getMessage();
		}
	}
	
	public void stopReadingText(){
		read = false;
	}
	
	public void restartReadingText(){
		read = true;
	}
	
	public void shutDown(){		
		try {
			System.out.println("\nDisconnecting...");
			socket.close();
			server.close();
			System.out.println("Done.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
