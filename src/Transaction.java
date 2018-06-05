import java.security.*;
import java.util.ArrayList;

/**
 * Class for a transaction. To generate and verify signatures
 * 
 * @author MPinheiro
 *
 */
public class Transaction {
	
	public String transID;				// Transaction ID. Hash of transaction
	public PublicKey sender;			// The public key of the sender
	public PublicKey receiver;			// The public key of the receiver
	public float value;					// The value to send	
	public byte[] signature;			// The signature to approve the transaction. To prevent any one else from spending funds
	
	public ArrayList<TransactionInput> input = new ArrayList<TransactionInput>();		// References to previous transactions to prove sender has enough funds
	public ArrayList<TransactionOutput> output = new ArrayList<TransactionOutput>();	// Amount of relevant addresses received in transaction
	
	private static int seq = 0; 		// Count of how many transactions have been generated. To avoid having two transactions having the same hash
	
	/* Constructor */
	public Transaction(PublicKey send, PublicKey rec, float value, ArrayList<TransactionInput> in)
	{
		this.sender = send;
		this.receiver = rec;
		this.value = value;
		this.input = in;
	}
	
	/**
	 * Method to calulate the sha256 hash from the Keys, value and sequence
	 * 
	 * @return
	 */
	public String calcHash()
	{
		// Update sequence to make sure that no two transaction hashes are the same
		seq++;
		return Util.sha256(Util.getStringFromKey(sender) + Util.getStringFromKey(receiver) + Float.toString(value) + seq);
	}
	
	/**
	 * Method to generate a signature for the transaction
	 * 
	 * @param privateKey				The privateKey of the sender
	 */
	public void generateSignature(PrivateKey privateKey)
	{
		String data = Util.getStringFromKey(sender) + Util.getStringFromKey(receiver) + Float.toString(value);
		signature = Util.applyECDSA(privateKey, data);
	}
	
	/**
	 * Method to verify the signature
	 * 
	 * @return							Whether the signature is valid or not
	 */
	public boolean verifySignature()
	{
		String data = Util.getStringFromKey(sender) + Util.getStringFromKey(receiver) + Float.toString(value);
		return Util.verifyECDSA(sender, data, signature);
	}
	
	/**
	 * Method to process a transaction
	 * 
	 * @return				Whether the transaction was processed
	 */
	public boolean proccessTransaction()
	{
		if(verifySignature() == false)
		{
			System.out.println("Transaction signature not verified!");
			return false;
		}
		
		// Get all of the transaction inputs that are unspent
		for(TransactionInput x : input)
		{
			x.UTXO = TestChain.UTXO.get(x.transOutID);
		}
		
		// Check if transaction is valid
		if(getInputsValue() < TestChain.minimumTrans)
		{
			System.out.println("Transaction inputs too small: " + getInputsValue());
			return false;
		}
		
		// Get value of inputs and calculate what's left over
		float left = getInputsValue() - value;
		transID = calcHash();
		
		// Send the value to the receiver
		output.add(new TransactionOutput(this.receiver, value, transID));
		// Send the left over to the sender
		output.add(new TransactionOutput(this.sender, left, transID));
		
		// Add all the outputs to the UTXO list
		for(TransactionOutput x : output)
		{
			TestChain.UTXO.put(x.id, x);
		}
		
		// Remove the transaction inputs from the UTXO list as they are spent
		for(TransactionInput x : input)
		{
			if(x.UTXO == null)
				continue;
			TestChain.UTXO.remove(x.UTXO.id);
		}
		
		return true;
	}
	
	/**
	 * Method to return the sum of all the inputs UTXO values
	 * 
	 * @return					The sum
	 */
	public float getInputsValue()
	{
		float total = 0;
		for(TransactionInput x : input)
		{
			if(x.UTXO == null)
				continue;
			total += x.UTXO.value;
		}
		
		return total;
	}
	
	/**
	 * Method to return the sum of all the outputs
	 * 
	 * @return					The sum
	 */
	public float getOutputsValue()
	{
		float total = 0;
		for(TransactionOutput x : output)
		{
			total += x.value;
		}
		
		return total;
	}

}
