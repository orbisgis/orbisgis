package org.orbisgis.editorViews.toc.actions.cui.legends;

import java.awt.Color;
import java.awt.Graphics2D;

import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.renderer.legend.AbstractLegend;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.RenderException;
import org.orbisgis.renderer.symbol.Symbol;
import org.orbisgis.renderer.symbol.SymbolFactory;

public class MultipointLinkerLegend extends AbstractLegend implements Legend {

	@Override
	public void drawImage(Graphics2D g) {
		// TODO Auto-generated method stub

	}

	@Override
	public int[] getImageSize(Graphics2D g) {
		// TODO Auto-generated method stub
		return new int[] { 0, 0 };
	}

	@Override
	public String getJAXBContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getJAXBObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLegendTypeId() {
		return "org.orbisgis.legends.MultipointLinkerLegend";
	}

	@Override
	public String getLegendTypeName() {
		return "Multipoint Linker";
	}

	@Override
	public Symbol getSymbol(SpatialDataSourceDecorator sds, long row)
			throws RenderException {
		/*
		 * TODO Create a new Arrow symbol that draws multipoints
		 */
		return SymbolFactory.createLineSymbol(Color.black, 2);
	}

	@Override
	public int getSymbolAttributesSource() {
		return ONE_FEATURE_SOURCE;
	}

	@Override
	public Legend newInstance() {
		return new MultipointLinkerLegend();
	}

	@Override
	public void setJAXBObject(Object jaxbObject) {
		// TODO Auto-generated method stub

	}

}
