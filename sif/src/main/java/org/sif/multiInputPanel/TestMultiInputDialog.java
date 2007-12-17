package org.sif.multiInputPanel;

import org.sif.UIFactory;

public class TestMultiInputDialog {

	public static void main(String[] args) {
		MultiInputPanel mip = new MultiInputPanel("org.test",
				"Connect to database");
		mip.setInfoText("Introduce the connection parameters");
		mip.addInput("host", "Host:", "127.0.0.1", new StringType(10));
		mip.addInput("port", "Port:", "19", new IntType());
		mip.addText("Enter the name\n of the database");
		mip.addInput("database", "Database name:", null, new ComboBoxChoice("gdms",
				"template1", "template2"));
		mip.addInput("password", "Password:", "", new PasswordType(8));

		mip.addValidationExpression("host is not null",
				"you have to put some host");

//		mip.group("Host parameters", "host", "port", "database");
//		mip.group("Connection parameters", "password");

		if (UIFactory.showDialog(mip)) {
			System.out.println(mip.getInput("host"));
			System.out.println(mip.getInput("port"));
			System.out.println(mip.getInput("password"));
			System.out.println(mip.getInput("database"));
		}
	}
}
