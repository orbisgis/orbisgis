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

import java.io.IOException;
import java.io.RandomAccessFile;

import org.gdms.driver.DriverException;
import org.gdms.driver.memory.MemoryDataSetDriver;

/**
 * A DXF block contains a block of geometries. The dxf driver can read entities
 * inside a block, but it will not remember that the entities are in a same
 * block.
 * 
 * @author Micha�l Michaud
 * @version 0.5.0
 */
// History
public final class DxfBLOCKS {
	// final static FeatureSchema DXF_SCHEMA = new FeatureSchema();

	private DxfBLOCKS() {
		/*
		 * DXF_SCHEMA.addAttribute("GEOMETRY", AttributeType.GEOMETRY);
		 * DXF_SCHEMA.addAttribute("LAYER", AttributeType.STRING);
		 * DXF_SCHEMA.addAttribute("LTYPE", AttributeType.STRING);
		 * DXF_SCHEMA.addAttribute("THICKNESS", AttributeType.DOUBLE);
		 * DXF_SCHEMA.addAttribute("COLOR", AttributeType.INTEGER);
		 * DXF_SCHEMA.addAttribute("TEXT", AttributeType.STRING);
		 */
	}

	/*
	 * public static DxfBLOCKS readBlocks(RandomAccessFile raf) throws
	 * IOException { DxfBLOCKS blocks = new DxfBLOCKS(); try { DxfGroup group =
	 * null; String nomVariable; while (null != (group =
	 * DxfGroup.readGroup(raf)) && !group.equals(DxfFile.ENDSEC)) { } }
	 * catch(IOException ioe) {throw ioe;} return blocks; }
	 */

	public static DxfBLOCKS readEntities(RandomAccessFile raf,
			MemoryDataSetDriver driver) throws IOException, DriverException {
		DxfBLOCKS dxfEntities = new DxfBLOCKS();
			DxfGroup group = new DxfGroup(2, "BLOCKS");
			while (!group.equals(DxfFile.ENDSEC)) {
				if (group.getCode() == 0) {
					if (group.getValue().equals("POINT")) {
						group = DxfPOINT.readEntity(raf, driver);
					} else if (group.getValue().equals("TEXT")) {
						group = DxfTEXT.readEntity(raf, driver);
					} else if (group.getValue().equals("LINE")) {
						group = DxfLINE.readEntity(raf, driver);
					} else if (group.getValue().equals("POLYLINE")) {
						group = DxfPOLYLINE.readEntity(raf, driver);
					} else if (group.getValue().equals("TEXT")) {
						group = DxfTEXT.readEntity(raf, driver);
					} else {
						group = DxfGroup.readGroup(raf);
					}
				} else {
					// System.out.println("Group " + group.getCode() + " " +
					// group.getValue() + " UNKNOWN");
					group = DxfGroup.readGroup(raf);
				}
			}
		return dxfEntities;
	}

}
