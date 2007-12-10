package org.sif.multiInputPanel;

import java.awt.Component;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.sif.SQLUIPanel;

public class MultiInputPanel implements SQLUIPanel {

	private String id;
	private URL url;
	private String title;

	private ArrayList<String> expressions = new ArrayList<String>();
	private ArrayList<String> errors = new ArrayList<String>();
	private ArrayList<Input> inputs = new ArrayList<Input>();
	private HashMap<String, Input> nameInput = new HashMap<String, Input>();
	private InputPanel comp;
	private String infoText;

	public MultiInputPanel(String title) {
		this(null, title);
	}

	public MultiInputPanel(String id, String title) {
		this.id = id;
		this.setTitle(title);
	}

	public void addInput(String name, String text, String initialValue,
			InputType type) {
		Input input = new Input(name, text, initialValue, type);
		inputs.add(input);
		nameInput.put(name, input);
	}

	public void addText(String text) {
		Input input = new Input(null, text, null, new NoInputType());
		inputs.add(input);
	}

	public void addValidationExpression(String sql, String errorMsg) {
		this.expressions.add(sql);
		this.errors.add(errorMsg);
	}

	public void setIcon(URL url) {
		this.url = url;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setInfoText(String infoText) {
		this.infoText = infoText;
	}

	public String getInfoText() {
		return infoText;
	}

	public String[] getErrorMessages() {
		return errors.toArray(new String[0]);
	}

	public String[] getFieldNames() {
		ArrayList<String> ret = new ArrayList<String>();
		for (Input input : inputs) {
			if (input.getType().isPersistent()) {
				ret.add(input.getName());
			}
		}

		return ret.toArray(new String[0]);
	}

	public int[] getFieldTypes() {
		String[] fieldNames = getFieldNames();
		int[] ret = new int[fieldNames.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = nameInput.get(fieldNames[i]).getType().getType();
		}

		return ret;
	}

	public String getId() {
		return id;
	}

	public String validateInput() {
		return null;
	}

	public String[] getValidationExpressions() {
		return expressions.toArray(new String[0]);
	}

	public String[] getValues() {
		String[] fieldNames = getFieldNames();
		String[] ret = new String[fieldNames.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = nameInput.get(fieldNames[i]).getType().getValue();
		}

		return ret;
	}

	public void setValue(String fieldName, String fieldValue) {
		Input input = nameInput.get(fieldName);
		if (input != null) {
			input.getType().setValue(fieldValue);
		}
	}

	public Component getComponent() {
		if (comp == null) {
			comp = new InputPanel(inputs);
		}

		return comp;
	}

	public URL getIconURL() {
		return url;
	}

	public String getTitle() {
		return title;
	}

	public String initialize() {
		return null;
	}

	public String getInput(String inputName) {
		Input input = nameInput.get(inputName);
		if (input != null) {
			return input.getType().getValue();
		} else {
			return null;
		}
	}

	public void group(String title, String... inputs) {
		for (String inputName : inputs) {
			nameInput.get(inputName).setGroup(title);
		}
	}

	private class NoInputType implements InputType {

		public Component getComponent() {
			return null;
		}

		public int getType() {
			return STRING;
		}

		public String getValue() {
			return null;
		}

		public boolean isPersistent() {
			return false;
		}

		public void setValue(String value) {
		}

	}

}
