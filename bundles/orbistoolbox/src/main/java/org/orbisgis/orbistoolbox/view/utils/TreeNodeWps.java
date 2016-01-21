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

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.net.URI;

/**
 * Node of the process tree displayed in the ToolBox.
 *
 * A node ha one of those three types : FOLDER, PROCESS, HOST.
 *  - A node with a FOLDER type represent a folder containing processes (Simple interface)
 * or a category of processes (Advanced interface).
 *  - A node with a PROCESS type represent a process (the groovy script).
 *  - A node with a HOST_* type represent a server hosting a WPS service.
 *
 * The TreeNode icon depends on the node type (FOLDER, PROCESS, HOST) but custom icons can be defined thanks to
 * the methods setCustomIcon(...).
 *
 * @author Sylvain PALOMINOS
 **/

public class TreeNodeWps extends DefaultMutableTreeNode implements TreeNodeCustomIcon {

    /** Max length in character of the user object. */
    private static final int MAX_USEROBJECT_LENGTH = 30;

    /** Type of the node. */
    private NodeType nodeType = NodeType.FOLDER;
    /** File or folder name associated to the node. */
    private URI uri;
    /** Indicates if the node is a valid folder, script, host ... or not. */
    private boolean isValid = true;
    /** Name of the icon to use when the node is open. */
    private String customOpenIconName = null;
    /** Name of the icon to use when the node is closed. */
    private String customClosedIconName = null;
    /** Name of the icon to use when the node is a leaf. */
    private String customLeafIconName = null;
    /** Name of the icon to use when the node is a leaf. */
    private String customInvalidIconName = null;

    @Override
    public ImageIcon getLeafIcon() {
        //If custom icon were specified, use them
        if(isCustomIcon()){
            if(isValid){
                return ToolBoxIcon.getIcon(customLeafIconName);
            }
            else{
                return ToolBoxIcon.getIcon(customInvalidIconName);
            }
        }
        else {
            switch (nodeType) {
                //The localHost is always avaliable and valid.
                case HOST_LOCAL:
                    return ToolBoxIcon.getIcon("localhost");
                //The distant host can be invalid if it can't be reached.
                case HOST_DISTANT:
                    if (isValid) {
                        return ToolBoxIcon.getIcon("distanthost");
                    } else {
                        return ToolBoxIcon.getIcon("distanthost_invalid");
                    }
                    //If the folder is a leaf, it mean that it doesn't contain any script, so it is invalid.
                case FOLDER:
                    return ToolBoxIcon.getIcon("folder_closed");
                //The the process is invalid if it can't be parsed..
                case PROCESS:
                    if (isValid) {
                        return ToolBoxIcon.getIcon("script");
                    } else {
                        return ToolBoxIcon.getIcon("script_invalid");
                    }
                    //Else return the error icon.
                default:
                    return ToolBoxIcon.getIcon("error");
            }
        }
    }

    @Override
    public ImageIcon getClosedIcon() {
        //If custom icon were specified, use them
        if(isCustomIcon()){
            if(isValid){
                return ToolBoxIcon.getIcon(customClosedIconName);
            }
            else{
                return ToolBoxIcon.getIcon(customInvalidIconName);
            }
        }
        else {
            switch (nodeType) {
                //The localHost is always avaliable and valid.
                case HOST_LOCAL:
                    return ToolBoxIcon.getIcon("localhost");
                //The distant host can be invalid if it can't be reached.
                case HOST_DISTANT:
                    if (isValid) {
                        return ToolBoxIcon.getIcon("distanthost");
                    } else {
                        return ToolBoxIcon.getIcon("distanthost_invalid");
                    }
                    //The folder can be invalid if no valid script are found inside.
                case FOLDER:
                    if (isValid) {
                        return ToolBoxIcon.getIcon("folder_closed");
                    } else {
                        return ToolBoxIcon.getIcon("folder_closed");
                    }
                    //The the process is invalid if it can't be parsed..
                case PROCESS:
                    if (isValid) {
                        return ToolBoxIcon.getIcon("script");
                    } else {
                        return ToolBoxIcon.getIcon("script_invalid");
                    }
                    //Else return the error icon.
                default:
                    return ToolBoxIcon.getIcon("error");
            }
        }
    }

    @Override
    public ImageIcon getOpenIcon() {
        //If custom icon were specified, use them
        if(isCustomIcon()){
            if(isValid){
                return ToolBoxIcon.getIcon(customOpenIconName);
            }
            else{
                return ToolBoxIcon.getIcon(customInvalidIconName);
            }
        }
        else {
            switch (nodeType) {
                //The localHost is always avaliable and valid.
                case HOST_LOCAL:
                    return ToolBoxIcon.getIcon("localhost");
                //The distant host can be invalid if it can't be reached.
                case HOST_DISTANT:
                    if (isValid) {
                        return ToolBoxIcon.getIcon("distanthost");
                    } else {
                        return ToolBoxIcon.getIcon("distanthost_invalid");
                    }
                    //The folder can be invalid if no valid script are found inside.
                case FOLDER:
                    if (isValid) {
                        return ToolBoxIcon.getIcon("folder_open");
                    } else {
                        return ToolBoxIcon.getIcon("folder_closed");
                    }
                    //The the process is invalid if it can't be parsed..
                case PROCESS:
                    if (isValid) {
                        return ToolBoxIcon.getIcon("script");
                    } else {
                        return ToolBoxIcon.getIcon("script_invalid");
                    }
                    //Else return the error icon.
                default:
                    return ToolBoxIcon.getIcon("error");
            }
        }
    }

