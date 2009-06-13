
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

package com.vividsolutions.jump.ui;

import javax.swing.JComponent;

/**
 * A test for whether to enable or disable a menu, toolbar button, or other 
 * component. Provides a handy message about why a component is disabled.
 * @see EnableCheckFactory 
 */
public interface EnableCheck {
    /**
	 * Returns a non-null value if the check failed. Sometimes the return value
	 * is used (for example, it is displayed as a tooltip for menu-items);
	 * sometimes it is not (for example, toolbar buttons don't do anything with
	 * the return value). An advanced use of an EnableCheck is simply to change
	 * some property of a menu item (such as the text), as it is called when
	 * menu items are displayed.
	 * 
	 * @return an error message if the check failed, or null if the check
	 *              passed
	 */
    public String check(JComponent component);
}
