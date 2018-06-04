import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for wallets. Of which includes the public and private keys
 * 
 * @author MPinheiro
 *
 */
public class Wallet {
	public PrivateKey privateKey;
	public PublicKey publicKey;
	
	// UTXOs owned by this wallet
	public HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
	
	/* Constructor */
	public Wallet()
	{
		generateKeyPair();
	}
	
	public void generateKeyPair()
	{
		try
		{
			// Generate an elliptic curve key pair
			KeyPairGenerator genKey = KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom rand = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			
			// Initialize the generator and get a keypair
			genKey.initialize(ecSpec, rand);
			KeyPair keypair = genKey.generateKeyPair();
			
			// Set the private and public keys
			privateKey = keypair.getPrivate();
			publicKey = keypair.getPublic();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Method to get the balance of this wallet
	 * 
	 * @return					The total balance of this wallet
	 */
	public float getBalance()
	{
		float total = 0;
		
		// Go through the UTXOs in TestChain class
		for(Map.Entry<String, TransactionOutput> item : TestChain.UTXO.entrySet())
		{
			// Get the item
			TransactionOutput UTXO = item.getValue();
			
			// If the UTXO isMine, add the UTXO to the wallet list and add the total to the tally
			if(UTXO.isMine(publicKey))
			{
				UTXOs.put(UTXO.id, UTXO);
				total += UTXO.value;
			}
		}
		
		return total;
	}
	
	/**
	 * Method to send funds
	 * 
	 * @param receiver					The person receiving the funds
	 * @param value						The amount sending
	 * @return							The transaction
	 */
	public Transaction sendFunds(PublicKey receiver, float value)
	{
		// If the balance is insufficient
		if(getBalance() < value)
		{
			System.out.println("Not wnough funds to send transaction. Discarded");
			return null;
		}
		
		// Make new list for inputs
		ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
		
		float total = 0;
		
		// Go through the UTXOs from TestChain class
		for(Map.Entry<String, TransactionOutput> item : TestChain.UTXO.entrySet())
		{
			// Get the UTXO
			TransactionOutput UTXO = item.getValue();
			
			// Update the total
			total += UTXO.value;
			
			// Add to the selected output to the inputs list
			inputs.add(new TransactionInput(UTXO.id));
			
			// If the total is enough exit loop
			if(total > value)
				break;
		}
		
		// Create a new transaction and generate signature
		Transaction newTrans = new Transaction(publicKey, receiver, value, inputs);
		newTrans.generateSignature(privateKey);
		
		// Remove all the used transactions from the UTXO list
		for(TransactionInput input : inputs)
		{
			UTXOs.remove(input.transOutID);
		}
		
		
		return newTrans;
	}

}
