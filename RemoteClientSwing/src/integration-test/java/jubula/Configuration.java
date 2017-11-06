package jubula;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

	private Properties mappings;
	private Properties configuration;

	public Configuration() throws IOException {
		File mappingFile = new File("src/integration-test/resources/mappings.properties");
		mappings = new Properties();
		mappings.load(new FileInputStream(mappingFile));

		File configurationFile = new File("src/integration-test/resources/configuration.properties");
		configuration = new Properties();
		configuration.load(new FileInputStream(configurationFile));
	}

	public String getMapping(String key) {
		return mappings.getProperty(key);
	}

	public String getId() {
		return configuration.getProperty("id");
	}

	public String getExecutable() {
		return configuration.getProperty("executable");
	}

	public String getWorkDir() {
		return configuration.getProperty("workDir");
	}

	public String getHost() {
		return configuration.getProperty("host");
	}

	public int getPort() {
		return Integer.valueOf(configuration.getProperty("port"));
	}
	
}
