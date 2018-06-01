import java.security.MessageDigest;

public class Util {
	
	/**
	 * Method to apply SHA-256 algorithm
	 * 
	 * @param input			Input string consisting of prevHash + timeStamp + nonce + data
	 * @return				The SHA-256 hash
	 */
	public static String sha256(String input)
	{
		try
		{
			MessageDigest dig = MessageDigest.getInstance("SHA-256");
			
			byte[] hash = dig.digest(input.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();		// Hash as hex
			
			for(int i = 0; i < hash.length; i++)
			{
				String hex = Integer.toHexString(0xff & hash[i]);
				
				if(hex.length() == 1)
					hexString.append('0');
				
				hexString.append(hex);
			}
			
			return hexString.toString();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

}
