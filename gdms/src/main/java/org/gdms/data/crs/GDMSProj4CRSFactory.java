package org.gdms.data.crs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.DefaultSourceManager;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.SemanticException;

import fr.cts.CoordinateOperation;
import fr.cts.Ellipsoid;
import fr.cts.Identifiable;
import fr.cts.Identifier;
import fr.cts.Logging;
import fr.cts.Measure;
import fr.cts.Parameter;
import fr.cts.Quantity;
import fr.cts.Unit;
import fr.cts.crs.CoordinateReferenceSystem;
import fr.cts.crs.GeocentricCRS;
import fr.cts.crs.Geographic3DCRS;
import fr.cts.crs.ProjectedCRS;
import fr.cts.cs.Axis;
import fr.cts.cs.CoordinateSystem;
import fr.cts.cs.GeographicExtent;
import fr.cts.datum.Datum;
import fr.cts.datum.GeodeticDatum;
import fr.cts.datum.PrimeMeridian;
import fr.cts.op.Identity;
import fr.cts.op.projection.LambertConicConformal1SP;
import fr.cts.op.projection.LambertConicConformal2SP;
import fr.cts.op.projection.Projection;
import fr.cts.op.projection.TransverseMercator;
import fr.cts.op.projection.UniversalTransverseMercator;
import fr.cts.op.transformation.GeocentricTranslation;
import fr.cts.op.transformation.NTv2GridShiftTransformation;
import fr.cts.op.transformation.SevenParameterTransformation;

public class GDMSProj4CRSFactory {

	private DataSourceFactory dsf;

	static Logger LOG = Logging.getLogger();

	public GDMSProj4CRSFactory(DataSourceFactory dsf) {
		this.dsf = dsf;
	}

	public DataSourceFactory getDsf() {
		return dsf;
	}

