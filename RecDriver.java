import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class RecDriver {

	
	public static void main(String[] args) throws InterruptedException, IOException {
		Receiver r = new Receiver();
		r.listen();
		
//		Thread t = new Thread(r);
//		t.start();
//		r.stopReadingText();
		
		r.updateRec();
		
//		FilePacket f = r.receiveData();
//		
//		FileOutputStream fos = new FileOutputStream("test2.txt");
//		BufferedOutputStream bos = new BufferedOutputStream(fos);
//		
//		bos.write(f.fileArray);
//		bos.flush();
//		
//		bos.close();
//		System.out.println(Arrays.toString(f.fileArray));
//		
//		System.out.println(Arrays.toString(f.fileChecksum));
//		System.out.println(Arrays.toString(saltMD5.computeMD5(f.fileArray)));
		
		r.shutDown();
	}
}
