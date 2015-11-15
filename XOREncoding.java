import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class XOREncoding {
	private static byte[] key;
	
	XOREncoding() {}
	
	/**
	 * Given a Path for a File and Key, this method returns the string of the XOR of the two files.
	 * <p>
	 * Converts String to byte[] and XOR's bit by bit.
	 * @param keyPath
	 * @return
	 * @throws IOException
	 */
	public static byte[] encode(byte[] file, String keyPath) throws IOException {
		key = Files.readAllBytes(Paths.get(keyPath));
		byte[] encodedString = new byte[file.length];
		
		for(int i = 0, j = 0; i < file.length; i++, j++) {
			if (j >= key.length) j = 0;
			
			encodedString[i] = (byte)(0xff & ((int)file[i] ^ (int)key[j]));
		}
		
		return encodedString;
	}
	
	/**
	 * Given a encoded String and Path to a Key, this method returns the string of the XOR of the two.
	 * <p>
	 * Converts String to byte[] and XOR's bit by bit.
	 * @param encodedString
	 * @param keyPath
	 * @return
	 * @throws IOException
	 */
	public static String decode (byte[] encodedString, String keyPath) throws IOException {
		key = Files.readAllBytes(Paths.get(keyPath));
		byte[] encode = encodedString;
		byte[] decodedString = new byte[encode.length];
		
		for(int i = 0, j = 0; i < encode.length; i++, j++) {
			if (j >= key.length) j = 0;
			
			decodedString[i] = (byte)(0xff & ((int)encode[i] ^ (int)key[j]));
		}
		return new String(decodedString);
	}
	
	public static byte[] decodeByte (byte[] encodedString, String keyPath) throws IOException {
		key = Files.readAllBytes(Paths.get(keyPath));
		byte[] encode = encodedString;
		byte[] decodedString = new byte[encode.length];
		
		for(int i = 0, j = 0; i < encode.length; i++, j++) {
			if (j >= key.length) j = 0;
			
			decodedString[i] = (byte)(0xff & ((int)encode[i] ^ (int)key[j]));
		}
		return decodedString;
	}
}
