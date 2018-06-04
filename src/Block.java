import java.util.ArrayList;
import java.util.Date;

/**
 * Class for a block of the chain
 * 
 * @author MPinheiro
 *
 */
public class Block {

	public String hash;
	public String prevHash;
	public String merkleRoot;
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	private long timeStamp;
	private int nonce;
	
	/* Constructor */
	public Block(String prevHash)
	{;
		this.prevHash = prevHash;
		this.timeStamp = new Date().getTime();
		this.nonce = 0;							// Start nonce at 0
		this.hash = getHash();					// **IMPORTANT** must call this function AFTER the other values have been set
		
		
	}
	
	/**
	 * Method to get the hash for the current block. Using the prevHash, timeStamp, nonce and data so that it cannot be tampered with
	 * 
	 * @return			The hash for the current block
	 */
	public String getHash()
	{
		String calcHash = Util.sha256(prevHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
		
		return calcHash;
	}
	
	/**
	 * Method to mine the block and get the merkle root
	 * 
	 * @param difficulty				The difficulty of the hash
	 */
	public void mineBlock(int difficulty)
	{
		// Get the merkle root
		merkleRoot = Util.getMerkleRoot(transactions);
		
		// Create a new string of which consists of "difficulty" amount of 0's
		String targetString = Util.getDifficultyString(difficulty);
		
		// Continue updating the nonce until the block is mined
		while(!this.hash.substring(0, difficulty).equals(targetString))
		{
			nonce++;
			hash = getHash();
		}
		
		System.out.println("Block mined: " + this.hash);
	}
	
	public boolean addTransaction(Transaction transaction)
	{
		if(transaction == null)
			return false;
		
		// Only process if the block is not the genesis block
		if(prevHash != "0")
		{
			if((transaction.proccessTransaction() != true))
			{
				System.out.println("Transaction failed to proccess, discarded.");
				return false;
			}
		}
		
		// Add the transaction to the list
		transactions.add(transaction);
		System.out.println("Transaction successfully added to the Block!");
		return true;
	}
	
}
