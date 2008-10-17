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
 * This class implements the Mercator projection.
 * 
 * @version $Revision: 1.1 $
 * @author $Author: javamap $
 * 
 * <pre>
 *  $Id: Mercator.java,v 1.1 2005/06/16 15:25:29 javamap Exp $
 *  $Date: 2005/06/16 15:25:29 $

 *  $Log: Mercator.java,v $
 *  Revision 1.1  2005/06/16 15:25:29  javamap
 *  *** empty log message ***
 *
 *  Revision 1.2  2005/05/03 15:23:55  javamap
 *  *** empty log message ***
 *
 *  Revision 1.2  2003/11/05 05:12:52  dkim
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

public class Mercator extends Projection {

  double L0;// central meridian
  double X0;// false Easting
  double Y0;// false Northing
  Geographic q = new Geographic();

  public Mercator() {
    super();
  }

  /**
   *@param  centralMeridian  in degrees
   *@param  falseEasting     in metres
   *@param  falseNorthing    in metres
   */
  public void setParameters(double centralMeridian,
      double falseEasting,
      double falseNorthing) {
    L0 = centralMeridian / 180.0 * Math.PI;
    X0 = falseEasting;
    Y0 = falseNorthing;
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

  void forward(Geographic q, Planar p) {
    double a;
    double e;
    a = currentSpheroid.getA();
    e = currentSpheroid.getE();
    p.x = a * (q.lon - L0);
    p.y = (a / 2.0) * Math.log(
        ((1.0 + Math.sin(q.lat)) / (1.0 - Math.sin(q.lat)))
         * Math.pow(((1.0 - e * Math.sin(q.lat)) / (1.0 + e * Math.sin(q.lat))), e));
  }

  void inverse(Planar p, Geographic q) {
    double t;
    double delta;
    double phiI;
    double phi;
    double lambda;
    double a;
    double e;
    a = currentSpheroid.getA();
    e = currentSpheroid.getE();
    t = Math.exp(-p.y / a);
    //phi = Math.PI / 2.0 - 2.0 * Math.tan(t); -- transcription error
    phi = Math.PI / 2.0 - 2.0 * Math.atan(t);
    delta = 10000.0;
    do {
      phiI = Math.PI / 2.0 - 2.0 * Math.atan(
          t * Math.pow(((1.0 - e * Math.sin(phi)) / (1.0 + e * Math.sin(phi))),
          (e / 2.0)));
      delta = Math.abs(phiI - phi);
      phi = phiI;
    } while (delta > 1.0e-014);
    lambda = p.x / a + L0;
    q.lat = phi;
    q.lon = lambda;
  }

}
