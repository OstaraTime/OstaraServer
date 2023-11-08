package ostara.server;

import java.util.HashMap;
import java.lang.StringBuilder;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;

import ostara.server.Session;
import ostara.data.Data;
import ostara.exceptions.InvalidLoginException;

public class Auth{

	private HashMap<String, Session> sessions;
	public static long SESSIONEXPIRY = 3600;

	public Auth(){
		this.sessions = new HashMap<String, Session>();
	}

	public void addSession(Session session){
		this.sessions.put(session.getSessionToken(), session);
	}

	public void removeSession(String session){
		this.sessions.remove(session);
	}

	public boolean validateSession(String sessionToken){
		if(!this.sessions.containsKey(sessionToken)){
			return false;
		}
		Session session = this.sessions.get(sessionToken);
		if(!session.isValid()){
			this.sessions.remove(sessionToken);
			return false;
		}
		return true;
	}

	public Session getSession(String sessionToken){
		return this.sessions.get(sessionToken);
	}

	public Session authenticate(String username, String password, String salt){
		Session r;
		try{
			r = Data.authenticate(username, password, salt);
			this.addSession(r);
		}catch(InvalidLoginException e){
			r = null;
		}
		return r;

	}

	public static String generateToken(int size){
		StringBuilder r = new StringBuilder();
		try{
			SecureRandom srng = SecureRandom.getInstance("NativePRNG");
			int t;
			byte[] tb = new byte[4];
			for(int i=0; i<size; i++){
				srng.nextBytes(tb);
				t = tb[0];
				t<<=8;
				t+= tb[1];
				t<<=8;
				t+= tb[2];
				t<<=8;
				t+= tb[3];
				r.append(Integer.toHexString(t));
			}
		}catch(NoSuchAlgorithmException e){
			return "ERROR";
		}
		return r.toString();
	}

/*
	boolean verifyToken(String token){
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXUyJ9.eyJpc3MiOiJhdXRoMCJ9.AbIJTDMFc7yUa5MhvcP03nJPyCPzZtQcGEp-zWfOkEE";
		DecodedJWT decodedJWT;
		try {
		    Algorithm algorithm = Algorithm.RSA256(rsaPublicKey, rsaPrivateKey);
		    JWTVerifier verifier = JWT.require(algorithm)
		        // specify an specific claim validations
		        .withIssuer("auth0")
		        // reusable verifier instance
		        .build();
		    decodedJWT = verifier.verify(token);
		} catch (JWTVerificationException exception){
		    // Invalid signature/claims
		}
	}
*/

}
