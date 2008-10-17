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

import org.contrib.model.jump.coordsys.Spheroid;


/**
 * @author $Author: javamap $
 * @version $Revision: 1.1 $
 * <pre>
 * $Id: MeridianArcLength.java,v 1.1 2005/06/16 15:25:29 javamap Exp $
 * $Date: 2005/06/16 15:25:29 $
 * 
 * 
 *  $Log: MeridianArcLength.java,v $
 *  Revision 1.1  2005/06/16 15:25:29  javamap
 *  *** empty log message ***
 *
 *  Revision 1.2  2005/05/03 15:23:55  javamap
 *  *** empty log message ***
 *
 *  Revision 1.2  2003/11/05 05:13:43  dkim
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
 *</pre>
 */

public class MeridianArcLength {

  public double s, a0, a2, a4, a6, a8;

  public void compute(Spheroid spheroid, double lat, int diff) {
//  Returns the meridian arc length given the latitude
    double e2;
//  Returns the meridian arc length given the latitude
    double e4;
//  Returns the meridian arc length given the latitude
    double e6;
//  Returns the meridian arc length given the latitude
    double e8;
    double a;
    double e;
    a = spheroid.getA();
    e = spheroid.getE();
    e2 = e * e;
    e4 = e2 * e2;
    e6 = e4 * e2;
    e8 = e4 * e4;
    a0 = 1.0 - e2 / 4.0 - 3.0 * e4 / 64.0 - 5.0 * e6 / 256.0 - 175.0 * e8 / 16384.0;
    a2 = 3.0 / 8.0 * (e2 + e4 / 4.0 + 15.0 * e6 / 128.0 - 455.0 * e8 / 4096.0);
    a4 = 15.0 / 256.0 * (e4 + 3.0 * e6 / 4.0 - 77.0 * e8 / 128.0);
    a6 = 35.0 / 3072.0 * (e6 - 41.0 * e8 / 32.0);
    a8 = -315.0 * e8 / 131072.0;
    if (diff == 0) {
      s = a * (a0 * lat - a2 * Math.sin(2.0 * lat) + a4 * Math.sin(4.0 * lat)
           - a6 * Math.sin(6.0 * lat) + a8 * Math.sin(8.0 * lat));
    }
    else {
      s = a0 * lat - 2.0 * a2 * Math.cos(2.0 * lat) + 4.0 * a4 * Math.cos(4.0 * lat)
           - 6.0 * a6 * Math.cos(6.0 * lat) + 8.0 * a8 * Math.cos(8.0 * lat);
    }
  }

}
