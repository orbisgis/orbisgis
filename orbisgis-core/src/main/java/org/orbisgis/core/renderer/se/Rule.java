/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.renderer.se;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.vividsolutions.jts.geom.*;
import net.opengis.se._2_0.core.ElseFilterType;
import net.opengis.se._2_0.core.RuleType;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Description;
import org.orbisgis.core.renderer.se.graphic.ExternalGraphic;
import org.orbisgis.core.renderer.se.graphic.Graphic;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.visitors.FeaturesVisitor;
import org.orbisgis.sputilities.GeometryTypeCodes;
import org.orbisgis.sputilities.SFSUtilities;
import org.orbisgis.sputilities.SpatialResultSet;

import javax.sql.DataSource;

/**
 * Rules are used to group rendering instructions by feature-property conditions and map scales.
 * Rule definitions are placed immediately inside of FeatureType- or coverage-style definitions.</p>
 * <p>According to SE 2.0, a <code>Rule</code> contains only one <code>Symbolizer</code> - but that  
 * <code>Symbolizer</code> can be a composite one. This implementation directly embedded 
 * a <code>CompositeSymbolizer</code> that will contain one -or more- actual <code>Symbolizer</code>
 * representation.
 * @author Maxence Laurent
 */
public final class Rule extends AbstractSymbolizerNode {

    /**
     * The name set to every rule, if not set externally.
     */
    public static final String DEFAULT_NAME = "Default Rule";
    private String name = "";
    private Description description;
    private String where;
    private boolean fallbackRule = false;
    private Double minScaleDenom = null;
    private Double maxScaleDenom = null;
    private CompositeSymbolizer symbolizer;

    /**
     * Create a default, empty Rule, with a default inner (and empty) CompositeSymbolizer.
     */
    public Rule() {
        symbolizer = new CompositeSymbolizer();
        symbolizer.setParent(this);
    }

    @Override
    public String toString() {
        if (name != null && !name.equalsIgnoreCase("")) {
            return name;
        } else {
            return "Untitled rule";
        }
    }

    /**
     * Build a Rule using a ILayer. This contains a CompositeSymbolizer, populated 
     * according to the first found geometry in the DataSet  embedded in the ILayer.
     * That means we'll obtain a <code>LineSymbolizer</code> if this first geometry is of 
     * dimension 1, a <code>PolygonSymbolizer</code> if it is of dimension 2,
     * and a <code>PointSymbolizer</code> otherwise.
     * @param layer The layer that will receive a new default symbolizer.
     */
    public Rule(ILayer layer) {
        this();

        this.name = "Default Rule";
        createSymbolizer(layer);

    }

