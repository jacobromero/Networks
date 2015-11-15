import java.util.Arrays;

public class SendDriver {

	public static void main(String[] args) {
		Sender s = new Sender();
    	s.runServer();
    	
    	String str = "this is a test."; //"3c5qFwunv3BMIPFglu8fAk7xkCqlq5jGgoMRpAXyq6Tl6g96ZWmhWPnomO0NROzRiwkiO9IX7yhioFlLCo730xjkvEEkuY6k2yMMijvxk8uXpKiSWiVBcGm0UctaBud4lhIku1Lpduo9NoYl5hBscXvxF21nLIwvBQsqBOePzxc3YTJUFz3OocBEiEx6OEl3LynbQUK7esEKvKaREeXJuNV6CvNXPBX6P7YhfjaMxawidDrcgmhO1r282vOLuJJsC9JPt9XDn7ASVqSb9ruud3bTWqKL940QJKXmTxMgpo52IWLmLWowIkTyRQFeefscTJLkPYil9Sg7cEP5wBJlhg4VQQ1eKIU2lZmT8NpNEkMjmBlrPMJxayXM6zAHiExrYuJwRQS23U3boplKmSU7qa5yjUlK61XRQdNw9eTqYJrtgRfw0FEgL1kJ2rgEpXTNYsLEG68oz9A2vtAFhTPQoJ4SveGScfIIcu5Dku5g1a3Vd9yUeXtNP12MgbQB0YXvj42au81S2262lHFw1Gnkz3CSuTuGwj0m7O7kQwEfUgDUCqkx1aLYd0ImizF5o53WHLOCuvDepgDmKFUhVhnHtnZpbtjPDMMS2uKUubEOsmXdIcV2uv0ZlZPs2DhchY5KFef53HACe4Mbp7UnYeCKqnAhErUVl85K8GOlSbFD6lGxTGX1jFCzgs5kbmvvH7w8edFvE4N7BUUxAmiGyrJ7ateZCZJFCoaw6xKqRpnO3HzEhOARjjO1H5A6BkGmpRjYbx9NNWbLsaciLG0MaHNjcxACOewpCMl0bO9P0x8HkzgEpLlF57v6eD7V6PNWfSDUlQbUzjcbDoIwDimFwYlwIqgX5mUHaudKIAixtr9z25oVjyenKTO6VcNRuUEVW7TK7eAQxAL9sjDfJU0HtTLKhB914lmhewfkVhur5e8OpU6Gx7MP5UcD6uoWGdV18Z0B1kCgUZGvgp79Ga2Vpl1Ouh9ywitImzJpFKrD8F3UG5rxSFopYMPmmMHIV5pr2u0s11tq0vdJUd7WBqaPJjE5N8N83qigMyq8jeTyUKYPUTQuBn6dqbAhjfZQefMBWXGI6sQotKBB3i5VDjRCxFrsVcsv5loH0z89J7mKmbca6qXR5RpAhuphdvsb5ynFiDhcZALf685Mqiemk6f9PJmnZr7OWewgcWErhz1PTPjhybKXAqvLEVANfTksvJmy38fB6N6uvRJBheJ1cIwzLCUHZ1FWOO7UiYQAx96df1ZFhUdXk6LfVL6cU28aF6yJ4pKD1qHq8BeV92F27e8QNcDolvCGQOlQzM1QPJAufNC3";
    	byte[] b = str.getBytes();
    	byte[] filesum = saltMD5.computeMD5(b);
    	
    	
//    	System.out.println(Arrays.toString(filesum));
    	FilePacket toSend = new FilePacket(b, filesum);
    	
    	System.out.println("Sending file...");
    	s.sendByte(toSend);
    	System.out.println("Done.");
    	
    	s.closeConnection();
	}
}
