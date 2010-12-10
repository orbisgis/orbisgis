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

import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The TABLES section of a DXF file. It contains LAYERs, LTYPEs...
 * There is a static reader to read the TABLES section in a DXF file
 * and a toString method able to write the section in a DXF form
 * @author Micha�l Michaud
 * @version 0.5.0
 */
// History
// 2006-11-12 : Bug fixed x==Double.NaN --> Double.isNaN(x)
public class DxfTABLES {
    public final static DxfGroup TABLE = new DxfGroup(0, "TABLE");
    public final static DxfGroup ENDTAB = new DxfGroup(0, "ENDTAB");
    public final static DxfGroup NBMAX = new DxfGroup(70, "NBMAX");
    public final static DxfGroup APPID = new DxfGroup(2, "APPID");
    public final static DxfGroup DIMSTYLE = new DxfGroup(2, "DIMSTYLE");
    public final static DxfGroup LTYPE = new DxfGroup(2, "LTYPE");
    public final static DxfGroup LAYER = new DxfGroup(2, "LAYER");
    public final static DxfGroup STYLE = new DxfGroup(2, "STYLE");
    public final static DxfGroup UCS = new DxfGroup(2, "UCS");
    public final static DxfGroup VIEW = new DxfGroup(2, "VIEW");
    public final static DxfGroup VPORT = new DxfGroup(2, "VPORT");
    
    Map appId;
    Map dimStyle;
    Map lType;
    Map layer;
    Map style;
    Map ucs;
    Map view;
    Map vPort;

    public DxfTABLES() {
        appId = new HashMap();
        dimStyle = new HashMap();
        lType = new HashMap();
        layer = new HashMap();
        style = new HashMap();
        ucs = new HashMap();   // 
        view = new HashMap();
        vPort = new HashMap();
    }

