package br.ufpe.cin.aac3.gryphon.model.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import br.ufpe.cin.aac3.gryphon.model.Database;

public final class PostgreSQLDatabase extends Database {

	public PostgreSQLDatabase(String host, int port, String username, String password, String dbName) {
		super(host, port, username, password, dbName);
		
		this.sgbd = SGBD.POSTGRESQL;
		this.jdbcURL = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);
		this.jdbcDriverClass = "org.postgresql.Driver";
	}
	
	@Override
	public boolean testConnection() {
		Properties props = new Properties();
		props.put("user", username);
		props.put("password", password);
		
		try {
			Connection connection = DriverManager.getConnection(jdbcURL, props);
			return connection != null && !connection.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}