/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vividsolutions.jts.geom.*;
import net.opengis.se._2_0.core.ElseFilterType;
import net.opengis.se._2_0.core.RuleType;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.FilterDataSourceDecorator;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryDimensionConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.GeometryValue;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Description;
import org.orbisgis.core.renderer.se.graphic.ExternalGraphic;
import org.orbisgis.core.renderer.se.graphic.Graphic;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.visitors.FeaturesVisitor;

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
    private Description description = new Description();
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
                Metadata metadata = layer.getDataSource().getMetadata();
                for (int i = 0; i < metadata.getFieldCount(); i++) {
                    Type fieldType = metadata.getFieldType(i);
                    int typeCode = fieldType.getTypeCode();
                    if ((TypeFactory.isVectorial(typeCode) && typeCode != Type.NULL)
                            || (typeCode == Type.RASTER)) {
                        int test;
                        if(typeCode == Type.GEOMETRY || typeCode == Type.GEOMETRYCOLLECTION){
                            test = getAccurateType(layer, i);
                        } else {
                            test = typeCode;
                        }
                        switch (test) {
                            case Type.GEOMETRY:
                            case Type.GEOMETRYCOLLECTION:
                                symb = new PointSymbolizer();
                                break;
                            case Type.POINT:
                            case Type.MULTIPOINT:
                                symb = new PointSymbolizer();
                                break;
                            case Type.LINESTRING:
                            case Type.MULTILINESTRING:
                                symb = new LineSymbolizer();
                                break;
                            case Type.POLYGON:
                            case Type.MULTIPOLYGON:
                                symb = new AreaSymbolizer();
                                break;
                            case Type.RASTER:
                                symb = new RasterSymbolizer();
                                break;
                            default:
                                throw new UnsupportedOperationException("Can't get the dimension of this type : " + TypeFactory.getTypeName(typeCode));
                        }
                        break;
                    }
                }
                if (symb != null) {
                    symbolizer.addSymbolizer(symb);
                }
            } catch (DriverException ex) {
                Logger.getLogger(Rule.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * We want to handle Geometry and GeometryCollection in a special way. We first check we don't have a dimension
     * constraint on the column. If we don't, we use the value of the first
     * @param layer The layer we want to analyze
     * @param sfi The index we will query in the data source contained in the input layer. Must be a valid spatial field.
     * @return The type found after analysis.
     */
    private int getAccurateType(ILayer layer, int sfi){
        try {
            DataSource ds = layer.getDataSource();
            boolean opened = false;
            if(!ds.isOpen()){
                ds.open();
                opened = true;
            }
            Type fieldType = ds.getMetadata().getFieldType(sfi);
            int ret = fieldType.getTypeCode();
            GeometryDimensionConstraint gdc =
                    (GeometryDimensionConstraint) fieldType.getConstraint(Constraint.DIMENSION_2D_GEOMETRY);
            if (gdc == null) {
                if(ds.getRowCount() > 0 && sfi > -1){
                    GeometryValue gv = (GeometryValue) ds.getFieldValue(0,sfi);
                    Geometry geom = gv.getAsGeometry();
                    if(geom instanceof Point || geom instanceof MultiPoint){
                        ret = Type.POINT;
                    } else if(geom instanceof LineString || geom instanceof MultiLineString){
                        ret = Type.LINESTRING;
                    } else if(geom instanceof Polygon || geom instanceof MultiPolygon){
                        ret = Type.POLYGON;
                    } else {
                        ret = Type.GEOMETRY;
                    }
                }
            } else {
                int dim = gdc.getDimension();
                switch (dim) {
                    case Type.POINT:
                    case Type.MULTIPOINT:
                        ret = Type.POINT;
                        break;
                    case Type.LINESTRING:
                    case Type.MULTILINESTRING:
                        ret = Type.LINESTRING;
                        break;
                    case Type.POLYGON:
                    case Type.MULTIPOLYGON:
                        ret = Type.POLYGON;
                        break;
                    default:
                        throw new UnsupportedOperationException("Can't get the dimension of this type : "
                                + TypeFactory.getTypeName(dim));
                }
            }
            if(opened){
                ds.close();
            }
            return ret;
        } catch (DriverException e) {
            throw new UnsupportedOperationException("Not able to open the input data source.",e);
        }
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
     * Return a new Spatial data source, according to rule filter and specified extent
     * In the case there is no filter to apply, sds is returned
     *
     * If the returned data source not equals sds, the new new data source must be purged
     *
     * @return The filtered data source.
     * @throws DriverException
     */
    public FilterDataSourceDecorator getFilteredDataSet(FilterDataSourceDecorator fds)
            throws DataSourceCreationException, DriverException {
        if (where != null && !where.isEmpty()) {
            return new FilterDataSourceDecorator(fds, where + getOrderBy());
        } else if (!getOrderBy().isEmpty()) {
            return new FilterDataSourceDecorator(fds, "1=1 " + getOrderBy());
        } else {
            return fds;
        }
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
