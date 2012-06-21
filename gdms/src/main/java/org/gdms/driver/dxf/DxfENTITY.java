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

import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * A DXF ENTITY is equivalent to a JUMP feature. This class is the parent class
 * for POLYLINE, POINT, LINE and every kind of geometric entity present in a DXF
 * file.
 * 
 * @author Micha�l Michaud
 * @version 0.5.0
 */
// History
// 2006-10-19 : add multi-geometry export
// add attribute tests an ability to export ANY jump layer
// add ability to export holes in a separate layer or not
public class DxfENTITY {
	public final static DxfGroup LINE = new DxfGroup(0, "LINE");
	public final static DxfGroup POINT = new DxfGroup(0, "POINT");
	public final static DxfGroup CIRCLE = new DxfGroup(0, "CIRCLE");
	public final static DxfGroup ARC = new DxfGroup(0, "ARC");
	public final static DxfGroup TRACE = new DxfGroup(0, "TRACE");
	public final static DxfGroup SOLID = new DxfGroup(0, "SOLID");
	public final static DxfGroup TEXT = new DxfGroup(0, "TEXT");
	public final static DxfGroup SHAPE = new DxfGroup(0, "SHAPE");
	public final static DxfGroup BLOCK = new DxfGroup(0, "BLOCK");
	public final static DxfGroup ENDBLK = new DxfGroup(0, "ENDBLK");
	public final static DxfGroup INSERT = new DxfGroup(0, "INSERT");
	public final static DxfGroup ATTDEF = new DxfGroup(0, "ATTDEF");
	public final static DxfGroup ATTRIB = new DxfGroup(0, "ATTRIB");
	public final static DxfGroup POLYLINE = new DxfGroup(0, "POLYLINE");
	public final static DxfGroup LWPOLYLINE = new DxfGroup(0, "LWPOLYLINE");
	public final static DxfGroup VERTEX = new DxfGroup(0, "VERTEX");
	public final static DxfGroup SEQEND = new DxfGroup(0, "SEQEND");
	public final static DxfGroup _3DFACE = new DxfGroup(0, "3DFACE");
	public final static DxfGroup VIEWPORT = new DxfGroup(0, "VIEWPORT");
	public final static DxfGroup DIMENSION = new DxfGroup(0, "DIMENSION");
	public final static PrecisionModel DPM = new PrecisionModel();
	public static int precision = 3;

	private String layerName = "DEFAULT";
//	private String lineType = null;
//	private float elevation = 0f;
//	private float thickness = 0f;
//	private int colorNumber = 256;
//	private int space = 0;
//	private double[] extrusionDirection = null;
//	private int flags = 0;

