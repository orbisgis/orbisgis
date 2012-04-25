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
 * The STYLE item in the TABLES section
 * There is a static reader to read the item in a DXF file
 * and a toString method able to write it in a DXF form
 * @author Micha�l Michaud
 * @version 0.5.0
 */
// History
public class DxfTABLE_STYLE_ITEM extends DxfTABLE_ITEM {

        private float textHeight;
        private float widthFactor;
        private float obliquingAngle;
        private int textGenerationFlags;
        private float lastHeightUsed;
        private String primaryFontFileName;
        private String bigFontFileName;

        public DxfTABLE_STYLE_ITEM(String name, int flags) {
                super(name, flags);
                this.textHeight = 0f;
                this.widthFactor = 0f;
                this.obliquingAngle = 0f;
                this.textGenerationFlags = 0;
                this.lastHeightUsed = 0f;
                this.primaryFontFileName = "";
                this.bigFontFileName = "";
        }

        public DxfTABLE_STYLE_ITEM(String name, int flags,
                float textHeight,
                float widthFactor,
                float obliquingAngle,
                int textGenerationFlags,
                float lastHeightUsed,
                String primaryFontFileName,
                String bigFontFileName) {
                super(name, flags);
                this.textHeight = textHeight;
                this.widthFactor = widthFactor;
                this.obliquingAngle = obliquingAngle;
                this.textGenerationFlags = textGenerationFlags;
                this.lastHeightUsed = lastHeightUsed;
                this.primaryFontFileName = primaryFontFileName;
                this.bigFontFileName = bigFontFileName;
        }

        public float getTextHeight() {
                return textHeight;
        }

        public float getWidthFactor() {
                return widthFactor;
        }

        public float getObliquingAngle() {
                return obliquingAngle;
        }

        public int getTextGenerationFlags() {
                return textGenerationFlags;
        }

        public float getLastHeightUsed() {
                return lastHeightUsed;
        }

        public String getPrimaryFontFileName() {
                return primaryFontFileName;
        }

        public String getBigFontFileName() {
                return bigFontFileName;
        }

        public void setTextHeight(float textHeight) {
                this.textHeight = textHeight;
        }

        public void setWidthFactor(float widthFactor) {
                this.widthFactor = widthFactor;
        }

        public void setObliquingAngle(float obliquingAngle) {
                this.obliquingAngle = obliquingAngle;
        }

        public void setTextGenerationFlags(int textGenerationFlags) {
                this.textGenerationFlags = textGenerationFlags;
        }

        public void setLastHeightUsed(float lastHeightUsed) {
                this.lastHeightUsed = lastHeightUsed;
        }

        public void setPrimaryFontFileName(String primaryFontFileName) {
                this.primaryFontFileName = primaryFontFileName;
        }

        public void setBigFontFileName(String bigFontFileName) {
                this.bigFontFileName = bigFontFileName;
        }

        public static Map readTable(RandomAccessFile raf) throws IOException {
                DxfTABLE_STYLE_ITEM item = new DxfTABLE_STYLE_ITEM("DEFAULT", 0);
                Map table = new LinkedHashMap();
                try {
                        DxfGroup group;
                        while (null != (group = DxfGroup.readGroup(raf)) && !group.equals(ENDTAB)) {
                                if (group.equals(STYLE)) {
                                        item = new DxfTABLE_STYLE_ITEM("DEFAULT", 0);
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
                                } else if (group.getCode() == 40) {
                                        item.setTextHeight(group.getFloatValue());
                                } else if (group.getCode() == 41) {
                                        item.setWidthFactor(group.getFloatValue());
                                } else if (group.getCode() == 50) {
                                        item.setObliquingAngle(group.getFloatValue());
                                } else if (group.getCode() == 71) {
                                        item.setTextGenerationFlags(group.getIntValue());
                                } else if (group.getCode() == 42) {
                                        item.setLastHeightUsed(group.getFloatValue());
                                } else if (group.getCode() == 3) {
                                        item.setPrimaryFontFileName(group.getValue());
                                } else if (group.getCode() == 4) {
                                        item.setBigFontFileName(group.getValue());
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
                sb.append(DxfGroup.toString(40, textHeight, 3));
                sb.append(DxfGroup.toString(41, widthFactor, 3));
                sb.append(DxfGroup.toString(50, obliquingAngle, 3));
                sb.append(DxfGroup.toString(71, textGenerationFlags));
                sb.append(DxfGroup.toString(42, lastHeightUsed, 3));
                sb.append(DxfGroup.toString(3, primaryFontFileName));
                sb.append(DxfGroup.toString(4, bigFontFileName));
                return sb.toString();
        }
}
