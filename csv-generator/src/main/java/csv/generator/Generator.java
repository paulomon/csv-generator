package csv.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.logging.log4j.Level;

import com.opencsv.CSVWriter;

import csv.generator.db.ConnectionFactory;
import csv.generator.log.Log;

public class Generator {

	private Connection conn;

	public Generator() throws IOException {
		Log.logger.info("Recuperando conexão com o banco de dados");
		conn = ConnectionFactory.getConnection();	
	}

	public void generate() throws IOException, SQLException {

		ArrayList<String> files = Init.getFiles();

		for (String file : files) {
			Log.logger.info("Iniciando leitura do arquivo " + file);
			File fileObject = new File(file);

			if (!fileObject.exists() || !fileObject.isFile()) {
				Log.logger.error("Arquivo " + file + " não existe ou não é um diretório valido");
				System.exit(1);
			}

			FileReader fileReader = new FileReader(fileObject);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			Log.logger.info("Recuperando script do arquivo lido");
			StringBuilder sql = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				sql.append(line);
			}

			bufferedReader.close();
			fileReader.close();

			Log.logger.info("Executando script");
			ResultSet resultSet = getResultSet(sql.toString());
			try {
				writer(resultSet, fileObject.getName().replace(".sql", ".csv"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public ResultSet getResultSet(String sql) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(sql);
		return ps.executeQuery();
	}

	public void writer(ResultSet resultSet, String fileName) throws IOException, SQLException {
		CSVWriter writer = new CSVWriter(new FileWriter(Init.getDirPaste() + fileName));
		writer.writeAll(resultSet, true);
		writer.close();
	}

}
