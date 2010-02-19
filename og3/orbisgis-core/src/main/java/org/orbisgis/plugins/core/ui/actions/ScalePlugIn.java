/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2009 Erwan BOCHER, Pierre-yves FADET
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    Pierre-Yves.Fadet_at_ec-nantes.fr
 *    thomas.leduc _at_ cerma.archi.fr
 */

package org.orbisgis.plugins.core.ui.actions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Observable;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.layerModel.MapContext;
import org.orbisgis.plugins.core.map.MapTransform;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.editor.IEditor;
import org.orbisgis.plugins.core.ui.views.MapEditorPlugIn;
import org.orbisgis.plugins.core.ui.views.editor.EditorManager;
import org.orbisgis.plugins.core.ui.workbench.Names;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.images.IconLoader;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class ScalePlugIn extends AbstractPlugIn {
	public static final String SCALE_1000 = "1000";
	public static final String SCALE_5000 = "5000";
	public static final String SCALE_10000 = "10000";
	public static final String SCALE_25000 = "25000";
	public static final String SCALE_50000 = "50000";
	public static final String SCALE_100000 = "100000";
	public static final String SCALE_500000 = "500000";
	public static final String SCALE_1000000 = "1000000";
	public static final String SCALE_5000000 = "5000000";
	public static final String SCALE_10000000 = "10000000";

	private JPanel panel;

	public boolean execute(PlugInContext context) throws Exception {
		System.out.println(this.getClass() + ": execute");

		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();
		if ("Map".equals(em.getEditorId(editor)) && editor != null) {
			MapEditorPlugIn mapEditor = (MapEditorPlugIn) editor;
			MapTransform mt = mapEditor.getMapTransform();

			if (mt != null) {

				Envelope envelope = mt.getAdjustedExtent();
				if (envelope != null) {

					JComboBox cb = (JComboBox) getActionComponent();
					String scale = (String) cb.getSelectedItem();

					if (scale.equals(SCALE_1000)) {

						envelope = getEnveloppeFromScale(
								mt.getAdjustedExtent(), mt.getWidth(), 1000);

						mt.setExtent(envelope);

					} else if (scale.equals(SCALE_5000)) {

						envelope = getEnveloppeFromScale(
								mt.getAdjustedExtent(), mt.getWidth(), 5000);

						mt.setExtent(envelope);

					} else if (scale.equals(SCALE_10000)) {
						envelope = getEnveloppeFromScale(
								mt.getAdjustedExtent(), mt.getWidth(), 10000);

						mt.setExtent(envelope);

					} else if (scale.equals(SCALE_25000)) {
						envelope = getEnveloppeFromScale(
								mt.getAdjustedExtent(), mt.getWidth(), 25000);

						mt.setExtent(envelope);

					} else if (scale.equals(SCALE_50000)) {
						envelope = getEnveloppeFromScale(
								mt.getAdjustedExtent(), mt.getWidth(), 50000);

						mt.setExtent(envelope);

					} else if (scale.equals(SCALE_100000)) {
						envelope = getEnveloppeFromScale(
								mt.getAdjustedExtent(), mt.getWidth(), 100000);

						mt.setExtent(envelope);

					} else if (scale.equals(SCALE_1000000)) {
						envelope = getEnveloppeFromScale(
								mt.getAdjustedExtent(), mt.getWidth(), 1000000);

						mt.setExtent(envelope);

					} else if (scale.equals(SCALE_500000)) {
						envelope = getEnveloppeFromScale(
								mt.getAdjustedExtent(), mt.getWidth(), 500000);

						mt.setExtent(envelope);

					}

					else if (scale.equals(SCALE_5000000)) {
						envelope = getEnveloppeFromScale(
								mt.getAdjustedExtent(), mt.getWidth(), 5000000);

						mt.setExtent(envelope);

					}

					else if (scale.equals(SCALE_10000000)) {
						envelope = getEnveloppeFromScale(
								mt.getAdjustedExtent(), mt.getWidth(), 10000000);

						mt.setExtent(envelope);

					}

				}
			}
			return true;
		}

		return false;
	}

	public void initialize(PlugInContext context) throws Exception {
		panel = new JPanel(new BorderLayout());
		JLabel label = new JLabel("Scale : ");
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		label.setForeground(Color.BLUE);
		JComboBox combobox = new JComboBox(new String[] { SCALE_1000,
				SCALE_5000, SCALE_10000, SCALE_25000, SCALE_50000,
				SCALE_100000, SCALE_500000, SCALE_1000000, SCALE_5000000,
				SCALE_10000000 });
		combobox.setAlignmentX(Component.RIGHT_ALIGNMENT);
		combobox.setMaximumSize(new Dimension(100, 20));
		panel.add(label);
		panel.add(combobox);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		setActionComponent(combobox);
		setTypeListener("item");
		WorkbenchContext wbcontext = context.getWorkbenchContext();
		wbcontext.getWorkbench().getFrame().getNavigationToolBar().addPlugIn(
				this, panel);
	}

	public void update(Observable o, Object arg) {
		IEditor editor = Services.getService(EditorManager.class)
				.getActiveEditor();
		if (editor != null && editor instanceof MapEditorPlugIn)
			panel.setEnabled(isEnabled(editor));
		else
			panel.setEnabled(false);
	}

	public boolean isEnabled(IEditor editor) {
		MapContext mc = (MapContext) editor.getElement().getObject();
		return mc.getLayerModel().getLayerCount() > 0;
	}

	public boolean isVisible() {
		return true;
	}

	public static ImageIcon getIcon() {
		return IconLoader.getIcon(Names.EXIT_ICON);
	}

	private Envelope getEnveloppeFromScale(Envelope oldEnvelope,
			int panelWidth, int scale) {

		// -- get zoom factor
		double factor = scale / getHorizontalMapScale(panelWidth, oldEnvelope);

		// --calculating new screen using the envelope of the corner
		// LineString

		double xc = 0.5 * (oldEnvelope.getMaxX() + oldEnvelope.getMinX());
		double yc = 0.5 * (oldEnvelope.getMaxY() + oldEnvelope.getMinY());
		double xmin = xc - 1 / 2.0 * factor * oldEnvelope.getWidth();
		double xmax = xc + 1 / 2.0 * factor * oldEnvelope.getWidth();
		double ymin = yc - 1 / 2.0 * factor * oldEnvelope.getHeight();
		double ymax = yc + 1 / 2.0 * factor * oldEnvelope.getHeight();
		Coordinate[] coords = new Coordinate[] { new Coordinate(xmin, ymin),
				new Coordinate(xmax, ymax) };
		Geometry g1 = new GeometryFactory().createLineString(coords);

		return g1.getEnvelopeInternal();
	}

	/**
	 * 
	 * This method has been copied from openjump GIS : http://wwww.openjump.org
	 * 
	 * OpenJUMP is distributed under GPL 2 license. Delivers the scale of the
	 * map shown on the display. The scale is calculated for the horizontal map
	 * direction
	 * <p>
	 * note: The scale may differ for horizontal and vertical direction due to
	 * the type of map projection.
	 * 
	 * @param panel
	 *            width and current envelope
	 * 
	 * @return actual scale
	 */

	public double getHorizontalMapScale(double panelWidth, Envelope oldEnvelope) {

		double horizontalScale = 0;
		// [sstein] maybe store screenres on the blackboard
		// if obtaining is processing intensive?
		double SCREENRES = Toolkit.getDefaultToolkit().getScreenResolution(); // 72
																				// dpi
																				// or
																				// 96
																				// dpi
																				// or
																				// ..
		double INCHTOCM = 2.54; // cm

		// panelWidth in pixel
		double modelWidth = oldEnvelope.getWidth(); // m
		// -----
		// example:
		// screen resolution: 72 dpi
		// 1 inch = 2.54 cm
		// ratio = 2.54/72 (cm/pix) ~ 0.35mm
		// mapLength[cm] = noPixel * ratio
		// scale = realLength *100 [m=>cm] / mapLength
		// -----
		horizontalScale = modelWidth * 100
				/ (INCHTOCM / SCREENRES * panelWidth);

		return horizontalScale;
	}
}
