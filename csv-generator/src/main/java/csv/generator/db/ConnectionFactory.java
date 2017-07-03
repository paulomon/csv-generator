package csv.generator.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.Level;

import csv.generator.Init;
import csv.generator.log.Log;

public class ConnectionFactory {
	
	private static Connection connection;

	public static Connection getConnection() throws SQLException{
		try {
			connection = DriverManager.getConnection(Init.getInfoConnection().getProperty("url"), Init.getInfoConnection());
			return connection;
		} catch (SQLException e) {
			Log.logger.error("Não foi possível se conectar ao banco de dados");
			Log.logger.error(Init.getInfoConnection());
			Log.logger.catching(Level.DEBUG, e);
			throw e;
		}
	}
}
