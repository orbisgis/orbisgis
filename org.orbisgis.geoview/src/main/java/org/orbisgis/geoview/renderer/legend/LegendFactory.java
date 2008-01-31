package org.orbisgis.geoview.renderer.legend;


public class LegendFactory {

	public static UniqueSymbolLegend createUniqueSymbolLegend() {
		return new DefaultUniqueSymbolLegend();
	}

	public static UniqueValueLegend createUniqueValueLegend() {
		return new DefaultUniqueValueLegend();
	}

	public static IntervalLegend createIntervalLegend() {
		return new DefaultIntervalLegend();
	}

	public static ProportionalLegend createProportionalLegend() {
		return new DefaultProportionalLegend();
	}

	public static Legend createLegendComposite(Legend... legends) {
		return new LegendComposite(legends);
	}

	public static LabelLegend createLabelLegend() {
		return new DefaultLabelLegend();
	}

}
