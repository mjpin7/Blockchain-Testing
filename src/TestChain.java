import java.util.ArrayList;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.GsonBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Class for the "Crypto Currency"
 * 
 * @author MPinheiro
 *
 */
public class TestChain {
	
	public static HashMap<String, Person> people = new HashMap<String, Person>();
	public static ArrayList<Transaction> trans = new ArrayList<Transaction>();
	public static ArrayList<Block> chain = new ArrayList<Block>();
	public static HashMap<String, TransactionOutput> UTXO = new HashMap<String, TransactionOutput>();	// List of all unspent transactions
	public static int difficulty = 5;
	public static float minimumTrans = 0.1f;
	//public static Wallet wallet1;
	//public static Wallet wallet2;
	public static Transaction genesisTrans;
	
	public static void main(String[] args)
	{		
		// BouncyCastle as provider
		Security.addProvider(new BouncyCastleProvider());
		
		Wallet generator = new Wallet(); // Generator wallet
		Wallet coinbase = new Wallet(); // Generator wallet
		Scanner in = new Scanner(System.in);
		boolean quit = false;
		
		// Manually create the genesis transaction
		genesisTrans = new Transaction(generator.publicKey, coinbase.publicKey, Float.MAX_VALUE, null);
		// Manually generate the signature for the genesis transaction
		genesisTrans.generateSignature(generator.privateKey);
		// Manually set the genesis transaction ID
		genesisTrans.transID = "0";
		// Manually add the transaction output
		genesisTrans.output.add(new TransactionOutput(genesisTrans.receiver, genesisTrans.value, genesisTrans.transID));
		// Manually store the first transaction in the UTXO list
		UTXO.put(genesisTrans.output.get(0).id, genesisTrans.output.get(0));
		
		// Create genesis block, add the genesis transaction to the block and add the block to the chain
		System.out.println("Creating genesis block...");
		Block genBlock = new Block("0");
		genBlock.addTransaction(genesisTrans);
		addBlock(genBlock);
		System.out.print("\n");
		
		// Loop until requested exit
		while(!quit)
		{
			System.out.println("What would you like to do?");
			System.out.println("1. Add person");
			System.out.println("2. Check balance");
			System.out.println("3. List people and balances");
			System.out.println("4. Send funds");
			System.out.println("5. Print chain");
			System.out.println("6. Quit");
			System.out.print("Enter the number of what you would like to do: ");
			int input = in.nextInt();
			System.out.print("\n");
			
			switch(input)
			{
				// Add person
				case 1:
					System.out.print("Enter the name: ");
					String name = in.next();
					System.out.print("Enter the starting balance: ");
					float bal = in.nextFloat();
					
					// Add the person to the map
					people.put(name, new Person(name));

					// Create the block, add the transaction to the block and add the block to the chain
					System.out.println("Creating new block...");
					Block newBlock = new Block(chain.get(chain.size() - 1).hash);
					newBlock.addTransaction(coinbase.sendFunds(people.get(name).myWallet.publicKey, bal));
					addBlock(newBlock);
					
					System.out.println("Person added with name \"" + name + "\" and starting balance " + bal + " coin.");
					break;
				// Check balance
				case 2:
					System.out.print("Enter the name of whom you'd like to check the balance of: ");
					String name1 = in.next();
					
					if(people.containsKey(name1))
					{
						System.out.println("Balance for " + name1 + " is: " + people.get(name1).myWallet.getBalance());
					}
					else
					{
						System.out.println("Person with name \"" + name1 + "\" does not exist in the people list.");
					}
					
					break;
				// Print people
				case 3:
					
					if(people.size() != 0)
					{
						// Loop through the map
						for(Map.Entry<String, Person> p: people.entrySet())
						{
							System.out.println("Name: " + p.getKey() + ". Balance: " + p.getValue().myWallet.getBalance() + " coin.");
						}
					}
					else
					{
						System.out.println("No people to show!");
					}
					
					
					break;
				// Send funds
				case 4:
					break;
				// Print chain
				case 5:
					String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(chain);
					System.out.println("\nThe block chain: ");
					System.out.println(blockchainJson);
					break;
				// Quit
				case 6:
					System.out.println("Goodbye");
					quit = true;
					break;
				// Invalid input
				default:
					System.out.println("Invalid input.");
					break;	
			}
			
			System.out.print("\n\n");
			isValid();
			System.out.print("\n");
		}
		
		/*
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
		*/
		
		
		
		in.close();
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
		
		System.out.println("\n\nChain is valid!");
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
