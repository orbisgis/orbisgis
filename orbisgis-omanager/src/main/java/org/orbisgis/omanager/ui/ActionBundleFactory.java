/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.service.packageadmin.PackageAdmin;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Create actions related to the current state of the provided bundle.
 * @author Nicolas Fortin
 */
public class ActionBundleFactory {
    private static I18n I18N = I18nFactory.getI18n(ActionBundleFactory.class);
    private BundleContext bundleContext;

    public ActionBundleFactory(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public List<Action> create(final BundleItem bundleItem) {
        List<Action> actions = new ArrayList<Action>();

        if(bundleItem.isStartReady()) {
            actions.add(new ActionBundle(I18N.tr("Start"),I18N.tr("Activate the selected plug-in"))
                    .setActionListener(EventHandler.create(ActionListener.class, bundleItem.getBundle(), "start")));
        }
        if(bundleItem.isStopReady()) {
            actions.add(new ActionBundle(I18N.tr("Stop"),I18N.tr("Deactivate the selected plug-in"))
                    .setActionListener(EventHandler.create(ActionListener.class, bundleItem.getBundle(), "stop")));
        }
        if(bundleItem.isUpdateReady()) {
            actions.add(new ActionBundle(I18N.tr("Update"), I18N.tr("Update the plug-in with the same version."))
                    .setActionListener(EventHandler.create(ActionListener.class, bundleItem.getBundle(), "update")));
        }
        if(bundleItem.isUninstallReady()) {
            actions.add(new ActionUninstall(bundleItem));
        }
        return actions;
    }

    private class ActionUninstall extends ActionBundle {
        private BundleItem bundleItem;

        private ActionUninstall(BundleItem bundleItem) {
            super(I18N.tr("Uninstall"), I18N.tr("Remove the selected plug-in"));
            this.bundleItem = bundleItem;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            try {
                // Refresh export
                // Get package admin service.
                ServiceReference ref = bundleContext.getServiceReference(
                        PackageAdmin.class);
                if (ref == null)
                {
                    LOGGER.error(I18N.tr("PackageAdmin service is unavailable."));
                    return;
                }

                PackageAdmin pa = (PackageAdmin) bundleContext.getService(ref);
                if (pa == null)
                {
                    LOGGER.error(I18N.tr("PackageAdmin service is unavailable."));
                    return;
                }
                bundleItem.getBundle().uninstall();
                pa.refreshPackages(new Bundle[] {bundleItem.getBundle()});
            } catch (Exception ex) {
                LOGGER.error(ex.getLocalizedMessage(),ex);
            }
        }
    }
}
