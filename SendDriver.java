import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class SendDriver {

	//NOTES: I decided that multithreading was causing more trouble that it was worth so I have move away from it, to read text from the network, call s.readText()
	//it will continue reading untill it received the string "Finished = True", after that read all message from the queue, and handle them accordingly. Now no need for timing the threads

	//Also note that I now require file length set in the reciever class this is to help me trim the file, since it was giving incorrect checksums without the length.
	public static void main(String[] args) throws IOException, InterruptedException {
		String filePath = "test1.txt";
		
		//search network for host
		Sender s = new Sender();
    	s.searchForClients();  	
    	
    	//read in user name and/or settings
    	s.readText();
    	System.out.println("Done reading");
		
		//read messages from queue and handle them
		while(!s.messages.isEmpty())
			System.out.println(s.messages.remove());
		
		//send file length (important), insert file name here
		Path path = Paths.get(filePath);
    	byte[] data = Files.readAllBytes(path);

    	s.sendText(Integer.toString(data.length));
    	System.out.println(Integer.toString(data.length));
    	s.sendText("Finished = True");
    	
    	//package file for sending
		FilePacket f = new FilePacket();
		f.fileArray = data;
		f.fileChecksum = saltMD5.computeMD5(data);

		System.out.println("Actual array checksum - " + Arrays.toString(f.fileChecksum));
		
		//send file
		s.sendData(f, "key.txt");
		
		s.readText();
    	System.out.println("Done reading");
    	
    	//read messages from queue
		while(!s.messages.isEmpty())
			System.out.println(s.messages.remove());
    	
    	s.shutDown();
	}
}
