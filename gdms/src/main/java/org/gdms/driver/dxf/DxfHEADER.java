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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A DXF HEADER section.
 * This class has a static method to read the header section of a DXF file
 * and a toString method to format the header section into DXF format
 * @author Micha�l Michaud
 * @version 0.5.0
 */
// History
public class DxfHEADER {
    public static final String ACADVER = "ACADVER";
    public static final String ANGBASE = "ANGBASE";
    public static final String ANGDIR = "ANGDIR";
    public static final String ATTDIA = "ATTDIA";
    public static final String ATTMODE = "ATTMODE";
    public static final String ATTREQ = "ATTREQ";
    public static final String AUNITS = "AUNITS";
    public static final String AUPREC = "AUPREC";
    public static final String AXISMODE = "AXISMODE";
    public static final String AXISUNIT = "AXISUNIT";
    public static final String BLIPMODE = "BLIPMODE";
    public static final String CECOLOR = "CECOLOR";
    public static final String CELTYPE = "CELTYPE";
    public static final String CHAMFERA = "CHAMFERA";
    public static final String CHAMFERB = "CHAMFERB";
    public static final String CLAYER = "CLAYER";
    public static final String COORDS = "COORDS";
    public static final String DIMALT = "DIMALT";
    public static final String DIMALTD = "DIMALTD";
    public static final String DIMALTF = "DIMALTF";
    public static final String DIMAPOST = "DIMAPOST";
    public static final String DIMASO = "DIMASO";
    public static final String DIMASZ = "DIMASZ";
    public static final String DIMBLK = "DIMBLK";
    public static final String DIMBLK1 = "DIMBLK1";
    public static final String DIMBLK2 = "DIMBLK2";
    public static final String DIMCEN = "DIMCEN";
    public static final String DIMCLRD = "DIMCLRD";
    public static final String DIMCLRE = "DIMCLRE";
    public static final String DIMCLRT = "DIMCLRT";
    public static final String DIMDLE = "DIMDLE";
    public static final String DIMDLI = "DIMDLI";
    public static final String DIMEXE = "DIMEXE";
    public static final String DIMEXO = "DIMEXO";
    public static final String DIMGAP = "DIMGAP";
    public static final String DIMLFAC = "DIMLFAC";
    public static final String DIMLIM = "DIMLIM";
    public static final String DIMPOST = "DIMPOST";
    public static final String DIMRND = "DIMRND";
    public static final String DIMSAH = "DIMSAH";
    public static final String DIMSCALE = "DIMSCALE";
    public static final String DIMSE1 = "DIMSE1";
    public static final String DIMSE2 = "DIMSE2";
    public static final String DIMSHO = "DIMSHO";
    public static final String DIMSOXD = "DIMSOXD";
    public static final String DIMSTYLE = "DIMSTYLE";
    public static final String DIMTAD = "DIMTAD";
    public static final String DIMTFAC = "DIMTFAC";
    public static final String DIMTIH = "DIMTIH";
    public static final String DIMTIX = "DIMTIX";
    public static final String DIMTM = "DIMTM";
    public static final String DIMTOFL = "DIMTOFL";
    public static final String DIMTOH = "DIMTOH";
    public static final String DIMTOL = "DIMTOL";
    public static final String DIMTP = "DIMTP";
    public static final String DIMTSZ = "DIMTSZ";
    public static final String DIMTVP = "DIMTVP";
    public static final String DIMTXT = "DIMTXT";
    public static final String DIMZIN = "DIMZIN";
    public static final String DRAGMODE = "DRAGMODE";
    public static final String DWGCODEPAGE = "DWGCODEPAGE";
    public static final String ELEVATION = "ELEVATION";
    public static final String EXTMAX = "EXTMAX";
    public static final String EXTMIN = "EXTMIN";
    public static final String FASTZOOM = "FASTZOOM";
    public static final String FILLETRAD = "FILLETRAD";
    public static final String FILLMODE = "FILLMODE";
    public static final String GRIDMODE = "GRIDMODE";
    public static final String GRIDUNIT = "GRIDUNIT";
    public static final String HANDLING = "HANDLING";
    public static final String HANDSEED = "HANDSEED";
    public static final String INSBASE = "INSBASE";
    public static final String LIMCHECK = "LIMCHECK";
    public static final String LIMMAX = "LIMMAX";
    public static final String LIMMIN = "LIMMIN";
    public static final String LTSCALE = "LTSCALE";
    public static final String LUNITS = "LUNITS";
    public static final String LUPREC = "LUPREC";
    public static final String MAXACTVP = "MAXACTVP";
    public static final String MENU = "MENU";
    public static final String MIRRTEXT = "MIRRTEXT";
    public static final String ORTHOMODE = "ORTHOMODE";
    public static final String OSMODE = "OSMODE";
    public static final String PDMODE = "PDMODE";
    public static final String PDSIZE = "PDSIZE";
    public static final String PELEVATION = "PELEVATION";
    public static final String PEXTMAX = "PEXTMAX";
    public static final String PEXTMIN = "PEXTMIN";
    public static final String PLIMCHECK = "PLIMCHECK";
    public static final String PLIMMAX = "PLIMMAX";
    public static final String PLIMMIN = "PLIMMIN";
    public static final String PLINEGEN = "PLINEGEN";
    public static final String PLINEWID = "PLINEWID";
    public static final String PSLTSCALE = "PSLTSCALE";
    public static final String PUCSNAME = "PUCSNAME";
    public static final String PUCSORG = "PUCSORG";
    public static final String PUCSXDIR = "PUCSXDIR";
    public static final String PUCSYDIR = "PUCSYDIR";
    public static final String QTEXTMODE = "QTEXTMODE";
    public static final String REGENMODE = "REGENMODE";
    public static final String SHADEDGE = "SHADEDGE";
    public static final String SHADEDIF = "SHADEDIF";
    public static final String SKETCHINC = "SKETCHINC";
    public static final String SKPOLY = "SKPOLY";
    public static final String SNAPANG = "SNAPANG";
    public static final String SNAPBASE = "SNAPBASE";
    public static final String SNAPISOPAIR = "SNAPISOPAIR";
    public static final String SNAPMODE = "SNAPMODE";
    public static final String SNAPSTYLE = "SNAPSTYLE";
    public static final String SNAPUNIT = "SNAPUNIT";
    public static final String SPLFRAME = "SPLFRAME";
    public static final String SPLINESEGS = "SPLINESEGS";
    public static final String SPLINETYPE = "SPLINETYPE";
    public static final String SURFTAB1 = "SURFTAB1";
    public static final String SURFTAB2 = "SURFTAB2";
    public static final String SURFTYPE = "SURFTYPE";
    public static final String SURFU = "SURFU";
    public static final String SURFV = "SURFV";
    public static final String TDCREATE = "TDCREATE";
    public static final String TDINDWG = "TDINDWG";
    public static final String TDUPDATE = "TDUPDATE";
    public static final String TDUSRTIMER = "TDUSRTIMER";
    public static final String TEXTSIZE = "TEXTSIZE";
    public static final String TEXTSTYLE = "TEXTSTYLE";
    public static final String THICKNESS = "THICKNESS";
    public static final String TILEMODE = "TILEMODE";
    public static final String TRACEWID = "TRACEWID";
    public static final String UCSNAME = "UCSNAME";
    public static final String UCSORG = "UCSORG";
    public static final String UCSXDIR = "UCSXDIR";
    public static final String UCSYDIR = "UCSYDIR";
    public static final String UNITMODE = "UNITMODE";
    public static final String USERI1 = "USERI1";
    public static final String USERI2 = "USERI2";
    public static final String USERI3 = "USERI3";
    public static final String USERI4 = "USERI4";
    public static final String USERI5 = "USERI5";
    public static final String USERR1 = "USERR1";
    public static final String USERR2 = "USERR2";
    public static final String USERR3 = "USERR3";
    public static final String USERR4 = "USERR4";
    public static final String USERR5 = "USERR5";
    public static final String USRTIMER = "USRTIMER";
    public static final String VIEWCTR = "VIEWCTR";
    public static final String VIEWDIR = "VIEWDIR";
    public static final String VIEWSIZE = "VIEWSIZE";
    public static final String VISRETAIN = "VISRETAIN";
    public static final String WORLDVIEW = "WORLDVIEW";
    
