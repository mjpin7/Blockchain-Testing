import java.util.ArrayList;
import java.security.Security;
import java.util.HashMap;
import com.google.gson.GsonBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Class for the "Crypto Currency"
 * 
 * @author MPinheiro
 *
 */
public class TestChain {
	
	public static ArrayList<Block> chain = new ArrayList<Block>();
	public static HashMap<String, TransactionOutput> UTXO = new HashMap<String, TransactionOutput>();	// List of all unspent transactions
	public static int difficulty = 5;
	public static float minimumTrans = 0.1f;
	public static Wallet wallet1;
	public static Wallet wallet2;
	public static Transaction genesisTrans;
	
	public static void main(String[] args)
	{		
		// BouncyCastle as provider
		Security.addProvider(new BouncyCastleProvider());
		
		// Generate the wallets
		wallet1 = new Wallet();
		wallet2 = new Wallet();
		Wallet coinbase = new Wallet();
		
		// Create the genesis transaction, send 100 coin to wallet1
		genesisTrans = new Transaction(coinbase.publicKey, wallet1.publicKey, 100f, null);
		// Sign the genesis transaction
		genesisTrans.generateSignature(coinbase.privateKey);
		// Manually set the transaction ID
		genesisTrans.transID = "0";
		// Add the transaction output
		genesisTrans.output.add(new TransactionOutput(genesisTrans.receiver, genesisTrans.value, genesisTrans.transID));
		// Store the first transaction in the UTXO list
		UTXO.put(genesisTrans.output.get(0).id, genesisTrans.output.get(0));
		
		System.out.println("Creating genesis block");
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTrans);
		addBlock(genesis);
		
		Block block1 = new Block(genesis.hash);
		System.out.println("\nWallet 1's balance is: " + wallet1.getBalance());
		System.out.println("\nWallet 1 is attempting to send funds (40) to wallet2...");
		block1.addTransaction(wallet1.sendFunds(wallet2.publicKey, 40f));
		addBlock(block1);
		System.out.println("\nWallet 1's balance is: " + wallet1.getBalance());
		System.out.println("\nWallet 2's balance is: " + wallet2.getBalance());
		
		Block block2 = new Block(block1.hash);
		System.out.println("\nWallet 2 is attempting to send more funds than it has (1000) to wallet1...");
		block2.addTransaction(wallet2.sendFunds(wallet1.publicKey, 1000f));
		addBlock(block2);
		System.out.println("\nWallet 1's balance is: " + wallet1.getBalance());
		System.out.println("\nWallet 2's balance is: " + wallet2.getBalance());
		
		isValid();
		
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(chain);
		System.out.println("\nThe block chain: ");
		System.out.println(blockchainJson);
	}
	
	/**
	 * Method to check if the chain is valid
	 * 
	 * @return			If the chain is valid
	 */
	public static boolean isValid()
	{
		Block prevBlock;
		Block currBlock;
		String target = new String(new char[difficulty]).replace('\0', '0');
		HashMap<String, TransactionOutput> tempUTXO = new HashMap<String, TransactionOutput>();
		tempUTXO.put(genesisTrans.output.get(0).id, genesisTrans.output.get(0));
		
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
			
			TransactionOutput tempOut;
			
			// Check the transactions
			for(int j = 0; j < currBlock.transactions.size(); j++)
			{
				// Get the transaction
				Transaction currTrans = currBlock.transactions.get(j);
				
				// If the signature is not valid
				if(!currTrans.verifySignature())
				{
					System.out.println("Signature on transaction " + j + " is invalid.");
					return false;
				}
				
				// If the inputs are not valid
				if(currTrans.getInputsValue() != currTrans.getOutputsValue())
				{
					System.out.println("Inputs are not equal to outputs on transaction " + j + ".");
					return false;
				}
				
				// Go through the inputs list on the current transaction
				for(TransactionInput in : currTrans.input)
				{
					// Get the UTXO
					tempOut = tempUTXO.get(in.transOutID);
					
					// If the UTXO isn't there
					if(tempOut == null)
					{
						System.out.println("Referenced input on transaction " + j + " is missing.");
						return false;
					}
					
					// If the value isn't the same
					if(in.UTXO.value != tempOut.value)
					{
						System.out.println("Referrenced input on transaction " + j + " value is invalid.");
						return false;
					}
					
					tempUTXO.remove(in.transOutID);
				}
				
				// Go through the output list of the transaction
				for(TransactionOutput out : currTrans.output)
				{
					// Put the transaction in the temp UTXO list
					tempUTXO.put(out.id, out);				
				}
				
				// If the transaction receiver is not correct
				if(currTrans.output.get(0).receiver != currTrans.receiver)
				{
					System.out.println("Transaction " + j + " output receiver is not correct.");
					return false;
				}
				
				if(currTrans.output.get(1).receiver != currTrans.sender)
				{
					System.out.println("Transaction " + j + " output change is not sender.");
					return false;
				}
			}
		}
		
		System.out.println("Chain is valid!");
		return true;
	}
	
	/**
	 * Method to mine a block, then add the block to the chain
	 * 
	 * @param newBlock					The block to add
	 */
	public static void addBlock(Block newBlock)
	{
		newBlock.mineBlock(difficulty);
		chain.add(newBlock);
	}

}
