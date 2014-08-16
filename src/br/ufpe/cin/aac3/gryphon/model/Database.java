package br.ufpe.cin.aac3.gryphon.model;

import java.io.File;
import java.net.URI;

import br.ufpe.cin.aac3.gryphon.Gryphon;
import br.ufpe.cin.aac3.gryphon.GryphonConfig;

import com.hp.hpl.jena.rdf.model.InfModel;

public abstract class Database {
	protected enum SGBD {
		MYSQL,
		POSTGRESQL
	}
	
	protected int port = 0;
	protected String host = null;
	protected String username = null;
	protected String password = null;
	protected String dbName = null;
	protected String jdbcURL = null;
	protected String jdbcDriverClass = null;
	protected InfModel model = null;
	protected SGBD sgbd = null;
	
	public Database(String host, int port, String username, String password, String dbName) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.dbName = dbName;
	}

	public boolean testConnection() {
		return false;
	}

	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getDbName() {
		return dbName;
	}
	
	public String getJdbcURL() {
		return jdbcURL;
	}
	
	public String getJdbcDriverClass() {
		return jdbcDriverClass;
	}
	
	public InfModel getModel() {
		return model;
	}
}