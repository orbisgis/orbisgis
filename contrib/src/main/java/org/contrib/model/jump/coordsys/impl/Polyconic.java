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

package org.contrib.model.jump.coordsys.impl;

import org.contrib.model.jump.coordsys.Geographic;
import org.contrib.model.jump.coordsys.Planar;
import org.contrib.model.jump.coordsys.Projection;


/**
 * Implements the Polyconic projection.
 * *
 * @author $Author: javamap $
 * @version $Revision: 1.1 $
 * <pre>
 * $Id: Polyconic.java,v 1.1 2005/06/16 15:25:29 javamap Exp $
 * $Date: 2005/06/16 15:25:29 $
 * 
 * 
 *  $Log: Polyconic.java,v $
 *  Revision 1.1  2005/06/16 15:25:29  javamap
 *  *** empty log message ***
 *
 *  Revision 1.2  2005/05/03 15:23:55  javamap
 *  *** empty log message ***
 *
 *  Revision 1.2  2003/11/05 05:14:18  dkim
 *  Added global header; cleaned up Javadoc.
 *
 *  Revision 1.1  2003/09/15 20:26:12  jaquino
 *  Reprojection
 *
 *  Revision 1.2  2003/07/25 17:01:04  gkostadinov
 *  Moved classses reponsible for performing the basic projection to a new
 *  package -- base.
 *
 *  Revision 1.1  2003/07/24 23:14:44  gkostadinov
 *  adding base projection classes
 *
 *  Revision 1.1  2003/06/20 18:34:31  gkostadinov
 *  Entering the source code into the CVS.
 * 
 * </pre>
 *
 */

public class Polyconic extends Projection {

// private:
  double L0;// central meridian
  double k0;// scale factor
  double phi1;// 1st standard parallel
  double phi2;// 2nd standard parallel
  double phi0;// Latitude of projection
  double X0;// false Easting
  double Y0;// false Northing
  int zone;// UTMzone
  MeridianArcLength S = new MeridianArcLength();
  Geographic q = new Geographic();

  public Polyconic() {
    super();
  }

  public void setParameters(double originLatitude, double originLongitude) {
    // Polyconic projection
    L0 = originLongitude / 180.0 * Math.PI;
    phi0 = originLatitude / 180.0 * Math.PI;
  }

  public Planar asPlanar(Geographic q0, Planar p) {
    q.lat = q0.lat / 180.0 * Math.PI;
    q.lon = q0.lon / 180.0 * Math.PI;
    forward(q, p);
    return p;
  }

  public Geographic asGeographic(Planar p, Geographic q) {
    inverse(p, q);
    q.lat = q.lat * 180.0 / Math.PI;
    q.lon = q.lon * 180.0 / Math.PI;
    return q;
  }


  public void forward(Geographic q, Planar p) {
    double M;
    double M0;
    S.compute(currentSpheroid, q.lat, 0);
    M = S.s;
    S.compute(currentSpheroid, phi0, 0);
    M0 = S.s;
    double a;
    double e;
    double e2;
    a = currentSpheroid.a;
    e = currentSpheroid.e;
    e2 = e * e;
    double N;
    double t;
    t = Math.sin(q.lat);
    N = a / Math.sqrt(1.0 - e2 * t * t);
    double E;
    E = (q.lon - L0) * Math.sin(q.lat);
    t = 1.0 / Math.tan(q.lat);
    p.x = N * t * Math.sin(E);
    p.y = M - M0 + N * t * (1.0 - Math.cos(E));
  }


  public void inverse(Planar p, Geographic q) {
    double a;
    double e;
    double es;
    a = currentSpheroid.getA();
    e = currentSpheroid.getE();
    es = e * e;
    double A;
    double B;
    double M0;
    S.compute(currentSpheroid, phi0, 0);
    M0 = S.s;
    A = (M0 + p.y) / a;
    B = (p.x * p.x) / (a * a) + A * A;
    double C;
    double phiN;
    double M;
    double Mp;
    double Ma;
    double Ma2;
    double s2p;
    q.lat = A;
    int count = 0;
    do {
      phiN = q.lat;
      C = Math.sqrt(1.0 - es * Math.sin(phiN) * Math.sin(phiN)) * Math.tan(phiN);
      S.compute(currentSpheroid, phiN, 0);
      M = S.s;
      Ma = M / a;
      Ma2 = Ma * Ma;
      S.compute(currentSpheroid, phiN, 1);
      Mp = S.s;
      s2p = Math.sin(2.0 * phiN);
      q.lat = q.lat - (A * (C * Ma + 1.0) - Ma - 0.5 * (Ma2 + B) * C) /
          (es * s2p * (Ma2 + B - 2.0 * A * Ma) / 4.0 * C + (A - Ma) * (C * Mp - 2.0 / s2p) - Mp);
    } while (Math.abs(q.lat - phiN) > 1.0e-6 && count++ < 100);//1.0e-12);
    q.lon = Math.asin(p.x * C / a) / Math.sin(q.lat) + L0;
  }

}
