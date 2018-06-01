import java.util.Date;

public class Block {

	public String hash;
	public String prevHash;
	public String data;
	private long timeStamp;
	private int nonce;
	
	/* Constructor */
	public Block(String data, String prevHash)
	{
		this.data = data;
		this.prevHash = prevHash;
		this.timeStamp = new Date().getTime();
		this.nonce = 0;
		this.hash = getHash();					// **IMPORTANT** must call this function AFTER the other values have been set
		
		
	}
	
	/**
	 * Method to get the hash for the current block. Using the prevHash, timeStamp, nonce and data so that it cannot be tampered with
	 * 
	 * @return			The hash for the current block
	 */
	public String getHash()
	{
		String calcHash = Util.sha256(prevHash + Long.toString(timeStamp) + Integer.toString(nonce) + data);
		
		return calcHash;
	}
	
	public void mineBlock(int difficulty)
	{
		// Create a new string of which consists of "difficulty" amount of 0's
		String target = new String(new char[difficulty]).replace('\0', '0');
		
		while(!this.hash.substring(0, difficulty).equals(target));
		{
			nonce++;
			hash = getHash();
		}
		
		System.out.println("Block mined: " + this.hash);
	}
	
}
