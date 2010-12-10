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
 * The VIEW item in the TABLES section
 * There is a static reader to read the item in a DXF file
 * and a toString method able to write it in a DXF form
 * @author Micha�l Michaud
 * @version 0.5.0
 */
// History
public class DxfTABLE_VIEW_ITEM extends DxfTABLE_ITEM {
    private float viewHeight;
    private float viewWidth;
    private double viewCenterPointX;
    private double viewCenterPointY;
    private double[] viewDirectionFromTarget;
    private double[] targetPoint;
    private float lensLength;
    private double frontClippingPlaneOffset;
    private double backClippingPlaneOffset;
    private float twistAngle;
    private int viewMode;

    public DxfTABLE_VIEW_ITEM(String name, int flags) {
        super(name, flags);
        this.viewHeight = 0f;
        this.viewWidth = 0f;
        this.viewCenterPointX = 0.0;
        this.viewCenterPointY = 0;
        this.viewDirectionFromTarget = new double[3];
        this.targetPoint = new double[3];
        this.lensLength = 0f;
        this.frontClippingPlaneOffset = 0.0;
        this.backClippingPlaneOffset = 0.0;
        this.twistAngle = 0f;
        this.viewMode = 0;
    }

    public DxfTABLE_VIEW_ITEM(String name, int flags,
                              float viewHeight,
                              float viewWidth,
                              double viewCenterPointX,
                              double viewCenterPointY,
                              double[] viewDirectionFromTarget,
                              double[] targetPoint,
                              float lensLength,
                              double frontClippingPlaneOffset,
                              double backClippingPlaneOffset,
                              float twistAngle,
                              int viewMode) {
        super(name, flags);
        this.viewHeight = viewHeight;
        this.viewWidth = viewWidth;
        this.viewCenterPointX = viewCenterPointX;
        this.viewCenterPointY = viewCenterPointY;
        this.viewDirectionFromTarget = viewDirectionFromTarget;
        this.targetPoint = targetPoint;
        this.lensLength = lensLength;
        this.frontClippingPlaneOffset = frontClippingPlaneOffset;
        this.backClippingPlaneOffset = backClippingPlaneOffset;
        this.twistAngle = twistAngle;
        this.viewMode = viewMode;
    }

    public float getViewHeight() {return viewHeight;}
    public float getViewWidth() {return viewWidth;}
    public double getViewCenterPointX() {return viewCenterPointX;}
    public double getViewCenterPointY() {return viewCenterPointY;}
    public double[] getViewDirectionFromTarget() {return viewDirectionFromTarget;}
    public double[] getTargetPoint() {return targetPoint;}
    public float getLensLength() {return lensLength;}
    public double getFrontClippingPlaneOffset() {return frontClippingPlaneOffset;}
    public double getBackClippingPlaneOffset() {return backClippingPlaneOffset;}
    public float getTwistAngle() {return twistAngle;}
    public int getViewMode() {return viewMode;}

    public void setViewHeight(float viewHeight) {this.viewHeight = viewHeight;}
    public void setViewWidth(float viewWidth) {this.viewWidth = viewWidth;}
    public void setViewCenterPointX(double viewCenterPointX) {this.viewCenterPointX = viewCenterPointX;}
    public void setViewCenterPointY(double viewCenterPointY) {this.viewCenterPointY = viewCenterPointY;}
    public void setViewDirectionFromTarget(double[] viewDirectionFromTarget) {this.viewDirectionFromTarget = viewDirectionFromTarget;}
    public void setTargetPoint(double[] targetPoint) {this.targetPoint = targetPoint;}
    public void setLensLength(float lensLength) {this.lensLength = lensLength;}
    public void setFrontClippingPlaneOffset(double frontClippingPlaneOffset) {this.frontClippingPlaneOffset = frontClippingPlaneOffset;}
    public void setBackClippingPlaneOffset(double backClippingPlaneOffset) {this.backClippingPlaneOffset = backClippingPlaneOffset;}
    public void setTwistAngle(float twistAngle) {this.twistAngle = twistAngle;}
    public void setViewMode(int viewMode) {this.viewMode = viewMode;}

    public static Map readTable(RandomAccessFile raf) throws IOException {
        DxfTABLE_VIEW_ITEM item = new DxfTABLE_VIEW_ITEM("DEFAULT", 0);
        Map table  = new LinkedHashMap();
        try {
            DxfGroup group;
            while (null != (group = DxfGroup.readGroup(raf)) && !group.equals(ENDTAB)) {
                if (group.equals(VIEW)) {
                    item = new DxfTABLE_VIEW_ITEM("DEFAULT", 0);
                }
                else if (group.getCode()==2) {
                    item.setName(group.getValue());
                    table.put(item.getName(), item);
                }
                else if (group.getCode()==5) {}   // tag appeared in version 13 of DXF
                else if (group.getCode()==100) {} // tag appeared in version 13 of DXF
                else if (group.getCode()==70) {item.setFlags(group.getIntValue());}
                else if (group.getCode()==40) {item.setViewHeight(group.getFloatValue());}
                else if (group.getCode()==41) {item.setViewWidth(group.getFloatValue());}
                else if (group.getCode()==10) {item.setViewCenterPointX(group.getDoubleValue());}
                else if (group.getCode()==20) {item.setViewCenterPointY(group.getDoubleValue());}
                else if (group.getCode()==11) {
                  item.getViewDirectionFromTarget()[0] = group.getDoubleValue();
                }
                else if (group.getCode()==21) {
                  item.getViewDirectionFromTarget()[1] = group.getDoubleValue();
                }
                else if (group.getCode()==31) {
                  item.getViewDirectionFromTarget()[2] = group.getDoubleValue();
                }
                else if (group.getCode()==12) {
                  item.getTargetPoint()[0] = group.getDoubleValue();
                }
                else if (group.getCode()==22) {
                  item.getTargetPoint()[1] = group.getDoubleValue();
                }
                else if (group.getCode()==32) {
                  item.getTargetPoint()[2] = group.getDoubleValue();
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
                  item.setTwistAngle(group.getFloatValue());
                }
                else if (group.getCode()==71) {
                  item.setViewMode(group.getIntValue());
                }
                else {}
            }
        } catch(IOException ioe) {throw ioe;}
        return table;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(super.toString());
        sb.append(DxfGroup.toString(40, viewHeight, 6));
        sb.append(DxfGroup.toString(41, viewWidth, 6));
        sb.append(DxfGroup.toString(10, viewCenterPointX, 6));
        sb.append(DxfGroup.toString(20, viewCenterPointY, 6));
        sb.append(DxfGroup.toString(11, viewDirectionFromTarget[0], 6));
        sb.append(DxfGroup.toString(21, viewDirectionFromTarget[1], 6));
        sb.append(DxfGroup.toString(31, viewDirectionFromTarget[2], 6));
        sb.append(DxfGroup.toString(12, targetPoint[0], 6));
        sb.append(DxfGroup.toString(22, targetPoint[1], 6));
        sb.append(DxfGroup.toString(32, targetPoint[2], 6));
        sb.append(DxfGroup.toString(42, lensLength, 6));
        sb.append(DxfGroup.toString(43, frontClippingPlaneOffset, 6));
        sb.append(DxfGroup.toString(44, backClippingPlaneOffset, 6));
        sb.append(DxfGroup.toString(50, twistAngle, 6));
        sb.append(DxfGroup.toString(71, viewMode));
        return sb.toString();
    }

}
