/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2009 Erwan BOCHER, Pierre-yves FADET
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    Pierre-Yves.Fadet_at_ec-nantes.fr
 *    thomas.leduc _at_ cerma.archi.fr
 */

package org.orbisgis.plugins.core.ui.editors.TableEditor;

import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.Observable;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.components.sif.AskValidRow;
import org.orbisgis.plugins.core.ui.editor.IEditor;
import org.orbisgis.plugins.core.ui.editors.table.TableEditableElement;
import org.orbisgis.plugins.core.ui.views.editor.EditorManager;
import org.orbisgis.plugins.core.ui.workbench.Names;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchFrame;
import org.orbisgis.plugins.errorManager.ErrorManager;
import org.orbisgis.plugins.images.IconLoader;
import org.orbisgis.plugins.sif.UIFactory;

public class NewRowTablePlugIn extends AbstractPlugIn {

	private JButton btn;
	private boolean menuItemIsVisible;

	public NewRowTablePlugIn() {
		btn = new JButton(getIcon());
	}

	@Override
	public boolean execute(PlugInContext context) throws Exception {
		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();
		TableEditableElement element = (TableEditableElement) editor
				.getElement();
		addRow(element);
		return true;
	}

	@Override
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = (WorkbenchFrame) wbContext.getWorkbench()
				.getFrame().getTableEditor();
		wbContext.getWorkbench().getFrame().getEditionTableToolBar().addPlugIn(
				this, btn);
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TABLE_ADDROW_PATH1 },
				Names.POPUP_TABLE_ADDROW_GROUP, false,
				getIcon(Names.POPUP_TABLE_ADDROW_ICON), wbContext);
	}

	@Override
	public void update(Observable o, Object arg) {
		btn.setEnabled(isEnabled());
		btn.setVisible(true);
		menuItemIsVisible = (arg instanceof MouseEvent) ? true : false;
	}

	public static void addRow(TableEditableElement element) {
		DataSource ds = element.getDataSource();
		try {
			AskValidRow rowInput = new AskValidRow("Introduce row values", ds);
			if (UIFactory.showDialog(rowInput)) {
				ds.insertFilledRow(rowInput.getRow());
			}
		} catch (IllegalArgumentException e) {
			Services.getService(ErrorManager.class).error("Cannot add row", e);
		} catch (IncompatibleTypesException e) {
			Services.getService(ErrorManager.class).error(
					"Incompatible types at insertion", e);
		} catch (DriverException e) {
			Services.getService(ErrorManager.class).error("Data access error",
					e);
		} catch (ParseException e) {
			Services.getService(ErrorManager.class).error("Unrecognized input",
					e);
		}
	}

	public boolean isEnabled() {
		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();
		if ("Table".equals(em.getEditorId(editor)) && editor != null) {
			TableEditableElement element = (TableEditableElement) editor
					.getElement();
			if (element.isEditable())
				return true;
			else if (element.getMapContext() == null)
				return isEnabled(element);
		}
		return false;
	}

	public boolean isEnabled(TableEditableElement element) {
		if (element.getDataSource().isEditable())
			return true;
		else if (element.getMapContext() == null) {
			return element.getDataSource().isEditable();
		}
		return false;
	}

	public boolean isVisible() {
		return menuItemIsVisible && isEnabled();
	}

	public static ImageIcon getIcon() {
		return IconLoader.getIcon(Names.POPUP_TABLE_ADDROW_ICON);
	}
}
