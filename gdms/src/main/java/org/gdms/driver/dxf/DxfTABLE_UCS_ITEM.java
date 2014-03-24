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
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The UCS item in the TABLES section
 * There is a static reader to read the item in a DXF file
 * and a toString method able to write it in a DXF form
 * @author Micha�l Michaud
 * @version 0.5.0
 */
// History
public class DxfTABLE_UCS_ITEM extends DxfTABLE_ITEM {

        private double[] origin = new double[3];
        private double[] xAxisDirection = new double[3];
        private double[] yAxisDirection = new double[3];

        public DxfTABLE_UCS_ITEM(String name, int flags) {
                super(name, flags);
                this.origin = new double[3];
                this.xAxisDirection = new double[3];
                this.yAxisDirection = new double[3];
        }

        public DxfTABLE_UCS_ITEM(String name, int flags,
                double[] origin,
                double[] xAxisDirection,
                double[] yAxisDirection) {
                super(name, flags);
                this.origin = origin;
                this.xAxisDirection = xAxisDirection;
                this.yAxisDirection = yAxisDirection;
        }

        public double[] getOrigin() {
                return origin;
        }

        public double getOriginX() {
                return origin[0];
        }

        public double getOriginY() {
                return origin[1];
        }

        public double getOriginZ() {
                return origin[2];
        }

        public double[] getXAxisDirection() {
                return xAxisDirection;
        }

        public double getXAxisDirectionX() {
                return xAxisDirection[0];
        }

        public double getXAxisDirectionY() {
                return xAxisDirection[1];
        }

        public double getXAxisDirectionZ() {
                return xAxisDirection[2];
        }

        public double[] getYAxisDirection() {
                return yAxisDirection;
        }

        public double getYAxisDirectionX() {
                return yAxisDirection[0];
        }

        public double getYAxisDirectionY() {
                return yAxisDirection[1];
        }

        public double getYAxisDirectionZ() {
                return yAxisDirection[2];
        }

        public void setOrigin(double[] origin) {
                this.origin = origin;
        }

        public void setOriginX(double originX) {
                this.origin[0] = originX;
        }

        public void setOriginY(double originY) {
                this.origin[1] = originY;
        }

        public void setOriginZ(double originZ) {
                this.origin[2] = originZ;
        }

        public void setXAxisDirection(double[] xAxisDirection) {
                this.xAxisDirection = xAxisDirection;
        }

        public void setXAxisDirectionX(double xAxisDirectionX) {
                this.xAxisDirection[0] = xAxisDirectionX;
        }

        public void setXAxisDirectionY(double xAxisDirectionY) {
                this.xAxisDirection[1] = xAxisDirectionY;
        }

        public void setXAxisDirectionZ(double xAxisDirectionZ) {
                this.xAxisDirection[2] = xAxisDirectionZ;
        }

        public void setYAxisDirection(double[] yAxisDirection) {
                this.yAxisDirection = yAxisDirection;
        }

        public void setYAxisDirectionX(double yAxisDirectionX) {
                this.yAxisDirection[0] = yAxisDirectionX;
        }

        public void setYAxisDirectionY(double yAxisDirectionY) {
                this.yAxisDirection[1] = yAxisDirectionY;
        }

        public void setYAxisDirectionZ(double yAxisDirectionZ) {
                this.yAxisDirection[2] = yAxisDirectionZ;
        }

        public static Map readTable(RandomAccessFile raf) throws IOException {
                DxfTABLE_UCS_ITEM item = new DxfTABLE_UCS_ITEM("DEFAULT", 0);
                Map table = new LinkedHashMap();
                try {
                        DxfGroup group;
                        while (null != (group = DxfGroup.readGroup(raf)) && !group.equals(ENDTAB)) {
                                if (group.equals(UCS)) {
                                        item = new DxfTABLE_UCS_ITEM("DEFAULT", 0);
                                } else if (group.getCode() == 2) {
                                        item.setName(group.getValue());
                                        table.put(item.getName(), item);
//                                } else if (group.getCode() == 5) {
//                                        // tag appeared in version 13 of DXF
//                                }
//                                else if (group.getCode() == 100) {
//                                        // tag appeared in version 13 of DXF
                                } 
                                else if (group.getCode() == 70) {
                                        item.setFlags(group.getIntValue());
                                } else if (group.getCode() == 10) {
                                        item.setOriginX(group.getDoubleValue());
                                } else if (group.getCode() == 20) {
                                        item.setOriginY(group.getDoubleValue());
                                } else if (group.getCode() == 30) {
                                        item.setOriginZ(group.getDoubleValue());
                                } else if (group.getCode() == 11) {
                                        item.setXAxisDirectionX(group.getDoubleValue());
                                } else if (group.getCode() == 21) {
                                        item.setXAxisDirectionY(group.getDoubleValue());
                                } else if (group.getCode() == 31) {
                                        item.setXAxisDirectionZ(group.getDoubleValue());
                                } else if (group.getCode() == 12) {
                                        item.setYAxisDirectionX(group.getDoubleValue());
                                } else if (group.getCode() == 22) {
                                        item.setYAxisDirectionY(group.getDoubleValue());
                                } else if (group.getCode() == 32) {
                                        item.setYAxisDirectionZ(group.getDoubleValue());
                                }
                        }
                } catch (IOException ioe) {
                        throw ioe;
                }
                return table;
        }

        @Override
        public String toString() {
                StringBuilder sb = new StringBuilder(super.toString());
                sb.append(DxfGroup.toString(10, origin[0], 6));
                sb.append(DxfGroup.toString(20, origin[1], 6));
                sb.append(DxfGroup.toString(30, origin[2], 6));
                sb.append(DxfGroup.toString(11, xAxisDirection[0], 6));
                sb.append(DxfGroup.toString(21, xAxisDirection[1], 6));
                sb.append(DxfGroup.toString(31, xAxisDirection[2], 6));
                sb.append(DxfGroup.toString(12, yAxisDirection[0], 6));
                sb.append(DxfGroup.toString(22, yAxisDirection[1], 6));
                sb.append(DxfGroup.toString(32, yAxisDirection[2], 6));
                return sb.toString();
        }
}
