import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

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
//    	
//    	
//    	FileOutputStream fos = new FileOutputStream("test2.txt");
//		BufferedOutputStream bos = new BufferedOutputStream(fos);
//		
//		bos.write(data);
//		bos.flush();
//		
//		bos.close();
		
		FilePacket f = new FilePacket();
		f.fileArray = data;
		f.fileChecksum = saltMD5.computeMD5(data);
		System.out.println(f.fileArray.length);
		System.out.println(Arrays.toString(f.fileArray));
		System.out.println(Arrays.toString(f.fileChecksum));
		s.sendData(f);
    	s.shutDown();
	}
}
