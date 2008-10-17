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
 * Implements the Albers projection.
 * 
 * 
 * @author  $Author: javamap $
 * @version $Revision: 1.1 $
 * 
 * <pre>
 *  $Id: Albers.java,v 1.1 2005/06/16 15:25:29 javamap Exp $
 *  $Date: 2005/06/16 15:25:29 $
 *  $Log: Albers.java,v $
 *  Revision 1.1  2005/06/16 15:25:29  javamap
 *  *** empty log message ***
 *
 *  Revision 1.2  2005/05/03 15:23:55  javamap
 *  *** empty log message ***
 *
 *  Revision 1.2  2003/11/05 05:10:31  dkim
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
 * </pre>
 *
 */

public class Albers extends Projection {

  double L0;// central meridian
  double k0;// scale factor
  double phi1;// 1st standard parallel
  double phi2;// 2nd standard parallel
  double phi0;// Latitude of projection
  double X0;// false Easting
  double Y0;// false Northing
  double A_n, A_C, A_p0;// variables for Albers, see constructor
  Geographic q = new Geographic();

  public Albers() {
    super();
  }

  public void setParameters(double centralMeridian,
      double firstStandardParallel,
      double secondStandardParallel,
      double latitudeOfProjection,
      double falseEasting,
      double falseNorthing) {
    L0 = centralMeridian * Math.PI / 180.0;
    phi1 = firstStandardParallel * Math.PI / 180.0;
    phi2 = secondStandardParallel * Math.PI / 180.0;
    phi0 = latitudeOfProjection * Math.PI / 180.0;
    X0 = falseEasting;
    Y0 = falseNorthing;
    double m1;
    double m2;
    double q1;
    double q2;
    double q0;
    m1 = albersM(phi1);
    m2 = albersM(phi2);
    q1 = albersQ(phi1);
    q2 = albersQ(phi2);
    q0 = albersQ(phi0);
    A_n = (m1 * m1 - m2 * m2) / (q2 - q1);
    A_C = m1 * m1 + A_n * q1;
    double a;
    a = currentSpheroid.getA();
    A_p0 = (a * Math.sqrt(A_C - A_n * q0)) / A_n;
  }// END - constructor for Albers projection plane

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

  void forward(Geographic q, Planar p) {
    double que;
    double theta;
    double pee;
    double a;
    a = currentSpheroid.getA();
    que = albersQ(q.lat);
    theta = A_n * (q.lon - L0);
    pee = (a * Math.sqrt(A_C - A_n * que)) / A_n;
    p.x = pee * Math.sin(theta) + X0;
    p.y = A_p0 - pee * Math.cos(theta) + Y0;
  }


  void inverse(Planar p, Geographic q) {
    double es;
    double x;
    double y;
    double theta;
    double pee;
    double que;
    double a;
    double e;
    a = currentSpheroid.getA();
    e = currentSpheroid.getE();
    es = e * e;
    x = p.x - X0;
    y = p.y - Y0;
    theta = Math.atan2(x, A_p0 - y);
    pee = Math.sqrt(x * x + Math.pow((A_p0 - y), 2.0));
    que = (A_C - (pee * pee * A_n * A_n) / (a * a)) / A_n;
    q.lon = L0 + theta / A_n;
    double li;
    double delta;
    double j1;
    double k1;
    double k2;
    double k3;
    double lip1;
    //li = Math.sin(que / 2.0); -- transcription error
    li = Math.asin(que / 2.0);
    delta = 10e010;
    do {
      j1 = Math.pow((1.0 - es * Math.pow(Math.sin(li), 2.0)), 2.0) / (2.0 * Math.cos(li));
      k1 = que / (1.0 - es);
      k2 = Math.sin(li) / (1.0 - es * Math.pow(Math.sin(li), 2.0));
      k3 = (1.0 / (2.0 * e)) * Math.log((1.0 - e * Math.sin(li)) / (1.0 + e * Math.sin(li)));
      lip1 = li + j1 * (k1 - k2 + k3);
      delta = Math.abs(lip1 - li);
      li = lip1;
    } while (delta > 1.0e-012);
    q.lat = li;
  }


  double albersQ(double lat) {
    double q;
    double e;
    e = currentSpheroid.getE();
    q = (1.0 - e * e) * (Math.sin(lat) / (1.0 - e * e * Math.pow(Math.sin(lat), 2.0))
         - (1.0 / (2.0 * e)) * Math.log((1.0 - e * Math.sin(lat)) / (1.0 + e * Math.sin(lat))));
    return q;
  }


  double albersM(double lat) {
    double m;
    double e;
    e = currentSpheroid.getE();
    m = Math.cos(lat) / Math.sqrt(1.0 - e * e * Math.pow(Math.sin(lat), 2.0));
    return m;
  }


}
