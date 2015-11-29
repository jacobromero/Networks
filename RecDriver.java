import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class RecDriver {

	//NOTES: I decided that multithreading was causing more trouble that it was worth so I have move away from it, to read text from the network, call r.readText()
	//it will continue reading untill it received the string "Finished = True", after that read all message from the queue, and handle them accordingly.

	//Also note that I now require file length set in the reciever class this is to help me trim the file, since it was giving incorrect checksums without the length. 
	public static void main(String[] args) throws InterruptedException, IOException {
		String filePath = "abc2.avi";
		
		Receiver r = new Receiver();
		r.listen();
		
		//send user name password
		r.sendText("User Name = Jacob");
		r.sendText("Password = Password");
		
		
		//if authenticate then move on to the rest, other wise repeate
		
		//send more options to sender
		r.sendText("Ascii Armor = True");
		
		r.sendText("Finished = True");

		//after sending options
		//read for file size.
		r.readText();
		System.out.println("Done reading");
		
		//get file length, important for trimming and checksumming entire file.
		while(!r.messages.isEmpty()){
			r.fileLength = Integer.parseInt(r.messages.remove());
		}
		
		System.out.println("File size of byte array = " + r.fileLength);
		
		//tell sender to send file
		r.sendText("Send File = True");
				
		//receive file
		FilePacket fp = r.receiveData("key.txt", false);
		
		//check if receiving file failed, and tell sender
		if(fp == null){
			System.out.println("No file received");
//			r.shutDown()
//			System.exit(1);
		}
		else{		
			//checksum files to make sure they are the same
			byte[] filecheck = fp.fileChecksum;
			byte[] actualcheck = saltMD5.computeMD5(fp.fileArray);
			
			System.out.println("Checksum sent - " + Arrays.toString(filecheck));
			System.out.println("Checksum clac - " + Arrays.toString(actualcheck));
			
			for(int i = 0; i < filecheck.length; i++){
				if(filecheck[i] != actualcheck[i]){
	    				System.out.println("Failed");
	//    				r.shutDown();
	//    				System.exit(1);
	    		}
	    	}
			
				
		
			r.sendText("File Received = True");
			
			//create file
			System.out.println("File Received.");
			//create file from bytes
			FileOutputStream fos = new FileOutputStream(filePath); //insert output file name here
			BufferedOutputStream bos = new BufferedOutputStream(fos);
		 	bos.write(fp.fileArray);
			bos.flush();
			bos.close();
			fos.close();
		}

		
		//shutdown
		r.shutDown();
	}
}
