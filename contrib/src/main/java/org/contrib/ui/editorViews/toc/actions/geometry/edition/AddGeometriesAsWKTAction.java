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
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
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
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.contrib.ui.editorViews.toc.actions.geometry.edition;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.contrib.model.jump.ui.EnterWKTDialog;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.editorViews.toc.action.ILayerAction;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.views.editor.EditorManager;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.valid.IsValidOp;

public class AddGeometriesAsWKTAction implements ILayerAction {

	private SpatialDataSourceDecorator sds;

	private String geomTypeName;

	private MapContext mapContext;

	public boolean accepts(ILayer layer) {
		EditorManager em = (EditorManager) Services
				.getService(EditorManager.class);
		MapContext mc = (MapContext) em.getActiveElement().getObject();
		return mc.getActiveLayer() == layer;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount == 1;
	}

	public void execute(MapContext mapContext, ILayer layer) {

		this.mapContext = mapContext;
		createDialog(layer.getName());

	}

	public void createDialog(String layerName) {

		sds = mapContext.getActiveLayer().getDataSource();

		final EnterWKTDialog enterWKTDialog = new EnterWKTDialog("", true);
		enterWKTDialog.setTitle("add geometries to" + " " + layerName);
		enterWKTDialog.setDescription("<HTML>" + "enter-well-known-text for "
				+ getGeomType() + " type " + "</HTML>");
		enterWKTDialog.setSize(500, 400);
		enterWKTDialog.setVisible(true);

		enterWKTDialog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (enterWKTDialog.wasOKPressed()) {
						String text = enterWKTDialog.getText();
						if (text.length() > 0) {
							readAndInsertGeometries(text);
							enterWKTDialog.setVisible(false);
						}

						else {
							JOptionPane.showMessageDialog(null,
									"Please add a wkt text");
							enterWKTDialog.setVisible(true);
						}
					} else {
						enterWKTDialog.setVisible(false);
					}
				} catch (Throwable t) {
					Services.getErrorManager().error(
							"Cannot obtain the WKT description", t);
				}
			}
		});

	}

	public String getGeomType() {
		try {

			Type fieldType = sds.getFieldType(sds.getFieldIndexByName(sds
					.getDefaultGeometry()));
			GeometryConstraint gc = (GeometryConstraint) fieldType
					.getConstraint(Constraint.GEOMETRY_TYPE);
			geomTypeName = gc.getConstraintHumanValue();

		} catch (DriverException e) {
			Services.getErrorManager().error(
					"Cannot read the geometry type from the layer ", e);
		}
		return geomTypeName;

	}

	public void readAndInsertGeometries(String text) {

		String separator = ";";

		String[] wktText = text.split(separator);

		try {

			Value[] row = new Value[sds.getMetadata().getFieldCount()];

			for (int i = 0; i < wktText.length; i++) {

				WKTReader wktReader = new WKTReader();
				Geometry geom;

				geom = wktReader.read(wktText[i]);

				IsValidOp op = new IsValidOp(geom);
				if (!op.isValid()) {

				} else {
					System.out.println(geom);
					row[sds.getSpatialFieldIndex()] = ValueFactory
							.createValue(geom);
					sds.insertFilledRow(row);
				}

			}

		} catch (ParseException e) {
			Services.getErrorManager().error(
					"Cannot parse the WKT geometry description ", e);

		} catch (DriverException e) {
			Services.getErrorManager().error(
					"Cannot read the resulting datasource from the layer ", e);
		}

	}

}
