/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
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
