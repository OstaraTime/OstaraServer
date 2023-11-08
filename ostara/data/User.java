package ostara.data;

import ostara.exceptions.InvalidLoginException;
import ostara.server.Auth;
import ostara.server.Session;
import ostara.data.Data;

public class User{
	private int ID;
	private String username;
	private String password;	//Hash, don't worry :)
	private Person person;

	public User(int ID, String username, String password, int person){
		this.ID = ID;
		this.username = username;
		this.password = password;
		this.person = Data.getPerson(person);
	}

	public Session authenticate(String username, String password, String salt) throws InvalidLoginException{
		if(!this.username.equals(username)){
			throw new InvalidLoginException();
		}
		String saltedHashIn = MD5(password+salt);
		String saltedHashReal = MD5(this.password+salt);

		if(saltedHashReal.equals(saltedHashIn)){
			throw new InvalidLoginException();
		}
		return new Session(this);
	}

	private String MD5(String md5){
		try{
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < array.length; ++i){
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
			}
			return sb.toString();
		}catch(java.security.NoSuchAlgorithmException e){
			System.out.println("MD5 error :(");
		}
		return null;
	}

	public Person getPerson(){
		return this.person;
	}

}
