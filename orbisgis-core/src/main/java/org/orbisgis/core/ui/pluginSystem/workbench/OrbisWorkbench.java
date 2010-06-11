/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *  
 *  Lead Erwan BOCHER, scientific researcher, 
 *
 *  Developer lead : Pierre-Yves FADET, computer engineer. 
 *  
 *  User support lead : Gwendall Petit, geomatic engineer. 
 * 
 * Previous computer developer : Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 * 
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 * 
 * Copyright (C) 2010 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
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
 * For more information, please consult: <http://orbisgis.cerma.archi.fr/>
 * <http://sourcesup.cru.fr/projects/orbisgis/>
 * 
 * or contact directly: 
 * erwan.bocher _at_ ec-nantes.fr 
 * Pierre-Yves.Fadet _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 **/

package org.orbisgis.core.ui.pluginSystem.workbench;

import java.io.File;

import org.orbisgis.core.Services;
import org.orbisgis.core.ui.pluginSystem.PlugInManager;
import org.orbisgis.core.ui.pluginSystem.WorkbenchProperties;
import org.orbisgis.core.ui.windows.mainFrame.OrbisGISFrame;

import com.vividsolutions.jts.util.Assert;
import com.vividsolutions.jts.util.AssertionFailedException;

//create WorkbenchContext
public class OrbisWorkbench {

	private WorkbenchContext context;
	private PlugInManager plugInManager;
	private WorkbenchProperties properties;

	public PlugInManager getPlugInManager() {
		return plugInManager;
	}

	private OrbisGISFrame frame;

	public OrbisWorkbench(OrbisGISFrame frame) {
		context = new OrbisWorkbenchContext(this);
		Services.registerService(WorkbenchContext.class,
				"Gives access to the current WorkbenchContext", this.context);
		this.frame = frame;
	}
	


	public OrbisWorkbench() {
		context = new OrbisWorkbenchContext(this);
		Services.registerService(WorkbenchContext.class,
				"Gives access to the current WorkbenchContext", this.context);
	}

	public void runWorkbench() {
		File extensionsDirectory = new File("lib/ext");
		boolean fileExists = true;
		try {
			Assert.isTrue((extensionsDirectory == null)
	                || extensionsDirectory.isDirectory());
		} catch (AssertionFailedException e) {
			fileExists = false;
			Services.getErrorManager().error("Plugins not loaded");
		}
		/*
		 * File defaultPlugins = new File("default-plugins.xml"); try {
		 * properties = new WorkbenchPropertiesFile(defaultPlugins); } catch
		 * (JDOMException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (IOException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); }
		 */
		OrbisConfiguration setup = new OrbisConfiguration();
		try {			
			if(fileExists)
				plugInManager = new PlugInManager(context, extensionsDirectory);	
			setup.setup(context);
			if( fileExists && plugInManager!=null ) 
				context.getWorkbench().getPlugInManager().load();
				
			context.setLastAction("Orbisgis started");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public OrbisGISFrame getFrame() {
		return frame;
	}

	public WorkbenchContext getWorkbenchContext() {
		return context;
	}

	public WorkbenchProperties getProperties() {
		return properties;
	}
}
