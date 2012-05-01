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
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
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
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.core.ui;

import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JLabel;


import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.DefaultDataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.DefaultMapContext;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.ui.editors.map.tool.ToolManager;
import org.orbisgis.core.ui.editors.map.tools.SelectionTool;

import com.vividsolutions.jts.geom.Envelope;
import org.junit.Assume;
import org.gdms.data.DataSourceFactory;
import org.junit.Before;
import org.orbisgis.core.ApplicationInfo;
import org.orbisgis.core.OrbisGISApplicationInfo;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.background.JobQueue;
import org.orbisgis.core.errorManager.DefaultErrorManager;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.geocognition.DefaultGeocognition;
import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.core.ui.pluginSystem.workbench.OrbisWorkbench;
import org.orbisgis.core.ui.pluginSystem.workbench.OrbisWorkbenchContext;
import org.orbisgis.core.ui.plugins.views.geocognition.GeocognitionView;
import org.orbisgis.core.ui.plugins.views.sqlConsole.language.SQLMetadataManager;
import org.orbisgis.core.ui.windows.mainFrame.OrbisGISFrame;
import org.orbisgis.core.workspace.DefaultWorkspace;
import org.orbisgis.core.workspace.Workspace;
import org.orbisgis.progress.NullProgressMonitor;

public abstract class AbstractToolTest {

        protected DefaultMapContext mapContext;
        protected MapTransform mapTransform;
        protected ToolManager tm;
        protected SelectionTool defaultTool;
        private DefaultDataManager dataManager;

        @Before
        public void setUp() throws Exception {
                // 05/01/2012 AG
                
                // WARNING: this will make your eyes bleed!
                // Nicolas, if you ever read this: all this MUST NOT make it into
                // the OrbisGIS 4.0 UI. Do not bring the Tool tests in the new UI branch.
                // Let them rest in peace... and write new ones ;-)
                // I only fixed this to let the tests pass (as expected) until orbisgis-ui is scrapped.
                
                // all the crap below needs Swing all over the place...
                Assume.assumeTrue(!GraphicsEnvironment.isHeadless());
                
                
                DataSourceFactory dsf = new DataSourceFactory(
                        "src/test/resources/backup", "src/test/resources/backup");
                
                dataManager = new DefaultDataManager(dsf);
                Services.registerService(DataManager.class, "", dataManager);
                DefaultWorkspace wk = new DefaultWorkspace();
                File d = File.createTempFile("og-ui", ".d");
                d.delete();
                d.mkdir();
                wk.setWorkspaceFolder(d.getAbsolutePath());
                d.deleteOnExit();
                
                // This is the modern version of GOTOs and spaghetti code:
                // tightly coupled blocs faking to be separated into "services"
                // that are supposed to work on their own. Obviously they don't.
                // The order of initialization below is somewhat arbitrary: 
                // at least it works for me...
                
                Services.registerService(Workspace.class, "", wk);
                createSource("mixed", TypeFactory.createType(Type.GEOMETRY));
                Services.registerService(ApplicationInfo.class, "", new OrbisGISApplicationInfo());
                Services.registerService(ErrorManager.class, "", new DefaultErrorManager());
                Services.registerService(BackgroundManager.class, "", new JobQueue());
                Services.registerService(Geocognition.class, "", new DefaultGeocognition());
                Services.registerService(SQLMetadataManager.class, "", new SQLMetadataManager());
                OrbisWorkbench wb = new OrbisWorkbench(new OrbisGISFrame());
                OrbisWorkbenchContext wbc = new OrbisWorkbenchContext(wb);
                Services.registerService(OrbisWorkbenchContext.class, "", wbc);
                
                mapContext = new DefaultMapContext();
                mapContext.open(new NullProgressMonitor());
                mapContext.getLayerModel().addLayer(dataManager.createLayer("mixed"));
                mapTransform = new MapTransform();
                mapTransform.setImage(new BufferedImage(100, 100,
                        BufferedImage.TYPE_INT_ARGB));
                mapTransform.setExtent(new Envelope(0, 100, 0, 100));
                defaultTool = new SelectionTool();
                tm = new ToolManager(defaultTool, mapContext, mapTransform,
                        new JLabel());
        }
        
        public void tearDown() {
                mapContext.close(new NullProgressMonitor());
        }

        private void createSource(String name, Type geomType) {
                MemoryDataSetDriver omd = new MemoryDataSetDriver(
                        new String[]{"the_geom"}, new Type[]{geomType});
                dataManager.getSourceManager().register(name, omd);
        }
}
