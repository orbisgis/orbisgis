/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2003 Vivid Solutions
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */

package org.gdms.model.coordsys;

/**
 * 
 * @author $Author: dkim $
 * @version $Revision: 1.2 $
 * 
 * <pre>
 *   $Id: Spheroid.java,v 1.2 2003/11/05 05:26:52 dkim Exp $
 *   $Date: 2003/11/05 05:26:52 $
 *   $Log: Spheroid.java,v $
 *   Revision 1.2  2003/11/05 05:26:52  dkim
 *   Added global header; cleaned up Javadoc.
 * 
 *   Revision 1.1  2003/09/15 20:26:11  jaquino
 *   Reprojection
 * 
 *   Revision 1.2  2003/07/25 17:01:03  gkostadinov
 *   Moved classses reponsible for performing the basic projection to a new
 *   package -- base.
 * 
 *   Revision 1.1  2003/07/24 23:14:43  gkostadinov
 *   adding base projection classes
 * 
 *   Revision 1.1  2003/06/20 18:34:30  gkostadinov
 *   Entering the source code into the CVS.
 * </pre>
 * 
 */

/**
 * GRS80 spheroid.
 */
public class Spheroid {

	public double a;// semimajor axis

	public double b;// semiminor axis

	public double f;// flattening

	public double e;// eccentricity (first)

	double es;// first eccentricity squared

	double t1, t2, t3;// t1,...,t6 used for

	double t4, t5, t6;// area computation (local Math.sinusoidal)

	public Spheroid(Radius rad) {
		// constructor base on a and b
		a = rad.a;
		if (rad.b > 1.0) {
			b = rad.b;
			f = 1.0 - b / a;
		} else {
			f = 1.0 / rad.rf;
			b = a - a * f;
		}
		es = f + f - f * f;
		e = Math.sqrt(es);
		// constaints for local Math.sinusoidal equal-area projection
		// used for area computations.
		double e4;
		// constaints for local Math.sinusoidal equal-area projection
		// used for area computations.
		double e6;
		// constaints for local Math.sinusoidal equal-area projection
		// used for area computations.
		double e8;
		// constaints for local Math.sinusoidal equal-area projection
		// used for area computations.
		double e10;
		double t0;
		t0 = a * (1.0 - es);
		e4 = es * es;
		e6 = e4 * es;
		e8 = e6 * es;
		e10 = e8 * es;
		t1 = t0
				* (1.0 + 3.0 * es / 4.0 + 45.0 * e4 / 64.0 + 175.0 * e6 / 256.0
						+ 11025.0 * e8 / 16384.0 + 43659.0 * e10 / 65536.0);
		t2 = t0
				* (3.0 * es / 4.0 + 15.0 * e4 / 16.0 + 525.0 * e6 / 512.0
						+ 2205.0 * e8 / 2048.0 + 72765.0 * e10 / 65536.0) / 2.0;
		t3 = t0
				* (15.0 * e4 / 64.0 + 105.0 * e6 / 256.0 + 2205.0 * e8 / 4096.0 + 10395.0 * e10 / 16384.0)
				/ 4.0;
		t4 = t0
				* (35.0 * e6 / 512.0 + 315.0 * e8 / 2048.0 + 31185.0 * e10 / 131072.0)
				/ 6.0;
		t5 = t0 * (315.0 * e8 / 16384.0 + 3465.0 * e10 / 65536.0) / 8.0;
		t6 = t0 * (693.0 * e10 / 131072.0) / 10.0;
	}

	public double getA() {
		return a;
	}

	public double getB() {
		return b;
	}

	public double getF() {
		return f;
	}

	public double getE() {
		return e;
	}

