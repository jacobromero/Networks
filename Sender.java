import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Calendar;

public class Sender {
	private int port  = 12345;
	private ServerSocket server = null;
    private Socket socket = null;
    
	public void runServer(){
		//need to change so the user can change port number
		while(port == -1){
			System.out.println("Please set a port to listen on.");
		}
		
		try {
			server = new ServerSocket(port);
			socket = server.accept();
			
			//read in user name & password here
			//call validate to make sure they have access
			//exit if they are invalid
			
			//can while loop to allow change mid runtime?
				//either choose to sendData text or sendData a file here
				//sendFile(filePath)
				//sendText();
			//after while close server and exit
//			System.out.println("Waiting for connections");
//			closeConnection();
//			System.out.println("Done");
		} catch (IOException e) {
			e.printStackTrace();
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
  
  public void setPort(int portNum){
	  port = portNum;
  }

  //used this method to sendData encrypted bytes to receiver.
  //TODO chnge b, filesum into one object, see filePacket
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
	    catch(UnknownHostException unhe){
	      System.out.println("UnknownHostException: " + unhe.getMessage());
	    }
	    catch(InterruptedIOException intioe){
	      System.out.println("Timeout while attempting to establish socket connection.");
	    }catch(IOException ioe){
	      System.out.println();
	    }
	    finally{
	      try{
	        socket.close();
	        server.close();
	      }catch(IOException ioe){
	        System.out.println("IOException: " + ioe.getMessage());
	      }
	    }
  	}
} 