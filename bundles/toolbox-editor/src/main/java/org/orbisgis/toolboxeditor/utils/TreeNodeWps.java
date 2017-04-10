/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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

package org.orbisgis.toolboxeditor.utils;

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
    private URI identifier;
    /** Indicates if the node is a valid folder, script, host ... or not. */
    private boolean isValid = true;
    /** Indicates if the node is a default process from OrbisGIS. */
    private boolean isRemovable = false;
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
                ImageIcon icon = ToolBoxIcon.getIcon(customLeafIconName);
                if(icon != null) {
                    return icon;
                }
            }
            else{
                return ToolBoxIcon.getIcon(customInvalidIconName);
            }
        }
        switch (nodeType) {
            //The localHost is always avaliable and valid.
            case HOST_LOCAL:
                return ToolBoxIcon.getIcon(ToolBoxIcon.LOCALHOST);
            //The distant host can be invalid if it can't be reached.
            case HOST_DISTANT:
                if (isValid) {
                    return ToolBoxIcon.getIcon(ToolBoxIcon.DISTANT_HOST);
                } else {
                    return ToolBoxIcon.getIcon(ToolBoxIcon.DISTANT_HOST_INVALID);
                }
                //If the folder is a leaf, it mean that it doesn't contain any script, so it is invalid.
            case FOLDER:
                return ToolBoxIcon.getIcon(ToolBoxIcon.FOLDER_CLOSED);
            //The the process is invalid if it can't be parsed..
            case PROCESS:
                if (isValid) {
                    return ToolBoxIcon.getIcon(ToolBoxIcon.PROCESS);
                } else {
                    return ToolBoxIcon.getIcon(ToolBoxIcon.PROCESS_INVALID);
                }
                //Else return the error icon.
            default:
                return ToolBoxIcon.getIcon(ToolBoxIcon.ERROR);
        }
    }

    @Override
    public ImageIcon getClosedIcon() {
        //If custom icon were specified, use them
        if(isCustomIcon()){
            if(isValid){
                ImageIcon icon = ToolBoxIcon.getIcon(customLeafIconName);
                if(icon != null) {
                    return icon;
                }
            }
            else{
                return ToolBoxIcon.getIcon(customInvalidIconName);
            }
        }
        switch (nodeType) {
            //The localHost is always avaliable and valid.
            case HOST_LOCAL:
                return ToolBoxIcon.getIcon(ToolBoxIcon.LOCALHOST);
            //The distant host can be invalid if it can't be reached.
            case HOST_DISTANT:
                if (isValid) {
                    return ToolBoxIcon.getIcon(ToolBoxIcon.DISTANT_HOST);
                } else {
                    return ToolBoxIcon.getIcon(ToolBoxIcon.DISTANT_HOST_INVALID);
                }
                //The folder can be invalid if no valid script are found inside.
            case FOLDER:
                if (isValid) {
                    return ToolBoxIcon.getIcon(ToolBoxIcon.FOLDER_CLOSED);
                } else {
                    return ToolBoxIcon.getIcon(ToolBoxIcon.FOLDER_CLOSED);
                }
                //The the process is invalid if it can't be parsed..
            case PROCESS:
                if (isValid) {
                    return ToolBoxIcon.getIcon(ToolBoxIcon.PROCESS);
                } else {
                    return ToolBoxIcon.getIcon(ToolBoxIcon.PROCESS_INVALID);
                }
                //Else return the error icon.
            default:
                return ToolBoxIcon.getIcon(ToolBoxIcon.ERROR);
        }
    }

    @Override
    public ImageIcon getOpenIcon() {
        //If custom icon were specified, use them
        if(isCustomIcon()){
            if(isValid){
                ImageIcon icon = ToolBoxIcon.getIcon(customLeafIconName);
                if(icon != null) {
                    return icon;
                }
            }
            else{
                return ToolBoxIcon.getIcon(customInvalidIconName);
            }
        }
        switch (nodeType) {
            //The localHost is always avaliable and valid.
            case HOST_LOCAL:
                return ToolBoxIcon.getIcon(ToolBoxIcon.LOCALHOST);
            //The distant host can be invalid if it can't be reached.
            case HOST_DISTANT:
                if (isValid) {
                    return ToolBoxIcon.getIcon(ToolBoxIcon.DISTANT_HOST);
                } else {
                    return ToolBoxIcon.getIcon(ToolBoxIcon.DISTANT_HOST_INVALID);
                }
                //The folder can be invalid if no valid script are found inside.
            case FOLDER:
                if (isValid) {
                    return ToolBoxIcon.getIcon(ToolBoxIcon.FOLDER_OPEN);
                } else {
                    return ToolBoxIcon.getIcon(ToolBoxIcon.FOLDER_CLOSED);
                }
                //The the process is invalid if it can't be parsed..
            case PROCESS:
                if (isValid) {
                    return ToolBoxIcon.getIcon(ToolBoxIcon.PROCESS);
                } else {
                    return ToolBoxIcon.getIcon(ToolBoxIcon.PROCESS_INVALID);
                }
                //Else return the error icon.
            default:
                return ToolBoxIcon.getIcon(ToolBoxIcon.ERROR);
        }
    }

    /**
     * Returns the URI of the process represented by the node.
     * The URI value has no impact on the node appearance.
     * @return The URI of the process represented by the node.
     */
    public URI getIdentifier() {
        return identifier;
    }

    /**
     * Sets the URI of the process represented by the node.
     * @param identifier The URI of the process represented by the node.
     */
    public void setIdentifier(URI identifier){
        this.identifier = identifier;
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
        this.customInvalidIconName = ToolBoxIcon.ERROR;
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
        copy.identifier = this.identifier;
        copy.isValid = this.isValid;
        copy.nodeType = this.nodeType;
        copy.userObject = this.userObject;
        copy.customOpenIconName = this.customOpenIconName;
        copy.customClosedIconName = this.customClosedIconName;
        copy.customLeafIconName = this.customLeafIconName;
        copy.customInvalidIconName = this.customInvalidIconName;
        copy.isRemovable = this.isRemovable;

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
     * Tells if this node is a default node from OrbisGIS
     * @return True if the node is a default node from OrbisGIS, false otherwise.
     */
    public boolean isRemovable() {
        return isRemovable;
    }

    /**
     * Sets if the node is a default node from OrbisGIS or not.
     * @param isRemovable True if the node is a default node from OrbisGIS, false otherwise.
     */
    public void setIsRemovable(boolean isRemovable) {
        this.isRemovable = isRemovable;
    }

    /**
     * Enumeration of the available node types.
     */
    public enum NodeType{HOST_LOCAL, HOST_DISTANT, FOLDER, PROCESS}
}
