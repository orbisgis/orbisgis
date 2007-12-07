package org.sif;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.util.HashMap;

import javax.swing.JPanel;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.ExecutionException;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.SyntaxException;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
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

	private static final String LAST_INPUT = "lastInput";
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
		JPanel uiPanel = new JPanel();

		uiPanel.setLayout(new BorderLayout());

		Component comp = panel.getComponent();
		uiPanel.add(comp, BorderLayout.CENTER);
		msgPanel = new MsgPanel(panel.getIconURL());
		msgPanel.setTitle(panel.getTitle());

		this.setLayout(new BorderLayout());
		this.add(msgPanel, BorderLayout.NORTH);

		JPanel centerPanel;
		if (panel instanceof SQLUIPanel) {
			SQLUIPanel sqlPanel = (SQLUIPanel) panel;
			String id = sqlPanel.getId();
			if (id != null) {
				JPanel controlPanel;
				try {
					controlPanel = new ControlPanel(sqlPanel);
					JPanel split = new JPanel();
					split.setLayout(new BorderLayout());
					split.add(controlPanel, BorderLayout.WEST);
					split.add(uiPanel, BorderLayout.CENTER);
					centerPanel = split;
				} catch (DriverException e) {
					centerPanel = uiPanel;
				} catch (DataSourceCreationException e) {
					centerPanel = uiPanel;
				}
			} else {
				centerPanel = uiPanel;
			}
		} else {
			centerPanel = uiPanel;
		}

		this.add(centerPanel, BorderLayout.CENTER);
	}

	public void initialize() {
		String err = null;
		try {
			err = panel.initialize();
		} catch (Exception e) {
			err = "Cannot initialize dialog: " + e.getMessage();
		}
		if (err == null) {
			validateInput();
		} else {
			msgPanel.setText(err);
			frame.cannotContinue();
		}
	}

	void validateInput() {
		String err = null;
		try {
			err = panel.validate();
		} catch (Exception e) {
			err = "Error validating: " + e.getMessage();
		}
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
					} catch (NumberFormatException e) {
						err = sqlPanel.getFieldNames()[i]
								+ " must be an int expression";
					}
					break;
				case SQLUIPanel.DOUBLE:
					try {
						Double.parseDouble(values[i]);
					} catch (NumberFormatException e) {
						err = sqlPanel.getFieldNames()[i]
								+ " must be a floating point expression";
					}
					break;
				}
			}

			if (err == null) {
				// Evaluate SQL
				registerUISource(sqlPanel);

				String[] validationExpr = sqlPanel.getValidationExpressions();
				String[] errMsgs = sqlPanel.getErrorMessages();
				if (validationExpr != null) {
					try {
						for (int i = 0; i < errMsgs.length; i++) {
							DataSource result = UIFactory.dsf
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

	private void registerUISource(SQLUIPanel sqlPanel) {
		ObjectMemoryDriver omd = new ObjectMemoryDriver(sqlPanel
				.getFieldNames(), getGDMSTypes(sqlPanel.getFieldTypes()));
		omd.addValues(getGDMSValues(sqlPanel.getValues(), sqlPanel
				.getFieldTypes()));
		if (UIFactory.dsf.exists(dsName)) {
			UIFactory.dsf.remove(dsName);
		}
		UIFactory.dsf.registerDataSource(dsName,
				new ObjectSourceDefinition(omd));
	}

	private Value[] getGDMSValues(String[] values, int[] types) {
		Value[] row = new Value[values.length];
		for (int i = 0; i < row.length; i++) {
			if (values[i] == null) {
				row[i] = ValueFactory.createNullValue();
			} else {
				if (values[i].length() == 0) {
					row[i] = ValueFactory.createNullValue();
				} else {
					switch (types[i]) {
					case SQLUIPanel.STRING:
						row[i] = ValueFactory.createValue(values[i]);
						break;
					case SQLUIPanel.INT:
						row[i] = ValueFactory.createValue(Integer
								.parseInt(values[i]));
						break;
					case SQLUIPanel.DOUBLE:
						row[i] = ValueFactory.createValue(Double
								.parseDouble(values[i]));
						break;
					}
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

	public void saveInput() {
		if (panel instanceof SQLUIPanel) {
			SQLUIPanel sqlPanel = (SQLUIPanel) panel;
			String id = sqlPanel.getId();
			if (id != null) {
				registerUISource(sqlPanel);
				try {
					createLastInput(sqlPanel);
					registerLastInput(id);
					DataSource ds = UIFactory.dsf.getDataSource(LAST_INPUT);
					ds.open();
					ds.insertEmptyRow();
					String[] values = sqlPanel.getValues();
					for (int i = 0; i < values.length; i++) {
						ds.setString(0, i, values[i]);
					}
					ds.commit();
				} catch (DriverException e) {
					msgPanel.setText("Cannot save input");
				} catch (FreeingResourcesException e) {
				} catch (NonEditableDataSourceException e) {
					throw new RuntimeException("bug", e);
				} catch (DriverLoadException e) {
					throw new RuntimeException("bug", e);
				} catch (NoSuchTableException e) {
					throw new RuntimeException("bug", e);
				} catch (DataSourceCreationException e) {
					msgPanel.setText("Cannot save input");
				}
			}
		}
	}

	private void createLastInput(SQLUIPanel sqlPanel) throws DriverException {
		File lastInputFile = getLastInputFile(sqlPanel.getId());
		if (lastInputFile.exists()) {
			lastInputFile.delete();
		}
		FileSourceCreation fsc = new FileSourceCreation(lastInputFile,
				getMetadata(sqlPanel));
		UIFactory.dsf.getSourceManager().createDataSource(fsc);
	}

	private Metadata getMetadata(SQLUIPanel sqlPanel) {
		DefaultMetadata ddm = new DefaultMetadata();
		String[] names = sqlPanel.getFieldNames();
		for (int i = 0; i < names.length; i++) {
			try {
				ddm.addField(names[i], Type.STRING);
			} catch (InvalidTypeException e) {
				throw new RuntimeException("bug");
			}
		}

		return ddm;
	}

	private void registerLastInput(String id) {
		if (UIFactory.dsf.exists(LAST_INPUT)) {
			UIFactory.dsf.remove(LAST_INPUT);
		}
		UIFactory.dsf.getSourceManager().register(LAST_INPUT,
				getLastInputFile(id));
	}

	private File getLastInputFile(String id) {
		return new File(UIFactory.baseDir, id + "-last.csv");
	}

	public boolean loadInput(HashMap<String, String> inputs) {
		if (panel instanceof SQLUIPanel) {
			SQLUIPanel sqlPanel = (SQLUIPanel) panel;
			String id = sqlPanel.getId();
			if (id != null) {
				String inputName = inputs.get(id);
				if (inputName != null) {
					PersistentPanelDecorator pd = new PersistentPanelDecorator(
							sqlPanel);
					return pd.loadEntry(inputName);
				} else {
					File lastInputFile = getLastInputFile(id);
					if (lastInputFile.exists()) {
						registerLastInput(id);
						try {
							DataSource ds = UIFactory.dsf
									.getDataSource(LAST_INPUT);
							ds.open();
							if (ds.getRowCount() > 0) {
								String[] fieldNames = ds.getFieldNames();
								for (String fieldName : fieldNames) {
									sqlPanel.setValue(fieldName, ds.getString(
											0, fieldName));
								}
							}
							ds.cancel();
						} catch (DriverException e) {
							msgPanel.setText("Cannot restore last input");
						} catch (DriverLoadException e) {
							msgPanel.setText("Cannot restore last input");
						} catch (NoSuchTableException e) {
							msgPanel.setText("Cannot restore last input");
						} catch (DataSourceCreationException e) {
							msgPanel.setText("Cannot restore last input");
						}
					}
				}
			}
		}
		return false;
	}

}
