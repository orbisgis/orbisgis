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
 * The VPORT item in the TABLES section
 * There is a static reader to read the item in a DXF file
 * and a toString method able to write it in a DXF form
 * @author Micha�l Michaud
 * @version 0.5.0
 */
// History
public class DxfTABLE_VPORT_ITEM extends DxfTABLE_ITEM {
    private double[] lowerLeftCorner;   // XY 0.0 to 1.0
    private double[] upperRightCorner;  // XY 0.0 to 1.0
    private double[] centerPoint;       // XY
    private double[] snapBasePoint;     // XY
    private double[] snapSpacing;       // X and Y
    private double[] gridSpacing;       // X and Y
    private double[] viewDirection;     // XYZ
    private double[] viewTargetPoint;   // XYZ
    private double viewHeight;
    private float aspectRatio;
    private float lensLength;
    private double frontClippingPlaneOffset;
    private double backClippingPlaneOffset;
    private float snapRotationAngle;
    private float twistAngle;
    private int viewMode;
    private int circleZoomPercent;
    private int fastZoomSetting;
    private int ucsIconSetting;
    private int snapOnOff;
    private int gridOnOff;
    private int snapStyle;
    private int snapIsoPair;
    

    public DxfTABLE_VPORT_ITEM(String name, int flags) {
        super(name, flags);
        this.lowerLeftCorner = new double[2];
        this.upperRightCorner = new double[2];
        this.centerPoint = new double[2];
        this.snapBasePoint = new double[2];
        this.snapSpacing = new double[2];
        this.gridSpacing = new double[2];
        this.viewDirection = new double[3];
        this.viewTargetPoint = new double[3];
        this.viewHeight = 0;
        this.aspectRatio = 0;
        this.lensLength = 0;
        this.frontClippingPlaneOffset = 0;
        this.backClippingPlaneOffset = 0;
        this.snapRotationAngle = 0;
        this.twistAngle = 0;
        this.viewMode = 0;
        this.circleZoomPercent = 0;
        this.fastZoomSetting = 0;
        this.ucsIconSetting = 0;
        this.snapOnOff = 0;
        this.gridOnOff = 0;
        this.snapStyle = 0;
        this.snapIsoPair = 0;
    }

    public DxfTABLE_VPORT_ITEM(String name, int flags,
                                double[] lowerLeftCorner,
                                double[] upperRightCorner,
                                double[] centerPoint,
                                double[] snapBasePoint,
                                double[] snapSpacing,
                                double[] gridSpacing,
                                double[] viewDirection,
                                double[] viewTargetPoint,
                                double viewHeight,
                                float aspectRatio,
                                float lensLength,
                                double frontClippingPlaneOffset,
                                double backClippingPlaneOffset,
                                float snapRotationAngle,
                                float twistAngle,
                                int viewMode,
                                int circleZoomPercent,
                                int fastZoomSetting,
                                int ucsIconSetting,
                                int snapOnOff,
                                int gridOnOff,
                                int snapStyle,
                                int snapIsoPair ) {
        super(name, flags);
        this.lowerLeftCorner = lowerLeftCorner;
        this.upperRightCorner = upperRightCorner;
        this.centerPoint = centerPoint;
        this.snapBasePoint = snapBasePoint;
        this.snapSpacing = snapSpacing;
        this.gridSpacing = gridSpacing;
        this.viewDirection = viewDirection;
        this.viewTargetPoint = viewTargetPoint;
        this.viewHeight = viewHeight;
        this.aspectRatio = aspectRatio;
        this.lensLength = lensLength;
        this.frontClippingPlaneOffset = frontClippingPlaneOffset;
        this.backClippingPlaneOffset = backClippingPlaneOffset;
        this.snapRotationAngle = snapRotationAngle;
        this.twistAngle = twistAngle;
        this.viewMode = viewMode;
        this.circleZoomPercent = circleZoomPercent;
        this.fastZoomSetting = fastZoomSetting;
        this.ucsIconSetting = ucsIconSetting;
        this.snapOnOff = snapOnOff;
        this.gridOnOff = gridOnOff;
        this.snapStyle = snapStyle;
        this.snapIsoPair = snapIsoPair;
    }

    public double[] getLowerLeftCorner() {return lowerLeftCorner;}
    public double[] getUpperRightCorner() {return upperRightCorner;}
    public double[] getCenterPoint() {return centerPoint;}
    public double[] getSnapBasePoint() {return snapBasePoint;}
    public double[] getSnapSpacing() {return snapSpacing;}
    public double[] getGridSpacing() {return gridSpacing;}
    public double[] getViewDirection() {return viewDirection;}
    public double[] getViewTargetPoint() {return viewTargetPoint;}
    public double getViewHeight() {return viewHeight;}
    public float getAspectRatio() {return aspectRatio;}
    public float getLensLength() {return lensLength;}
    public double getFrontClippingPlaneOffset() {return frontClippingPlaneOffset;}
    public double getBackClippingPlaneOffset() {return backClippingPlaneOffset;}
    public float getSnapRotationAngle() {return snapRotationAngle;}
    public float getTwistAngle() {return twistAngle;}
    public int getViewMode() {return viewMode;}
    public int getCircleZoomPercent() {return circleZoomPercent;}
    public int getFastZoomSetting() {return fastZoomSetting;}
    public int getUcsIconSetting() {return ucsIconSetting;}
    public int getSnapOnOff() {return snapOnOff;}
    public int getGridOnOff() {return gridOnOff;}
    public int getSnapStyle() {return snapStyle;}
    public int getSnapIsoPair() {return snapIsoPair;}

