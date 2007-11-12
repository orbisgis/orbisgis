package org.orbisgis.geoview.renderer.style;
/* 
 * Copyright (C) 2002-2004 XimpleWare, info@ximpleware.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

/*This is the XPath version of RSSReader
 */
import com.ximpleware.*;
import com.ximpleware.xpath.*;
import java.io.*;

public class RSSReader2 {

  public static void main(String argv[]){
     try {
	// open a file and read the content into a byte array
        File f = new File("..//..//datas2tests//sld//blackLine.sld");
	FileInputStream fis =  new FileInputStream(f);
        byte[] b = new byte[(int) f.length()];
	fis.read(b);
	//instantiate VTDGen
	//and call parse 
	VTDGen vg = new VTDGen();
	vg.setDoc(b);
	vg.parse(true);  // set namespace awareness to true
	VTDNav vn = vg.getNav();
	AutoPilot ap = new AutoPilot(vn);
	ap.declareXPathNameSpace("sld","http://www.opengis.net/sld");
	ap.selectXPath("//sld:*");
	int result = -1;
	int count = 0;
	
	
	while((result = ap.evalXPath())!=-1){
		System.out.print(""+result+"  ");     
		System.out.print("Element name ==> "+vn.toString(result));
		int t = vn.getText(); // get the index of the text (char data or CDATA)
		if (t!=-1)
		  System.out.println(" Text  ==> "+vn.toNormalizedString(t));
		System.out.println("\n ============================== ");
		count++;
	}
	System.out.println("Total # of element "+count);

     }
     catch (ParseException e){
	     System.out.println(" XML file parsing error \n"+e);
     }
     catch (NavException e){
	     System.out.println(" Exception during navigation "+e);
     }
     catch (XPathParseException e){
	     
     }
     catch (XPathEvalException e){
	    
     }
     catch (java.io.IOException e)
     {
	     System.out.println(" IO exception condition"+e);
     }
  }
}
