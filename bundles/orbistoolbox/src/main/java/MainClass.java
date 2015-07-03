/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 * <p/>
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 * <p/>
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p/>
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 * <p/>
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

/**
 * Created by sylvain on 02/07/15.
 */

import org.orbisgis.orbistoolbox.controller.ProcessManager;
import org.orbisgis.orbistoolbox.controller.parser.ParserController;
import org.orbisgis.orbistoolbox.model.Process;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sylvain PALOMINOS
 **/

public class MainClass {

    public static void main(String[] args) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        /*ParserController pc = new ParserController();
        Class clazz = pc.parseProcess("/home/sylvain/workspace/orbisgis/bundles/orbistoolbox-api/src/test/ressources/org/orbisgis/orbistoolboxapi/annotations/example0.groovy").getValue();
        Object o = clazz.newInstance();
        Field f = clazz.getDeclaredField("input");
        f.setAccessible(true);
        f.set(o, "123456789");*/
        ProcessManager processManager = new ProcessManager();
        processManager.addLocalSource("/home/sylvain/workspace/orbisgis/bundles/orbistoolbox-api/src/test/ressources/org/orbisgis/orbistoolboxapi/annotations/");
        Map<URI, Object> map = new HashMap<>();
        map.put(URI.create("orbisgis:wps:occurence:literalinput:word"), "1234567890");
        processManager.setProcessData(map, URI.create("orbisgis:wps:example1.groovy:process"));
        processManager.executeProcess(URI.create("orbisgis:wps:example1.groovy:process"));
        int i = 0;
    }
}