    /**
     * Returns the URI represented by the node.
     * The URI value has no impact on the node appearance.
     * @return The URI represented by the node.
     */
    public URI getUri() {
        return uri;
    }

    /**
     * Sets the URI represented by the node.
     * @param uri The URI represented by the node.
     */
    public void setUri(URI uri){
        this.uri = uri;
    }

    /**
     * Sets the node type.
     * If no custom icon are defined, the node type will define the appearance of the node.
     * @param type New NodeType.
     */
    public void setNodeType(NodeType type){
        this.nodeType = type;
    }

    /**
     * Returns the NodeType.
     * @return TheNodeType.
     */
    public NodeType getNodeType(){
        return nodeType;
    }

    /**
     * Sets the validity of the node.
     * If the node is not valid the icon will be set to the invalid version.
     * @param isValid
     */
    public void setValidNode(boolean isValid) {
        this.isValid = isValid;
    }

    /**
     * Tells if the node is valid or not.
     * @return If the node is valid.
     */
    public boolean isValidNode(){
        return isValid;
    }

    /**
     * Gives to the node the customIcon it should use.
     * The icon name should be the icon file name without the extension (image.png => image).
     * The same icon will be used no matter if the node is open, closed or if it is a leaf.
     * But if the node is not valid, it will use the default OrbisToolBox invalid icon.
     * @param iconName Name of the icon to use for this node.
     */
    public void setCustomIcon(String iconName){
        this.customClosedIconName = iconName;
        this.customOpenIconName = iconName;
        this.customLeafIconName = iconName;
        this.customInvalidIconName = "error";
    }

    /**
     * Gives to the node the customIcons it should use.
     * The icon name should be the icon file name without the extension (image.png => image).
     * Each icon has its usage :
     * - customCloseIconName : icon when the node is closed
     * - customOpenIconName : icon when the node is open
     * - customLeafIconName : icon when the node is a leaf
     * - customInvalidIconName : icon when the node is not valid.
     * @param customCloseIconName Name of the icon to use for this node when it is closed.
     * @param customOpenIconName Name of the icon to use for this node when it is open.
     * @param customLeafIconName Name of the icon to use for this node when it is a leaf.
     * @param customInvalidIconName Name of the icon to use for this node when it is not valid.
     */
    public void setCustomIcon(String customCloseIconName,
                              String customOpenIconName,
                              String customLeafIconName,
                              String customInvalidIconName){
        this.customClosedIconName = customCloseIconName;
        this.customOpenIconName = customOpenIconName;
        this.customLeafIconName = customLeafIconName;
        this.customInvalidIconName = customInvalidIconName;
    }

    /**
     * Removes all the custom icon.
     * Once done, the node will use the default icons according to its NodeType.
     */
    public void unsetCusomIcon(){
        this.customClosedIconName = null;
        this.customOpenIconName = null;
        this.customLeafIconName = null;
        this.customInvalidIconName = null;
    }

    /**
     * Tells if the custom icon should be use or not
     * @return True if the custom should be used, false otherwise.
     */
    public boolean isCustomIcon(){
        return this.customClosedIconName != null &&
                this.customOpenIconName != null &&
                this.customLeafIconName != null &&
                this.customInvalidIconName != null;
    }

    /**
     * Does a deep copy of a node.
     * @return The node deep copy.
     */
    public TreeNodeWps deepCopy(){
        TreeNodeWps copy = new TreeNodeWps();
        copy.uri = this.uri;
        copy.isValid = this.isValid;
        copy.nodeType = this.nodeType;
        copy.userObject = this.userObject;
        copy.customOpenIconName = this.customOpenIconName;
        copy.customClosedIconName = this.customClosedIconName;
        copy.customLeafIconName = this.customLeafIconName;
        copy.customInvalidIconName = this.customInvalidIconName;

        return copy;
    }

    @Override
    public void setUserObject(Object userObject){
        String str = userObject.toString();
        if(str.length()>MAX_USEROBJECT_LENGTH){
            str = str.substring(0, MAX_USEROBJECT_LENGTH)+"...";
        }
        super.setUserObject(str);
    }

    /**
     * Enumeration of the available node types.
     */
    public enum NodeType{HOST_LOCAL, HOST_DISTANT, FOLDER, PROCESS}
}