	public CoordinateReferenceSystem getCRSFromSRID(String srid) {
		StringBuffer sb = new StringBuffer("SELECT * FROM "
				+ DefaultSourceManager.SPATIAL_REF_SYSTEM + " WHERE SRID = '");
		sb.append(srid + "'");
		DataSource ds;
		CoordinateReferenceSystem crs = null;
		try {
			ds = dsf.getDataSourceFromSQL(sb.toString());

			ds.open();

			long rowCount = ds.getRowCount();
			if (rowCount == 1) {
				System.out.println(rowCount);
				// nameSpace = AUTH_NAME
				// name = SRTEXT PROJCS OR GEOGCS
				// parameters = PROJ4TEXT
				String nameSpace = ds.getFieldValue(0, 1).getAsString();
				String PROJ4TEXT = ds.getFieldValue(0, 4).getAsString();

				Map<String, String> parameters = getPROJ4TEXTParameters(PROJ4TEXT);
				String nameIdentifier = nameSpace + ":" + srid;
				// TODO : Is it necessary to get the full name.
				// String SRTEXT = values[3].getAsString();
				// String name = getCRSNameIdentifier(SRTEXT);
				crs = parseCRS(nameSpace, nameIdentifier, srid, parameters);

			}

			ds.close();
		} catch (DriverLoadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataSourceCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SemanticException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return crs;

	}

	// TODO : Check the name in SRTEXT
	private String getCRSNameIdentifier(String SRTEXT) {
		return SRTEXT;
	}

	/**
	 * Check CRS parameters from PROJ4TEXT representation
	 * 
	 * @return
	 */
	private Map<String, String> getPROJ4TEXTParameters(String PROJ4TEXT) {

		String[] tokens = PROJ4TEXT.split("[ ]+\\+");
		Map<String, String> parameters = new HashMap<String, String>();
		for (String token : tokens) {
			String[] namevalue = token.split("=");
			if (namevalue.length == 2) {
				parameters.put(namevalue[0], namevalue[1]);
			}
		}
		return parameters;
	}

	private static CoordinateReferenceSystem parseCRS(String namespace,
			String name, String id, Map<String, String> parameters) {
		GeodeticDatum gd = getGeodeticDatum(parameters);
		if (gd == null) {
			LOG.fine("No datum definition");
			return null;
		}
		// setToWGS84parameters(gd, parameters);
		String sproj = parameters.get("+proj");
		String sunit = parameters.get("units");
		String stometer = parameters.get("to_meter");
		if (null == sproj) {
			LOG
					.fine("No projection defined for this Coordinate Reference System");
			return null;
		}
		if (sproj.equals("geocent")) {
			Unit unit = Unit.METER;
			if (stometer != null) {
				unit = new Unit(Quantity.LENGTH, "", Double.parseDouble(sunit),
						"");
			}
			CoordinateSystem cs = new CoordinateSystem(new Axis[] { Axis.X,
					Axis.Y, Axis.Z }, new Unit[] { unit, unit, unit });
			return new GeocentricCRS(new Identifier(namespace, id, name), gd,
					cs);
		} else if (sproj.equals("longlat")) {
			Unit unit = Unit.DEGREE;
			if (stometer != null) {
				unit = new Unit(Quantity.LENGTH, "", Double.parseDouble(sunit),
						"");
			}
			CoordinateSystem cs = new CoordinateSystem(new Axis[] {
					Axis.LONGITUDE, Axis.LATITUDE, Axis.HEIGHT }, new Unit[] {
					unit, unit, Unit.METER });
			return new Geographic3DCRS(new Identifier(namespace, id, name), gd,
					cs);
		} else {
			Projection proj = getProjection(sproj, gd.getEllipsoid(),
					parameters);
			if (null != proj) {
				return new ProjectedCRS(new Identifier(namespace, id, name),
						gd, proj);
			} else {
				LOG.fine("Unknown projection : " + sproj);
				return null;
			}
		}
	}

	private static GeodeticDatum getGeodeticDatum(Map<String, String> param) {
		String datum = param.get("datum");
		if (null != datum) {
			if (datum.equals("WGS84")) {
				PrimeMeridian pm = getPrimeMeridian(param);
				if (null != pm && pm != PrimeMeridian.GREENWICH) {
					return new GeodeticDatum(
							new Identifier(GeodeticDatum.class, "WGS84 ("
									+ pm.getName() + ")"), pm,
							GeodeticDatum.WGS84.getEllipsoid(),
							GeographicExtent.WORLD, null, null);
				} else
					return GeodeticDatum.WGS84;
			} else {
				LOG.fine(datum + " datum is not yet implemented");
				return null;
			}
			// TODO
			// Faire pour autres datum (ex: D_NTF pour WKTFactory, ici mettre
			// son équivalent en PROJ4)
		} else {
			Ellipsoid ell = getEllipsoid(param);
			PrimeMeridian pm = getPrimeMeridian(param);
			if (null != pm && null != ell) {
				GeodeticDatum gd = new GeodeticDatum(pm, ell);
				setDefaultWGS84Parameters(gd, param);
				// Proj4 file can specify NTV2 grid to use for precise
				// transformations
				// but it does not specify the target datum
				// ex. <LAMBE> +title=Lambert II etendu +proj=lcc
				// +nadgrids=ntf_r93.gsb,null
				// +towgs84=-168.0000,-60.0000,320.0000
				// does not identify RGF93 as the target datum of ntf_r93.gsb
				String nadGrids = param.get("nadgrids");
				if (nadGrids != null) {

					// TODO improve this. Bad patch

					if (nadGrids.contains("ntf_r93.gsb")) {

						// TODO
						NTv2GridShiftTransformation ntv2 = new NTv2GridShiftTransformation(
								Datum.class.getResource("ntf_r93.gsb")
										.getPath(), 0.001);
						try {
							ntv2.setMode(NTv2GridShiftTransformation.SPEED);
							ntv2.loadGridShiftFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
						gd.addCoordinateOperation(GeodeticDatum.RGF93, ntv2);
					}
				}
				return gd;
			} else {
				LOG.fine("A datum needs a prime meridian and an ellipsoid");
				return null;
			}
		}
	}

	// Set the towgs84 parameters
	private static void setDefaultWGS84Parameters(GeodeticDatum gd,
			Map<String, String> param) {
		CoordinateOperation op;
		String bw = param.get("towgs84");
		if (null == bw)
			return;
		double[] bwp = new double[7];
		String[] sbwp = bw.split(",");
		int nullparam = 0;
		boolean identity = true;
		boolean translation = true;
		for (int i = 0; i < sbwp.length; i++) {
			bwp[i] = Double.parseDouble(sbwp[i]);
			if (bwp[i] != 0)
				identity = false;
			if (bwp[i] != 0 && i > 2)
				translation = false;
		}
		if (identity) {
			op = Identity.IDENTITY;
		} else if (translation) {
			op = new GeocentricTranslation(bwp[0], bwp[1], bwp[2]);
		} else {
			op = SevenParameterTransformation.createBursaWolfTransformation(
					bwp[0], bwp[1], bwp[2], bwp[3], bwp[4], bwp[5], bwp[6]);
		}
		if (op != null) {
			gd.setDefaultToWGS84Operation(op);
		}
		/*
		 * gd.addCoordinateOperation(GeodeticDatum.WGS84, tf7p); try {
		 * GeodeticDatum.WGS84.addCoordinateOperation(gd, tf7p.inverse()); }
		 * catch(NonInvertibleOperationException e) {
		 * LOG.throwing("Proj4FileReader", "setToWGS84parameters", e);
		 * e.printStackTrace(); }
		 */
	}

	// TODO : use datumaliasestable.csv
	// Try first to find a known ellipsoid
	// then to create an ellipsoid from its parameter
	// then to get the ellipsoid associated with a known datum
	private static Ellipsoid getEllipsoid(Map<String, String> param) {
		String ellps = param.get("ellps");
		String a = param.get("a");
		String b = param.get("b");
		String rf = param.get("rf");
		String datum = param.get("datum");
		// Try first to find a known ellipsoid
		if (null != ellps) {
			if (ellps.equals("GRS80"))
				return Ellipsoid.GRS80;
			else if (ellps.equals("WGS84"))
				return Ellipsoid.WGS84;
			else if (ellps.equals("intl"))
				return Ellipsoid.INTERNATIONAL1924;
			else if (ellps.equals("clrk66"))
				return Ellipsoid.CLARKE1866;
			else if (ellps.equals("clrk80"))
				return Ellipsoid.CLARKE1880ARC;
			else if (ellps.equals("sphere"))
				return Ellipsoid.SPHERE;
			else if (ellps.equals("bessel"))
				return Ellipsoid.BESSEL1841;
			else if (ellps.equals("krass"))
				return Ellipsoid.KRASSOWSKI;
			else {
				// TODO : Create ellipsoid on the fly
				LOG.fine(ellps + " ellipsoid is not yet implemented");
				return null;
			}
		} else if (null != a && (null != b || null != rf)) {
			double a_ = Double.parseDouble(param.get("a"));
			if (null != b) {
				double b_ = Double.parseDouble(param.get("b"));
				return Ellipsoid.createEllipsoidFromSemiMinorAxis(a_, b_);
			} else if (null != rf) {
				double rf_ = Double.parseDouble(param.get("rf"));
				return Ellipsoid.createEllipsoidFromInverseFlattening(a_, rf_);
			} else {
				LOG.fine("Ellipsoid cannot be defined");
				return null;
			}
		} else if (null != datum) {
			GeodeticDatum gd = getGeodeticDatum(param);
			if (gd != null)
				return gd.getEllipsoid();
			else {
				LOG.fine("The unknown datum do not define an ellipsoid");
				return null;
			}
		} else {
			LOG.fine("WARNING, no way found to define an ellipsoid");
			return null;
		}
	}

	// Try first to find a known prime meridian,
	// then the prime meridian associated with a known datum
	private static PrimeMeridian getPrimeMeridian(Map<String, String> param) {
		String spm = param.get("pm");
		PrimeMeridian pm;
		if (null != spm) {
			if (spm.equals("greenwich"))
				return PrimeMeridian.GREENWICH;
			else if (spm.equals("paris"))
				return PrimeMeridian.PARIS;
			else if (spm.equals("lisbon"))
				return PrimeMeridian.LISBON;
			else if (spm.equals("bogota"))
				return PrimeMeridian.BOGOTA;
			else if (spm.equals("madrid"))
				return PrimeMeridian.MADRID;
			else if (spm.equals("rome"))
				return PrimeMeridian.ROME;
			else if (spm.equals("bern"))
				return PrimeMeridian.BERN;
			else if (spm.equals("jakarta"))
				return PrimeMeridian.JAKARTA;
			else if (spm.equals("ferro"))
				return PrimeMeridian.FERRO;
			else if (spm.equals("brussels"))
				return PrimeMeridian.BRUSSELS;
			else if (spm.equals("stockholm"))
				return PrimeMeridian.STOCKHOLM;
			else if (spm.equals("athens"))
				return PrimeMeridian.ATHENS;
			else if (spm.equals("oslo"))
				return PrimeMeridian.OSLO;
			else {
				try {
					double pmdd = Double.parseDouble(spm);
					return PrimeMeridian.createPrimeMeridianFromDDLongitude(
							new Identifier(PrimeMeridian.class,
									Identifiable.UNKNOWN), pmdd);
				} catch (NumberFormatException nfe) {
					LOG.fine(spm + " prime meridian is not parsable");
					return null;
				}
			}
		}
		// getGeodeticDatum fait rentrer le processus dans une boucle infinie
		// on ne devrait pas avoir � appeler le datum pour avoir le m�ridien
		/*
		 * else if (null != param.get("datum")) { GeodeticDatum gd =
		 * getGeodeticDatum(param); if (null != gd) return
		 * gd.getPrimeMeridian(); else {System.out.println(
		 * "WARNING, prime meridian is defined by an unknown datum"); return
		 * null; } }
		 */
		else {
			return PrimeMeridian.GREENWICH;
		}
	}

	private static Projection getProjection(String proj, Ellipsoid ell,
			Map<String, String> param) {
		String slat_0 = param.get("lat_0");
		String slat_1 = param.get("lat_1");
		String slat_2 = param.get("lat_2");
		String slon_0 = param.get("lon_0");
		String sk_0 = param.get("k_0");
		String sx_0 = param.get("x_0");
		String sy_0 = param.get("y_0");
		double lat_0 = slat_0 != null ? Double.parseDouble(slat_0) : 0.;
		double lat_1 = slat_1 != null ? Double.parseDouble(slat_1) : 0.;
		double lat_2 = slat_2 != null ? Double.parseDouble(slat_2) : 0.;
		double lon_0 = slon_0 != null ? Double.parseDouble(slon_0) : 0.;
		double k_0 = sk_0 != null ? Double.parseDouble(sk_0) : 0.;
		double x_0 = sx_0 != null ? Double.parseDouble(sx_0) : 0.;
		double y_0 = sy_0 != null ? Double.parseDouble(sy_0) : 0.;
		Map<String, Measure> map = new HashMap<String, Measure>();
		map.put(Parameter.CENTRAL_MERIDIAN, new Measure(lon_0, Unit.DEGREE));
		map.put(Parameter.LATITUDE_OF_ORIGIN, new Measure(lat_0, Unit.DEGREE));
		map.put(Parameter.STANDARD_PARALLEL_1, new Measure(lat_1, Unit.DEGREE));
		map.put(Parameter.STANDARD_PARALLEL_2, new Measure(lat_2, Unit.DEGREE));
		map.put(Parameter.SCALE_FACTOR, new Measure(k_0, Unit.UNIT));
		map.put(Parameter.FALSE_EASTING, new Measure(x_0, Unit.METER));
		map.put(Parameter.FALSE_NORTHING, new Measure(y_0, Unit.METER));
		if (proj.equals("lcc")) {
			if (param.get("lat_2") != null) {
				return new LambertConicConformal2SP(ell, map);
			} else {
				return new LambertConicConformal1SP(ell, map);
			}
		} else if (proj.equals("tmerc")) {
			return new TransverseMercator(ell, map);
		} else if (proj.equals("utm")) {
			int zone = param.get("zone") != null ? Integer.parseInt(param
					.get("zone")) : 0;
			lon_0 = (6.0 * (zone - 1) + 183.0) % 360.0;
			y_0 = param.containsKey("south") ? 10000000.0 : 0.0;
			map
					.put(Parameter.CENTRAL_MERIDIAN, new Measure(lon_0,
							Unit.DEGREE));
			map.put(Parameter.FALSE_NORTHING, new Measure(y_0, Unit.METER));
			return new UniversalTransverseMercator(ell, map);
		} else {
			LOG.fine(proj + " is not yet implemented");
			return null;
		}
	}

}
