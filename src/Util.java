import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.security.*;

/**
 * Class for various utilities needed in the program
 * 
 * @author MPinheiro
 *
 */
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
	
	/**
	 * Method to get the difficulty string of 0's
	 * 
	 * @param difficulty				The level of difficulty
	 * @return							The difficulty string 
	 */
	public static String getDifficultyString(int difficulty)
	{
		return new String(new char[difficulty]).replace('\0', '0');
	}
	
	/**
	 * Method to take in privateKey and input, signs it and returns the array of bytes
	 * 
	 * @param privateKey				The senders private key
	 * @param input						The input, consisting of the sender and receivers keys, and the value
	 * @return							The signature in bytes[];
	 */
	public static byte[] applyECDSA(PrivateKey privateKey, String input)
	{
		Signature dsa;
		byte[] out = new byte[0];
		
		try
		{
			dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);
			byte[] strByte = input.getBytes();
			dsa.update(strByte);
			byte[] realSign = dsa.sign();
			out = realSign;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		return out;
	}
	
	/**
	 * Method to take in the signature, public key and the data and return whether the signature is valid
	 * 
	 * @param publicKey					The public key of the sender
	 * @param data						The data, consisting of the sender and receivers keys, and the value
	 * @param sig						The signature
	 * @return							Whether the signature is valid or not (true or false)
	 */
	public static boolean verifyECDSA(PublicKey publicKey, String data, byte[] sig)
	{
		try
		{
			Signature ecdsaVer = Signature.getInstance("ECDSA", "BC");
			ecdsaVer.initVerify(publicKey);
			ecdsaVer.update(data.getBytes());
			return ecdsaVer.verify(sig);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Method to return encoded string from a key
	 * 
	 * @param key					The key to encode
	 * @return						The encoded string from key
	 */
	public static String getStringFromKey(Key key)
	{
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
	
	/**
	 * Method to return a merkle root from an array of transactions
	 * 
	 * @param transactions
	 * @return
	 */
	public static String getMerkleRoot(ArrayList<Transaction> transactions)
	{
		int count = transactions.size();
		
		ArrayList<String> previousTreeLayer = new ArrayList<String>();
		
		// Add all of the transactions to the list
		for(Transaction t : transactions)
		{
			previousTreeLayer.add(t.transID);
		}
		
		ArrayList<String> treeLayer = previousTreeLayer;
		
		// Continue until there is only 1.. The root
		while(count > 1)
		{
			treeLayer = new ArrayList<String>();
			
			// Combine the last two layers to one new layer
			for(int i = 1; i < previousTreeLayer.size(); i++)
			{
				treeLayer.add(sha256(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
			}
			
			// Get the size of the layer
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}
		
		// If the size of the layer is 1, get the first element being the merkle root
		String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
		
		return merkleRoot;
	}

}
