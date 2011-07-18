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

/**
 * The LTYPE item in the TABLES section
 * There is a static reader to read the item in a DXF file
 * and a toString method able to write it in a DXF form
 * @author Micha�l Michaud
 * @version 0.5.0
 */
// History
public class DxfTABLE_LTYPE_ITEM extends DxfTABLE_ITEM {

        private String description;
        private int alignment;
        private float patternLength;
        private float[] pattern;

        public DxfTABLE_LTYPE_ITEM(String name, int flags) {
                super(name, flags);
                this.description = "";
                this.alignment = 0;
                this.patternLength = 1f;
                this.pattern = new float[]{1f};
        }

        public DxfTABLE_LTYPE_ITEM(String name, int flags, String description, int alignment, float patternLength, float[] pattern) {
                super(name, flags);
                this.description = description;
                this.alignment = alignment;
                this.patternLength = patternLength;
                this.pattern = pattern;
        }

        public String getDescription() {
                return description;
        }

        public void setDescription(String description) {
                this.description = description;
        }

        public int getAlignment() {
                return alignment;
        }

        public void setAlignment(int alignment) {
                this.alignment = alignment;
        }

        public float getPatternLength() {
                return patternLength;
        }

        public void setPatternLength(float patternLength) {
                this.patternLength = patternLength;
        }

        public float[] getPattern() {
                return pattern;
        }

        public void setPattern(float[] pattern) {
                this.pattern = pattern;
        }

        public static Map readTable(RandomAccessFile raf) throws IOException {
                DxfTABLE_LTYPE_ITEM item = new DxfTABLE_LTYPE_ITEM("DEFAULT", 0);
                Map table = new LinkedHashMap();
                try {
                        DxfGroup group;
                        int patternDashCount = 0;
                        while (null != (group = DxfGroup.readGroup(raf)) && !group.equals(ENDTAB)) {
                                if (group.equals(LTYPE)) {
                                        item = new DxfTABLE_LTYPE_ITEM("DEFAULT", 0);
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
                                } else if (group.getCode() == 3) {
                                        item.setDescription(group.getValue());
                                } else if (group.getCode() == 72) {
                                        item.setAlignment(group.getIntValue());
                                } else if (group.getCode() == 73) {
                                        item.setPattern(new float[group.getIntValue()]);
                                } else if (group.getCode() == 40) {
                                        item.setPatternLength(group.getFloatValue());
                                } else if (group.getCode() == 49 && patternDashCount < item.getPattern().length) {
                                        item.getPattern()[patternDashCount++] = group.getFloatValue();
                                }
                        }
                } catch (IOException ioe) {
                        throw ioe;
                }
                return table;
        }

        public String toString() {
                StringBuffer sb = new StringBuffer(super.toString());
                sb.append(DxfGroup.toString(3, description));
                sb.append(DxfGroup.toString(72, alignment));
                sb.append(DxfGroup.toString(73, pattern.length));
                sb.append(DxfGroup.toString(40, patternLength, 3));
                for (int i = 0; i < pattern.length; i++) {
                        sb.append(DxfGroup.toString(49, pattern[i], 3));
                }
                return sb.toString();
        }
}
