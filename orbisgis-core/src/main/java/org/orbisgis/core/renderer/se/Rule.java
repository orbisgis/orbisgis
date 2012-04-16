/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se;

import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.opengis.se._2_0.core.ElseFilterType;
import net.opengis.se._2_0.core.RuleType;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.FilterDataSourceDecorator;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryDimensionConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.graphic.ExternalGraphic;
import org.orbisgis.core.renderer.se.graphic.Graphic;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;

/**
 * Rules are used to group rendering instructions by featyre-property conditions and map scales. 
 * Rule definitions are placed immediately inside of featuretype- or coverage-style definitions.</p>
 * <p>According to SE 2.0, a <code>Rule</code> contains only one <code>Symbolizer</code> - but that  
 * <code>Symbolizer</code> can be a composite one. This implementation directly embedded 
 * a <code>CompositeSymbolizer</code> that will contain one -or more- actual <code>Symbolizer</code>
 * representation.
 * @author maxence
 */
public final class Rule implements SymbolizerNode {

    /**
     * The name set to every rule, if not set externally.
     */
    public static final String DEFAULT_NAME = "Default Rule";
    private String name = "";
    private String description = "";
    private boolean visible = true;
    private SymbolizerNode fts;
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
     * according to the first found geometry in the DataSource  embedded in the ILayer.
     * That means we'll obtain a <code>LineSymbolizer</code> if this first geometry is of 
     * dimension 1, a <code>PolygonSymbolizer</code> if it is of dimension 2,
     * and a <code>PointSymbolizer</code> otherwise.
     * @param layer 
     */
    public Rule(ILayer layer) {
        this();

        this.name = "Default Rule";
        createSymbolizer(layer);

    }

    /**
     * Short circuit method to create the good symbolizer 
     * according the first spatial field.
     * @param layer 
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
                        switch (typeCode) {
                            case Type.GEOMETRY:
                            case Type.GEOMETRYCOLLECTION:
                                GeometryDimensionConstraint gdc =
                                        (GeometryDimensionConstraint) fieldType.getConstraint(Constraint.DIMENSION_2D_GEOMETRY);
                                if (gdc == null) {
                                    symb = new PointSymbolizer();
                                    break;
                                } else {
                                    int dim = gdc.getDimension();
                                    switch (dim) {
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
                                        default:
                                            throw new UnsupportedOperationException("Can't get the dimension of this type : " + TypeFactory.getTypeName(typeCode));
                                    }
                                }
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
     * Build a rule, using both a <code>RuleType</code> and an <code>ILayer</code>.
     * The inner <code>CompositeSymbolizer</code> will be populated according to 
     * the informations contained in <code>rt</code>
     * @param rt
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
         * TODO  Replace with WhereClause !!
        if (rt.getDomainConstraints() != null && rt.getDomainConstraints().getTimePeriod() != null){
        this.setWhere(rt.getDomainConstraints().getTimePeriod());
        }*/
        if (rt.getWhereClause() != null) {
            this.setWhere(rt.getWhereClause());
        }
    }

    /**
     * Replace the current inner <code>CompositeSymbolizer</code> with <code>cs</code>
     * @param cs 
     */
    public void setCompositeSymbolizer(CompositeSymbolizer cs) {
        this.symbolizer = cs;
        cs.setParent(this);
    }

    /**
     * Get the inner <code>CompositeSymbolizer</code>
     * @return 
     */
    public CompositeSymbolizer getCompositeSymbolizer() {
        return symbolizer;
    }

    /**
     * Fill and return a Jaxb representation of this rule (ie a <code>RuleType</code>)
     * @return 
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
            //rt.setDomainConstraints(new DomainConstraintsType());
            //rt.getDomainConstraints().setTimePeriod(this.getWhere());
        }

        rt.setSymbolizer(this.symbolizer.getJAXBElement());

        return rt;
    }

    @Override
    public Uom getUom() {
        return null;
    }

    @Override
    public SymbolizerNode getParent() {
        return fts;
    }

    @Override
    public void setParent(SymbolizerNode fts) {
        this.fts = fts;
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
     * @param where 
     */
    public void setWhere(String where) {
        this.where = where;
    }

    /**
     * Return a new Spatial data source, according to rule filter and specified extent
     * In the case there is no filter to apply, sds is returned
     *
     * If the returned data source not equals sds, the new new datasource must be purged
     *
     * @return
     * @throws DriverLoadException
     * @throws DataSourceCreationException
     * @throws DriverException
     * @throws ParseException
     * @throws SemanticException
     */
    public FilterDataSourceDecorator getFilteredDataSource(FilterDataSourceDecorator fds)
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
     * @return 
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
                            f.append(" ");
                            f.append(mark.getViewBox().dependsOnFeature());
                        }
                    } else if (g instanceof ExternalGraphic) {
                        ExternalGraphic extG = (ExternalGraphic) g;
                        if (extG.getViewBox() != null) {
                            f.append(" ");
                            f.append(extG.getViewBox().dependsOnFeature());
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
     * If <code>falbackrule</code> is true, this rule will be considered as an
     * <code>ElseFilter</code> clause.
     * @param fallbackRule 
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
     * 
     * @return 
     * True if the Rule is visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * If set to true, the rule is visible.
     * @param visible 
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * This method checks that this rule is valid for the given 
     * {@link MapTransform}. That means that, if {@code scale} is the scale
     * denominator associated to the {@code MapTransform mt}, this method 
     * returns true if {@code minScaleDenom <= scale <= maxScalDenom}
     * @param mt
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
     * @return 
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description associated to this rule.
     * @param description 
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the name of this rule.
     * @return 
     */
    public String getName() {
        return name;
    }

    /**
     * Set a new name to this rule.
     * @param name 
     */
    public void setName(String name) {
        this.name = name;
    }

    public HashSet<String> dependsOnFeature() {
        return symbolizer.dependsOnFeature();
    }
}
