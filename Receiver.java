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
		try {
			byte[] data = new byte[1000];
			byte[] chunkcheck = new byte[16];
			byte[] fileSum = new byte[16];
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			BufferedInputStream buf = new BufferedInputStream(socket.getInputStream());
			
			//read in entire file checksum
			buf.read(fileSum, 0, fileSum.length);
			
			//read in data portion of the packet
			int bytes = buf.read(data);
			while(bytes != -1){
				
				//read in checksum of the packet
				buf.read(chunkcheck);
				
				//decrypt both files
				data = XOREncoding.decodeByte(data, "key.txt");
				chunkcheck = XOREncoding.decodeByte(chunkcheck, "key.txt");
				
				//compare data checksum with packet checksum
				byte[] compare = saltMD5.computeMD5(data);	
				for(int i = 0; i < chunkcheck.length; i++){
					if(chunkcheck[i] != compare[i]){
		    				System.out.println("Failed");
		    				sendText("Transmission = Failed");
		    				return null;
		    		}
		    	}

				baos.write(data);
				bytes = buf.read(data, 0, data.length);
			}
			byte[] f = baos.toByteArray();
			
			FilePacket fp = new FilePacket(f, fileSum);
			
			buf.close();
			return fp;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
