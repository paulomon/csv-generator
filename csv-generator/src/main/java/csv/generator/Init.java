package csv.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.logging.log4j.Level;

import csv.generator.log.Log;

public class Init {
	
	private static Properties info;
	private static File dirPaste;
	
	public Init() throws IOException, SQLException {
		System.setProperty("log4j.configurationFile", "Log4j2.xml");
		
		Log.logger.info("Iniciando processamento");
		getInfo();
		new Generator().generate();
		Log.logger.info("Processo encerrado");
	}
	
	public static void main(String[] args) throws SQLException, IOException {
		new Init();
	}
	
	private void getInfo() throws IOException {
		Log.logger.info("Recuperando connection.properties");
		File file = new File("connection.properties");
		
		if(!file.exists()){
			Log.logger.error("connection.properties não encontrado");
			System.exit(1);
		}
		
		info = new Properties();
		info.load(new FileInputStream(file));
		
		Log.logger.info("Recuperando dirPaste");
		try{
			dirPaste = new File(info.getProperty("dirPaste"));	
		}catch(NullPointerException e){
			Log.logger.error("Tag dirPaste não encontrada");
			System.exit(1);
		}
		
		if(!dirPaste.exists() || !dirPaste.isDirectory()){
			Log.logger.error("Diretório[dirPaste] " + Init.getDirPaste() + " não existe ou não é um diretório valido");
			System.exit(1);
		}
	}
	
	public static Properties getInfoConnection(){
		return info;
	}
	
	public static ArrayList<String> getFiles(){
		int i = 0;
		
		ArrayList<String> files = new ArrayList<String>();
		
		Log.logger.info("Recuperando lista de arquivos de scripts a serem lidos");
		while(info.getProperty("file."+i) != null){
			files.add(info.getProperty("file."+i));
			i++;
		}
		
		if(files.size() == 0){
			Log.logger.error("Nenhum arquivo de script encontrado");
			System.exit(1);
		}
		
		if(Log.logger.getLevel().compareTo(Level.DEBUG) == 0){
			Log.logger.debug("Arquivos recuperados");
			for(String arq : files){
				Log.logger.debug(arq);
			}
		}
		return files;
	}
	
	public static File getDirPaste(){
		return dirPaste;
	}
}
