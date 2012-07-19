/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.geocatalog.sourceWizards.db;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.gdms.data.types.GeometryTypeConstraint;

import org.gdms.data.types.Type;
import org.gdms.driver.TableDescription;
import org.orbisgis.view.icons.OrbisGISIcon;

public class TableTreeCellRenderer extends DefaultTreeCellRenderer {

        private static final long serialVersionUID = -4952164947192738782L;

        @Override
        public Component getTreeCellRendererComponent(JTree tree,
                Object value, boolean selected, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {

                // Get default label with its icon, background and focus colors
                JLabel label = (JLabel) super.getTreeCellRendererComponent(
                        tree, value, selected, expanded, leaf, row, hasFocus);

                if (value instanceof DefaultMutableTreeNode) {
                        Object object = ((DefaultMutableTreeNode) value).getUserObject();

                        // Change label icons

                        if (object instanceof TableNode) {
                                TableNode tableNode = (TableNode) object;
                                label.setIcon(tableNode.getIcon());
                        } else if (object instanceof SchemaNode) {
                                SchemaNode schemaNode = (SchemaNode) object;
                                label.setIcon(schemaNode.getIcon());
                        } else if (object.toString().equals("Tables")) {
                                Icon icon = OrbisGISIcon.getIcon("table");
                                label.setIcon(icon);
                        } else if (object.toString().equals("Views")) {
                                Icon icon = OrbisGISIcon.getIcon("eye");
                                label.setIcon(icon);
                        } // root is the database
                        else if (row == 0) {
                                Icon icon = OrbisGISIcon.getIcon("database");
                                label.setIcon(icon);
                        }


                }
                return label;
        }
}

class SchemaNode {

        private String schemaName;

        public SchemaNode(String schemaName) {
                this.schemaName = schemaName;
        }

        public String getSchemaName() {
                return schemaName;
        }

        public Icon getIcon() {
                return OrbisGISIcon.getIcon("table_multiple");
        }

        @Override
        public String toString() {
                return this.schemaName;
        }
}

class TableNode {

        private TableDescription tableDescription;

        public TableNode(TableDescription tableDescription) {
                this.tableDescription = tableDescription;
        }

        @Override
        public String toString() {
                return tableDescription.getName();

        }

        public Icon getIcon() {

                int geomType = tableDescription.getGeometryType();

                if ((geomType == Type.POLYGON)
                        || (geomType == Type.MULTIPOLYGON)) {
                        return OrbisGISIcon.getIcon("layerpolygon");
                } else if ((geomType == Type.LINESTRING)
                        || (geomType == Type.MULTILINESTRING)) {
                        return OrbisGISIcon.getIcon("layerline");
                } else if ((geomType == Type.POINT)
                        || (geomType == Type.MULTIPOINT)) {
                        return OrbisGISIcon.getIcon("layerpoint");
                }//any other geom type
                else if (geomType != 0) {
                        return OrbisGISIcon.getIcon("layermixe");
                }

                return OrbisGISIcon.getIcon("table");

        }

        public String getName() {
                return tableDescription.getName();
        }

        public boolean isView() {
                if (tableDescription.getType().equals("VIEW")) {
                        return true;
                }
                return false;
        }

        public String getSchema() {
                return tableDescription.getSchema();
        }
}

class ViewNode extends TableNode {

        public ViewNode(TableDescription tableDescription) {
                super(tableDescription);
        }

        @Override
        public Icon getIcon() {
                return OrbisGISIcon.getIcon("eye");
        }
}