    /**
     * Short circuit method to create the good symbolizer 
     * according the first spatial field.
     * @param layer The layer that will receive a new default symbolizer.
     */
    public void createSymbolizer(ILayer layer) {
        if (layer != null) {

            try {
                    Symbolizer symb = null;
                    DataSource ds = Services.getService(DataSource.class);
                    Connection connection = ds.getConnection();
                    try {
                        /*
                            TODO
                            case GeometryTypeCodes.RASTER:
                                symb = new RasterSymbolizer();
                                break;
                        */
                        int typeCode = SFSUtilities.getGeometryType(connection,SFSUtilities.splitCatalogSchemaTableName(layer.getTableReference()),"");
                        if(typeCode== GeometryTypeCodes.GEOMETRY || typeCode==GeometryTypeCodes.GEOMCOLLECTION) {
                            // No symbol for Geometry type code, parse the ResultSet
                            typeCode = getAccurateType(layer);
                        }
                        switch (typeCode) {
                            case GeometryTypeCodes.POINT:
                            case GeometryTypeCodes.MULTIPOINT:
                                symb = new PointSymbolizer();
                                break;
                            case GeometryTypeCodes.LINESTRING:
                            case GeometryTypeCodes.MULTILINESTRING:
                                symb = new LineSymbolizer();
                                break;
                            case GeometryTypeCodes.POLYGON:
                            case GeometryTypeCodes.MULTIPOLYGON:
                                symb = new AreaSymbolizer();
                                break;
                            default:
                                symb = new PointSymbolizer();
                        }
                        symbolizer.addSymbolizer(symb);
                    } finally {
                        connection.close();
                    }
            } catch (SQLException ex) {
                Logger.getLogger(Rule.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * We want to handle Geometry and GeometryCollection in a special way. We first check we don't have a dimension
     * constraint on the column. If we don't, we use the value of the first
     * @param layer The layer we want to analyze
     * @return The type found after analysis.
     */
    private int getAccurateType(ILayer layer) throws SQLException {
        DataSource ds = Services.getService(DataSource.class);
        Connection connection = ds.getConnection();
        try {
            String tableRef = layer.getTableReference();
            SpatialResultSet rs = connection.createStatement().executeQuery("select * from "+tableRef+" LIMIT 1").unwrap(SpatialResultSet.class);
            if(rs.next()) {
                Geometry geom = rs.getGeometry();
                if(geom instanceof Point || geom instanceof MultiPoint){
                    return GeometryTypeCodes.POINT;
                } else if(geom instanceof LineString || geom instanceof MultiLineString){
                    return GeometryTypeCodes.LINESTRING;
                } else if(geom instanceof Polygon || geom instanceof MultiPolygon){
                    return GeometryTypeCodes.POLYGON;
                } else {
                    return GeometryTypeCodes.GEOMETRY;
                }
            }
        } finally {
            connection.close();
        }
        return GeometryTypeCodes.GEOMETRY;
    }

    /**
     * Build a rule, using both a <code>RuleType</code> and an <code>ILayer</code>.
     * The inner <code>CompositeSymbolizer</code> will be populated according to 
     * the information contained in <code>rt</code>
     * @param rt A JaXB representation of the input style.
     * @param layer
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public Rule(RuleType rt, ILayer layer) throws InvalidStyle {
        //this(layer);

        if (rt.getName() != null) {
            this.name = rt.getName();
        } else {
            this.name = Rule.DEFAULT_NAME;
        }
        if(rt.getDescription() != null){
            description = new Description(rt.getDescription());
        }

        /*
         * Is a fallback rule ?
         * If a ElseFilter is defined, this rule is a fallback one
         */
        this.fallbackRule = rt.getElseFilter() != null;

        if (rt.getMinScaleDenominator() != null) {
            this.setMinScaleDenom(rt.getMinScaleDenominator());
        }

        if (rt.getMaxScaleDenominator() != null) {
            this.setMaxScaleDenom(rt.getMaxScaleDenominator());
        }

        if (rt.getSymbolizer() != null) {
            this.setCompositeSymbolizer(new CompositeSymbolizer(rt.getSymbolizer()));
        } else {
                setCompositeSymbolizer(new CompositeSymbolizer());
        }

        /*
         * TODO  Replace with fes
         */
        if (rt.getWhereClause() != null) {
            this.setWhere(rt.getWhereClause());
        }
    }

    /**
     * Replace the current inner <code>CompositeSymbolizer</code> with <code>cs</code>
     * @param cs The new inner {@link CompositeSymbolizer}.
     */
    public void setCompositeSymbolizer(CompositeSymbolizer cs) {
        this.symbolizer = cs;
        cs.setParent(this);
    }

    /**
     * Get the inner <code>CompositeSymbolizer</code>
     * @return The inner {@link CompositeSymbolizer}.
     */
    public CompositeSymbolizer getCompositeSymbolizer() {
        return symbolizer;
    }

    /**
     * Fill and return a JaXB representation of this rule (ie a <code>RuleType</code>)
     * @return The JaXB representation of this Rule as a {@link RuleType} instance.
     */
    public RuleType getJAXBType() {
        RuleType rt = new RuleType();

        if (!this.name.equals(Rule.DEFAULT_NAME)) {
            rt.setName(this.name);
        }

        if (this.minScaleDenom != null) {
            rt.setMinScaleDenominator(minScaleDenom);
        }

        if (this.maxScaleDenom != null) {
            rt.setMaxScaleDenominator(maxScaleDenom);
        }

        if (this.isFallbackRule()) {
            rt.setElseFilter(new ElseFilterType());
        } else if (this.getWhere() != null && !this.getWhere().isEmpty()) {
            rt.setWhereClause(this.getWhere());
            // Temp HACK TODO !! Serialize Filters !!!!
        }
        if(description != null){
            rt.setDescription(description.getJAXBType());
        }

        rt.setSymbolizer(this.symbolizer.getJAXBElement());

        return rt;
    }
    /**
     * Get the <code>where</code> clause associated to this rule.
     * @return 
     *      The associated <code>where</code> clause.
     */
    public String getWhere() {
        return where;
    }

    /**
     * Replace the current inner <code>where</code> clause.
     * @param where The new where clause.
     */
    public void setWhere(String where) {
        this.where = where;
    }

    /**
     * Build a OrderBy clause to be used to optimize GDMS queries.
     * @return The "order by" clause
     */
    private String getOrderBy() {
        for (Symbolizer s : getCompositeSymbolizer().getSymbolizerList()) {
            if (s instanceof PointSymbolizer) {
                PointSymbolizer ps = (PointSymbolizer) s;
                GraphicCollection gc = ps.getGraphicCollection();
                int i;
                StringBuilder f = new StringBuilder();
                for (i = 0; i < gc.getNumGraphics(); i++) {
                    Graphic g = gc.getGraphic(i);
                    if (g instanceof MarkGraphic) {
                        MarkGraphic mark = (MarkGraphic) g;
                        if (mark.getViewBox() != null) {
                            FeaturesVisitor fv = new FeaturesVisitor();
                            mark.getViewBox().acceptVisitor(fv);
                            f.append(" ");
                            f.append(fv.getResult());
                        }
                    } else if (g instanceof ExternalGraphic) {
                        ExternalGraphic extG = (ExternalGraphic) g;
                        if (extG.getViewBox() != null) {
                            FeaturesVisitor fv = new FeaturesVisitor();
                            extG.getViewBox().acceptVisitor(fv);
                            f.append(" ");
                            f.append(fv.getResult());
                        }
                    }
                    // TODO add others cases !
                }

                // If view box depends on features => order by 
                String result = f.toString().trim();
                if (!result.isEmpty()) {
                    String[] split = result.split(" ");
                    return " ORDER BY " + split[0] + " DESC";
                } else {
                    return "";
                }
            }
        }
        return "";
    }

    /**
     * Checks if this <code>Rule</code> is a fallback one or not, ie if the
     * <code>where</code> associated to this <code>Rule</code> was included 
     * in an <code>ElseFilter</code>.
     * 
     * @return 
     *      <code>true</code> if this rule contains an <code>ElseFilter</code>
     */
    public boolean isFallbackRule() {
        return fallbackRule;
    }

    /**
     * If <code>fallbackRule</code> is true, this rule will be considered as an
     * <code>ElseFilter</code> clause.
     * @param fallbackRule If true, this Rule will be considered as an ElseFilter.
     */
    public void setFallbackRule(boolean fallbackRule) {
        this.fallbackRule = fallbackRule;
    }

    /**
     * Gets the maximum scale for which this <code>Rule</code> (and the features it is applied on)
     * must be displayed.
     * @return 
     * The maximum scale for rendering this Rule. The returned value is actually 
     * <b>the denominator of the scale</b>. Consequently, a value of 10 000 000
     * means a <b>scale of 1:10-million</b>
     */
    public Double getMaxScaleDenom() {
        return maxScaleDenom;
    }

    /**
     * Set the maximum scale for which this <code>Rule</code> (and the things
     * rendered from it) is displayed.
     * @param maxScaleDenom The  expected value is actually 
     * <b>the denominator of the scale</b>. Consequently, a value of 10 000 000
     * means a <b>scale of 1:10-million</b>
     */
    public void setMaxScaleDenom(Double maxScaleDenom) {
        if (maxScaleDenom != null && maxScaleDenom > 0) {
            this.maxScaleDenom = maxScaleDenom;
        } else {
            this.maxScaleDenom = null;
        }
    }

    /**
     * Gets the minimum scale for which this <code>Rule</code> (and the features it is applied on)
     * must be displayed.
     * @return 
     * The minimum scale for rendering this Rule. The returned value is actually 
     * <b>the denominator of the scale</b>. Consequently, a value of 10 000 000
     * means a <b>scale of 1:10-million</b>
     */
    public Double getMinScaleDenom() {
        return minScaleDenom;
    }

    /**
     * Set the minimum scale for which this <code>Rule</code> (and the things
     * rendered from it) is displayed.
     * @param minScaleDenom The  expected value is actually 
     * <b>the denominator of the scale</b>. Consequently, a value of 10 000 000
     * means a <b>scale of 1:10-million</b>
     */
    public void setMinScaleDenom(Double minScaleDenom) {
        if (minScaleDenom != null && minScaleDenom > 0) {
            this.minScaleDenom = minScaleDenom;
        } else {
            this.minScaleDenom = null;
        }
    }

    /**
     * This method checks that this rule is valid for the given 
     * {@link MapTransform}. That means that, if {@code scale} is the scale
     * denominator associated to the {@code MapTransform mt}, this method 
     * returns true if {@code minScaleDenom <= scale <= maxScalDenom}
     * @param mt The tested MapTransform.
     * @return 
     * <ul><li>{@code true} if {@code minScaleDenom <= scale <= maxScalDenom}
     * . Note that {@code null} values for the inner scale denominator values
     * are considered to be equivalent to 0 and positive infinity.</li>
     * <li>false otherwise.</li></ul>
     */
    public boolean isDomainAllowed(MapTransform mt) {
        double scale = mt.getScaleDenominator();

        return (this.minScaleDenom == null && this.maxScaleDenom == null)
                || (this.minScaleDenom == null && this.maxScaleDenom != null && this.maxScaleDenom > scale)
                || (this.minScaleDenom != null && scale > this.minScaleDenom && this.maxScaleDenom == null)
                || (this.minScaleDenom != null && this.maxScaleDenom != null && scale > this.minScaleDenom && this.maxScaleDenom > scale);
    }

    /**
     * Get the description of this rule.
     * @return The Description of this Rule.
     * @see Description
     */
    public Description getDescription() {
        return description;
    }

    /**
     * Set the description associated to this rule.
     * @param description The new description for this.
     * @see Description
     */
    public void setDescription(Description description) {
        this.description = description;
    }

    /**
     * Get the name of this rule.
     * @return The name of the rule.
     */
    public String getName() {
        return name;
    }

    /**
     * Set a new name to this rule.
     * @param name The new name of this rule.
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<SymbolizerNode> getChildren() {
            List<SymbolizerNode> ls = new ArrayList<SymbolizerNode>();
            ls.add(getCompositeSymbolizer());
            return ls;
    }


}
