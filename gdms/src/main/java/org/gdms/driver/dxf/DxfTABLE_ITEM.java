/*
 * Library name : dxf
 * (C) 2006 Micha�l Michaud
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * For more information, contact:
 *
 * michael.michaud@free.fr
 *
 */

package org.gdms.driver.dxf;


/**
 * This class represent one of the TABLE of the TABLES section.
 * It has as many subclasses as the number of table types
 * @author Micha�l Michaud
 * @version 0.5.0
 */
// History
public class DxfTABLE_ITEM {
    public final static DxfGroup ENDTAB = new DxfGroup(0, "ENDTAB");
    public final static DxfGroup APPID = new DxfGroup(0, "APPID");
    public final static DxfGroup DIMSTYLE = new DxfGroup(0, "DIMSTYLE");
    public final static DxfGroup LAYER = new DxfGroup(0, "LAYER");
    public final static DxfGroup LTYPE = new DxfGroup(0, "LTYPE");
    public final static DxfGroup STYLE = new DxfGroup(0, "STYLE");
    public final static DxfGroup UCS = new DxfGroup(0, "UCS");
    public final static DxfGroup VIEW = new DxfGroup(0, "VIEW");
    public final static DxfGroup VPORT = new DxfGroup(0, "VPORT");

    private String name;
    private int flags = 0;

    public DxfTABLE_ITEM(String name, int flags) {
        this.name = name;
        this.flags = flags;
    }

    public String getName(){return name;}
    public void setName(String name) {this.name = name;}
    public int getFlags(){return flags;}
    public void setFlags(int flags) {this.flags = flags;}

    public boolean getFlag1(){return ((flags&1)==1);}
    public boolean getFlag2(){return ((flags&2)==2);}
    public boolean getFlag4(){return ((flags&4)==4);}
    public boolean getFlag8(){return ((flags&8)==8);}
    public boolean getFlag16(){return ((flags&16)==16);}
    public boolean getFlag32(){return ((flags&32)==32);}
    public boolean getFlag64(){return ((flags&64)==64);}
    public boolean getFlag128(){return ((flags&128)==128);}

    public String toString() {
        return DxfGroup.toString(2, name) + DxfGroup.toString(70, flags);
    }

}
