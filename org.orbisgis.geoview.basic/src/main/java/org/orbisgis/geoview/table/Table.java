package org.orbisgis.geoview.table;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;

public class Table extends JPanel {

	private JTable tbl;
	private DataSourceTableModel dataSourceTableModel = null;

	public Table() {
		this.setLayout(new BorderLayout());
		tbl = new JTable();
		tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.add(new JScrollPane(tbl));
	}

	public void setContents(DataSource ds) {
		dataSourceTableModel  = new DataSourceTableModel(ds);
		tbl.setModel(dataSourceTableModel);
	}

	public static void main(String[] args) throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		DataSource ds = dsf.getDataSource(new File("../../datas2tests/"
				+ "shp/bigshape2D/cantons.shp"));
		JFrame frm = new JFrame();
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Table tbl = new Table();
		tbl.setContents(ds);
		frm.getContentPane().add(tbl);
		frm.pack();
		frm.setLocationRelativeTo(null);
		frm.setVisible(true);
	}

	public DataSource getContents() {
		if (dataSourceTableModel == null) {
			return null;
 		} else {
 			return dataSourceTableModel.getDataSource();
 		}
	}

}
