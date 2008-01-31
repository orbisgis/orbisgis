package org.orbisgis.geoview.renderer;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Interface used at render process to control overlapings of symbols. The
 * drawing of a symbol will return an envelope that will keep other symbols to
 * draw there. The other symbols will know where to draw by asking to this
 * interface.
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public interface RenderPermission {

	boolean canDraw(Envelope env);
}
