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

package org.orbisgis.orbistoolbox.view.utils;

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
    private boolean isValidProcess = true;
    private boolean isRoot = false;
    private boolean canBeLeaf = true;
    private boolean isCustomIcon = false;

    private String customIconName;

    @Override
    public ImageIcon getLeafIcon() {
        if(isCustomIcon){
            return ToolBoxIcon.getIcon(customIconName);
        }

        if(!canBeLeaf){
                return ToolBoxIcon.getIcon("remove");
        }
        else {
            if (isRoot) {
                return ToolBoxIcon.getIcon("folder");
            } else if (isValidProcess) {
                return ToolBoxIcon.getIcon("script");
            } else {
                return ToolBoxIcon.getIcon("remove");
            }
        }
    }

    @Override
    public ImageIcon getClosedIcon() {
        if(isCustomIcon){
            return ToolBoxIcon.getIcon(customIconName);
        }

        if (isRoot) {
            return ToolBoxIcon.getIcon("folder");
        } else {
            return ToolBoxIcon.getIcon("folder");
        }
    }

    @Override
    public ImageIcon getOpenIcon() {
        if(isCustomIcon){
            return ToolBoxIcon.getIcon(customIconName);
        }

        if (isRoot) {
            return ToolBoxIcon.getIcon("folder");
        } else {
            return ToolBoxIcon.getIcon("folder_open");
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

    public void setValidProcess(boolean isValid) {
        this.isValidProcess = isValid;
    }

    public void setcanBeLeaf(boolean canBeLeaf){
        this.canBeLeaf = canBeLeaf;
    }

    public boolean canBeLeaf(){
        return canBeLeaf;
    }

    public void setIsCustomIcon(boolean isCustomIcon){
        this.isCustomIcon = isCustomIcon;
    }

    public void setCustomIcon(String customIconName){
        this.customIconName = customIconName;
    }

    public boolean isValidProcess(){
        return isValidProcess;
    }

    public TreeNodeWps deepCopy(){
        TreeNodeWps copy = new TreeNodeWps();
        copy.setFilePath(this.file);
        copy.isRoot = this.isRoot;
        copy.isCustomIcon = this.isCustomIcon;
        copy.customIconName = this.customIconName;
        copy.isValidProcess = this.isValidProcess;
        copy.canBeLeaf = this.canBeLeaf;

        return copy;
    }
}
