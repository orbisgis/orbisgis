/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */

package org.orbisgis.omanager.ui;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Create actions related to the current state of the provided bundle.
 * @author Nicolas Fortin
 */
public class ActionBundleFactory {
    private static final I18n I18N = I18nFactory.getI18n(ActionBundleFactory.class);
    private static final Logger LOGGER = Logger.getLogger("gui." + ActionBundleFactory.class);
    private Map<String,ImageIcon> buttonIcons = new HashMap<String, ImageIcon>();
    private final boolean isPlugin;
    private ImageIcon getIcon(String iconName) {
        ImageIcon icon = buttonIcons.get(iconName);
        if(icon==null) {
            try {
                icon = new ImageIcon(MainPanel.class.getResource(iconName + ".png"));
                buttonIcons.put(iconName,icon);
            } catch (Exception ex) {
                LOGGER.error("Cannot retrieve icon "+iconName,ex);
                return new ImageIcon();
            }
        }
        return icon;
    }

    private Component frame;
    private BundleContext bundleContext;

    public ActionBundleFactory(BundleContext bundleContext,Component frame, boolean isPlugin) {
        this.bundleContext = bundleContext;
        this.frame = frame;
        this.isPlugin =isPlugin;
    }

    public List<Action> create(final BundleItem bundleItem) {
        List<Action> actions = new ArrayList<Action>();
       
        if(!bundleItem.isFragment()){
        if(bundleItem.isStartReady()) {
            actions.add(new ActionBundle(I18N.tr("Start"),I18N.tr("Activate the selected plugin"),getIcon("execute"), frame, isPlugin)
                    .setActionListener(EventHandler.create(ActionListener.class, bundleItem.getBundle(), "start")));
        }
        if(bundleItem.isStopReady()) {
            actions.add(new ActionBundle(I18N.tr("Stop"),I18N.tr("Deactivate the selected plugin"),getIcon("stop"),frame, isPlugin)
                    .setActionListener(EventHandler.create(ActionListener.class, bundleItem.getBundle(), "stop")));
        }
        if(bundleItem.isUpdateReady()) {
            actions.add(new ActionBundle(I18N.tr("Update"), I18N.tr("Update the selected plugin"),getIcon("refresh"), frame, isPlugin)
                    .setActionListener(EventHandler.create(ActionListener.class, bundleItem.getBundle(), "update")));
        }
        }
        if(bundleItem.isUninstallReady()) {
            actions.add(new ActionBundle(I18N.tr("Uninstall"), I18N.tr("Remove the selected plugin"),getIcon("uninstall"), frame, isPlugin)
                    .setActionListener(EventHandler.create(ActionListener.class, bundleItem.getBundle(), "uninstall")));
        }
        if(bundleItem.isDeployReady()) {
            actions.add(new ActionDeploy(I18N.tr("Download"),I18N.tr("Download the selected plugin"),false,bundleItem.getObrResource(),bundleContext,frame,getIcon("download"), isPlugin));
        }
        if(!bundleItem.isFragment()){
        if(bundleItem.isDeployAndStartReady()) {
            actions.add(new ActionDeploy(I18N.tr("Download & Start"),I18N.tr("Download the selected plugin and start it"),true,bundleItem.getObrResource(),bundleContext,frame,getIcon("download_and_start"), isPlugin));
        }
        }
        return actions;
    }

}
