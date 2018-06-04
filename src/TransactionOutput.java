import java.security.PublicKey;

/**
 * Class to show the final amount sent to each person. Proof that you have coins to send
 * 
 * @author MPinheiro
 *
 */
public class TransactionOutput {
	
	public String id;
	public PublicKey receiver;
	public float value;
	public String parentTransID;
	
	/* Constructor */
	public TransactionOutput(PublicKey rec, float val, String parentID)
	{
		this.receiver = rec;
		this.value = val;
		this.parentTransID = parentID;
		this.id = Util.sha256(Util.getStringFromKey(rec) + Float.toString(val) + parentID);
	}
	
	/**
	 * Method to check whether a coin belongs to you
	 * 
	 * @param publicKey					The public key of the user wanted to verify
	 * @return							Whether the coin belongs to user 
	 */
	public boolean isMine(PublicKey publicKey)
	{
		return (publicKey == receiver);
	}

}
