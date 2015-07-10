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

package org.orbisgis.orbistoolbox.view;

import org.orbisgis.sif.components.fstree.TreeNodeCustomIcon;
import org.orbisgis.sif.components.fstree.TreeNodePath;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;

/**
 * @author Sylvain PALOMINOS
 **/

public class TreeNodeWps extends DefaultMutableTreeNode implements TreeNodeCustomIcon, TreeNodePath {

    private File file;
    private boolean isValid = true;
    private boolean isRoot = false;

    @Override
    public ImageIcon getLeafIcon() {
        if(isRoot){
            return ToolBoxIcon.getIcon("source");
        }
        else if(isValid) {
            return ToolBoxIcon.getIcon("script");
        }
        else {
            return ToolBoxIcon.getIcon("script_invalid");
        }
    }

    @Override
    public ImageIcon getClosedIcon() {
        if (isRoot) {
            return ToolBoxIcon.getIcon("source");
        } else {
            return ToolBoxIcon.getIcon("closed_folder");
        }
    }

    @Override
    public ImageIcon getOpenIcon() {
        if (isRoot) {
            return ToolBoxIcon.getIcon("source");
        } else {
            return ToolBoxIcon.getIcon("open_folder");
        }
    }

    @Override
    public File getFilePath() {
        return file;
    }

    public void setFilePath(File f){
        file = f;
        this.setUserObject(f.getName().replace(".groovy", ""));
    }

    public void setIsRoot(boolean isRoot){
        this.isRoot = isRoot;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }
}
