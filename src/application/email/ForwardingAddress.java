package application.email;

public class ForwardingAddress 
{
	private String Address;
	
	public ForwardingAddress(String address)
	{
		Address = address;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

}
