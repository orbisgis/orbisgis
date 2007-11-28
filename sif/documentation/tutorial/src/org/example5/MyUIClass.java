package org.example5;

import java.awt.Component;
import java.net.URL;

import org.sif.SQLUIPanel;

public class MyUIClass implements SQLUIPanel {
	// our UI is like a table with a single row (retrieve name, value and type
	// for each 'field')
	MyJPanel myJPanel;

	public Component getComponent() {
		if (null == myJPanel) {
			myJPanel = new MyJPanel();
		}
		return myJPanel;
	}

	public URL getIconURL() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTitle() {
		return "5th example - next example is persistance in UI";
	}

	public void initialize() {
		// TODO Auto-generated method stub

	}

	public String validate() {
		if (null == myOwnGetSelectionMethod()) {
			return "enter a non null value in ComboBox !";
		}
		return null;
	}

	public String myOwnGetSelectionMethod() {
		return ((MyJPanel) getComponent()).getJComboBoxSelection();
	}

	public String myOwnGetTextFieldMethod() {
		return ((MyJPanel) getComponent()).getJTextFieldEntry();
	}

	public String[] getErrorMessages() {
		return new String[] { "the text field entry must be greater than 123" } ;
	}

	public String[] getFieldNames() {
		return new String[] { "aComboBox", "aTextField" };
	}

	public int[] getFieldTypes() {
		return new int[] { STRING, DOUBLE };
	}

	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getValidationExpressions() {
		return new String[] { "aTextField > 123"};
	}

	public String[] getValues() {
		return new String[] { myOwnGetSelectionMethod(),
				myOwnGetTextFieldMethod() };
	}

	public void setValue(String fieldName, String fieldValue) {
		if (fieldName.equals("aComboBox")) {
			fieldValue = myOwnGetSelectionMethod();
		} else if (fieldName.equals("aTextField")) {
			fieldValue = myOwnGetTextFieldMethod();
		} else {
			throw new Error();
		}
	}
}
