import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SendDriver {

	public static void main(String[] args) throws IOException {
		Sender s = new Sender();
    	s.searchForClients();
    	
//    	Thread t = new Thread(s);
//		t.start();
		
//		s.sendText("hey bud");
		
//		s.stopReadingText();
    	Path path = Paths.get("test1.txt");
    	byte[] data = Files.readAllBytes(path);
    	
		
		FilePacket f = new FilePacket();
		f.fileArray = data;
		f.fileChecksum = saltMD5.computeMD5(data);

//		System.out.println(Arrays.toString(f.fileChecksum));
		s.sendData(f);
    	s.shutDown();
	}
}
