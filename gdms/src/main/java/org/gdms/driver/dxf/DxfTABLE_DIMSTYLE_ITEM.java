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
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
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
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The DIMSTYLE item in the TABLES section
 * There is a static reader to read the item in a DXF file
 * and a toString method able to write it in a DXF form
 * @author Micha�l Michaud
 * @version 0.5.0
 */
// History
public final class DxfTABLE_DIMSTYLE_ITEM extends DxfTABLE_ITEM {

        private DxfTABLE_DIMSTYLE_ITEM(String name, int flags) {
                super(name, flags);
        }

        public static Map readTable(RandomAccessFile raf) throws IOException {
                DxfTABLE_DIMSTYLE_ITEM item = new DxfTABLE_DIMSTYLE_ITEM("DEFAULT", 0);
                Map table = new LinkedHashMap();
                try {
                        DxfGroup group;
                        while (null != (group = DxfGroup.readGroup(raf)) && !group.equals(ENDTAB)) {
                                if (group.equals(DIMSTYLE)) {
                                        item = new DxfTABLE_DIMSTYLE_ITEM("DEFAULT", 0);
                                } else if (group.getCode() == 2) {
                                        item.setName(group.getValue());
                                        table.put(item.getName(), item);
                                } else if (group.getCode() == 5) {
                                } // tag appeared in version 13 of DXF
                                else if (group.getCode() == 100) {
                                } // tag appeared in version 13 of DXF
                                else if (group.getCode() == 70) {
                                        item.setFlags(group.getIntValue());
                                } else {
                                }
                        }
                } catch (IOException ioe) {
                        throw ioe;
                }
                return table;
        }
}
