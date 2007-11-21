package org.orbisgis.geoview.table;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;

public class Table extends JPanel {

	private JTable tbl;
	private DataSourceTableModel dataSourceTableModel = null;

	public Table() {
		this.setLayout(new BorderLayout());
		tbl = new JTable();
		tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.add(new JScrollPane(tbl));
	}

	public void setContents(DataSource ds) throws DriverException {
		ds.open();
		if (dataSourceTableModel != null) {
			dataSourceTableModel.getDataSource().cancel();
		}
		dataSourceTableModel  = new DataSourceTableModel(ds);
		tbl.setModel(dataSourceTableModel);
	}

	public DataSource getContents() {
		if (dataSourceTableModel == null) {
			return null;
 		} else {
 			return dataSourceTableModel.getDataSource();
 		}
	}

}
