package org.orbisgis.configurations;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.orbisgis.configuration.IConfiguration;

public class TestConfig implements IConfiguration {

	private static final File file = new File(TestConfig.class.getResource(
			"test.cfg").getFile());

	private JPanel panel;
	private JTextField field;

	public TestConfig() {
		panel = new JPanel();
		field = new JTextField();
		field.setPreferredSize(new Dimension(300, 30));
		panel.add(field);
	}

	@Override
	public JComponent getComponent() {
		return panel;
	}

	@Override
	public void load() {
		try {
			BufferedReader rdr = new BufferedReader(new FileReader(file));
			field.setText(rdr.readLine());
			rdr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void save() {
		try {
			BufferedWriter wr = new BufferedWriter(new FileWriter(file));
			wr.write(field.getText());
			wr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
