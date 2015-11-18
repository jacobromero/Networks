
public class SendDriver {

	public static void main(String[] args) {
		Sender s = new Sender();
    	s.searchForClients();
    	
    	Thread t = new Thread(s);
		t.start();
		
		s.sendText("hey bud");
		
		s.stopReadingText();
    	
    	
		String test = "hihihihih";
		byte[] a = test.getBytes();
		
//		System.out.println(Arrays.toString(a));
		
		FilePacket f = new FilePacket();
		f.fileArray = a;
		f.fileChecksum = saltMD5.computeMD5(a);
		
//		s.sendData(f);
    	s.shutDown();
	}
}
