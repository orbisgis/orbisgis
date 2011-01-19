/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se;

import com.vividsolutions.jts.geom.Geometry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gdms.data.DataSourceCreationException;
import org.gdms.data.FilterDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.SemanticException;

import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.DomainConstraintsType;
import org.orbisgis.core.renderer.persistance.se.ElseFilterType;
import org.orbisgis.core.renderer.persistance.se.RuleType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Uom;

/**
 *
 * @author maxence
 */
public final class Rule implements SymbolizerNode {

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

	public Rule(ILayer layer) {
		this();

		this.name = "Default Rule";

		Geometry geometry = null;
		try {
			geometry = layer.getSpatialDataSource().getGeometry(0);
		} catch (DriverException ex) {
			Logger.getLogger(Rule.class.getName()).log(Level.SEVERE, null, ex);
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

	public Rule(RuleType rt, ILayer layer) throws InvalidStyle {
		this(layer);

		if (rt.getName() != null) {
			this.name = rt.getName();
		} else {
			this.name = Rule.DEFAULT_NAME;
		}

		/*
		 * Is a fallback rule ?
		 */
		/*if (rt.getElseFilter() != null) {
			this.fallbackRule = true;
		} else {
			this.fallbackRule = false;
			//this.filter = new FilterOperator(rt.getFilter());
		}*/

		// If a ElseFilter is defined, this rule is a fallback one
		this.fallbackRule = rt.getElseFilter() != null;

		if (rt.getMinScaleDenominator() != null) {
			this.setMinScaleDenom(rt.getMinScaleDenominator());
		}

		if (rt.getMaxScaleDenominator() != null) {
			this.setMaxScaleDenom(rt.getMaxScaleDenominator());
		}

		if (rt.getSymbolizer() != null) {
			this.setCompositeSymbolizer(new CompositeSymbolizer(rt.getSymbolizer()));
		}

		if (rt.getDomainConstraints() != null && rt.getDomainConstraints().getTimePeriod() != null){
			this.setWhere(rt.getDomainConstraints().getTimePeriod());
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

		if (!this.name.equals(Rule.DEFAULT_NAME)) {
			rt.setName(this.name);
		}

		if (this.minScaleDenom != null) {
			rt.setMinScaleDenominator(minScaleDenom);
		}

		if (this.maxScaleDenom != null) {
			rt.setMaxScaleDenominator(maxScaleDenom);
		}

		if (this.isFallbackRule()){
			rt.setElseFilter(new ElseFilterType());
		} else if(this.getWhere() != null && !this.getWhere().isEmpty())
		{
			// Temp HACK TODO !! Serialize Filters !!!!
			rt.setDomainConstraints(new DomainConstraintsType());
		    rt.getDomainConstraints().setTimePeriod(this.getWhere());
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

	public String getWhere() {
		return where;
	}

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
	public FilterDataSourceDecorator getFilteredDataSource(FilterDataSourceDecorator fds) throws DriverLoadException, DataSourceCreationException, DriverException, ParseException, SemanticException {
		if (where != null && !where.isEmpty()) {
			return new FilterDataSourceDecorator(fds, where);
		} else {
			return fds;
		}
	}

	/**
	 * Return a Spatial data source, according to rule filter and specified extent
	 * @return
	 * @throws DriverLoadException
	 * @throws DataSourceCreationException
	 * @throws DriverException
	 * @throws ParseException
	 * @throws SemanticException
	 */
	/*
	public FilterDataSourceDecorator getFilteredDataSource() throws DriverLoadException, DataSourceCreationException, DriverException, ParseException, SemanticException {
		FeatureTypeStyle ft = (FeatureTypeStyle) fts;

		ILayer layer = ft.getLayer();
		SpatialDataSourceDecorator sds = layer.getDataSource();
		return this.getFilteredDataSource(sds);
	}*/

	public boolean isFallbackRule() {
		return fallbackRule;
	}

	public void setFallbackRule(boolean fallbackRule) {
		this.fallbackRule = fallbackRule;
	}

	public Double getMaxScaleDenom() {
		return maxScaleDenom;
	}

	public void setMaxScaleDenom(Double maxScaleDenom) {
		if (maxScaleDenom != null && maxScaleDenom > 0)
			this.maxScaleDenom = maxScaleDenom;
		else
			this.maxScaleDenom = null;
	}

	public Double getMinScaleDenom() {
		return minScaleDenom;
	}

	public void setMinScaleDenom(Double minScaleDenom) {
		if (minScaleDenom != null && minScaleDenom > 0)
			this.minScaleDenom = minScaleDenom;
		else
			this.minScaleDenom = null;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isDomainAllowed(MapTransform mt) {
		double scale = mt.getScaleDenominator();

		return (this.minScaleDenom == null && this.maxScaleDenom == null)
				|| (this.minScaleDenom == null && this.maxScaleDenom != null && this.maxScaleDenom > scale)
				|| (this.minScaleDenom != null && scale > this.minScaleDenom && this.maxScaleDenom == null)
				|| (this.minScaleDenom != null && this.maxScaleDenom != null && scale > this.minScaleDenom && this.maxScaleDenom > scale);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
