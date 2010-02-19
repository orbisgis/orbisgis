package org.gdms.tool;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.driver.DriverException;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.SQLProcessor;
import org.gdms.sql.strategies.SemanticException;

public class Shell {

	private final String EOL = System.getProperty("line.separator");

	static DataSourceFactory dsf = new DataSourceFactory();

	public Shell() {
	}

	public void execute(String scriptFileName) throws SemanticException,
			DriverException, ParseException, IOException {
		SQLProcessor sqlProcessor = new SQLProcessor(dsf);
		String[] instructions = sqlProcessor
				.getScriptInstructions(readScriptFile(scriptFileName));
		for (String instruction : instructions) {
			try {
				sqlProcessor.execute(instruction, null);
			} catch (ExecutionException e) {
				throw new RuntimeException("Error in " + instruction, e);
			} catch (SemanticException e) {
				throw new RuntimeException("Error in " + instruction, e);
			} catch (DriverException e) {
				throw new RuntimeException("Error in " + instruction, e);
			}
		}
	}

	public String readScriptFile(String scriptFileName) throws IOException {
		final BufferedReader in = new BufferedReader(new FileReader(
				scriptFileName));
		String line;
		StringBuffer ret = new StringBuffer();
		while ((line = in.readLine()) != null) {
			ret.append(line + EOL);
		}
		in.close();

		return ret.toString();
	}

	public static void main(String[] args) throws SemanticException,
			DriverException, ParseException, IOException {

		if (args != null) {
			String script = args[0];

			if (script != null) {
				new Shell().execute(script);
			}
		}
	}
}
