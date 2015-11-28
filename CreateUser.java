import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

public class CreateUser {

	public static void main( String[] args ) {
		Scanner Porygon = new Scanner( System.in );
		String username;
		String password;
		boolean successful;
		System.out.println( "What is the username?" );
		username = Porygon.nextLine();
		System.out.println( "What is the password?" );
		password = Porygon.nextLine();
		successful = create( username, password );
		if ( successful ) {
			System.out.println( "The new user " + username + " has been created." );
		} else {
			System.out.println( "Another user with the same username already exists." );
		}
		Porygon.close();
	}
	
	public static boolean create( String user, String pass ) {
		ArrayList<String> ALusernames = new ArrayList<String>();
		String[] usernames;
		try{
			String text = "";
            BufferedReader in = new BufferedReader(new FileReader(new File("users.txt")));
            text = in.readLine();
			while ( text != null ) {
				ALusernames.add( text );
				System.out.println( text );
				in.readLine();
				text = in.readLine();
			}
			in.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		usernames = ALusernames.toArray( new String[ ALusernames.size() ] );
		for ( int x = 0; x < usernames.length; x++ ) {
			if ( user.equals( usernames[x] ) ) {
				return false;
			}
		}
		System.out.println( "DEBUG: Name not found, proceed." );
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("users.txt", true)))) {
			out.println(user);
			String salt = saltMD5.getNextSalt();
			pass = salt + pass;
			out.println(Base64Coding.encode(salt) + ":" + Base64Coding.encode(saltMD5.toHexString(saltMD5.computeMD5(pass.getBytes()))));
			out.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public static String parseSalt( String data ) {
		String salt = "";
		for ( int x = 0; x < data.length(); x++ ) {
			if ( data.charAt(x) != ':' ) {
				salt = salt + data.charAt(x);
			}
		}
		return salt;
	}
	
	public static String parseHash( String data ) {
		String hash = "";
		int count = 0;
		for ( int x = 0; x < data.length(); x++ ) {
			if ( data.charAt(x) == ':' ) {
				count = x + 1;
				x = data.length();
			}
		}
		for ( int x = count; x < data.length(); x++ ) {
			hash = hash + data.charAt(x);
		}
		return hash;
	}
}
