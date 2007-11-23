package org.orbisgis.geoview;

public interface IGeoviewAction {

	public void actionPerformed(GeoView2D geoview);

	public boolean isEnabled(GeoView2D geoView2D);

	public boolean isVisible(GeoView2D geoView2D);

}
