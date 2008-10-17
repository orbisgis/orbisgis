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

import java.util.Date;

import org.contrib.model.jump.coordsys.Geographic;
import org.contrib.model.jump.coordsys.Planar;
import org.contrib.model.jump.coordsys.Projection;



/**
 
 * This class implements the Transverse Mercator Projection.

 *  @version $Revision: 1.1 $
 *  @author $Author: javamap $

 *<pre>
 *  $Id: TransverseMercator.java,v 1.1 2005/06/16 15:25:29 javamap Exp $
 *  $Date: 2005/06/16 15:25:29 $
 
 *  $Log: TransverseMercator.java,v $
 *  Revision 1.1  2005/06/16 15:25:29  javamap
 *  *** empty log message ***
 *
 *  Revision 1.2  2005/05/03 15:23:55  javamap
 *  *** empty log message ***
 *
 *  Revision 1.3  2003/11/05 05:16:00  dkim
 *  Added global header; cleaned up Javadoc.
 *
 *  Revision 1.2  2003/09/15 21:43:27  jaquino
 *  Global flag for enabling/disabling 
 *  CoordinateSystemSupport
 *
 *  Revision 1.1  2003/09/15 20:26:11  jaquino
 *  Reprojection
 *
 *  Revision 1.2  2003/07/25 17:01:03  gkostadinov
 *  Moved classses reponsible for performing the basic projection to a new
 *  package -- base.
 *
 *  Revision 1.1  2003/07/24 23:14:43  gkostadinov
 *  adding base projection classes
 *
 *  Revision 1.1  2003/06/20 18:34:30  gkostadinov
 *  Entering the source code into the CVS.
 *</pre>
 */

public class TransverseMercator extends Projection {

  double L0;// central meridian
  double k0;

  public TransverseMercator() { }

  /**
   *@param  centralMeridian  in degrees
   */
  public void setParameters(double centralMeridian) {
    L0 = centralMeridian / 180.0 * Math.PI;
  }

  /**
   *@param  q  in degrees
   */
  public Geographic asGeographic(Planar p, Geographic q) {
    planarToGeographicInRadians(p, q);
    q.lat = q.lat * 180.0 / Math.PI;
    q.lon = q.lon * 180.0 / Math.PI;
    return q;
  }//scale factor

  /**
   *@param  q0  in degrees
   */
  public Planar asPlanar(Geographic q0, Planar p) {
    Geographic q = new Geographic();
    q.lat = q0.lat / 180.0 * Math.PI;
    q.lon = q0.lon / 180.0 * Math.PI;
    geographicInRadiansToPlanar(q, p);
    return p;
  }

  /**
   *@param  q  in radians
   */
  void planarToGeographicInRadians(Planar p, Geographic q) {
    double L1;
    L1 = footPointLatitude(p.y);
    double a;
    double b;
    a = currentSpheroid.getA();
    b = currentSpheroid.getB();
    double ep2;
    double N1;
    double M1;
    ep2 = (a * a - b * b) / (b * b);
    // N1 = the radius of curvature of the spheroid in the prime vertical plane
    // at the foot point latitude
    N1 = currentSpheroid.primeVerticalRadiusOfCurvature(L1);
    // M1 = meridian radius of curvature at the foot point latitude
    M1 = currentSpheroid.meridianRadiusOfCurvature(L1);
    double n1;
    double n12;
    double n14;
    double n16;
    double n18;
    n12 = ep2 * Math.pow(Math.cos(L1), 2.0);
    n1 = Math.sqrt(n12);
    n14 = n12 * n12;
    n16 = n14 * n12;
    n18 = n14 * n14;
    double t1;
    double t12;
    double t14;
    double t16;
    t1 = Math.tan(L1);
    t12 = t1 * t1;
    t14 = t12 * t12;
    t16 = t14 * t12;
    double u0;
    double u1;
    double v1;
    double u2;
    double v2;
    double u3;
    double v3;
    u0 = t1 * Math.pow(p.x, 2.0) / (2.0 * M1 * N1);
    u1 = t1 * Math.pow(p.x, 4.0) / (24.0 * M1 * Math.pow(N1, 3.0));
    u2 = t1 * Math.pow(p.x, 6.0) / (720.0 * M1 * Math.pow(N1, 5.0));
    u3 = t1 * Math.pow(p.x, 8.0) / (40320.0 * M1 * Math.pow(N1, 7.0));
    v1 = 5.0 + 3.0 * t12 + n12 - 4.0 * n14 - 9.0 * n12 * t12;
    v2 = 61.0 - 90.0 * t12 + 46.0 * n12 + 45.0 * t14 - 252.0 * t12 * n12 - 3.0 * n14
         + 100.0 * n16 - 66.0 * t12 * n14 - 90.0 * t14 * n12 + 88.0 * n18 + 225.0 * t14 * n14
         + 84.0 * t12 * n16 - 192.0 * t12 * n18;
    v3 = 1385.0 + 3633.0 * t12 + 4095.0 * t14 + 1575.0 * t16;

    q.lat = L1 - u0 + u1 * v1 - u2 * v2 + u3 * v3;
    double XdN1;
    XdN1 = p.x / N1;
    u0 = XdN1;
    u1 = Math.pow(XdN1, 3.0) / 6.0;
    u2 = Math.pow(XdN1, 5.0) / 120.0;
    u3 = Math.pow(XdN1, 7.0) / 5040.0;
    v1 = 1.0 + 2.0 * t12 + n12;
    v2 = 5.0 + 6.0 * n12 + 28.0 * t12 - 3.0 * n14 + 8.0 * t12 * n12 + 24.0 * t14 - 4.0 * n16 + 4.0 * t12 * n14 + 24.0 * t12 * n16;
    v3 = 61.0 + 662.0 * t12 + 1320.0 * t14 + 720.0 * t16;
    q.lon = 1.0 / Math.cos(L1) * (u0 - u1 * v1 + u2 * v2 - u3 * v3) + L0;
  }

