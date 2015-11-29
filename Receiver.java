import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class Receiver{
	protected int port;
	private ServerSocket server = null;
	private Socket socket = null;
	private PrintWriter pw = null;
	protected int fileLength;
	
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
	//End Constructors
	
	//set up connection to the server
	public void listen(){		
		//need to change so the user can change port number
		while(port == -1){
			System.out.println("Please enter a valid port");
		}
		
		boolean flag = true;
		while(flag){
			try {
				server = new ServerSocket(port);
				socket = server.accept();
				
				//open print writer for writing to socket
				pw = new PrintWriter(socket.getOutputStream(), true);
				System.out.println("Connected.");
				//stop listening for connections
				flag = false;
			} catch (ConnectException e) {
				//continue to search for server with specified ip and port open
				System.out.println("Continuing to listen...");
			}
			catch(Exception e){
				System.out.println("Something unexpected happened... \n" + e.getMessage());
				flag = false;
			}

		}
	}
	
	//use this method to received encrypted/ascii armored bytes, then pass them up to be decrypted
	public FilePacket receiveData( String keyname, boolean asciiArmor ) {
		try {
			//buffers for data, chunk checksum, and file checksum
			byte[] data = new byte[20000];
			byte[] chunkcheck = new byte[16];
			byte[] fileSum = new byte[16];
			
			//streams for reading/writing to
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			BufferedInputStream buf = new BufferedInputStream(socket.getInputStream());
			
			//read in entire file checksum
			buf.read(fileSum, 0, fileSum.length);

			//read in data portion of the packet
			int bytes = buf.read(data);
			int count = 0;
			while(bytes != -1){
				System.out.println("Receiving Chunk - " + ++count);
				//read in checksum of the packet
				buf.read(chunkcheck);
				
				//TODO implement ascii armoring
				if(asciiArmor){
					  
				}
				
				//decrypt both files
				data = XOREncoding.decodeByte(data, keyname);
				chunkcheck = XOREncoding.decodeByte(chunkcheck, keyname);
				
				//assume check is failed when sent.
				boolean failed = true;
				while(failed){
					//compare data checksum with packet checksum
					byte[] compare = saltMD5.computeMD5(data);	
					for(int i = 0; i < chunkcheck.length; i++){
						//check each byte to see if it matches
						if(chunkcheck[i] != compare[i]){
							//if it doesn't match tell the other side it failed, and it needs to resend.
							sendText("Failed");
							sendText("Finished = True");
							
							failed = true;
							
							//immediately break since we need the chunk again
							break;
						}
						//otherwise chunk passes and continue on
						else{
							failed = false;
						}
					}
					
					//if we fail repeat process of reading in chunk and comparing
					if(failed){
						buf.read(data, 0, data.length);
						
						//read in checksum of the packet
						buf.read(chunkcheck);
						
						//decrypt both files
						data = XOREncoding.decodeByte(data, keyname);
						chunkcheck = XOREncoding.decodeByte(chunkcheck, keyname);
					}
				}
				System.out.println("here");
				//tell otherside chunk passed
				sendText("Passed");
				sendText("Finished = True");
				
				//write chunk on to byte array stream
				baos.write(data);
				
				//read next chunk
				bytes = buf.read(data, 0, data.length);
			}
			byte[] f = baos.toByteArray();
			
			//trim the file array, to remove any extra null bytes if the packet was larger than the file size.
			f = Arrays.copyOf(f, fileLength);
			
			//create packet to pass up
			FilePacket fp = new FilePacket(f, fileSum);
			
			//close streams
			baos.close();
			buf.close();
			
			return fp;			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	//send text to 
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
	
	//shutdown socket
	public boolean shutDown(){		
		try {
			socket.close();
			server.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
