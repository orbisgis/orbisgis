/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *  
 *  Lead Erwan BOCHER, scientific researcher, 
 *
 *  Developer lead : Pierre-Yves FADET, computer engineer. 
 *  
 *  User support lead : Gwendall Petit, geomatic engineer. 
 * 
 * Previous computer developer : Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 * 
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 * 
 * Copyright (C) 2010 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
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
 * For more information, please consult: <http://orbisgis.cerma.archi.fr/>
 * <http://sourcesup.cru.fr/projects/orbisgis/>
 * 
 * or contact directly: 
 * erwan.bocher _at_ ec-nantes.fr 
 * Pierre-Yves.Fadet _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 **//*


package org.orbisgis.core.ui.plugins.editors.mapEditor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.generic.GenericObjectDriver;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.images.IconLoader;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.multiInputPanel.ComboBoxChoice;
import org.orbisgis.core.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.map.tools.ToolUtilities;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.MapEditorPlugIn;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.geom.util.GeometryTransformer;

import fr.cts.CoordinateOperation;
import fr.cts.Ellipsoid;
import fr.cts.Identifier;
import fr.cts.IllegalCoordinateException;
import fr.cts.Measure;
import fr.cts.Parameter;
import fr.cts.Unit;
import fr.cts.datum.PrimeMeridian;
import fr.cts.op.CoordinateOperationSequence;
import fr.cts.op.CoordinateRounding;
import fr.cts.op.CoordinateSwitch;
import fr.cts.op.Geocentric2Geographic;
import fr.cts.op.Geographic2Geocentric;
import fr.cts.op.LongitudeRotation;
import fr.cts.op.UnitConversion;
import fr.cts.op.projection.LambertConicConformal1SP;
import fr.cts.op.projection.LambertConicConformal2SP;
import fr.cts.op.transformation.FrenchGeocentricNTF2RGF;
import fr.cts.op.transformation.NTv2GridShiftTransformation;

*//**
 * Test CTS with GDMS
 *
 *//*

public class CTSPlugIn extends AbstractPlugIn {

	private JButton btn;

	CoordinateOperation NTF_L2E_RGF_L93_CIRCE;
	CoordinateOperation NTF_L2E_RGF_L93_NTV2;
	CoordinateOperation RGF_L93_NTF_L2E_NTV2;
	CoordinateOperation WGS84_RGF_L93;
	CoordinateOperation WGS84_RGF_L3_I;
	CoordinateOperation op;

	public static DataSourceFactory dsf = new DataSourceFactory();
	static SpatialDataSourceDecorator sds = null;

	public CTSPlugIn() {
		btn = new JButton(IconLoader.getIcon("worldmap.png"));
	}