    public void setLowerLeftCorner(double[] lowerLeftCorner) {
      this.lowerLeftCorner = lowerLeftCorner;
    }
    public void setUpperRightCorner(double[] upperRightCorner) {
      this.upperRightCorner = upperRightCorner;
    }
    public void setCenterPoint(double[] centerPoint) {
      this.centerPoint = centerPoint;
    }
    public void setSnapBasePoint(double[] snapBasePoint) {
      this.snapBasePoint = snapBasePoint;
    }
    public void setSnapSpacing(double[] snapSpacing) {
      this.snapSpacing = snapSpacing;
    }
    public void setGridSpacing(double[] gridSpacing) {
      this.gridSpacing = gridSpacing;
    }
    public void setViewDirection(double[] viewDirection) {
      this.viewDirection = viewDirection;
    }
    public void setViewTargetPoint(double[] viewTargetPoint) {
      this.viewTargetPoint = viewTargetPoint;
    }
    public void setViewHeight(double viewHeight) {
      this.viewHeight = viewHeight;
    }
    public void setAspectRatio(float aspectRatio) {
      this.aspectRatio = aspectRatio;
    }
    public void setLensLength(float lensLength) {
      this.lensLength = lensLength;
    }
    public void setFrontClippingPlaneOffset(double frontClippingPlaneOffset) {
      this.frontClippingPlaneOffset = frontClippingPlaneOffset;
    }
    public void setBackClippingPlaneOffset(double backClippingPlaneOffset) {
      this.backClippingPlaneOffset = backClippingPlaneOffset;
    }
    public void setSnapRotationAngle(float snapRotationAngle) {
      this.snapRotationAngle = snapRotationAngle;
    }
    public void setTwistAngle(float twistAngle) {
      this.twistAngle = twistAngle;
    }
    public void setViewMode(int viewMode) {
      this.viewMode = viewMode;
    }
    public void setCircleZoomPercent(int circleZoomPercent) {
      this.circleZoomPercent = circleZoomPercent;
    }
    public void setFastZoomSetting(int fastZoomSetting) {
      this.fastZoomSetting = fastZoomSetting;
    }
    public void setUcsIconSetting(int ucsIconSetting) {
      this.ucsIconSetting = ucsIconSetting;
    }
    public void setSnapOnOff(int snapOnOff) {
      this.snapOnOff = snapOnOff;
    }
    public void setGridOnOff(int gridOnOff) {
      this.gridOnOff = gridOnOff;
    }
    public void setSnapStyle(int snapStyle) {
      this.snapStyle = snapStyle;
    }
    public void setSnapIsoPair(int snapIsoPair) {
      this.snapIsoPair = snapIsoPair;
    }

