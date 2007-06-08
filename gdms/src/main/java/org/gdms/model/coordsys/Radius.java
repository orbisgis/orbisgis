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
 * Utility class, holds constants associated with Radius for various
 * projections.
 * 
 * @author $Author: dkim $
 * @version $Revision: 1.2 $
 * 
 * <pre>
 * 
 *   $Id: Radius.java,v 1.2 2003/11/05 05:24:47 dkim Exp $
 *   $Date: 2003/11/05 05:24:47 $
 *   $Log: Radius.java,v $
 *   Revision 1.2  2003/11/05 05:24:47  dkim
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
 *   Revision 1.1  2003/06/20 18:34:31  gkostadinov
 *   Entering the source code into the CVS.
 * 
 * </pre>
 */

public class Radius {

	public double a, b, rf;

	public final static int WGS72 = 1;

	public final static int CLARKE = 2;

	public final static int GRS80 = 0;

	public Radius(int type) {
		switch (type) {
		case Radius.GRS80:
			a = 6378137.0;
			b = -1.0;
			rf = 298.257222101;
			break;
		case Radius.WGS72:
			a = 6378135.0;
			b = 6356750.5;
			rf = -1.0;
			break;
		case Radius.CLARKE:
			a = 6378206.4;
			b = 6356583.8;
			rf = -1.0;
			break;
		}
	}
}
