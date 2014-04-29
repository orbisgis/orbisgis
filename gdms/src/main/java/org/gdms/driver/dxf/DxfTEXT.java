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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.io.RowWriter;

/**
 * A TEXT and its static readEntity method to read a TEXT in a DXF file.
 * 
 * @author Micha�l Michaud
 * @version 0.5.0
 */
// History
// 2006-11-12 : Bug fixed x==Double.NaN --> Double.isNaN(x)
public class DxfTEXT extends DxfENTITY {

        public DxfTEXT() {
                super("DEFAULT");
        }

        public static DxfGroup readEntity(RandomAccessFile raf,
                RowWriter v) throws IOException, DriverException {

                Value[] values = new Value[DxfFile.DXF_SCHEMACount];

                /*
                 * Feature feature = new BasicFeature(DxfFile.DXF_SCHEMA);
                 * feature.setAttribute("LTYPE", "BYLAYER");
                 * feature.setAttribute("ELEVATION", new Double(0.0));
                 * feature.setAttribute("THICKNESS", new Double(0.0));
                 * feature.setAttribute("COLOR", new Integer(256)); // equivalent to
                 * BYLAYER feature.setAttribute("TEXT", "");
                 * feature.setAttribute("TEXT_HEIGHT", new Double(0.0));
                 * feature.setAttribute("TEXT_STYLE", "STANDARD");
                 */
                double x = Double.NaN, y = Double.NaN, z = Double.NaN;
                DxfGroup group;
                try {
                        while (null != (group = DxfGroup.readGroup(raf))
                                && group.getCode() != 0) {
                                if (group.getCode() == 8) {
                                        values[1] = ValueFactory.createValue(group.getValue());
                                } else if (group.getCode() == 6) {
                                        values[2] = ValueFactory.createValue(group.getValue());
                                } else if (group.getCode() == 38) {
                                        values[3] = ValueFactory.createValue(group.getDoubleValue());
                                } else if (group.getCode() == 39) {
                                        values[4] = ValueFactory.createValue(group.getDoubleValue());
                                } else if (group.getCode() == 62) {
                                        values[5] = ValueFactory.createValue(group.getIntValue());
                                } else if (group.getCode() == 10) {
                                        x = group.getDoubleValue();
                                } else if (group.getCode() == 20) {
                                        y = group.getDoubleValue();
                                } else if (group.getCode() == 30) {
                                        z = group.getDoubleValue();
                                } else if (group.getCode() == 1) {
                                        values[6] = ValueFactory.createValue(group.getValue());
                                } else if (group.getCode() == 40) {
                                        values[7] = ValueFactory.createValue(group.getDoubleValue());
                                } else if (group.getCode() == 7) {
                                        values[8] = ValueFactory.createValue(group.getValue());
                                }
                        }
                        if (!Double.isNaN(x) && !Double.isNaN(y)) {
                                values[0] = ValueFactory.createValue(new Point(new Coordinate(
                                        x, y, z), DPM, 0));
                                v.addValues(values);
                        }
                } catch (IOException ioe) {
                        throw ioe;
                }
                return group;
        }
}
