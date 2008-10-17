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
import org.contrib.model.jump.coordsys.Spheroid;

import com.vividsolutions.jts.util.Assert;

/**
 * This class implements the Universal Transverse Mercator Projection.
 
 *  @version $Revision: 1.1 $
 *  @author $Author: javamap $
 * <pre>
 *  $Id: UniversalTransverseMercator.java,v 1.1 2005/06/16 15:25:29 javamap Exp $
 *  $Date: 2005/06/16 15:25:29 $
 *  $Log: UniversalTransverseMercator.java,v $
 *  Revision 1.1  2005/06/16 15:25:29  javamap
 *  *** empty log message ***
 *
 *  Revision 1.2  2005/05/03 15:23:55  javamap
 *  *** empty log message ***
 *
 *  Revision 1.2  2003/11/05 05:18:44  dkim
 *  Added global header; cleaned up Javadoc.
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
 * </pre>
 */



public class UniversalTransverseMercator extends Projection {

  private final static double SCALE_FACTOR = 0.9996;
  private final static double FALSE_EASTING = 500000.0;
  private final static double FALSE_NORTHING = 0.0;

  private TransverseMercator transverseMercator = new TransverseMercator();

  public UniversalTransverseMercator() { }

  private int zone = -1;

  /**
   * @param utmZone must be between 7 and 11
   */
  public void setParameters(int zone) {

    Assert.isTrue(zone >= 7, "UTM zone " + zone + " not supported");
    Assert.isTrue(zone <= 11, "UTM zone " + zone + " not supported");

    switch (zone) {
      case 7:
        transverseMercator.setParameters(-141.0);
        break;
      case 8:
        transverseMercator.setParameters(-135.0);
        break;
      case 9:
        transverseMercator.setParameters(-129.0);
        break;
      case 10:
        transverseMercator.setParameters(-123.0);
        break;
      case 11:
        transverseMercator.setParameters(-117.0);
        break;
      case 12:
        transverseMercator.setParameters(-111.0);
        break;
      default:
        Assert.shouldNeverReachHere();
    }
    this.zone = zone;
  }

  public void setSpheroid(Spheroid s) {
    transverseMercator.setSpheroid(s);
  }

  public Geographic asGeographic(Planar p, Geographic q) {

    Assert.isTrue(zone != -1, "Call #setParameters first");

    p.x = (p.x - FALSE_EASTING) / SCALE_FACTOR;
    p.y = (p.y - FALSE_NORTHING) / SCALE_FACTOR;
    transverseMercator.asGeographic(p, q);
    return q;
  }

  public Planar asPlanar(Geographic q0, Planar p) {

    Assert.isTrue(zone != -1, "Call #setParameters first");

    transverseMercator.asPlanar(q0, p);
    p.x = SCALE_FACTOR * p.x + FALSE_EASTING;
    p.y = SCALE_FACTOR * p.y + FALSE_NORTHING;
    return p;
  }

}
