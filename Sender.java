import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Queue;


public class Sender{
	protected String url;
	protected int port;
    private Socket socket = null;
    private PrintWriter pw = null;
    
    public boolean failIntentionally = false;
    public boolean read = true;
    public Queue<String> messages = new LinkedList<String>();
    
    //Constructors
    public Sender(){
    	this.url = "localhost";
    	this.port= 12345;
    }
    
    public Sender(int port){
    	this.port = port;
    }
    
    public Sender(int port, String ip){
    	this.port = port;
    	this.url = ip;
    }
    
    public Sender(String ip){
    	this.url = ip;
    }
    //End Constructors
    
	public boolean searchForClients(){
		//need to change so the user can change port number
		while(port == -1){
			System.out.println("Please set a port to listen on.");
		}
		
		boolean flag = true;
		while(flag){
			try {
				socket = new Socket(url, port);
				socket.setSoTimeout(0);
				
				//open printwriter for writing to socket
				pw = new PrintWriter(socket.getOutputStream(), true);
				System.out.println("Connected.");
				flag = false;
				
				//stop searching for clients when connection is made.
				return flag;
			} catch (ConnectException e) {
				//continue to search for server with specified ip and port open
				System.out.println("Continuing to listen...");
			}
			catch(Exception e){
				System.out.println("Something unexpected happened... \n" + e.getMessage());
			}
		}
		return false;
	}

  //used this method to sendData encrypted bytes to receiver.
  public void sendData(FilePacket sendData, String keyname, boolean asciiArmor ) {
	  try{
		  //create byte array the length of the file we are sending
		  //can specify the length of the array to sendData in chunks, here 1000 bytes = 1kb chunks
		  byte[] data = new byte[20000];
		  
		  //Streams for reading/writing
		  ByteArrayInputStream bis = new ByteArrayInputStream(sendData.fileArray);
		  OutputStream os = socket.getOutputStream();
		  
		  //write the overall file checksum into the stream, to be read by the receiver. (this may or may not be corrupted on sender side
		  os.write(sendData.fileChecksum, 0, sendData.fileChecksum.length);
		  
		  byte[] chunkChecksum = new byte[16];
		  int bytes = bis.read(data, 0, data.length);
		  
		  int count = 0;
		  while(bytes != -1){
			  System.out.println("Sending Chunk - " + ++count);
			  //chunkChecksum sum the 1kb chunk 'data'
			  chunkChecksum = saltMD5.computeMD5(data);
			  
			  //if we want to fail the transmission intentionally
			  if(failIntentionally){
				  chunkChecksum[0] = (byte) (chunkChecksum[0] + 1);
				  
				  //only fail one checksum, can fail all of them if we want
				  failIntentionally = false;
			  }
			  
			  //TODO implement ascii armoring
			  if(asciiArmor){
				  
			  }
			  
			  //write the chunk to the output stream, encrypted
			  os.write(XOREncoding.encode(data, keyname));
			  os.write(XOREncoding.encode(chunkChecksum, keyname));
			  
			  //clear data on the byte output stream
			  os.flush();
			  
			  readText();
			  while (messages.remove().equals("Failed")){
				//chunkChecksum sum the 1kb chunk 'data', re-checksummed for debug purposes.
				chunkChecksum = saltMD5.computeMD5(data);
				
				os.write(XOREncoding.encode(data, keyname));
				os.write(XOREncoding.encode(chunkChecksum, keyname));
				os.flush();
				
				readText();
			  }

			  bytes = bis.read(data, 0, data.length);
		  }
		  
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
  	
  	//send text to other side
  	public void sendText(String toSend){
		pw.println(toSend);
  	}
	
  	//method to read text form the other side
	public void readText(){
		//keep reading until other side passes sentinel string
		read = true;
		try{
			BufferedReader readText = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			String clientText = "";
			while(!clientText.equals("Finished = True")){
				//keep looping until socket can be read from
				while(!readText.ready());
				
				clientText = readText.readLine();
				
				if(clientText != null && !clientText.equals("Finished = True")){
					//add messages onto queue for main program to handle.
					messages.add(clientText);
				}
				//if the string is null just keep reading
				else
					continue;
			}
		}
		catch(IOException ioe){
			ioe.getMessage();
		}
	}
	
	//url mutator
	public void setServerUrl(String url){
		this.url = url;
	}
	
	//port mutator
	public void setPort(int portNum){
		this.port = portNum;
	}
	
	//shutdown socket
		public boolean shutDown(){		
			try {
				socket.close();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
} 