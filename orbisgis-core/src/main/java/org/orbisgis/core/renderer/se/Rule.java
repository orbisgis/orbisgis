/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se;

import com.vividsolutions.jts.geom.Geometry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.SemanticException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.RuleType;
import org.orbisgis.core.renderer.se.common.Uom;

/**
 *
 * @author maxence
 */
public class Rule implements SymbolizerNode {

    public Rule(ILayer layer) {
        symbolizer = new CompositeSymbolizer();
        symbolizer.setParent(this);

        Geometry geometry = null;
        try {
            geometry = layer.getDataSource().getGeometry(0);
        } catch (DriverException ex) {
        }

        Symbolizer symb;

        if (geometry != null) {
            switch (geometry.getDimension()) {
                case 1:
                    symb = new LineSymbolizer();
                    break;
                case 2:
                    symb = new AreaSymbolizer();
                    break;
                case 0:
                default:
                    symb = new PointSymbolizer();
                    break;
            }
        } else {
            symb = new PointSymbolizer();
        }

        symbolizer.addSymbolizer(symb);
    }

    public Rule(RuleType rt, ILayer layer) {
        this(layer);

        if (rt.getMinScaleDenominator() != null) {
            this.setMinScaleDenom(rt.getMinScaleDenominator());
        }

        if (rt.getMaxScaleDenominator() != null) {
            this.setMaxScaleDenom(rt.getMaxScaleDenominator());
        }

        if (rt.getSymbolizer() != null) {
            this.setCompositeSymbolizer(new CompositeSymbolizer(rt.getSymbolizer()));
        }
    }

    public void setCompositeSymbolizer(CompositeSymbolizer cs) {
        this.symbolizer = cs;
        cs.setParent(this);
    }

    public CompositeSymbolizer getCompositeSymbolizer() {
        return symbolizer;
    }

    public RuleType getJAXBType() {
        RuleType rt = new RuleType();

        if (this.minScaleDenom > 0) {
            rt.setMinScaleDenominator(minScaleDenom);
        }

        if (this.maxScaleDenom > 0) {
            rt.setMaxScaleDenominator(maxScaleDenom);
        }

        rt.setSymbolizer(this.symbolizer.getJAXBElement());

        return rt;
    }
    private CompositeSymbolizer symbolizer;

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

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public SpatialDataSourceDecorator getFilteredDataSource() throws DriverLoadException, DataSourceCreationException, DriverException, ParseException, SemanticException {
        FeatureTypeStyle ft = (FeatureTypeStyle) fts;

        ILayer layer = ft.getLayer();

        SpatialDataSourceDecorator ds = layer.getDataSource();

        String query = "select * from " + ds.getName();

        if (where != null) {
            query += " " + where;
        }

        return new SpatialDataSourceDecorator(layer.getDataSource().getDataSourceFactory().getDataSourceFromSQL(query));
    }

    /*
     * Is the feature id fit with this rule filter
     * 
     */
    public boolean isFeatureAllowed(long fid) {
        try {
            SpatialDataSourceDecorator filteredDataSource = this.getFilteredDataSource();
            filteredDataSource.getFeature(fid);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isFallbackRule() {
        return fallbackRule;
    }

    public void setFallbackRule(boolean fallbackRule) {
        this.fallbackRule = fallbackRule;
    }

    public double getMaxScaleDenom() {
        return maxScaleDenom;
    }

    public void setMaxScaleDenom(double maxScaleDenom) {
        this.maxScaleDenom = maxScaleDenom;
    }

    public double getMinScaleDenom() {
        return minScaleDenom;
    }

    public void setMinScaleDenom(double minScaleDenom) {
        this.minScaleDenom = minScaleDenom;
    }

    public boolean isDomainAllowed(MapTransform mt) {
        double scale = mt.getScaleDenominator();
        return (this.minScaleDenom < 0 && this.maxScaleDenom < 0)
                || (this.minScaleDenom < 0 && this.maxScaleDenom > scale)
                || (scale > this.minScaleDenom && this.maxScaleDenom < 0)
                || (scale > this.minScaleDenom && this.maxScaleDenom > scale);
    }
    private SymbolizerNode fts;
    private String where;
    private boolean fallbackRule = false;
    private double minScaleDenom = -1;
    private double maxScaleDenom = -1;
}