	public double distance(Geographic r, Geographic s) {
		// compute Math.sin and cos of latitudes and reduced latitudes
		double L1;
		// compute Math.sin and cos of latitudes and reduced latitudes
		double L2;
		// compute Math.sin and cos of latitudes and reduced latitudes
		double sinU1;
		// compute Math.sin and cos of latitudes and reduced latitudes
		double sinU2;
		// compute Math.sin and cos of latitudes and reduced latitudes
		double cosU1;
		// compute Math.sin and cos of latitudes and reduced latitudes
		double cosU2;
		L1 = Math.atan((1.0 - f) * Math.tan(r.lat));
		L2 = Math.atan((1.0 - f) * Math.tan(s.lat));
		sinU1 = Math.sin(L1);
		sinU2 = Math.sin(L2);
		cosU1 = Math.cos(L1);
		cosU2 = Math.cos(L2);

		// compute delta longitude on the sphere
		double dl;

		// compute delta longitude on the sphere
		double dl1;

		// compute delta longitude on the sphere
		double dl2;

		// compute delta longitude on the sphere
		double dl3;

		// compute delta longitude on the sphere
		double cosdl1;

		// compute delta longitude on the sphere
		double sindl1;
		double cosSigma;
		double sigma;
		double azimuthEQ;
		double tsm;
		dl = s.lon - r.lon;
		dl1 = dl;
		cosdl1 = Math.cos(dl);
		sindl1 = Math.sin(dl);
		do {
			cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosdl1;
			sigma = Math.acos(cosSigma);
			azimuthEQ = Math.asin((cosU1 * cosU2 * sindl1) / Math.sin(sigma));
			tsm = Math.acos(cosSigma - (2.0 * sinU1 * sinU2)
					/ (Math.cos(azimuthEQ) * Math.cos(azimuthEQ)));
			dl2 = deltaLongitude(azimuthEQ, sigma, tsm);
			dl3 = dl1 - (dl + dl2);
			dl1 = dl + dl2;
			cosdl1 = Math.cos(dl1);
			sindl1 = Math.sin(dl1);
		} while (Math.abs(dl3) > 1.0e-032);

		// compute expansions A and B
		double u2;

		// compute expansions A and B
		double A;

		// compute expansions A and B
		double B;
		u2 = mu2(azimuthEQ);
		A = bigA(u2);
		B = bigB(u2);

		// compute length of geodesic
		double dsigma;
		dsigma = B
				* Math.sin(sigma)
				* (Math.cos(tsm) + (B * cosSigma * (-1.0 + 2.0 * (Math.cos(tsm) * Math
						.cos(tsm)))) / 4.0);
		return b * (A * (sigma - dsigma));
	}// END - spheroid::distance

	public double direction(Geographic r, Geographic s) {
		// compute Math.sin and cos of latitudes and reduced latitudes
		double L1;
		// compute Math.sin and cos of latitudes and reduced latitudes
		double L2;
		// compute Math.sin and cos of latitudes and reduced latitudes
		double sinU1;
		// compute Math.sin and cos of latitudes and reduced latitudes
		double sinU2;
		// compute Math.sin and cos of latitudes and reduced latitudes
		double cosU1;
		// compute Math.sin and cos of latitudes and reduced latitudes
		double cosU2;
		L1 = Math.atan((1.0 - f) * Math.tan(r.lat));
		L2 = Math.atan((1.0 - f) * Math.tan(s.lat));
		sinU1 = Math.sin(L1);
		sinU2 = Math.sin(L2);
		cosU1 = Math.cos(L1);
		cosU2 = Math.cos(L2);

		// compute delta longitude on the sphere
		double dl;

		// compute delta longitude on the sphere
		double dl1;

		// compute delta longitude on the sphere
		double dl2;

		// compute delta longitude on the sphere
		double dl3;

		// compute delta longitude on the sphere
		double cosdl1;

		// compute delta longitude on the sphere
		double sindl1;
		double cosSigma;
		double sigma;
		double azimuthEQ;
		double tsm;
		dl = s.lon - r.lon;
		dl1 = dl;
		cosdl1 = Math.cos(dl);
		sindl1 = Math.sin(dl);
		do {
			cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosdl1;
			sigma = Math.acos(cosSigma);
			azimuthEQ = Math.asin((cosU1 * cosU2 * sindl1) / Math.sin(sigma));
			tsm = Math.acos(cosSigma - (2.0 * sinU1 * sinU2)
					/ (Math.cos(azimuthEQ) * Math.cos(azimuthEQ)));
			dl2 = deltaLongitude(azimuthEQ, sigma, tsm);
			dl3 = dl1 - (dl + dl2);
			dl1 = dl + dl2;
			cosdl1 = Math.cos(dl1);
			sindl1 = Math.sin(dl1);
		} while (Math.abs(dl3) > 1.0e-032);

		// compute expansions A and B
		double u2;

		// compute expansions A and B
		double A;

		// compute expansions A and B
		double B;
		u2 = mu2(azimuthEQ);
		A = bigA(u2);
		B = bigB(u2);

		// compute length of geodesic
		double dsigma;

		// compute length of geodesic
		double d_tmp;
		dsigma = B
				* Math.sin(sigma)
				* (Math.cos(tsm) + (B * cosSigma * (-1.0 + 2.0 * (Math.cos(tsm) * Math
						.cos(tsm)))) / 4.0);
		d_tmp = b * (A * (sigma - dsigma));

		// compute forward azimuth
		double azimuthFD;
		azimuthFD = Math.atan2((cosU2 * sindl1), (cosU1 * sinU2 - sinU1 * cosU2
				* cosdl1));
		if (azimuthFD < 0.0) {
			azimuthFD = azimuthFD + 2.0 * Math.PI;
		}
		return azimuthFD;
	}// END - spheroid::direction

