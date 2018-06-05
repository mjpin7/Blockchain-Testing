/**
 * Class for a person.
 * 
 * @author MPinheiro
 *
 */
public class Person {
	
	public String name;					// Name of the person
	public Wallet myWallet;				// The persons wallet
	
	/* Constructor */
	public Person(String name)
	{
		this.name = name;
		this.myWallet = new Wallet();
	}

}
