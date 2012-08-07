/**
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
package org.orbisgis.core.ui.pluginSystem;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.Observable;

import javax.swing.JComponent;

import org.orbisgis.core.Services;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.utils.I18N;

public abstract class AbstractPlugIn implements PlugIn {

	/**
	 * Plug-in name
	 */
	private String name;
	/**
	 * Plug-in Context
	 */
	private PlugInContext plugInContext;
	/**
	 * If plug-in is a container, and not a simple swing component : It's
	 * necessary to define with which component into this container execute
	 * plug-in
	 */
	private JComponent actionComponent;
	/**
	 * actionComponent include his listener. This String define listener related
	 * : "item" or "action"
	 */
	private String typeListener;
	/**
	 * Plug-in developer can specify a different local than his system
	 */
	private String langAndCountry;
	/**
	 * Selected column in table Editor
	 */
	private int selectedColumn;
	/**
	 * Event in table editor
	 */
	private MouseEvent event;

	/**
	 * 
	 * Default Constructor : init i18n plug-in
	 */
	public AbstractPlugIn() {
		getI18n();
	}

	/**
	 * Redefine i18n : defaut language is locale system. Method redefines with
	 * langAndCountry parameter specific 18n for plug-in
	 * 
	 * @param langAndCountry
	 *            : i18n for plug-in
	 */
	public void i18nConfigure(String langAndCountry) {
		delI18n();
		this.langAndCountry = langAndCountry;
		getI18n();
	}

	/**
	 * Remove default i18n for plug-in
	 */
	private void delI18n() {
		I18N.delI18n(null, this.getClass());
	}

	/**
	 * Attribute specific i18n with
	 * 
	 * @param langAndCountry
	 * 
	 */
	public void getI18n() {
		I18N.addI18n(langAndCountry, null, this.getClass());
	}

	/**
	 * 
	 * For table plug-in can be a popup menu on header or row table. Plug-in is
	 * notified of event (header or row event) by Observer (WorkbenchContext) To
	 * identify plug-in on header plug-in receive the number's selected column.
	 * In the other case plug-in receive MouseEvent. MouseEvent contains the
	 * point and then the row selected.
	 * 
	 */
	public void update(Observable o, Object arg) {
		if (arg != null && !(arg instanceof String)) {
			try {
				event = (MouseEvent) arg;
				selectedColumn = -1;
			} catch (Exception e) {
				selectedColumn = (Integer) arg;
			}
		}
		isEnabled();
		isSelected();
	}

	/**
	 * By default PlugIn is not selectable
	 */
	public boolean isSelected() {
		return false;
	}

	/**
	 * Creates plug-in context from Workbench context
	 * 
	 * @param WorkbenchContext
	 */
	public void createPlugInContext(WorkbenchContext context) {
		if (plugInContext == null)
			plugInContext = context.createPlugInContext(this);
		if (!context.getPopupPlugInObservers().contains(this))
			context.getPopupPlugInObservers().add(this);
	}

	/**
	 * 
	 * Add action listener to plug-in
	 * 
	 * @param plugIn
	 * @param workbenchContext
	 * @return implemented plug-in listener
	 */
	public static ActionListener toActionListener(final PlugIn plugIn,
			final WorkbenchContext workbenchContext) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					PlugInContext plugInContext = workbenchContext
							.createPlugInContext();
					plugIn.execute(plugInContext);
				} catch (Exception e) {
					Services.getErrorManager().error(
							"Add Action listener to plugin failed !", e);
				}
			}
		};
	}

	/**
	 * 
	 * Add item listener to plug-in
	 * 
	 * @param plugIn
	 * @param workbenchContext
	 * @return implemented plug-in listener
	 * 
	 */
	public static ItemListener toItemListener(final PlugIn plugIn,
			final WorkbenchContext workbenchContext) {
		return new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent evt) {
				try {
					PlugInContext plugInContext = workbenchContext
							.createPlugInContext();
					plugIn.execute(plugInContext);

				} catch (Exception e) {
					Services.getErrorManager().error(
							"Add Item listener to plugin failed !", e);
				}
			}
		};
	}

	/**
	 * @return plug-in name
	 */
	public String getName() {
		return name == null ? createName(getClass()) : name;
	}

	/**
	 * 
	 * Creates plug-in name from class
	 * 
	 * @param plugInClass
	 * @return plug-in name from class
	 */
	public static String createName(Class plugInClass) {
		return plugInClass.getName();
	}

	/**
	 * 
	 * @return plug-in context
	 */
	protected PlugInContext getPlugInContext() {
		return plugInContext;
	}

	/**
	 * Set plug-in context
	 * 
	 * @param context
	 */
	public void setPlugInContext(PlugInContext context) {
		this.plugInContext = context;
	}

	/**
	 * (if plug-in is a container)
	 * 
	 * @return action component
	 */
	public Component getActionComponent() {
		return actionComponent;
	}

	/**
	 * Set action component (if plug-in is a container)
	 * 
	 * @param actionComponent
	 */
	public void setActionComponent(JComponent actionComponent) {
		this.actionComponent = actionComponent;
	}

	/**
	 * About table editor plug-in
	 * 
	 * @return selected column
	 */
	public int getSelectedColumn() {
		return selectedColumn;
	}

	/**
	 * About table editor plug-in (header/row)
	 * 
	 * @return mouse event
	 */
	public MouseEvent getEvent() {
		return event;
	}

	/**
	 * (if plug-in is a container)
	 * 
	 * @return what listener type
	 */
	public String getTypeListener() {
		return typeListener;
	}

	/**
	 * (if plug-in is a container)
	 * 
	 * @param what
	 *            listener type
	 */
	public void setTypeListener(String typeListener) {
		this.typeListener = typeListener;
	}

}
