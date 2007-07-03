package org.gdms.sql.customQuery.utility;

import javax.swing.ImageIcon;
import javax.swing.JDialog;

import org.gdms.data.AlreadyClosedException;
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
			
			String query = null;
			String tableName = null;
			
			if (values.length==1){
				query = values[0].toString();
				
			}
			else if (values.length==2) {
				query = values[0].toString();
				 tableName = values[1].toString();
			}
			else {
				
				throw new ExecutionException("Syntaxe error");
			}
			
			
			if (query.substring(0, 6).equalsIgnoreCase("select")) {
				try {
				DataSource dsResult = dsf.executeSQL(query);
				dsResult.open();
				Table table = new Table(dsResult);
				JDialog dlg = new JDialog();
					
				if (tableName!=null){
					dlg.setTitle("Attributes for "+ tableName);
				}
				else {
					dlg.setTitle("Attributes for "+ dsResult.getName());
				}	
										
				java.net.URL url = this.getClass().getResource("mini_orbisgis.png");
				dlg.setIconImage(new ImageIcon(url).getImage());
				
				dlg.setModal(true);
				dlg	.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dlg.getContentPane().add(table);
				dlg.pack();
				dlg.setVisible(true);
				
				
				dsResult.cancel();
				
				} catch (AlreadyClosedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DriverException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DriverLoadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchTableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
							
			
			
			
			else {
			
		
				throw new ExecutionException("Show only operates on select");
			}
			
			
		
			
				return null;
	}

	public String getName() {
		return "SHOW";
	}
	
}