


public class RecDriver {

	public static void main(String[] args) throws InterruptedException {
		Receiver r = new Receiver();
		r.listen();
		
		Thread t = new Thread(r);
		t.start();
		r.stopReadingText();
		
		r.shutDown();
	}
}
