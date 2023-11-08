package ostara.data;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.*;

import ostara.Main;

public class Client{
	int id;
	String name;
	String location;
	private String auth;

	public Client(int id, String name, String location, String auth){
		this.id = id;
		this.name = name;
		this.location = location;
		this.auth = auth;
	}

	public int getID(){
		return this.id;
	}

	public String getName(){
		return this.name;
	}

	public String toString(){
		return this.getName();
	}

	public boolean verify(String token){
		DecodedJWT decodedJWT;
		try{
			Algorithm algorithm = Algorithm.HMAC512(this.auth);
			JWTVerifier verifier = JWT.require(algorithm).withIssuer(Main.getJWTIssuer()).build();
			decodedJWT = verifier.verify(token);
		}catch(JWTVerificationException e){
			return false;
		}
		return true;
	}

}
