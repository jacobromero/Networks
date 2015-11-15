import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Scanner;

public class Receiver {
	private String serverurl = "127.0.0.1";
	private int serverport = 12345;
	private Socket socket = null;
	
	public void connectToServer(){
		if(serverurl.equals("127.0.0.1")){
			System.out.println("WARNING CONNECTION ON LOOPBACK ADDRESS(127.0.0.1)");
		}
		
		//need to change so the user can change port number
		while(serverport == -1){
			System.out.println("Please enter a valid port");
		}
		
		boolean flag = true;
		while(flag){
			try {
				socket = new Socket(serverurl, serverport);
				//10 second timeout
				socket.setSoTimeout(10000);
				
				System.out.println("Connected.");
				flag = false;
			} catch (ConnectException e) {
				System.out.println("Continuing to listen...");
			}
			catch(Exception e){
				System.out.println("Something unexpected happened... \n" + e.getMessage());
			}
		}
	}
	
	public void setServerUrl(String url){
		serverurl = url;
	}
	
	public void setPort(int portNum){
		serverport = portNum;
	}
	
	public void closeConnection(){
		try {
			socket.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
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
	      
//	      System.out.println(Arrays.toString(fileSum));
	      
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
	    catch(UnknownHostException unhe){
	      System.out.println("UnknownHostException: " + unhe.getMessage());
	    }
	    catch(InterruptedIOException intioe){
	      System.out.println("Timeout while attempting to establish socket connection.");
	    }
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

}
