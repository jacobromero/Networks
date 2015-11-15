
public class RecDriver {

	public static void main(String[] args) throws InterruptedException {
		Receiver r = new Receiver();
		
		r.connectToServer();
//		
//		Thread.sleep(2000);
		FilePacket test = r.recieveByteArray();
//		r.recieveByteArray();
	}

}
