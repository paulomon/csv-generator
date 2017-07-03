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

import com.opencsv.CSVWriter;

import csv.generator.db.ConnectionFactory;
import csv.generator.log.Log;

public class Generator {

	private Connection conn;

	public Generator() throws SQLException {
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
				conn.close();
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

			Log.logger.info("Executando script e recuperando dados");
			Log.logger.debug(sql.toString());
			ResultSet resultSet = getResultSet(sql.toString());
			try {
				Log.logger.info("Gerando arquivo csv " + fileObject.getName().replace(".sql", ".csv"));
				writer(resultSet, fileObject.getName().replace(".sql", ".csv"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public ResultSet getResultSet(String sql) throws SQLException{
		try{
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			return rs;
		}catch(SQLException e){
			Log.logger.error("Erro ao exercutar script no banco de dados");
			throw e;
		}
	}

	public void writer(ResultSet resultSet, String fileName) throws IOException, SQLException {
		File path = new File(Init.getDirPaste());
		
		if(!path.exists() || !path.isDirectory()){
			Log.logger.error("Diretório[dirPaste] " + path + " não existe ou não é um diretório valido");
			conn.close();
			System.exit(1);
		}
	
		CSVWriter writer = new CSVWriter(new FileWriter(path + "/" + fileName));
		writer.writeAll(resultSet, true);
		writer.close();
	}

}
