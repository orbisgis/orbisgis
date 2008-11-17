package org.orbisgis.editorViews.toc.actions.cui.legends;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.orbisgis.editorViews.toc.actions.cui.LegendContext;
import org.orbisgis.editorViews.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.renderer.legend.Legend;

public class PnlMultipointLinker extends JPanel implements ILegendPanel {

	private MultipointLinkerLegend legend;

	@Override
	public boolean acceptsGeometryType(int geometryType) {
		return (geometryType & (POINT | POLYGON)) > 0;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public Legend getLegend() {
		return legend;
	}

	@Override
	public void initialize(LegendContext lc) {
		legend = new MultipointLinkerLegend();
		legend.setName(legend.getLegendTypeName());
		this.add(new JLabel(
				"Configurate the linking fields here: start, end and id"));
	}

	@Override
	public ILegendPanel newInstance() {
		return new PnlMultipointLinker();
	}

	@Override
	public void setLegend(Legend legend) {
		this.legend = (MultipointLinkerLegend) legend;
	}

	@Override
	public String validateInput() {
		return null;
	}

}
