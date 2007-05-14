/* OrbisCAD. The Community cartography editor
 *
 * Copyright (C) 2005, 2006 OrbisCAD development team
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  OrbisCAD development team
 *   elgallego@users.sourceforge.net
 */
package org.orbisgis.plugin.view.tools.instances;

import java.awt.Graphics;
import java.net.URL;

import org.orbisgis.plugin.view.tools.DrawingException;
import org.orbisgis.plugin.view.tools.FinishedAutomatonException;
import org.orbisgis.plugin.view.tools.TransitionException;
import org.orbisgis.plugin.view.tools.instances.generated.Delete;

public class DeleteTool extends Delete {

    @Override
    public void transitionTo_Standby() throws FinishedAutomatonException, TransitionException {
        if (!ec.isActiveThemeWritable()) {
            throw new TransitionException(Messages.getString("DeleteTool.0")); //$NON-NLS-1$
        }

        ec.removeSelected();
    }

    @Override
    public void drawIn_Standby(Graphics g) throws DrawingException {
    }

    public boolean isEnabled() {
        return ec.atLeastNGeometriesSelected(1) &&
        ec.isActiveThemeWritable();
    }

    public boolean isVisible() {
        return true;
    }

    public URL getMouseCursor() {
        return null;
    }

}
