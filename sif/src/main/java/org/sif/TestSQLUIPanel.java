package org.sif;

public class TestSQLUIPanel extends TestUIPanel implements SQLUIPanel {

	public static void main(String[] args) {
		SIFDialog dlg = UIFactory.getSimpleDialog(new TestSQLUIPanel(), null);
		dlg.setSize(300, 300);
		dlg.setModal(true);
		dlg.setVisible(true);
		System.out.println(dlg.isAccepted());
	}

	public String[] getErrorMessages() {
		return new String[] { "Text must be like a*f" };
	}

	public String[] getFieldNames() {
		return new String[] { "txt" };
	}

	public int[] getFieldTypes() {
		return new int[] { STRING };
	}

	public String[] getValidationExpressions() {
		return new String[] { " txt like 'a%f'" };
	}

	public String[] getValues() {
		return new String[] { txt.getText() };
	}

	public void setValues(String[] fieldNames) {
		txt.setText(fieldNames[0]);
	}

	public String getId() {
		return "org.sif.test";
	}

	public void setValue(String fieldName, String fieldValue) {
		if (fieldName.equals("txt")) {
			txt.setText(fieldValue);
		}
	}

}