    public static Map readTable(RandomAccessFile raf) throws IOException {
        DxfTABLE_VPORT_ITEM item = new DxfTABLE_VPORT_ITEM("DEFAULT", 0);
        Map table  = new LinkedHashMap();
        try {
            DxfGroup group;
            while (null != (group = DxfGroup.readGroup(raf)) && !group.equals(ENDTAB)) {
                if (group.equals(VPORT)) {
                    item = new DxfTABLE_VPORT_ITEM("DEFAULT", 0);
                }
                else if (group.getCode()==2) {
                    item.setName(group.getValue());
                    table.put(item.getName(), item);
                }
                else if (group.getCode()==5) {}   // tag appeared in version 13 of DXF
                else if (group.getCode()==100) {} // tag appeared in version 13 of DXF
                else if (group.getCode()==70) {
                  item.setFlags(group.getIntValue());
                }
                else if (group.getCode()==10) {
                  item.getLowerLeftCorner()[0] = group.getDoubleValue();
                }
                else if (group.getCode()==20) {
                  item.getLowerLeftCorner()[1] = group.getDoubleValue();
                }
                else if (group.getCode()==11) {
                  item.getUpperRightCorner()[0] = group.getDoubleValue();
                }
                else if (group.getCode()==21) {
                  item.getUpperRightCorner()[1] = group.getDoubleValue();
                }
                else if (group.getCode()==12) {
                  item.getCenterPoint()[0] = group.getDoubleValue();
                }
                else if (group.getCode()==22) {
                  item.getCenterPoint()[1] = group.getDoubleValue();
                }
                else if (group.getCode()==13) {
                  item.getSnapBasePoint()[0] = group.getDoubleValue();
                }
                else if (group.getCode()==23) {
                  item.getSnapBasePoint()[1] = group.getDoubleValue();
                }
                else if (group.getCode()==14) {
                  item.getSnapSpacing()[0] = group.getDoubleValue();
                }
                else if (group.getCode()==24) {
                  item.getSnapSpacing()[1] = group.getDoubleValue();
                }
                else if (group.getCode()==15) {
                  item.getGridSpacing()[0] = group.getDoubleValue();
                }
                else if (group.getCode()==25) {
                  item.getGridSpacing()[1] = group.getDoubleValue();
                }
                else if (group.getCode()==16) {
                  item.getViewDirection()[1] = group.getDoubleValue();
                }
                else if (group.getCode()==26) {
                  item.getViewDirection()[1] = group.getDoubleValue();
                }
                else if (group.getCode()==36) {
                  item.getViewDirection()[2] = group.getDoubleValue();
                }
                else if (group.getCode()==17) {
                  item.getViewTargetPoint()[1] = group.getDoubleValue();
                }
                else if (group.getCode()==27) {
                  item.getViewTargetPoint()[1] = group.getDoubleValue();
                }
                else if (group.getCode()==37) {
                  item.getViewTargetPoint()[2] = group.getDoubleValue();
                }
                else if (group.getCode()==40) {
                  item.setViewHeight(group.getDoubleValue());
                }
                else if (group.getCode()==41) {
                  item.setAspectRatio(group.getFloatValue());
                }
                else if (group.getCode()==42) {
                  item.setLensLength(group.getFloatValue());
                }
                else if (group.getCode()==43) {
                  item.setFrontClippingPlaneOffset(group.getDoubleValue());
                }
                else if (group.getCode()==44) {
                  item.setBackClippingPlaneOffset(group.getDoubleValue());
                }
                else if (group.getCode()==50) {
                  item.setSnapRotationAngle(group.getFloatValue());
                }
                else if (group.getCode()==51) {
                  item.setTwistAngle(group.getFloatValue());
                }
                else if (group.getCode()==71) {
                  item.setViewMode(group.getIntValue());
                }
                else if (group.getCode()==72) {
                  item.setCircleZoomPercent(group.getIntValue());
                }
                else if (group.getCode()==73) {
                  item.setFastZoomSetting(group.getIntValue());
                }
                else if (group.getCode()==74) {
                  item.setUcsIconSetting(group.getIntValue());
                }
                else if (group.getCode()==75) {
                  item.setSnapOnOff(group.getIntValue());
                }
                else if (group.getCode()==76) {
                  item.setGridOnOff(group.getIntValue());
                }
                else if (group.getCode()==77) {
                  item.setSnapStyle(group.getIntValue());
                }
                else if (group.getCode()==78) {
                    item.setSnapIsoPair(group.getIntValue());
                }
                else {}
            }
        } catch(IOException ioe) {throw ioe;}
        return table;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(super.toString());
        sb.append(DxfGroup.toString(10, lowerLeftCorner[0], 6));
        sb.append(DxfGroup.toString(20, lowerLeftCorner[1], 6));
        sb.append(DxfGroup.toString(11, upperRightCorner[0], 6));
        sb.append(DxfGroup.toString(21, upperRightCorner[1], 6));
        sb.append(DxfGroup.toString(12, centerPoint[0], 6));
        sb.append(DxfGroup.toString(22, centerPoint[1], 6));
        sb.append(DxfGroup.toString(13, snapBasePoint[0], 6));
        sb.append(DxfGroup.toString(23, snapBasePoint[1], 6));
        sb.append(DxfGroup.toString(14, snapSpacing[0], 6));
        sb.append(DxfGroup.toString(24, snapSpacing[1], 6));
        sb.append(DxfGroup.toString(15, gridSpacing[0], 6));
        sb.append(DxfGroup.toString(25, gridSpacing[1], 6));
        sb.append(DxfGroup.toString(16, viewDirection[0], 6));
        sb.append(DxfGroup.toString(26, viewDirection[1], 6));
        sb.append(DxfGroup.toString(36, viewDirection[2], 6));
        sb.append(DxfGroup.toString(17, viewTargetPoint[0], 6));
        sb.append(DxfGroup.toString(27, viewTargetPoint[1], 6));
        sb.append(DxfGroup.toString(37, viewTargetPoint[2], 6));
        sb.append(DxfGroup.toString(40, viewHeight, 6));
        sb.append(DxfGroup.toString(41, aspectRatio, 6));
        sb.append(DxfGroup.toString(42, lensLength, 6));
        sb.append(DxfGroup.toString(43, frontClippingPlaneOffset, 6));
        sb.append(DxfGroup.toString(44, backClippingPlaneOffset, 6));
        sb.append(DxfGroup.toString(50, snapRotationAngle, 6));
        sb.append(DxfGroup.toString(51, twistAngle, 6));
        sb.append(DxfGroup.toString(71, viewMode));
        sb.append(DxfGroup.toString(72, circleZoomPercent));
        sb.append(DxfGroup.toString(73, fastZoomSetting));
        sb.append(DxfGroup.toString(74, ucsIconSetting));
        sb.append(DxfGroup.toString(75, snapOnOff));
        sb.append(DxfGroup.toString(76, gridOnOff));
        sb.append(DxfGroup.toString(77, snapStyle));
        sb.append(DxfGroup.toString(78, snapIsoPair));
        return sb.toString();
    }

}
