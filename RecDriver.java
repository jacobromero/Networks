import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class RecDriver {

	
	public static void main(String[] args) throws InterruptedException, IOException {
		Receiver r = new Receiver();
		r.listen();
		
//		Thread t = new Thread(r);
//		t.start();
//		r.stopReadingText();
		
		FilePacket fp = r.receiveData();
		
		if(fp == null){
			System.out.println("fp is null");
			System.exit(1);
		}
		
		FileOutputStream fos = new FileOutputStream("abc2.zip");
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		
	 	bos.write(fp.fileArray);
		bos.flush();
		bos.close();
		fos.close();
		
		r.shutDown();
	}
}
