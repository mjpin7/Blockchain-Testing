import java.util.ArrayList;
import com.google.gson.GsonBuilder;

public class TestChain {
	
	public static ArrayList<Block> chain = new ArrayList<Block>();
	public static int difficulty = 1;
	
	public static void main(String[] args)
	{
		
		chain.add(new Block("First Block!", "0"));
		System.out.println("Mining genisis block...");
		chain.get(0).mineBlock(difficulty);
		
		chain.add(new Block("Second Block", chain.get(chain.size() - 1).hash));
		System.out.println("Mining second block...");
		chain.get(1).mineBlock(difficulty);
		
		chain.add(new Block("Third Block", chain.get(chain.size() - 1).hash));
		System.out.println("Mining third block...");
		chain.get(2).mineBlock(difficulty);
		
		if(isValid())
		{
			System.out.println("\nChain is valid");
		}
		else
		{
			System.out.println("\nChain is not valid");
		}
		
		//Make the Json to make the output look nice
		String chainJson = new GsonBuilder().setPrettyPrinting().create().toJson(chain);
		System.out.println(chainJson);
	}
	
	/**
	 * Method to check if the chain is valid
	 * 
	 * @return
	 */
	public static boolean isValid()
	{
		Block prevBlock;
		Block currBlock;
		String target = new String(new char[difficulty]).replace('\0', '0');
		
		// Go through the chain
		for(int i = 1; i < chain.size(); i++)
		{
			// Get the current block and the previous block
			currBlock = chain.get(i);
			prevBlock = chain.get(i - 1);
			
			// Check if the current hashes are the same
			if(!currBlock.hash.equals(currBlock.getHash()))
			{
				System.out.println("Current hashes not equal!");
				return false;
			}
			
			// Check if the previous hash itself matches with the prevHash variable from the currBlock
			if(!prevBlock.hash.equals(currBlock.prevHash))
			{
				System.out.println("Previous hashes not equal!");
				return false;
			}
			
			// Check if the block has been mined
			if(!currBlock.hash.substring(0, difficulty).equals(target))
			{
				System.out.println("Block hasn't been mined!");
				return false;
			}
		}
		
		return true;
	}

}
