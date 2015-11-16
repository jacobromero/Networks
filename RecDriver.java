
public class RecDriver {

	public static void main(String[] args) throws InterruptedException {
		Receiver r = new Receiver();
		r.listen();
		
		Thread t = new Thread(r);
		t.start();
		r.sendText();

		FilePacket test = r.recieveByteArray();
	

		//debug code
		String str = new String(test.fileArray).replaceAll("\u0000", "");
		System.out.println(str);
	}
}
