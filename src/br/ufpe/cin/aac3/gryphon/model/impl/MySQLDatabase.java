package br.ufpe.cin.aac3.gryphon.model.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import br.ufpe.cin.aac3.gryphon.model.Database;

public final class MySQLDatabase extends Database {

	public MySQLDatabase(String host, int port, String username, String password, String dbName) {
		super(host, port, username, password, dbName);
		
		this.sgbd = SGBD.MYSQL;
		this.jdbcURL = String.format("jdbc:mysql://%s:%s/%s", host, port, dbName);
		this.jdbcDriverClass = "com.mysql.jdbc.Driver";
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