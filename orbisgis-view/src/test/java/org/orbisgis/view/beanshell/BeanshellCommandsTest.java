/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * For more information, please consult: <http://www.orbisgis.org/> or contact
 * directly: info_at_ orbisgis.org
 */
package org.orbisgis.view.beanshell;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.util.List;
import org.gdms.data.DataSource;
import org.junit.Test;
import org.orbisgis.view.CoreBaseTest;
import org.orbisgis.view.docking.DockingPanel;
import static org.junit.Assert.*;

/**
 *
 * Unit test of beanshell import commnands
 *
 * @author ebocher
 */
public class BeanshellCommandsTest extends CoreBaseTest {
        
        @Test
        public void beanshellSQLCommand() throws Exception {
                List<DockingPanel> dockingPanels = instance.getDockManager().getPanels();
                for (DockingPanel dockingPanel : dockingPanels) {
                        if (dockingPanel instanceof BeanShellFrame) {
                                BeanShellFrame beanShellFrame = (BeanShellFrame) dockingPanel;
                                BshConsolePanel bshConsolePanel = (BshConsolePanel) beanShellFrame.getComponent();
                                bshConsolePanel.getInterpreter().eval("sql(\"CREATE TABLE result as SELECT 'POINT(10 10)'::geometry;\")");
                                DataSource ds = instance.getMainContext().getDataSourceFactory().getDataSource("result");
                                assertTrue(ds != null);
                                ds.open();
                                assertTrue(ds.getGeometry(0).equalsExact(new GeometryFactory().createPoint(new Coordinate(10, 10))));
                                ds.close();                                
                        }
                }
        }
}
