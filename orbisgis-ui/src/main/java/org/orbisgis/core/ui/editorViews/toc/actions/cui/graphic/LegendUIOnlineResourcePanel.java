/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */



package org.orbisgis.core.ui.editorViews.toc.actions.cui.graphic;

import java.net.URISyntaxException;
import javax.swing.Icon;
import org.orbisgis.core.renderer.se.common.OnlineResource;
import org.orbisgis.core.renderer.se.graphic.ExternalGraphicSource;
import org.orbisgis.core.renderer.se.graphic.WellKnownName;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIComponent;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIController;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.TextInput;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

/**
 *
 * @author maxence
 */
public class LegendUIOnlineResourcePanel extends LegendUIComponent /*implements LegendUIExternalSourceComponent*/ {

	TextInput urlInput;
    OnlineResource onlineResource;

	public LegendUIOnlineResourcePanel(LegendUIController controller, LegendUIComponent parent, OnlineResource url) {
		super("URL", controller, parent, 0, false);

        this.onlineResource = url;
        if (onlineResource == null){
            onlineResource = new OnlineResource();
        }

        String initUrl = "";
        if (onlineResource.getUri() != null){
            initUrl = onlineResource.getUri().toString();
        }

        urlInput = new TextInput("URL", initUrl, 50, false) {

            @Override
            protected void valueChanged(String s) {
                try {
                    onlineResource.setUri(s);
                } catch (URISyntaxException ex) {
                    //urlInput.setValue("");
                }
            }
        };
	}

	@Override
	public Icon getIcon() {
		return OrbisGISIcon.PALETTE;
	}

	@Override
	protected void mountComponent() {
        editor.add(urlInput);
	}

	@Override
	protected void turnOff() {
        //updateOnlineResource(null);
	}

	@Override
	protected void turnOn() {
        //updateOnlineResource(this.onlineResource);
	}

	@Override
	public Class getEditedClass() {
		return WellKnownName.class;
	}

    /*@Override
    public ExternalGraphicSource getSource() {
        return onlineResource;
    }*/



    /**
     * Method to override by users
     * @param url
     */
    //public abstract void updateOnlineResqource(OnlineResource url);
}
