package org.sif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;

public class ControlPanel extends JPanel {
	private JList list;
	private JButton btnSave;
	private JLabel collapsed;
	private DataSourceFactory dsf;
	private JButton btnDelete;
	private JTextField txtNew;
	private SQLUIPanel sqlPanel;

	public ControlPanel(SQLUIPanel panel, DataSourceFactory dsf)
			throws DriverException, DataSourceCreationException {
		this.dsf = dsf;
		this.sqlPanel = panel;
		this.setLayout(new CRFlowLayout());
		list = new JList(getContents());
		list.setVisible(false);
		list.setBorder(BorderFactory.createLoweredBevelBorder());
		list.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				updateButtons();
			}

		});
		list.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					loadEntry();
				}
			}

		});
		list.setPreferredSize(new Dimension(130, 250));
		this.add(list);
		this.add(new CarriageReturn());
		txtNew = new JTextField(8);
		txtNew.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				updateButtons();
			}

		});
		btnSave = new JButton("Save");
		btnSave.setMargin(new Insets(0, 0, 0, 0));
		btnSave.setVisible(false);
		btnSave.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				saveInput();
			}

		});
		this.add(btnSave);
		this.add(txtNew);
		this.add(new CarriageReturn());
		btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				removeInput(list.getSelectedIndex());
			}

		});
		this.add(btnDelete);
		this.setBackground(Color.white);
		this.setPreferredSize(new Dimension(15, 0));

		collapsed = new JLabel(getVertical("CLICK HERE"));
		collapsed.setHorizontalAlignment(JLabel.CENTER);
		// this.add(collapsed);
		listen(this);
		this.setBorder(BorderFactory.createRaisedBevelBorder());

		collapse();
		updateButtons();
	}

	private void loadEntry() {
		try {
			DataSource ds = ControlPanel.this.dsf.getDataSource(getFile());
			ds.open();
			int index = list.getSelectedIndex();
			for (int i = 1; i < ds.getFieldCount(); i++) {
				sqlPanel.setValue(ds.getFieldName(i), ds.getString(index, i));
			}
			ds.cancel();
		} catch (DriverException e) {
		} catch (DriverLoadException e) {
		} catch (DataSourceCreationException e) {
		}
	}

	private void removeInput(int selectedIndex) {
		try {
			DataSource ds = dsf.getDataSource(getFile());
			ds.open();
			ds.deleteRow(selectedIndex);
			ds.commit();
			list.setListData(getContents());
		} catch (DriverException e) {
		} catch (FreeingResourcesException e) {
		} catch (NonEditableDataSourceException e) {
		} catch (DriverLoadException e) {
		} catch (DataSourceCreationException e) {
		}
	}

	private void saveInput() {
		File file = getFile();
		try {
			if (!file.exists()) {
				FileSourceCreation fsc = new FileSourceCreation(file,
						getMetadata());
				dsf.createDataSource(fsc);
			}
			DataSource ds = dsf.getDataSource(file);
			ds.open();
			ds.insertEmptyRow();
			long row = ds.getRowCount() - 1;
			ds.setString(row, 0, txtNew.getText());
			String[] values = sqlPanel.getValues();
			for (int j = 0; j < values.length; j++) {
				ds.setString(row, j + 1, values[j]);
			}
			ds.commit();
			list.setListData(getContents());
		} catch (DriverException e) {
		} catch (FreeingResourcesException e) {
		} catch (NonEditableDataSourceException e) {
		} catch (DriverLoadException e) {
		} catch (DataSourceCreationException e) {
		}
	}

	private Metadata getMetadata() {
		DefaultMetadata ddm = new DefaultMetadata();
		String[] names = sqlPanel.getFieldNames();
		try {
			ddm.addField("sifName", Type.STRING);
			for (int i = 0; i < names.length; i++) {
				ddm.addField(names[i], Type.STRING);
			}
		} catch (InvalidTypeException e) {
			throw new RuntimeException("bug");
		}

		return ddm;
	}

	private void updateButtons() {
		if (list.getSelectedIndex() == -1) {
			btnDelete.setEnabled(false);
		} else {
			btnDelete.setEnabled(true);
		}

		if (txtNew.getText().length() > 0) {
			btnSave.setEnabled(true);
		} else {
			btnSave.setEnabled(false);
		}
	}

	private Object[] getContents() throws DriverException,
			DataSourceCreationException {
		File file = getFile();
		if (file.exists()) {
			DataSource ds = dsf.getDataSource(file);
			ds.open();
			String[] ret = new String[(int) ds.getRowCount()];
			for (int i = 0; i < ds.getRowCount(); i++) {
				ret[i] = ds.getString(i, 0);
			}
			ds.cancel();
			return ret;
		} else {
			return new String[0];
		}
	}

	private File getFile() {
		return new File(System.getProperty("user.home") + "/.sif/"
				+ sqlPanel.getId() + "-favorites.csv");
	}

	private String getVertical(String string) {
		String ret = "<html>";
		for (int i = 0; i < string.length(); i++) {
			ret += string.charAt(i) + "<br/>";
		}

		return ret + "</html>";
	}

	private void collapse() {
		ControlPanel.this.setPreferredSize(new Dimension(10, 0));
		btnSave.setVisible(false);
		list.setVisible(false);
		btnDelete.setVisible(false);
		txtNew.setVisible(false);
		collapsed.setVisible(true);
	}

	private void expand() {
		ControlPanel.this.setPreferredSize(null);
		btnSave.setVisible(true);
		list.setVisible(true);
		btnDelete.setVisible(true);
		txtNew.setVisible(true);
		collapsed.setVisible(false);
	}

	private void listen(Component comp) {
		comp.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseExited(MouseEvent e) {
				if ((ControlPanel.this.getMousePosition() == null)
						&& !exitInLeftEdge(e)) {
					collapse();
				}
			}

			private boolean exitInLeftEdge(MouseEvent e) {
				int x = e.getLocationOnScreen().x;
				int controlX = ControlPanel.this.getLocationOnScreen().x;
				if (Math.abs(x - controlX) < 10) {
					return true;
				} else {
					return false;
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (!list.isVisible()) {
					expand();
				}
			}

		});

		if (comp instanceof Container) {
			Component[] children = ((Container) comp).getComponents();
			for (Component component : children) {
				listen(component);
			}
		}
	}
}
