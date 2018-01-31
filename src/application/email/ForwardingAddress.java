package application.email;

import java.io.Serializable;

public class ForwardingAddress implements Serializable
{
	private static final long serialVersionUID = -130925708320573990L;
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
