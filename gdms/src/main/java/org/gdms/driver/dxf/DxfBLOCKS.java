/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
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
import org.gdms.driver.io.RowWriter;

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
			RowWriter v) throws IOException, DriverException {
		DxfBLOCKS dxfEntities = new DxfBLOCKS();
			DxfGroup group = new DxfGroup(2, "BLOCKS");
			while (!group.equals(DxfFile.ENDSEC)) {
				if (group.getCode() == 0) {
					if (group.getValue().equals("POINT")) {
						group = DxfPOINT.readEntity(raf, v);
					} else if (group.getValue().equals("TEXT")) {
						group = DxfTEXT.readEntity(raf, v);
					} else if (group.getValue().equals("LINE")) {
						group = DxfLINE.readEntity(raf, v);
					} else if (group.getValue().equals("POLYLINE")) {
						group = DxfPOLYLINE.readEntity(raf, v);
					} else if (group.getValue().equals("TEXT")) {
						group = DxfTEXT.readEntity(raf, v);
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