	public Geographic project(Geographic r, double length, double angle) {
		double e2;
		double e2s;
		double L1;
		double cosU1;
		double sinU1;
		double cosa1;
		double sina1;
		double sig1;
		double sinae;
		double azimuthEQ;
		double u2;
		double A;
		double B;
		double s1;
		double sigma;
		double tsm;
		double del;
		double cis;
		e2 = Math.sqrt(a * a - b * b) / b;// second excentricity
		e2s = e2 * e2;
		L1 = Math.atan((1.0 - f) * Math.tan(r.lat));
		cosU1 = Math.cos(L1);
		sinU1 = Math.sin(L1);
		cosa1 = Math.cos(angle);
		sina1 = Math.sin(angle);
		sig1 = Math.atan(Math.tan(L1) / cosa1);
		sinae = cosU1 * sina1;
		azimuthEQ = Math.asin(sinae);
		u2 = mu2(azimuthEQ);
		A = bigA(u2);
		B = bigB(u2);
		s1 = length / (b * A);
		sigma = s1;
		do {
			tsm = 2.0 * sig1 + sigma;
			del = B
					* Math.sin(sigma)
					* (Math.cos(tsm) + 0.25 * B * Math.cos(sigma)
							* (-1.0 + 2.0 * (Math.cos(tsm) * Math.cos(tsm))));
			cis = sigma - (s1 + del);
			sigma = s1 + del;
		} while (Math.abs(cis) > 1.0e-032);
		double cossigma;
		double sinsigma;
		cossigma = Math.cos(sigma);
		sinsigma = Math.sin(sigma);

		Geographic s = new Geographic();
		double dm;
		double dl1;
		s.lat = sinU1 * cossigma + cosU1 * sinsigma * cosa1;
		dm = Math.sqrt(sinae * sinae
				+ (sinU1 * sinsigma - cosU1 * cossigma * cosa1)
				* (sinU1 * sinsigma - cosU1 * cossigma * cosa1));
		s.lat = Math.atan2(s.lat, ((1.0 - f) * dm));
		dl1 = Math.atan2((sinsigma * sina1), (cosU1 * cossigma - sinU1
				* sinsigma * cosa1));
		s.lon = r.lon + dl1 - deltaLongitude(azimuthEQ, sigma, tsm);
		return s;
	}// END - spheroid::project

	public double meridianRadiusOfCurvature(double latitude) {
		double er;
		double el;
		double M0;
		er = 1.0 - es * Math.sin(latitude) * Math.sin(latitude);
		el = Math.pow(er, 1.5);
		M0 = (a * (1.0 - es)) / el;
		return M0;
	}// END - spheroid::meridianRadiusOfCurvature

	public double primeVerticalRadiusOfCurvature(double latitude) {
		double T1;
		double T2;
		double T3;
		double N0;
		T1 = a * a;
		T2 = T1 * Math.cos(latitude) * Math.cos(latitude);
		T3 = b * b * Math.sin(latitude) * Math.sin(latitude);
		N0 = T1 / Math.sqrt(T2 + T3);
		return N0;
	}// END - spheroid::primeVerticalRadiusOfCurvature

	public double deltaLongitude(double azimuth, double sigma, double tsm) {
		// compute the expansion C
		double das;
		// compute the expansion C
		double C;
		das = Math.cos(azimuth) * Math.cos(azimuth);
		C = f / 16.0 * das * (4.0 + f * (4.0 - 3.0 * das));
		// compute the difference in longitude
		double ctsm;
		// compute the difference in longitude
		double DL;
		ctsm = Math.cos(tsm);
		DL = ctsm + C * Math.cos(sigma) * (-1.0 + 2.0 * ctsm * ctsm);
		DL = sigma + C * Math.sin(sigma) * DL;
		return (1.0 - C) * f * Math.sin(azimuth) * DL;
	}// END - spheroid::deltaLongitude

	public double mu2(double azimuth) {
		double e2;

		e2 = Math.sqrt(a * a - b * b) / b;
		return Math.cos(azimuth) * Math.cos(azimuth) * e2 * e2;
	}// END - spheroid::mu2

	public double bigA(double u2) {
		return 1.0 + u2 / 256.0 * (64.0 + u2 * (-12.0 + 5.0 * u2));
	}// END - spheroid::bigA

	public double bigB(double u2) {
		return u2 / 512.0 * (128.0 + u2 * (-64.0 + 37.0 * u2));
	}// END - spheroid::bigB

	public double M(double latitude) {
		// returns the length of a meridian arc from the equator
		// to the given latitude (from Richard Rapp).
		return t1 * latitude - t2 * Math.sin(2.0 * latitude) + t3
				* Math.sin(4.0 * latitude) - t4 * Math.sin(6.0 * latitude) + t5
				* Math.sin(8.0 * latitude) - t5 * Math.sin(10.0 * latitude);
	}// END - spheroid::M

}
