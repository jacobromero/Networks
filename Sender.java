import java.io.*;
import java.net.*;

public class Sender implements Runnable{
	private String url = "localhost";
	private int port  = 12345;
    private Socket socket = null;
    
    private PrintWriter pw = null;
    
    //TODO Discuss moving settings to another class for accessing.
    //receiver settings
    public boolean tansmissionSuccess = true;
    public boolean base64Encode = false;
    public boolean failIntentionally = false;
    
    
	public void searchForHost(){
		//need to change so the user can change port number
		while(port == -1){
			System.out.println("Please set a port to listen on.");
		}
		
		boolean flag = true;
		while(flag){
			try {
				socket = new Socket(url, port);
				socket.setSoTimeout(0);
				
				System.out.println("Connected.");
				
				//open printwriter for writing to socket
				pw = new PrintWriter(socket.getOutputStream(), true);
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
			  
			  //if we want to fail the transmission intentionally
			  if(failIntentionally){
				  chunkChecksum[0] = (byte) (chunkChecksum[0] + 1);
				  
				  //only fail one checksum, can fail all of them if we want
				  failIntentionally = false;
			  }
			  
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
  
  	public void sendText(String toSend){
		pw.println(toSend);	
  	}
  	
	public void setServerUrl(String url){
		this.url = url;
	}
	
	public void setPort(int portNum){
		this.port = portNum;
	}

	//async socket reader
	public void run(){
		try{
			BufferedReader readText = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			String clientText = "";

			while(true){
				clientText = readText.readLine();
				
				//change this preform an action if a certian string is read
				//ie if it reads "Encryption = True" set some boolean Encryption to true
				if(clientText != null)
					System.out.println(clientText);
				else
					return;
			}			
		}
		catch(IOException ioe){
			ioe.getMessage();
		}
	}
	
	public void shutDown(){		
		try {
			System.out.println("\nDisconnecting...");
			socket.close();
			System.out.println("Done.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
} 