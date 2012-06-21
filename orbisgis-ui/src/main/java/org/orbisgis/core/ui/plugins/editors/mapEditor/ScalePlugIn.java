/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.ui.plugins.editors.mapEditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.plugins.editor.PlugInEditorListener;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;
import org.orbisgis.core.ui.plugins.views.mapEditor.MapEditorPlugIn;
import org.orbisgis.utils.I18N;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class ScalePlugIn extends AbstractPlugIn {

	static ArrayList<Integer> DefaultScales = new ArrayList<Integer>();
	private JPanel panel;
	private JComboBox combobox;
	private double factor;

	static {
		DefaultScales.add(1000);
		DefaultScales.add(2000);
		DefaultScales.add(5000);
		DefaultScales.add(10000);
		DefaultScales.add(20000);
		DefaultScales.add(50000);
		DefaultScales.add(100000);
		DefaultScales.add(200000);
		DefaultScales.add(500000);
		DefaultScales.add(1000000);
		DefaultScales.add(2000000);
		DefaultScales.add(5000000);
		DefaultScales.add(10000000);
		DefaultScales.add(20000000);
		DefaultScales.add(50000000);
	}

	@Override
	public boolean execute(PlugInContext context) throws Exception {
		MapEditorPlugIn mapEditor = null;
		if ((mapEditor = context.getMapEditor()) != null) {
			MapTransform mt = mapEditor.getMapTransform();

			if (mt != null) {

				Envelope envelope = mt.getAdjustedExtent();
				if (envelope != null) {

					JComboBox cb = (JComboBox) getActionComponent();
					Integer scale = (Integer) cb.getSelectedItem();

					if (DefaultScales.contains(scale)) {
						envelope = getEnveloppeFromScale(
								mt.getAdjustedExtent(), mt.getWidth(), scale);
						mt.setExtent(envelope);
					}
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void initialize(PlugInContext context) throws Exception {
		panel = new JPanel(new BorderLayout());
		JLabel label = new JLabel(
				I18N
						.getString("orbisgis.org.orbisgis.core.ui.plugins.editors.mapEditor.scale")
						+ " : ");
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		combobox = new JComboBox(DefaultScales.toArray(new Integer[0]));
		combobox.setAlignmentX(Component.RIGHT_ALIGNMENT);
		combobox.setMaximumSize(new Dimension(100, 20));
		panel.add(label);
		panel.add(combobox);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setOpaque(false);
		panel.setVisible(true);
		setActionComponent(combobox);
		setTypeListener("item");
		EditorManager em = Services.getService(EditorManager.class);
		em.addEditorListener(new PlugInEditorListener(this, panel,
				Names.MAP_TOOLBAR_SCALE, null, context, true));

	}

	protected void updateComponent() {
		combobox.setModel(new DefaultComboBoxModel(DefaultScales
				.toArray(new Integer[0])));
		MapTransform mapTransform = null;
		if (getPlugInContext().getMapEditor() != null) {
			mapTransform = getPlugInContext().getMapEditor().getMapControl()
					.getMapTransform();
			int currentScale = (int) mapTransform.getScaleDenominator();
			int scaleSelected = 0;
			for (int i = 0; i < DefaultScales.size(); i++) {
				if (Math.abs(((Integer) DefaultScales.get(i)) - currentScale) == 0
						|| Math.abs(((Integer) DefaultScales.get(i))
								- currentScale) == 1) {
					scaleSelected = DefaultScales.get(i);
				}
			}
			if (scaleSelected == 0) {
				scaleSelected = new Integer(currentScale);
				combobox.addItem(scaleSelected);
			}
			combobox.setSelectedItem(scaleSelected);
		}
	}

	private Envelope getEnveloppeFromScale(Envelope oldEnvelope,
			int panelWidth, int scale) {

		// -- get zoom factor
		factor = scale / getHorizontalMapScale(panelWidth, oldEnvelope);

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

	@Override
	public boolean isEnabled() {
		boolean isVisible = false;
		IEditor editor = Services.getService(EditorManager.class)
				.getActiveEditor();
		if (editor != null && editor instanceof MapEditorPlugIn
				&& getPlugInContext().getMapEditor() != null) {
			MapContext mc = (MapContext) editor.getElement().getObject();
			// isVisible = !getPlugInContext().isGeographicCRS();
			isVisible = mc.getLayerModel().getLayerCount() > 0;
			if (isVisible) {
				updateComponent();
			}
		}
		panel.setEnabled(isVisible);
		return isVisible;
	}

}
