package ostara.server;

import java.time.Instant;

import ostara.data.User;
import ostara.server.Auth;

public class Session{
	private User user;
	private String sessionToken;
	private Instant expiry;

	public Session(User user){
		this.user = user;
		this.sessionToken = Auth.generateToken(32);
		this.expiry = Instant.now().plusSeconds(Auth.SESSIONEXPIRY);
	}

	public boolean isValid(){
		return Instant.now().isBefore(this.expiry);
	}

	public String getSessionToken(){
		return this.sessionToken;
	}

	public User getUser(){
		return this.user;
	}
}
