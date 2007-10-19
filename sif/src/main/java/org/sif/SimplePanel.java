package org.sif;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;

public class SimplePanel extends JPanel {

	private static final DataSourceFactory dsf = new DataSourceFactory();
	private static String dsName = "source";

	private MsgPanel msgPanel;
	private UIPanel panel;
	private OutsideFrame frame;

	/**
	 * This is the default constructor
	 */
	public SimplePanel(OutsideFrame frame, UIPanel panel) {
		this.panel = panel;
		this.frame = frame;
		initialize(panel);
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize(UIPanel panel) {

		this.setLayout(new BorderLayout());

		Component comp = panel.getComponent();
		this.add(comp, BorderLayout.CENTER);
		msgPanel = new MsgPanel(panel.getIconURL());
		msgPanel.setTitle(panel.getTitle());

		this.add(msgPanel, BorderLayout.NORTH);

		panel.initialize();
		validateInput();
	}

	void validateInput() {
		String err = panel.validate();
		if (err != null) {
		} else if ((err == null) && (panel instanceof SQLUIPanel)) {
			SQLUIPanel sqlPanel = (SQLUIPanel) panel;
			// Type check
			int[] types = sqlPanel.getFieldTypes();
			String[] values = sqlPanel.getValues();
			for (int i = 0; i < types.length; i++) {
				switch (types[i]) {
				case SQLUIPanel.INT:
					try {
						Integer.parseInt(values[i]);
					}catch (NumberFormatException e) {
						err = sqlPanel.getFieldNames()[i] + " must be an int expression";
					}
					break;
				case SQLUIPanel.DOUBLE:
					try {
						Double.parseDouble(values[i]);
					}catch (NumberFormatException e) {
						err = sqlPanel.getFieldNames()[i] + " must be a floating point expression";
					}
					break;
				}
			}

			if (err == null) {
				// Evaluate SQL
				ObjectMemoryDriver omd = new ObjectMemoryDriver(sqlPanel
						.getFieldNames(), getGDMSTypes(sqlPanel.getFieldTypes()));
				omd.addValues(getGDMSValues(values));
				if (dsf.existDS(dsName)) {
					dsf.remove(dsName);
				}
				dsf.registerDataSource(dsName, new ObjectSourceDefinition(omd));

				String[] validationExpr = sqlPanel.getValidationExpressions();
				String[] errMsgs = sqlPanel.getErrorMessages();
				if (validationExpr != null) {
					try {
						for (int i = 0; i < errMsgs.length; i++) {
							DataSource result = dsf
									.executeSQL("select * from source where "
											+ validationExpr[i]);
							result.open();
							long rowCount = result.getRowCount();
							result.cancel();
							if (rowCount == 0) {
								err = errMsgs[i];
								if (err == null) {
									err = "Invalid input";
								}
								break;
							}

						}
					} catch (SyntaxException e) {
						msgPanel.setText("Could not validate dialog! : "
								+ e.getMessage());
					} catch (DriverLoadException e) {
						msgPanel.setText("Could not validate dialog! : "
								+ e.getMessage());
					} catch (NoSuchTableException e) {
						msgPanel.setText("Could not validate dialog! : "
								+ e.getMessage());
					} catch (ExecutionException e) {
						msgPanel.setText("Could not validate dialog! : "
								+ e.getMessage());
					} catch (DriverException e) {
						msgPanel.setText("Could not validate dialog! : "
								+ e.getMessage());
					}
				}
			}
		} else {
			err = null;
		}

		if (err != null) {
			msgPanel.setText(err);
			frame.cannotContinue();
		} else {
			msgPanel.setText("Ok");
			frame.canContinue();
		}
	}

	private Value[] getGDMSValues(String[] values) {
		Value[] row = new Value[values.length];
		for (int i = 0; i < row.length; i++) {
			if (values[i] == null) {
				row[i] = ValueFactory.createNullValue();
			} else {
				if (values[i].length() == 0) {
					row[i] = ValueFactory.createNullValue();
				} else {
					row[i] = ValueFactory.createValue(values[i]);
				}
			}
		}
		return row;
	}

	private Type[] getGDMSTypes(int[] fieldTypes) {
		Type[] types = new Type[fieldTypes.length];
		for (int i = 0; i < types.length; i++) {
			try {
				switch (fieldTypes[i]) {
				case SQLUIPanel.DOUBLE:
					types[i] = TypeFactory.createType(Type.DOUBLE);
					break;
				case SQLUIPanel.INT:
					types[i] = TypeFactory.createType(Type.INT);
					break;
				case SQLUIPanel.STRING:
					types[i] = TypeFactory.createType(Type.STRING);
					break;
				}
			} catch (InvalidTypeException e) {
				throw new RuntimeException("bug", e);
			}
		}

		return types;
	}

}
