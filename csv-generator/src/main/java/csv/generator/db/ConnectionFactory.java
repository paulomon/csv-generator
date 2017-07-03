package csv.generator.db;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.logging.log4j.Level;

import csv.generator.Init;
import csv.generator.log.Log;

public class ConnectionFactory {

	public static Connection getConnection() {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(Init.getInfoConnection().getProperty("url"), Init.getInfoConnection());
		} catch (Exception e) {
			Log.logger.error("Não foi possível se conectar ao banco de dados");

			if (Log.logger.getLevel().compareTo(Level.DEBUG) == 0) {
				Log.logger.debug(Init.getInfoConnection());
				e.printStackTrace();
			}
			System.exit(1);
		}
		return connection;
	}
}
