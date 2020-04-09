package socket;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {

	public static Properties loadProperties(){
		Properties prop = new Properties();
		FileInputStream fileIn;
		try {
			fileIn = new FileInputStream(PropertiesUtil.class.getClassLoader().getResource("").getPath()+"/config.properties");
			prop.load(fileIn);
			fileIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	
	}
}
