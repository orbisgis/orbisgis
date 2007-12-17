package org.orbisgis.geoview.views.sqlConsole.util;



import java.util.Vector;





public class SQLConsoleUtilities {


	/**
	 * Cette fonction permet de s�parer une chaine de caract�res � partir d'un s�parateur.
	 * @param String value
	 * @param char separatorChar value
	 * @return String[]
	 */
	
	public final static String[] split( String str, String separator ) {
	      
		char separatorChar = separator.charAt(0);
		
			if ( str == null ) {
	         return null;
	      }
	      int       len    = str.length();
	      if ( len == 0 ) {
	         return null;
	      }
	      Vector    list   = new Vector();
	      int       i      = 0;
	      int       start  = 0;
	      boolean   match  = false;
	      while ( i < len ) {
	         if ( str.charAt( i ) == separatorChar ) {
	            if ( match ) {
	               list.addElement( str.substring( start, i ).trim() );
	               match = false;
	            }
	            start = ++i;
	            continue;
	         }
	         match = true;
	         i++;
	      }
	      if ( match ) {
	         list.addElement( str.substring( start, i ).trim() );
	      }
	      String[]  arr    = new String[list.size()];
	      list.copyInto( arr );
	      return arr;
	   }
	
		
}
