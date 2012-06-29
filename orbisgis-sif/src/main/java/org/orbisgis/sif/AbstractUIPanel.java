/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.sif;

import java.net.URL;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public abstract class AbstractUIPanel implements UIPanel {
        protected final static I18n i18n = I18nFactory.getI18n(AbstractUIPanel.class);
        private boolean showFavorites = true;
        
	@Override
	public URL getIconURL() {
		return UIFactory.getDefaultIcon();
	}

	@Override
	public String getInfoText() {
		return UIFactory.getDefaultOkMessage();
	}

	@Override
	public String postProcess() {
		return null;
	}

	@Override
	public String initialize() {
		return null;
	}

	public boolean showFavorites() {
		return showFavorites;
	}

        
        public void setShowFavorites(boolean showFavorites) {
                this.showFavorites = showFavorites;
        }
        
}