  private MeridianArcLength S = new MeridianArcLength();

  /**
   *@param  q  in radians
   */
  void geographicInRadiansToPlanar(Geographic q, Planar p) {
    double a;
    double b;
    a = currentSpheroid.getA();
    b = currentSpheroid.getB();
    double ep2;
    double N;
    // ep2 = the second eccentricity squared.
    ep2 = (a * a - b * b) / (b * b);
    // N = the radius of curvature of the spheroid in the prime vertical plane
    N = currentSpheroid.primeVerticalRadiusOfCurvature(q.lat);
    double n;
    double n2;
    double n4;
    double n6;
    double n8;
    n2 = ep2 * Math.pow(Math.cos(q.lat), 2.0);
    n = Math.sqrt(n2);
    n4 = n2 * n2;
    n6 = n4 * n2;
    n8 = n4 * n4;
    double t;
    double t2;
    double t4;
    double t6;
    t = Math.tan(q.lat);
    t2 = t * t;
    t4 = t2 * t2;
    t6 = t4 * t2;
    S.compute(currentSpheroid, q.lat, 0);
    double cosLat;
    double sinLat;
    cosLat = Math.cos(q.lat);
    sinLat = Math.sin(q.lat);
    double L;
    double L2;
    double L3;
    double L4;
    double L5;
    double L6;
    double L7;
    double L8;
    L = q.lon - L0;// 'L' for lambda (longitude) - must be in radians
    L2 = L * L;
    L3 = L2 * L;
    L4 = L2 * L2;
    L5 = L4 * L;
    L6 = L4 * L2;
    L7 = L5 * L2;
    L8 = L4 * L4;
    double u0;
    double u1;
    double v1;
    double u2;
    double v2;
    double u3;
    double v3;
    u0 = L * cosLat;
    u1 = L3 * Math.pow(cosLat, 3.0) / 6.0;
    u2 = L5 * Math.pow(cosLat, 5.0) / 120.0;
    u3 = L7 * Math.pow(cosLat, 7.0) / 5040.0;
    v1 = 1.0 - t2 + n2;
    v2 = 5.0 - 18.0 * t2 + t4 + 14.0 * n2 - 58.0 * t2 * n2 + 13.0 * n4 + 4.0 * n6 - 64.0 * n4 * t2 - 24.0 * n6 * t2;
    v3 = 61.0 - 479.0 * t2 + 179.0 * t4 - t6;
    p.x = u0 + u1 * v1 + u2 * v2 + u3 * v3;

    u0 = L2 / 2.0 * sinLat * cosLat;
    u1 = L4 / 24.0 * sinLat * Math.pow(cosLat, 3.0);
    u2 = L6 / 720.0 * sinLat * Math.pow(cosLat, 5.0);
    u3 = L8 / 40320.0 * sinLat * Math.pow(cosLat, 7.0);
    v1 = 5.0 - t2 + 9.0 * n2 + 4.0 * n4;
    v2 = 61.0 - 58.0 * t2 + t4 + 270.0 * n2 - 330.0 * t2 * n2 + 445.0 * n4 + 324.0 * n6 - 680.0 * n4 * t2
         + 88.0 * n8 - 600.0 * n6 * t2 - 192.0 * n8 * t2;
    v3 = 1385.0 - 311.0 * t2 + 543.0 * t4 - t6;
    p.y = S.s / N + u0 + u1 * v1 + u2 * v2 + u3 * v3;

    p.x = N * p.x;
    p.y = N * p.y;
  }

  private double footPointLatitude(double y) {
// returns the footpoint Latitude given the y coordinate
    double newlat;
// returns the footpoint Latitude given the y coordinate
    double Lat1;
// returns the footpoint Latitude given the y coordinate
    double flat;
// returns the footpoint Latitude given the y coordinate
    double dflat;
    double a;
    a = currentSpheroid.getA();
    newlat = y / a;
    int i = 0;
    do {
      Lat1 = newlat;
      i++;
      if (i == 100) {
          //Prevent infinite loop. I observed that a typical number of iterations is 5. [Jon Aquino]
          break; 
      }
      S.compute(currentSpheroid, Lat1, 0);
      flat = S.s - y;
      dflat = a * (S.a0 - 2.0 * S.a2 * Math.cos(2.0 * Lat1) + 4.0 * S.a4 * Math.cos(4.0 * Lat1)
           - 6.0 * S.a6 * Math.cos(6.0 * Lat1) + 8.0 * S.a8 * Math.cos(8.0 * Lat1));
      newlat = Lat1 - flat / dflat;
      //Increased tolerance from 1E-16 to 1E-15. 1E-16 was causing an infinite loop.
      //JA 6 Nov 2001.
    } while (Math.abs(newlat - Lat1) > 1.0e-015);
    Lat1 = newlat;
    return Lat1;
  }// END - footPointLatitude

}
