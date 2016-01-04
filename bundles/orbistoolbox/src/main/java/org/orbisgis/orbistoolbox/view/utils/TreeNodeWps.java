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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 * Node of the process tree displayed in the ToolBox.
 *
 * A node ha one of those three types : FOLDER, PROCESS, INSTANCE.
 * A node with a FOLDER type represent a folder containing processes (Simple interface)
 * or a category of processes (Advanced interface).
 * A node with a PROCESS type represent a process (the groovy script).
 * A node with a INSTANCE type represent a instance of a process (a running process).
 * A node with a HOST_* type represent a server hosting a WPS service.
 *
 * This class implements the PropertyChangeListener interface to be able to change dynamically the icon.
 *
 * @author Sylvain PALOMINOS
 **/

//TODO : Listen child node to change the icon to tell that a process instance has finished, crashed ... with a little number.

public class TreeNodeWps
        extends DefaultMutableTreeNode
        implements TreeNodeCustomIcon, TreeNodePath, PropertyChangeListener {

    /** HOST_LOCAL type static string. */
    public static final String HOST_LOCAL = "HOST_LOCAL";
    /** HOST_DISTANT type static string. */
    public static final String HOST_DISTANT = "HOST_DISTANT";
    /** FOLDER type static string. */
    public static final String FOLDER = "FOLDER";
    /** PROCESS type static string. */
    public static final String PROCESS = "PROCESS";
    /** INSTANCE type static string. */
    public static final String INSTANCE = "INSTANCE";

    /** Type of the node. */
    private String nodeType = FOLDER;
    /** File or folder name associated to the node. */
    private File file;
    /** Indicates if the node is a valid folder, script, host ... or not. */
    private boolean isValid = true;
    /** Name of the icon to use for the process instance. It can change according to the process state. */
    private String instanceIconName = "script";

    @Override
    public ImageIcon getLeafIcon() {
        switch(nodeType){
            //The localHost is always avaliable and valid.
            case HOST_LOCAL:
                return ToolBoxIcon.getIcon("localhost");
            //The distant host can be invalid if it can't be reached.
            case HOST_DISTANT:
                if(isValid) {
                    return ToolBoxIcon.getIcon("distanthost");
                }
                else{
                    return ToolBoxIcon.getIcon("distanthost_invalid");
                }
            //If the folder is a leaf, it mean that it doesn't contain any script, so it is invalid.
            case FOLDER:
                return ToolBoxIcon.getIcon("folder_invalid");
            //The the process is invalid if it can't be parsed..
            case PROCESS:
                if(isValid) {
                    return ToolBoxIcon.getIcon("script");
                }
                else{
                    return ToolBoxIcon.getIcon("script_invalid");
                }
            //The sate of the instance is defined thanks to the property change listener.
            case INSTANCE:
                return ToolBoxIcon.getIcon(instanceIconName);
            //Else return the error icon.
            default:
                return ToolBoxIcon.getIcon("error");
        }
    }

    @Override
    public ImageIcon getClosedIcon() {
        switch(nodeType){
            //The localHost is always avaliable and valid.
            case HOST_LOCAL:
                return ToolBoxIcon.getIcon("localhost");
            //The distant host can be invalid if it can't be reached.
            case HOST_DISTANT:
                if(isValid) {
                    return ToolBoxIcon.getIcon("distanthost");
                }
                else{
                    return ToolBoxIcon.getIcon("distanthost_invalid");
                }
            //The folder can be invalid if no valid script are found inside.
            case FOLDER:
                if(isValid) {
                    return ToolBoxIcon.getIcon("folder");
                }
                else{
                    return ToolBoxIcon.getIcon("folder_invalid");
                }
            //The the process is invalid if it can't be parsed..
            case PROCESS:
                if(isValid) {
                    return ToolBoxIcon.getIcon("script");
                }
                else{
                    return ToolBoxIcon.getIcon("script_invalid");
                }
            //Else return the error icon.
            default:
                return ToolBoxIcon.getIcon("error");
        }
    }

    @Override
    public ImageIcon getOpenIcon() {
        switch(nodeType){
            //The localHost is always avaliable and valid.
            case HOST_LOCAL:
                return ToolBoxIcon.getIcon("localhost");
            //The distant host can be invalid if it can't be reached.
            case HOST_DISTANT:
                if(isValid) {
                    return ToolBoxIcon.getIcon("distanthost");
                }
                else{
                    return ToolBoxIcon.getIcon("distanthost_invalid");
                }
            //The folder can be invalid if no valid script are found inside.
            case FOLDER:
                if(isValid) {
                    return ToolBoxIcon.getIcon("folder_open");
                }
                else{
                    return ToolBoxIcon.getIcon("folder_invalid");
                }
            //The the process is invalid if it can't be parsed..
            case PROCESS:
                if(isValid) {
                    return ToolBoxIcon.getIcon("script");
                }
                else{
                    return ToolBoxIcon.getIcon("script_invalid");
                }
            //Else return the error icon.
            default:
                return ToolBoxIcon.getIcon("error");
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

    public void setNodeType(String type){
        nodeType = type;
    }

    public void setValidNode(boolean isValid) {
        this.isValid = isValid;
    }

    public boolean isValidNode(){
        return isValid;
    }

    public TreeNodeWps deepCopy(){
        TreeNodeWps copy = new TreeNodeWps();
        copy.setFilePath(this.file);
        copy.instanceIconName = this.instanceIconName;
        copy.isValid = this.isValid;
        copy.nodeType = this.nodeType;

        return copy;
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        ProcessEditableElement.ProcessState state = (ProcessEditableElement.ProcessState) propertyChangeEvent.getNewValue();
        switch(state){
            case RUNNING:
                instanceIconName = "script_running";
                break;
            case COMPLETED:
                instanceIconName = "script_completed";
                break;
            case ERROR:
                instanceIconName = "script_error";
                break;
            case IDLE:
                instanceIconName = "script";
                break;
        }
    }
}
