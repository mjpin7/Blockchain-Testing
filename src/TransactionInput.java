
/**
 * Class to reference the transaction outputs that have not yet been spent
 * @author MPinheiro
 *
 */
public class TransactionInput {
	
	public String transOutID;					// Used to find the relevant transaction output, to verify ownership
	public TransactionOutput UTXO;				// Unspent transaction outputs
	
	/* Constructor */
	public TransactionInput(String transID)
	{
		this.transOutID = transID;
	}

}
