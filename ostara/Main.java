package ostara;

import java.sql.*;
import java.io.*;
import java.util.*;

import ostara.Database;
import ostara.data.Data;
import ostara.data.Person;
import ostara.data.Dept;
import ostara.data.Client;
import ostara.server.Server;
import ostara.server.Auth;

public class Main{

	static final String configFileDefault = "ostara.conf";

	private static String configFile = configFileDefault;
	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASS;
	private static String keystorePath;
	private static String keystorePass;
	private static int port;
	private static String JWTIssuer;
	private static String JWTSecret;
	private static int JWTLeeway;

	private static final String VERSION = "1.0.2";


	public static String getJWTIssuer(){
		return JWTIssuer;
	}

	public static String getJWTSecret(){
		return JWTSecret;
	}

	public static int getJWTLeeway(){
		return JWTLeeway;
	}

	public static void main(String[] args){
		// Print important stuff

		System.out.println("                                                                                                       ");
		System.out.println(" ██████╗ ███████╗████████╗ █████╗ ██████╗  █████╗     ███████╗███████╗██████╗ ██╗   ██╗███████╗██████╗ ");
		System.out.println("██╔═══██╗██╔════╝╚══██╔══╝██╔══██╗██╔══██╗██╔══██╗    ██╔════╝██╔════╝██╔══██╗██║   ██║██╔════╝██╔══██╗");
		System.out.println("██║   ██║███████╗   ██║   ███████║██████╔╝███████║    ███████╗█████╗  ██████╔╝██║   ██║█████╗  ██████╔╝");
		System.out.println("██║   ██║╚════██║   ██║   ██╔══██║██╔══██╗██╔══██║    ╚════██║██╔══╝  ██╔══██╗╚██╗ ██╔╝██╔══╝  ██╔══██╗");
		System.out.println("╚██████╔╝███████║   ██║   ██║  ██║██║  ██║██║  ██║    ███████║███████╗██║  ██║ ╚████╔╝ ███████╗██║  ██║");
		System.out.println(" ╚═════╝ ╚══════╝   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝    ╚══════╝╚══════╝╚═╝  ╚═╝  ╚═══╝  ╚══════╝╚═╝  ╚═╝");
		System.out.println("                                                                                                       ");
		System.out.println("                                   Version " + VERSION);


		// Load config
		try(FileInputStream fis = new FileInputStream(configFile)){
			Properties conf = new Properties();
			conf.load(fis);
			DB_URL = conf.getProperty("ostara.DB_URL");
			DB_USER = conf.getProperty("ostara.DB_USER");
			DB_PASS = conf.getProperty("ostara.DB_PASS");
			keystorePath = conf.getProperty("ostara.keystorePath");
			keystorePass = conf.getProperty("ostara.keystorePass");
			port = Integer.valueOf(conf.getProperty("ostara.port"));
			JWTIssuer = conf.getProperty("ostara.JWTIssuer");
			JWTSecret = conf.getProperty("ostara.JWTSecret");
			JWTLeeway = Integer.valueOf(conf.getProperty("ostara.JWTLeeway"));
		}catch (FileNotFoundException ex){
			System.out.printf("FATAL: Config file %s not found.\n", configFile);
			System.exit(1);
		}catch(IOException e){
			System.out.printf("FATAL: Error reading config file %s.\n", configFile);
			System.exit(2);
		}catch(NumberFormatException e){
			System.out.printf("FATAL: Illegal number entry in config file %s.\n", configFile);
			System.exit(3);
		}

		// Initialize database class
		Data.initialize();
		Database.initialize(DB_URL, DB_USER, DB_PASS);

		// Run server!
		Server.main(keystorePath, keystorePass, port);

		// Just a little something
		if(Math.random()*1000 < 5){
			System.out.println("This is an easter egg.");
		}

	}
}