    Map headerTable = null;
    
    public DxfHEADER() {
        headerTable = new LinkedHashMap();
    }
    
    public List getVariable(String nomVariable) {
        return (List)headerTable.get(nomVariable);
    }
    
    public void setVariable(String nomVariable, List groups) {
        headerTable.put(nomVariable, groups);
    }

    public static DxfHEADER readHeader(RandomAccessFile raf) throws IOException {
        DxfHEADER header = new DxfHEADER();
        try {
            DxfGroup group = null;
            String nomVariable = null;
            while (null != (group = DxfGroup.readGroup(raf)) &&
                                !group.equals(DxfFile.ENDSEC)) {
                if (group.getCode()==9) {
                    nomVariable = group.getValue();
                    nomVariable = nomVariable.substring(1,nomVariable.length());
                    header.headerTable.put(nomVariable, new ArrayList(1));
                }
                else if (group.getCode() == 999) {}
                else if (nomVariable != null) {
                    List groups = (List)header.headerTable.get(nomVariable);
                    groups.add(group);
                }
                else {
                    //System.out.println("Group " + group.getCode() + " " + group.getValue() + " UNKNOWN");
                }
            }
        } catch(IOException ioe) {throw ioe;}
        return header;
    }

    public String toString() {
        Iterator it = headerTable.keySet().iterator();
        StringBuffer sb = new StringBuffer(DxfFile.SECTION.toString());
        sb.append(DxfFile.HEADER.toString());
        while (it.hasNext()) {
            String var = (String)it.next();
            sb.append(DxfGroup.toString(9, "$"+var));
            List liste = (List)headerTable.get(var);
            for (int i = 0 ; i < liste.size() ; i++) {
                sb.append(((DxfGroup)liste.get(i)).toString());
            }
        }
        sb.append(DxfFile.ENDSEC.toString());
        return sb.toString();
    }
}
