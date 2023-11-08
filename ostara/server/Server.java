package ostara.server;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.security.KeyStore;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import com.sun.net.httpserver.HttpsParameters;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.ListIterator;
import java.lang.StringBuilder;
import java.net.URI;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import java.sql.SQLException;

import ostara.data.Client;
import ostara.data.Person;
import ostara.data.Data;
import ostara.data.EventType;
import ostara.Database;
import ostara.exceptions.TokenNotFoundException;
import ostara.exceptions.InvalidLoginException;
import ostara.Main;
import ostara.server.Auth;

/*
import com.auth0.jwt.*;
import com.auth0.jwt.algorithms.*;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.impl.*;
import com.auth0.jwt.interfaces.*;
*/

public class Server{

	static JWT jwt;

	public static SSLContext createSSLContext(String keystorePath, String keystorePassword) throws Exception {
		// Load the keystore
		KeyStore keyStore = KeyStore.getInstance("JKS");
		try(FileInputStream fis = new FileInputStream(keystorePath)){
			keyStore.load(fis, keystorePassword.toCharArray());
		}

		// Initialize key manager factory with the keystore
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(keyStore, keystorePassword.toCharArray());

		// Create the SSL context
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(kmf.getKeyManagers(), null, null);
		return sslContext;
	}

	public static HttpsServer createHttpsServer(String keystorePath, String keystorePassword, int port) throws Exception{
		SSLContext sslContext = createSSLContext(keystorePath, keystorePassword);

		// Create the HttpsServer with the SSLContext
		HttpsServer httpsServer = HttpsServer.create(new InetSocketAddress(port), 0);
		httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext){

			@Override
			public void configure(HttpsParameters params){
				params.setSSLParameters(sslContext.getDefaultSSLParameters());
			}
		});
		return httpsServer;
	}

	private static HashMap<String, String> decodeQueries(URI uri){
		HashMap<String, String> r = new HashMap<String, String>();
		String[] ii;
		for(String i : uri.getQuery().split("&")){
			ii = i.split("=");
			r.put(ii[0], ii[1]);
		}
		return r;
	}

	public static void main(String keystorePath, String keystorePassword, int port){

		Auth auth = new Auth();

		try{
			HttpsServer httpsServer = createHttpsServer(keystorePath, keystorePassword, port);
			jwt = new JWT();
			Algorithm algorithm = Algorithm.HMAC512(Main.getJWTSecret());

			// Set up request handlers
			httpsServer.createContext("/", (HttpExchange exchange) -> {
				String response = "Hello, this is the Ostara API server.";
				exchange.sendResponseHeaders(200, response.length());
				exchange.getResponseBody().write(response.getBytes());
				exchange.close();
			});

			 httpsServer.createContext("/api", (HttpExchange exchange) -> {
				StringBuilder log = new StringBuilder("Request recieved. ");
				String response = "Hello, HTTPS server.";
				try{
					String token = decodeQueries(exchange.getRequestURI()).get("token");
					if(token==null){
						log.append("Error: Token not found.");
						throw new TokenNotFoundException();
					}
					DecodedJWT decodedJWT;
					decodedJWT = jwt.decodeJwt(token);
					Client client = Data.getClient(Integer.valueOf(decodedJWT.getClaim("client").toString().replace("\"", "")));
					if(!client.verify(token)){
						log.append("Error: Invalid token signature.");
						response = "Invalid token signature";
					}else{
						String action = decodedJWT.getClaim("action").toString().replace("\"", "");
						log.append(action + "\t");
						String tokenValue;
						log.append("Action: " + action + " ");
						switch(action){
							case "addEvent":
								tokenValue = decodedJWT.getClaim("token").toString().replace("\"", "");
								String eventType = decodedJWT.getClaim("eventType").toString().replace("\"", "");
								log.append(String.format("token ID: %s\t event type: %s\t client ID %s", tokenValue, eventType, client.toString() ));
								Database.insertEvent(tokenValue, eventType, client.getID());
								response = "OK";
								break;
							case "getName":
								tokenValue = decodedJWT.getClaim("token").toString().replace("\"", "");
								Person person = Database.getPerson(tokenValue);
								log.append(String.format("token value: %s\tname: %s", tokenValue, person.toString()));
								response = JWT.create().withIssuer(Main.getJWTIssuer()).withClaim("name", person.getName()).sign(algorithm);
								break;
							case "getEventTypes":
								log.append(String.format("event types requested by: client %s", decodedJWT.getClaim("client")));
								response = JWT.create().withIssuer(Main.getJWTIssuer()).withClaim("eventTypeNames", Data.getEventTypeNames()).sign(algorithm);
								break;
							default:
								response = String.format("Illegal action: %s\n", action);
						}
					}
				}catch(NumberFormatException e){
					log.append("Error: Illegal number.");
				}catch(TokenNotFoundException e){
					log.append("Error: no token.");
				}catch(JWTCreationException e){
					log.append("Error: cannot create token.");
				}catch(IllegalArgumentException e){
					log.append("Error: Illegal argument");
					e.printStackTrace();
				}catch(SQLException e){
					log.append("Error: SQL Exception");
					e.printStackTrace();
				}
				System.out.println(log);
				exchange.sendResponseHeaders(200, response.length());
				exchange.getResponseBody().write(response.getBytes());
				exchange.close();
			});




			 httpsServer.createContext("/admin", (HttpExchange exchange) -> {
				StringBuilder log = new StringBuilder("Request recieved. ");
				String response = "Hello, admin HTTPS server.";
				try{
					String token = decodeQueries(exchange.getRequestURI()).get("token");
					if(token==null){
						log.append("Error: Token not found.");
						throw new TokenNotFoundException();
					}
					DecodedJWT decodedJWT;
					decodedJWT = jwt.decodeJwt(token);
					String action = decodedJWT.getClaim("action").toString().replace("\"", "");
					log.append(action + "\t");
					log.append("Action: " + action + " ");
					String sessionToken = null;
					switch(action){
						case "login":
							String username = decodedJWT.getClaim("user").toString().replace("\"", "");
							String password = decodedJWT.getClaim("pass").toString().replace("\"", "");	//IS MD5 and salted, don't worry
							String salt = decodedJWT.getClaim("salt").toString().replace("\"", "");

							log.append(String.format("user %s login ", username));
							sessionToken = auth.authenticate(username, password, salt).getSessionToken();
							if(sessionToken != null){
								response = JWT.create().withIssuer(Main.getJWTIssuer()).withClaim("status", "granted").withClaim("sessionToken", sessionToken).sign(algorithm);
								log.append("successful");
							}else{
								response = JWT.create().withIssuer(Main.getJWTIssuer()).withClaim("status", "denied").sign(algorithm);
								log.append("unsuccessful");
							}
							break;
						case "logout":
							sessionToken = decodedJWT.getClaim("sessionToken").toString().replace("\"", "");
							log.append(String.format("Session %s invalidated ", sessionToken));
							if(auth.validateSession(sessionToken)){
								log.append("successfully");
								auth.removeSession(sessionToken);
								response = JWT.create().withIssuer(Main.getJWTIssuer()).withClaim("status", "success").sign(algorithm);
							}else{
								log.append("unsuccessfully");
								response = JWT.create().withIssuer(Main.getJWTIssuer()).withClaim("status", "fail").sign(algorithm);
							}
							break;
						case "whoami":
							sessionToken = decodedJWT.getClaim("sessionToken").toString().replace("\"", "");
							System.out.println(auth.validateSession(sessionToken));
							String who = "Anon";
							if(auth.validateSession(sessionToken)){
								who = auth.getSession(sessionToken).getUser().getPerson().getName();
							}else{
							}
							log.append(String.format("Session key: %s\tname: %s", sessionToken, who));
							response = JWT.create().withIssuer(Main.getJWTIssuer()).withClaim("name", who).sign(algorithm);
							break;
						default:
							response = String.format("Illegal action: %s\n", action);
					}
				}catch(NumberFormatException e){
					log.append("Error: Illegal number.");
					//e.printStackTrace();
				}catch(TokenNotFoundException e){
					log.append("Error: no token.");
				}catch(JWTCreationException e){
					log.append("Error: cannot create token.");
				}catch(IllegalArgumentException e){
					log.append("Error: Illegal argument");
					e.printStackTrace();
}
				System.out.println(log);
				exchange.sendResponseHeaders(200, response.length());
				exchange.getResponseBody().write(response.getBytes());
				exchange.close();
			});





/*
			 httpsServer.createContext("/admin", (HttpExchange exchange) -> {
				String response = "?";

				exchange.sendResponseHeaders(200, response.length());
				exchange.getResponseBody().write(response.getBytes());
				exchange.close();
			});
*/
			// Start the server
			httpsServer.start();
			System.out.println("Server started on https://localhost:" + port);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
