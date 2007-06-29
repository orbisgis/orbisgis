package org.gdms.sql.customQuery.utility;

import javax.swing.JDialog;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.utility.Table;
import com.hardcode.driverManager.DriverLoadException;


public class ShowCall implements CustomQuery {

	public DataSource evaluate(DataSourceFactory dsf, DataSource[] tables, Value[] values)
			throws ExecutionException {
		
		if (values.length != 1) {
			
			throw new ExecutionException(
			"Show only operates on one query");
		}
		
		else {
					
		
			String query = values[0].toString();
			
			if (query.substring(0, 6).equalsIgnoreCase("select")) {
				
				try {
					
					DataSource dsResult = dsf.executeSQL(query);
					dsResult.open();
					
					Table table = new Table(dsResult);
					JDialog dlg = new JDialog();
					dlg.setModal(true);
					dlg
							.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dlg.getContentPane().add(table);
					dlg.pack();
					dlg.setVisible(true);
					
					dsResult.cancel();
					
				} catch (SyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DriverLoadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchTableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DriverException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			else {
			
				throw new ExecutionException(
				"Show only operates on select");
				
				
			}
		}
			
				return null;
	}

	public String getName() {
		return "SHOW";
	}
	
}