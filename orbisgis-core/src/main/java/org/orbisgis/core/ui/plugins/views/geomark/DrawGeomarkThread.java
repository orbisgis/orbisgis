package org.orbisgis.core.ui.plugins.views.geomark;

import java.awt.image.BufferedImage;

import org.gdms.data.ClosedDataSourceException;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.ui.editors.map.MapControl;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.progress.NullProgressMonitor;

import com.vividsolutions.jts.geom.Envelope;

public class DrawGeomarkThread implements BackgroundJob {

	private MapControl mc;
	private Envelope envelope;
	private int timeInSeconds = 1;

	public DrawGeomarkThread(MapControl mc, int timeInSeconds) {
		this.mc = mc;
		this.timeInSeconds = timeInSeconds;
	}

	public String getTaskName() {
		return "Drawing geomark";
	}

	public void run(IProgressMonitor pm) {
		try {

			MapTransform mt = mc.getMapTransform();
			mt.setExtent(envelope);
			BufferedImage bi = new BufferedImage(mc.getWidth(), mc.getHeight(),
					BufferedImage.TYPE_INT_ARGB);
			mt.setImage(bi);

			mc.getMapContext().draw(bi, mt.getAdjustedExtent(), pm);

			try {
				Thread.sleep(timeInSeconds * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (ClosedDataSourceException e) {
			throw e;

		} catch (RuntimeException e) {
			throw e;
		} catch (Error e) {
			throw e;
		} finally {
			// mc.repaint();
			mc.getMapContext().setBoundingBox(envelope);
		}
	}

	public void setExtend(Envelope envelope2) {
		this.envelope = envelope2;
	}
}