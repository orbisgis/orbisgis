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

 * @author $Author: dkim $
 * @version  $Revision: 1.2 $
 *
 * <pre>
 * $Id: Planar.java,v 1.2 2003/11/05 05:22:17 dkim Exp $
 * $Date: 2003/11/05 05:22:17 $
 *
 * $Log: Planar.java,v $
 * Revision 1.2  2003/11/05 05:22:17  dkim
 * Added global header; cleaned up Javadoc.
 *
 * Revision 1.1  2003/09/15 20:26:11  jaquino
 * Reprojection
 *
 * Revision 1.2  2003/07/25 17:01:04  gkostadinov
 * Moved classses reponsible for performing the basic projection to a new
 * package -- base.
 *
 * Revision 1.1  2003/07/24 23:14:44  gkostadinov
 * adding base projection classes
 *
 * Revision 1.1  2003/06/20 18:34:31  gkostadinov
 * Entering the source code into the CVS.
 *
 * </pre>
 */

/** 
 * A base class for planar coordinate systems.
 */
public class Planar {

  public double x, y, z;

  public Planar() {
    x = 0;
    y = 0;
    z = 0;
  }

  public Planar(double _x, double _y) {
    x = _x;
    y = _y;
    z = 0;
  }

  public String toString() {
    return x + ", " + y;
  }
}

