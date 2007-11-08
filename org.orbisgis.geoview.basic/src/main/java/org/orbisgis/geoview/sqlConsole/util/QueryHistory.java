/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.orbisgis.geoview.sqlConsole.util;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Vector;

/**
 * <code>History</code> is a class to store, read and write an history (such
 * as an history of SQL queries). The history is saved on disk using an XML
 * format with two elements:
 *
 * <ul>
 * <li><code>history</code> that is the root element for the history.
 * <li><code>item</code> for an entry of the history.
 * </ul>
 *
 * @author <a href="mailto:michel.casabianca@wanadoo.fr">Michel CASABIANCA</a>
 * and <a href="mailto:vixxes@noos.fr">Laurent ROMEO</a>
 * @version 1.0
 */
public class QueryHistory extends Vector {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The unique instance */
    static QueryHistory instance;
    /** Index of the current entry in the history */
    int current=-1;
    /** File name of the history */
    String file=null;
    /** Maximum number of entries in the history */
    int maxSaved=100;

    // constants for history parsing
    static final String opening="<history>";
    static final String closing="</history>";
    static final String itemOpening="<item>";
    static final String itemClosing="</item>";
    static final int bufferSize=4096;

    /**
     * Get the unique instance.
     */
    public static QueryHistory getInstance() {
	return instance;
    }

    /**
     * Creates a new <code>History</code> instance.
     */
    QueryHistory() {
	instance=this;
    }

    /**
     * Creates a new <code>History</code> instance.
     *
     * @param file a <code>String</code> that is the history file name
     */
    public QueryHistory(String file) {
	this();
        load(file);
    }

    /**
     * <code>load</code> an history from file.
     *
     * @param file a <code>String</code> that is the history file name
     */
    public void load(String file) {
        try {
            //this.file=System.getProperty("user.home")+File.separator+
          //      (isUnix()?".":"")+file;
            
        	
        	this.file =file;
           
            
            System.out.println(this.file);
            
            BufferedReader in=new BufferedReader(new InputStreamReader(new 
		    FileInputStream(this.file)));
            StringBuffer buffer=new StringBuffer();
            String line=null;
            while((line=in.readLine())!=null) buffer.append(line);
            in.close();
            String string=buffer.toString();
            int start=0;
            while((start=string.indexOf(itemOpening,start))>-1) {
                start+=itemOpening.length();
                int end=string.indexOf(itemClosing,start);
                addElement(string.substring(start,end).trim());
                start+=itemClosing.length();
            }
        } catch(Exception e) {}
    }

    /**
     * <code>save</code> the history in a file.
     *
     */
    public void save() {
        try {
        	
            BufferedWriter out=new 
		BufferedWriter(new OutputStreamWriter(new 
		    FileOutputStream(file)));
            out.write(opening,0,opening.length());
            out.newLine();
            for(int i=0;i<size() && i<maxSaved;i++) {
                out.write(itemOpening,0,itemOpening.length());
                out.newLine();
                out.write(((String)elementAt(i)).trim());
                out.newLine();
                out.write(itemClosing,0,itemClosing.length());
                out.newLine();
            }
            out.write(closing,0,closing.length());
            out.close();
        } catch(Exception e) {}
    }

    /**
     * <code>add</code> an entry to the history. This method checks that
     * the entry is not empty and different from the previous one.
     *
     * @param item a <code>String</code> the entry to add
     */
    public void add(String item) {
    	
    	
        // verify that the query is not empty
        if(item!=null && !item.equals("")) {
            // verify that the query is different from the preceding
            if(size()==0 || !item.equals((String)elementAt(0))) {
                insertElementAt(item,0);
            }
            current=-1;
        }
    }

    /**
     * <code>prevAvailable</code> indicates if a previous entry is
     * available (before the current one).
     *
     * @return a <code>boolean</code> value
     */
    public boolean isPrevAvailable() {
        return (size()>0 && current<size()-1);
    }

    /**
     * <code>nextAvailable</code> indicates if a next entry is available
     * (after the current one).
     *
     * @return a <code>boolean</code> value
     */
    public boolean isNextAvailable() {
        return current>-1;
    }

    /**
     * <code>prev</code> return the previous entry.
     *
     * @return a <code>String</code> that is the previous entry
     */
    public String getPrev() {
        if(isPrevAvailable()) return (String)elementAt(++current);
        else return null;
    }

    /**
     * <code>next</code> return the next entry.
     *
     * @return a <code>String</code> that is the next entry
     */
    public String getNext() {
        if(isNextAvailable()) {
            current--;
            if(current>-1) return (String)elementAt(current);
            else return "";
        }
        else return null;
    }

    /**
     * <code>isUnix</code> method determines if the system is Unix.
     *
     * @return a <code>boolean</code> indicating if the system is Unix.
     */
    public static boolean isUnix() {
        return (System.getProperty("file.separator").equals("/") &&
		System.getProperty("path.separator").equals(":") &&
		System.getProperty("line.separator").equals("\n"));
    }
}