	public boolean execute(PlugInContext context) throws Exception {
		IEditor editor = getPlugInContext().getActiveEditor();
		MapContext mc = (MapContext) editor.getElement().getObject();
		// ILayer[] layers = mc.getLayerModel().getLayersRecursively();
		final ILayer layer = mc.getActiveLayer();

		MultiInputPanel mip = new MultiInputPanel("org.test",
				"Cordinate Reference System", false);

		mip.setInfoText("Transformer les couches sélectionnées");
		mip.addInput("Transformation",
				"Sélectionner la transformation à effectuer:", null,
				new ComboBoxChoice("Lambert 2 �tendu --> Lambert 93 (Circe)",
						"Lambert 2 �tendu --> Lambert 93 (NTv2)",
						"WGS84 (lon/lat)  --> Lambert 93",
						"Lambert 93 --> Lambert 2 �tendu",
						"L3-->WGS84"));

		if (UIFactory.showDialog(mip)) {
			if (mip.getInput("Transformation").equals(
					"Lambert 2 �tendu --> Lambert 93 (Circe)")) {
				op = NTF_L2E_RGF_L93_CIRCE;
			} else if (mip.getInput("Transformation").equals(
					"Lambert 2 �tendu --> Lambert 93 (NTv2)")) {
				op = NTF_L2E_RGF_L93_NTV2;
			} else if (mip.getInput("Transformation").equals(
					"WGS84 (lon/lat)  --> Lambert 93")) {
				op = WGS84_RGF_L93;
			} else if (mip.getInput("Transformation").equals(
					"Lambert 93 --> Lambert 2 �tendu")) {
				op = RGF_L93_NTF_L2E_NTV2;
			}
			else if (mip.getInput("Transformation").equals(
				"L3-->WGS84")) {
				op = WGS84_RGF_L3_I;
			}
		}

		if (op == null)
			return false;

		BackgroundManager bm = Services.getService(BackgroundManager.class);
		bm.backgroundOperation(new BackgroundJob() {

			@Override
			public void run(IProgressMonitor pm) {
				if (layer.isVisible()) {
					try {
						sds = layer.getDataSource();

						sds.open();
						GenericObjectDriver driver = new GenericObjectDriver(
								sds.getMetadata());

						long rowCount = sds.getRowCount();
						for (int i = 0; i < rowCount; i++) {

							Value[] values = sds.getRow(i);
							int geomField = sds.getSpatialFieldIndex();
							Geometry geom = values[geomField].getAsGeometry();

							Geometry newGeom = transform(geom);

							values[geomField] = ValueFactory
									.createValue(newGeom);
							driver.addValues(values);

						}

						sds.close();

						saveCTS("data", driver);
					} catch (DriverException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

			@Override
			public String getTaskName() {
				return "Select none";
			}
		});
		return true;
	}

	private Geometry transform(Geometry geom) {

		GeometryTransformer gt = new GeometryTransformer() {
			protected CoordinateSequence transformCoordinates(
					CoordinateSequence cs, Geometry geom) {
				Coordinate[] cc = geom.getCoordinates();
				CoordinateSequence newcs = new CoordinateArraySequence(cc);
				for (int i = 0; i < cc.length; i++) {
					Coordinate c = cc[i];
					try {
						double[] xyz = op.transform(new double[] { c.x, c.y,
								c.z });
						newcs.setOrdinate(i, 0, xyz[0]);
						newcs.setOrdinate(i, 1, xyz[1]);
						newcs.setOrdinate(i, 2, xyz[2]);
					} catch (IllegalCoordinateException ice) {
						ice.printStackTrace();
					}
				}
				return newcs;
			}
		};

		return gt.transform(geom);

	}

	
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbcontext = context.getWorkbenchContext();
		wbcontext.getWorkbench().getFrame().getEditionMapToolBar().addPlugIn(
				this, btn, context);

		NTF_L2E_RGF_L93_CIRCE = new CoordinateOperationSequence(
				new Identifier("OJ", "L2E->L93 (gr2df97)",
						"NTF/Lambert 2 �tendu \nvers \nRGF93/Lambert 93 \n(IGN gr3df97 - circe)"),
				LambertConicConformal1SP.LAMBERT2E.inverse(), LongitudeRotation
						.getLongitudeRotationFrom(PrimeMeridian.PARIS),
				new Geographic2Geocentric(Ellipsoid.CLARKE1880IGN),
				new FrenchGeocentricNTF2RGF("resources/fr/cts/datum/"),
				new Geocentric2Geographic(Ellipsoid.GRS80),
				LambertConicConformal2SP.LAMBERT93,
				CoordinateRounding.MILLIMETER);

		NTv2GridShiftTransformation ntv2 = new NTv2GridShiftTransformation(
				"fr/cts/datum/ntf_r93.gsb", 0.001);
		ntv2.setMode(NTv2GridShiftTransformation.SPEED);
		ntv2.loadGridShiftFile();
		NTF_L2E_RGF_L93_NTV2 = new CoordinateOperationSequence(
				new Identifier("OJ", "L2E->L93 (ntv2)",
						"NTF/Lambert 2 �tendu\n vers \nRGF93/Lambert 93 \n(IGN ntf_r93.gsb - postgis)"),
				LambertConicConformal1SP.LAMBERT2E.inverse(), LongitudeRotation
						.getLongitudeRotationFrom(PrimeMeridian.PARIS), ntv2,
				LambertConicConformal2SP.LAMBERT93,
				CoordinateRounding.MILLIMETER);

		RGF_L93_NTF_L2E_NTV2 = new CoordinateOperationSequence(
				new Identifier("OJ", "L93->L2E (par ntv2)",
						"RGF93/Lambert 93\n vers \nNTF/Lambert 2 �tendu (IGN ntf_r93.gsb - postgis)"),
				LambertConicConformal2SP.LAMBERT93.inverse(), ntv2.inverse(),
				LongitudeRotation.getLongitudeRotationTo(PrimeMeridian.PARIS),
				LambertConicConformal1SP.LAMBERT2E,
				CoordinateRounding.MILLIMETER);

		WGS84_RGF_L93 = new CoordinateOperationSequence(new Identifier("OJ",
				"WGS84 (lon/lat)->L93",
				"WGS84 geographique (lon/lat)\n vers \nRGF93/Lambert 93"),
				CoordinateSwitch.SWITCH_LAT_LON, UnitConversion.DD2RAD,
				LambertConicConformal2SP.LAMBERT93,
				CoordinateRounding.MILLIMETER);
		
		 Map<String,Measure> params1 = new HashMap<String,Measure>();
         params1.put(Parameter.LATITUDE_OF_ORIGIN, new Measure(44.1, Unit.DEGREE));
         params1.put(Parameter.CENTRAL_MERIDIAN, new Measure(0.0, Unit.DEGREE));
         
         params1.put(Parameter.STANDARD_PARALLEL_1, new Measure(44.996093814511, Unit.DEGREE));
         params1.put(Parameter.STANDARD_PARALLEL_2, new Measure(43.199291275544, Unit.DEGREE));
         
         params1.put(Parameter.SCALE_FACTOR, new Measure(1, Unit.UNIT));
         params1.put(Parameter.FALSE_EASTING, new Measure(600000.0, Unit.METER));
         params1.put(Parameter.FALSE_NORTHING, new Measure(3200000.0, Unit.METER));
         final LambertConicConformal2SP LAMBERT3 = 
             new LambertConicConformal2SP(Ellipsoid.CLARKE1880IGN, params1);
		
		WGS84_RGF_L3_I = new CoordinateOperationSequence(new Identifier("OJ",				
				"L3 -> WGS84 geographique",
				"L3 sud vers WGS84 geographique (lon/lat)\n"),
				LAMBERT3.inverse(),
				LongitudeRotation.PARIS2GREENWICH);
	}



	public boolean isEnabled() {
		boolean flag = false;
		MapEditorPlugIn mapEditor = null;
		if ((mapEditor = getPlugInContext().getMapEditor()) != null) {
			MapContext mc = (MapContext) mapEditor.getElement().getObject();
			ILayer[] layers = mc.getLayerModel().getLayersRecursively();
			for (ILayer lyr : layers) {
				if (!lyr.isWMS())
					flag = true && ToolUtilities.isActiveLayerEditable(mc);
			}
		}
		btn.setEnabled(flag);
		return flag;
	}



	private static boolean saveCTS(String name, GenericObjectDriver driver) {

		try {

			File gdmsFile = new File("/tmp/cts.gdms"); // FIXME the path
			gdmsFile.delete();
			dsf.getSourceManager().register(name, gdmsFile);

			DataSource ds = dsf.getDataSource(driver);
			ds.open();
			dsf.saveContents(name, ds);
			ds.close();

			return true;

		} catch (DriverException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return false;
	}
}
*/