    public static DxfTABLES readTables(RandomAccessFile raf) throws IOException {
        DxfTABLES tables = new DxfTABLES();
        DxfGroup group = null;
        String nomVariable = null;
        // It�ration sur chaque table
        while (null != (group = DxfGroup.readGroup(raf)) && !group.equals(DxfFile.ENDSEC)) {
            Map map = null;
            if (group.equals(TABLE)) {
                // Lecture du groupe portant le nom de la table
                group = DxfGroup.readGroup(raf);
                if (group.equals(APPID)) {
                    //System.out.println("\tTABLE APPID");
                    tables.appId = DxfTABLE_APPID_ITEM.readTable(raf);
                }
                else if (group.equals(DIMSTYLE)) {
                    //System.out.println("\tTABLE DIMSTYLE");
                    tables.dimStyle = DxfTABLE_DIMSTYLE_ITEM.readTable(raf);
                }
                else if (group.equals(LTYPE)) {
                    //System.out.println("\tTABLE LTYPE");
                    tables.lType = DxfTABLE_LTYPE_ITEM.readTable(raf);
                }
                else if (group.equals(LAYER)) {
                    //System.out.println("\tTABLE LAYER");
                    tables.layer = DxfTABLE_LAYER_ITEM.readTable(raf);
                }
                else if (group.equals(STYLE)) {
                    //System.out.println("\tTABLE STYLE");
                    tables.style = DxfTABLE_STYLE_ITEM.readTable(raf);
                }
                else if (group.equals(UCS)) {
                    //System.out.println("\tTABLE UCS");
                    tables.ucs = DxfTABLE_UCS_ITEM.readTable(raf);
                }
                else if (group.equals(VIEW)) {
                    //System.out.println("\tTABLE VIEW");
                    tables.view= DxfTABLE_VIEW_ITEM.readTable(raf);
                }
                else if (group.equals(VPORT)) {
                    //System.out.println("\tTABLE VPORT");
                    tables.vPort= DxfTABLE_VPORT_ITEM.readTable(raf);
                }
                else if (group.getCode() == 999) {
                    //System.out.println("Commentaire : " + group.getValue());
                }
                else {
                    //System.out.println("Group " + group.getCode() + " " + group.getValue() + " UNKNOWN");
                }
            }
            else if (group.getCode() == 999) {
                //System.out.println("Commentaire : " + group.getValue());
            }
            else {
                //System.out.println("Group " + group.getCode() + " " + group.getValue() + " UNKNOWN");
            }
        }
        return tables;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(DxfFile.SECTION.toString());
        sb.append(DxfFile.TABLES.toString());
        if (vPort.size() >0) {
            sb.append(DxfTABLES.TABLE.toString());
            sb.append(DxfTABLES.VPORT.toString());
            sb.append(DxfGroup.toString(70, Integer.toString(vPort.size())));
            Iterator it = vPort.keySet().iterator();
            while (it.hasNext()) {
                sb.append(DxfGroup.toString(0, "VPORT"));
                sb.append(((DxfTABLE_VPORT_ITEM)vPort.get(it.next())).toString());
            }
            sb.append(DxfTABLES.ENDTAB.toString());
        }
        if (appId.size() > 0) {
            sb.append(DxfTABLES.TABLE.toString());
            sb.append(DxfTABLES.APPID.toString());
            sb.append(DxfGroup.toString(70, Integer.toString(appId.size())));
            Iterator it = appId.keySet().iterator();
            while (it.hasNext()) {
                sb.append(DxfGroup.toString(0, "APPID"));
                sb.append(((DxfTABLE_APPID_ITEM)appId.get(it.next())).toString());
            }
            sb.append(DxfTABLES.ENDTAB.toString());
        }
        if (dimStyle.size() >0) {
            sb.append(DxfTABLES.TABLE.toString());
            sb.append(DxfTABLES.DIMSTYLE.toString());
            sb.append(DxfGroup.toString(70, Integer.toString(dimStyle.size())));
            Iterator it = dimStyle.keySet().iterator();
            while (it.hasNext()) {
                sb.append(DxfGroup.toString(0, "DIMSTYLE"));
                sb.append(((DxfTABLE_DIMSTYLE_ITEM)dimStyle.get(it.next())).toString());
            }
            sb.append(DxfTABLES.ENDTAB.toString());
        }
        if (lType.size() >0) {
            sb.append(DxfTABLES.TABLE.toString());
            sb.append(DxfTABLES.LTYPE.toString());
            sb.append(DxfGroup.toString(70, Integer.toString(lType.size())));
            Iterator it = lType.keySet().iterator();
            while (it.hasNext()) {
                sb.append(DxfGroup.toString(0, "LTYPE"));
                sb.append(((DxfTABLE_LTYPE_ITEM)lType.get(it.next())).toString());
            }
            sb.append(DxfTABLES.ENDTAB.toString());
        }
        if (layer.size() >0) {
            sb.append(DxfTABLES.TABLE.toString());
            sb.append(DxfTABLES.LAYER.toString());
            sb.append(DxfGroup.toString(70, Integer.toString(layer.size())));
            Iterator it = layer.keySet().iterator();
            while (it.hasNext()) {
                sb.append(DxfGroup.toString(0, "LAYER"));
                sb.append(((DxfTABLE_LAYER_ITEM)layer.get(it.next())).toString());
            }
            sb.append(DxfTABLES.ENDTAB.toString());
        }
        if (style.size() >0) {
            sb.append(DxfTABLES.TABLE.toString());
            sb.append(DxfTABLES.STYLE.toString());
            sb.append(DxfGroup.toString(70, Integer.toString(style.size())));
            Iterator it = style.keySet().iterator();
            while (it.hasNext()) {
                sb.append(DxfGroup.toString(0, "STYLE"));
                sb.append(((DxfTABLE_STYLE_ITEM)style.get(it.next())).toString());
            }
            sb.append(DxfTABLES.ENDTAB.toString());
        }
        if (ucs.size() >0) {
            sb.append(DxfTABLES.TABLE.toString());
            sb.append(DxfTABLES.UCS.toString());
            sb.append(DxfGroup.toString(70, Integer.toString(ucs.size())));
            Iterator it = ucs.keySet().iterator();
            while (it.hasNext()) {
                sb.append(DxfGroup.toString(0, "UCS"));
                sb.append(((DxfTABLE_UCS_ITEM)ucs.get(it.next())).toString());
            }
            sb.append(DxfTABLES.ENDTAB.toString());
        }
        if (view.size() >0) {
            sb.append(DxfTABLES.TABLE.toString());
            sb.append(DxfTABLES.VIEW.toString());
            sb.append(DxfGroup.toString(70, Integer.toString(view.size())));
            Iterator it = view.keySet().iterator();
            while (it.hasNext()) {
                sb.append(DxfGroup.toString(0, "VIEW"));
                sb.append(((DxfTABLE_VIEW_ITEM)view.get(it.next())).toString());
            }
            sb.append(DxfTABLES.ENDTAB.toString());
        }
        sb.append(DxfFile.ENDSEC.toString());
        return sb.toString();
    }

}