	public String getLayerName() {
		return layerName;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public DxfENTITY(String layerName) {
		this.layerName = layerName;
	}

	/*
	 * public static String feature2Dxf(Feature feature, String layerName,
	 * boolean suffix) { Geometry g = feature.getGeometry(); if
	 * (g.getGeometryType().equals("Point")) { return point2Dxf(feature,
	 * layerName); } else if (g.getGeometryType().equals("LineString")) { return
	 * lineString2Dxf(feature, layerName); } else if
	 * (g.getGeometryType().equals("Polygon")) { return polygon2Dxf(feature,
	 * layerName, suffix); } else if (g instanceof GeometryCollection) {
	 * StringBuffer sb = new StringBuffer(); for (int i = 0 ; i <
	 * g.getNumGeometries() ; i++) { Feature ff = (Feature)feature.clone();
	 * ff.setGeometry(g.getGeometryN(i)); sb.append(feature2Dxf(ff, layerName,
	 * suffix)); } return sb.toString(); } else {return null;} }
	 * 
	 * public static String point2Dxf(Feature feature, String layerName) {
	 * StringBuffer sb = null; boolean hasText =
	 * (feature.getSchema().hasAttribute("TEXT") &&
	 * !feature.getAttribute("TEXT").equals("")); if (hasText) {sb = new
	 * StringBuffer(DxfGroup.toString(0, "TEXT"));} else {sb = new
	 * StringBuffer(DxfGroup.toString(0, "POINT"));} if
	 * (feature.getSchema().hasAttribute("LAYER") &&
	 * !feature.getString("LAYER").trim().equals("")) {
	 * sb.append(DxfGroup.toString(8, feature.getAttribute("LAYER"))); } else
	 * {sb.append(DxfGroup.toString(8, layerName));} if
	 * (feature.getSchema().hasAttribute("LTYPE") &&
	 * !feature.getAttribute("LTYPE").equals("BYLAYER")) {
	 * sb.append(DxfGroup.toString(6, feature.getAttribute("LTYPE"))); } if
	 * (feature.getSchema().hasAttribute("ELEVATION") &&
	 * !feature.getAttribute("ELEVATION").equals(new Float(0f))) {
	 * sb.append(DxfGroup.toString(38, feature.getAttribute("ELEVATION"))); } if
	 * (feature.getSchema().hasAttribute("THICKNESS") &&
	 * !feature.getAttribute("THICKNESS").equals(new Float(0f))) {
	 * sb.append(DxfGroup.toString(39, feature.getAttribute("THICKNESS"))); } if
	 * (feature.getSchema().hasAttribute("COLOR") &&
	 * !(((Integer)feature.getAttribute("COLOR")).intValue() == 256)) {
	 * sb.append(DxfGroup.toString(62,
	 * feature.getAttribute("COLOR").toString())); } Coordinate coord =
	 * ((Point)feature.getGeometry()).getCoordinate();
	 * sb.append(DxfGroup.toString(10, coord.x, precision));
	 * sb.append(DxfGroup.toString(20, coord.y, precision)); if
	 * (!Double.isNaN(coord.z)) sb.append(DxfGroup.toString(30, coord.z,
	 * precision)); if (feature.getSchema().hasAttribute("TEXT_HEIGHT") &&
	 * !feature.getAttribute("TEXT_HEIGHT").equals(new Float(0f))) {
	 * sb.append(DxfGroup.toString(40, feature.getAttribute("TEXT_HEIGHT"))); }
	 * if (hasText && feature.getSchema().hasAttribute("TEXT_HEIGHT")) {
	 * sb.append(DxfGroup.toString(1, feature.getAttribute("TEXT"))); } if
	 * (hasText && feature.getSchema().hasAttribute("TEXT_HEIGHT")) {
	 * sb.append(DxfGroup.toString(7, feature.getAttribute("TEXT_STYLE"))); }
	 * return sb.toString(); }
	 * 
	 * public static String lineString2Dxf(Feature feature, String layerName) {
	 * LineString geom = (LineString)feature.getGeometry(); Coordinate[] coords
	 * = geom.getCoordinates(); // Correction added by L. Becker and R
	 * Littlefield on 2006-11-08 // It writes 2 points-only polylines in a line
	 * instead of a polyline // to make it possible to incorporate big dataset
	 * in View32 boolean isLine = (coords.length == 2); StringBuffer sb; if
	 * (!isLine) { sb = new StringBuffer(DxfGroup.toString(0, "POLYLINE")); }
	 * else { sb = new StringBuffer(DxfGroup.toString(0, "LINE")); }
	 * //StringBuffer sb = new StringBuffer(DxfGroup.toString(0, "POLYLINE"));
	 * if (feature.getSchema().hasAttribute("LAYER") &&
	 * !feature.getString("LAYER").trim().equals("")) {
	 * sb.append(DxfGroup.toString(8, feature.getAttribute("LAYER"))); } else
	 * {sb.append(DxfGroup.toString(8, layerName));} if
	 * (feature.getSchema().hasAttribute("LTYPE") &&
	 * !feature.getAttribute("LTYPE").equals("BYLAYER")) {
	 * sb.append(DxfGroup.toString(6, feature.getAttribute("LTYPE"))); } if
	 * (feature.getSchema().hasAttribute("ELEVATION") &&
	 * !feature.getAttribute("ELEVATION").equals(new Float(0f))) {
	 * sb.append(DxfGroup.toString(38, feature.getAttribute("ELEVATION"))); } if
	 * (feature.getSchema().hasAttribute("THICKNESS") &&
	 * !feature.getAttribute("THICKNESS").equals(new Float(0f))) {
	 * sb.append(DxfGroup.toString(39, feature.getAttribute("THICKNESS"))); } if
	 * (feature.getSchema().hasAttribute("COLOR") &&
	 * !(((Integer)feature.getAttribute("COLOR")).intValue() == 256)) {
	 * sb.append(DxfGroup.toString(62,
	 * feature.getAttribute("COLOR").toString())); } // modified by L. Becker
	 * and R. Littlefield (add the Line case) if (isLine){
	 * sb.append(DxfGroup.toString(10, coords[0].x, precision));
	 * sb.append(DxfGroup.toString(20, coords[0].y, precision)); if
	 * (!Double.isNaN(coords[0].z)) sb.append(DxfGroup.toString(30, "0.0"));
	 * sb.append(DxfGroup.toString(11, coords[1].x, precision));
	 * sb.append(DxfGroup.toString(21, coords[1].y, precision)); if
	 * (!Double.isNaN(coords[1].z)) sb.append(DxfGroup.toString(31, "0.0")); }
	 * else { sb.append(DxfGroup.toString(66, 1));
	 * sb.append(DxfGroup.toString(10, "0.0")); sb.append(DxfGroup.toString(20,
	 * "0.0")); if (!Double.isNaN(coords[0].z)) sb.append(DxfGroup.toString(30,
	 * "0.0")); sb.append(DxfGroup.toString(70, 8));
	 * 
	 * for (int i = 0 ; i < coords.length ; i++) {
	 * sb.append(DxfGroup.toString(0, "VERTEX")); if
	 * (feature.getSchema().hasAttribute("LAYER") &&
	 * !feature.getString("LAYER").trim().equals("")) {
	 * sb.append(DxfGroup.toString(8, feature.getAttribute("LAYER"))); } else
	 * {sb.append(DxfGroup.toString(8, layerName));}
	 * sb.append(DxfGroup.toString(10, coords[i].x, precision));
	 * sb.append(DxfGroup.toString(20, coords[i].y, precision)); if
	 * (!Double.isNaN(coords[i].z)) sb.append(DxfGroup.toString(30, coords[i].z,
	 * precision)); sb.append(DxfGroup.toString(70, 32)); }
	 * sb.append(DxfGroup.toString(0, "SEQEND")); } return sb.toString(); }
	 * 
	 * public static String polygon2Dxf(Feature feature, String layerName,
	 * boolean suffix) { //System.out.println("polygon2Dxf " + suffix); Polygon
	 * geom = (Polygon)feature.getGeometry(); Coordinate[] coords =
	 * geom.getExteriorRing().getCoordinates(); StringBuffer sb = new
	 * StringBuffer(DxfGroup.toString(0, "POLYLINE"));
	 * sb.append(DxfGroup.toString(8, layerName)); if
	 * (feature.getSchema().hasAttribute("LTYPE") &&
	 * !feature.getAttribute("LTYPE").equals("BYLAYER")) {
	 * sb.append(DxfGroup.toString(6, feature.getAttribute("LTYPE"))); } if
	 * (feature.getSchema().hasAttribute("ELEVATION") &&
	 * !feature.getAttribute("ELEVATION").equals(new Float(0f))) {
	 * sb.append(DxfGroup.toString(38, feature.getAttribute("ELEVATION"))); } if
	 * (feature.getSchema().hasAttribute("THICKNESS") &&
	 * !feature.getAttribute("THICKNESS").equals(new Float(0f))) {
	 * sb.append(DxfGroup.toString(39, feature.getAttribute("THICKNESS"))); } if
	 * (feature.getSchema().hasAttribute("COLOR") &&
	 * !(((Integer)feature.getAttribute("COLOR")).intValue() == 256)) {
	 * sb.append(DxfGroup.toString(62,
	 * feature.getAttribute("COLOR").toString())); }
	 * sb.append(DxfGroup.toString(66, 1)); sb.append(DxfGroup.toString(10,
	 * "0.0")); sb.append(DxfGroup.toString(20, "0.0")); if
	 * (!Double.isNaN(coords[0].z)) sb.append(DxfGroup.toString(30, "0.0"));
	 * sb.append(DxfGroup.toString(70, 9)); for (int i = 0 ; i < coords.length ;
	 * i++) { sb.append(DxfGroup.toString(0, "VERTEX"));
	 * sb.append(DxfGroup.toString(8, layerName));
	 * sb.append(DxfGroup.toString(10, coords[i].x, precision));
	 * sb.append(DxfGroup.toString(20, coords[i].y, precision)); if
	 * (!Double.isNaN(coords[i].z)) sb.append(DxfGroup.toString(30, coords[i].z,
	 * precision)); sb.append(DxfGroup.toString(70, 32)); }
	 * sb.append(DxfGroup.toString(0, "SEQEND")); for (int h = 0 ; h <
	 * geom.getNumInteriorRing() ; h++) {
	 * //System.out.println("polygon2Dxf (hole)" + suffix);
	 * sb.append(DxfGroup.toString(0, "POLYLINE")); if (suffix)
	 * sb.append(DxfGroup.toString(8, layerName+"_")); else
	 * sb.append(DxfGroup.toString(8, layerName)); if
	 * (feature.getSchema().hasAttribute("LTYPE") &&
	 * !feature.getAttribute("LTYPE").equals("BYLAYER")) {
	 * sb.append(DxfGroup.toString(6, feature.getAttribute("LTYPE"))); } if
	 * (feature.getSchema().hasAttribute("THICKNESS") &&
	 * !feature.getAttribute("THICKNESS").equals(new Float(0f))) {
	 * sb.append(DxfGroup.toString(39, feature.getAttribute("THICKNESS"))); } if
	 * (feature.getSchema().hasAttribute("COLOR") &&
	 * !(((Integer)feature.getAttribute("COLOR")).intValue() == 256)) {
	 * sb.append(DxfGroup.toString(62, feature.getAttribute("COLOR"))); }
	 * sb.append(DxfGroup.toString(66, 1)); sb.append(DxfGroup.toString(10,
	 * "0.0")); sb.append(DxfGroup.toString(20, "0.0")); if
	 * (!Double.isNaN(coords[0].z)) sb.append(DxfGroup.toString(30, "0.0"));
	 * sb.append(DxfGroup.toString(70, 9)); coords =
	 * geom.getInteriorRingN(h).getCoordinates(); for (int i = 0 ; i <
	 * coords.length ; i++) { sb.append(DxfGroup.toString(0, "VERTEX")); if
	 * (suffix) sb.append(DxfGroup.toString(8, layerName+"_")); else
	 * sb.append(DxfGroup.toString(8, layerName));
	 * sb.append(DxfGroup.toString(10, coords[i].x, precision));
	 * sb.append(DxfGroup.toString(20, coords[i].y, precision)); if
	 * (!Double.isNaN(coords[i].z)) sb.append(DxfGroup.toString(30, coords[i].z,
	 * precision)); sb.append(DxfGroup.toString(70, 32)); }
	 * sb.append(DxfGroup.toString(0, "SEQEND")); }
	 * 
	 * return sb.toString(); }
	 */

}
