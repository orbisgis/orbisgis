package org.gdms.drivers;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class ColumnLists {

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 * @throws SQLException 
	 */
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException  {
			Connection con = null;
			String nomTable;
		
		
			Class.forName("org.h2.Driver");
		
			con = DriverManager.getConnection("jdbc:h2:/tmp/h2/myH2db", "sa", "");
			
		
			DatabaseMetaData databaseMeta = con.getMetaData();
			    
			String[] type = {"TABLE","VIEW"};
			
			ResultSet tables = databaseMeta.getTables(con.getCatalog(),null,"%",type);
		    
			while (tables.next()){	
				
				nomTable = tables.getString("TABLE_NAME");				
				System.out.println(nomTable);
				
				String requete = "select * from " + nomTable;
				
				Statement stmt = con.createStatement();
	  		    ResultSet rs = stmt.executeQuery(requete);
	  		    ResultSetMetaData rsmd = rs.getMetaData();
	  		    int nbCols = rsmd.getColumnCount();
	  		    
	  		    for (int i=1; i<=nbCols; i++){
	  		    	
	  		    	String columnName = rsmd.getColumnName(i); 				    
 				      
	  		    	String columnType = rsmd.getColumnTypeName(i);
 				    
 				   System.out.println(columnName + columnType.toLowerCase()+ rsmd.getColumnType(i));
	  			  
	  		    }
	  		    

				  rs.close();
	    		    stmt.close();		
				
			}
	    
	    
	    
		

	}

}
