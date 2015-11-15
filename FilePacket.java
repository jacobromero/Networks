
public class FilePacket {
	//set the entire byte[] of a file here
	byte[] fileArray;
	
	//set the entire checksum of the file here (uncorruputed/corrupted)
	byte[] fileChecksum;
	
	public FilePacket(byte[] file, byte[] checksum){
		fileArray = file;
		fileChecksum = checksum;
	}
}
