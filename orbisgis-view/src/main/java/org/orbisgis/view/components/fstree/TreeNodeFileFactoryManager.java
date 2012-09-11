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
package org.orbisgis.view.components.fstree;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;

/**
 * Manage all kind of Files factory
 * @author Nicolas Fortin
 */
public class TreeNodeFileFactoryManager {
        // Map of extensions
        private Map<String,TreeNodeFileFactory> factories =
                new HashMap<String,TreeNodeFileFactory>();
        public void addFactory(String extension, TreeNodeFileFactory factory) {
                factories.put(extension.toLowerCase(), factory);
        }
       /**
        * @return All registered TreeNodeFileFactory
        */
        public Collection<TreeNodeFileFactory> getFactories() {
                return factories.values();
        }
        
        
        
        public AbstractTreeNode create(File filePath) {
                TreeNodeFileFactory factory = factories.get(FilenameUtils.getExtension(filePath.getName()).toLowerCase());
                if(factory!=null) {
                        return factory.create(filePath);
                } else {
                        return null;
                }
        }
}
