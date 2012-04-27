/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.core.ui.plugins.views.sqlConsole.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.gdms.sql.function.FunctionManager;
import org.orbisgis.core.ui.components.jlist.OGList;
import org.orbisgis.core.ui.components.text.JButtonTextField;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.plugins.views.geocatalog.TransferableSource;
import org.orbisgis.core.ui.preferences.lookandfeel.images.IconLoader;
import org.orbisgis.utils.I18N;

/**
 * A simple panel to list all GDMS functions.
 * @author ebocher
 */
public class SQLFunctionsPanel extends JPanel implements DragGestureListener,
        DragSourceListener {

        private final DragSource dragSource;
        private final OGList list;
        private final FunctionListModel functionListModel;
        private final JButtonTextField txtFilter;
        private final JLabel functionLabelCount;
        private final JLabel collapsed;
        private final JToolBar east;
        private final FunctionManager functionManager;

        public SQLFunctionsPanel(JPanel panel) {
                this.setLayout(new BorderLayout());
                functionManager = Services.getService(DataManager.class).getDataSourceFactory().getFunctionManager();
                functionListModel = new FunctionListModel();
                txtFilter = new JButtonTextField();
                txtFilter.getDocument().addDocumentListener(new DocumentListener() {

                        @Override
                        public void removeUpdate(DocumentEvent e) {
                                doFilter();
                        }

                        @Override
                        public void insertUpdate(DocumentEvent e) {
                                doFilter();
                        }

                        @Override
                        public void changedUpdate(DocumentEvent e) {
                                doFilter();
                        }
                });

                list = new OGList() {

                        public String getToolTipText(MouseEvent evt) {
                                int index = locationToIndex(evt.getPoint());
                                FunctionElement item = (FunctionElement) getModel().getElementAt(index);
                                String toolTip = functionManager.getFunction(item.getFunctionName()).getDescription();

                                // Return the tool tip text
                                return toolTip;
                        }
                };
                list.setBorder(BorderFactory.createLoweredBevelBorder());
                list.setModel(functionListModel);
                list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                JButton btnCollapse = new JButton();
                btnCollapse.setIcon(IconLoader.getIcon("go-next.png"));
                btnCollapse.setToolTipText(I18N.getString("orbisgis.org.ui.sqlConsole.collapse"));
                btnCollapse.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                                collapse();
                        }
                });
                btnCollapse.setBorderPainted(false);

                east = new JToolBar();
                east.setFloatable(false);
                east.add(txtFilter);
                east.add(btnCollapse);
                east.setOpaque(false);
                this.add(east, BorderLayout.NORTH);
                this.add(new JScrollPane(list), BorderLayout.CENTER);
                FunctionListRenderer functionListRenderer = new FunctionListRenderer();
                list.setCellRenderer(functionListRenderer);

                functionLabelCount = new JLabel(I18N.getString(Names.FUNCTION_PANEL_NUMBER + " = " + functionListModel.getSize()));
                this.add(functionLabelCount, BorderLayout.SOUTH);

                dragSource = DragSource.getDefaultDragSource();
                dragSource.createDefaultDragGestureRecognizer(list,
                        DnDConstants.ACTION_COPY_OR_MOVE, this);

                collapsed = new JLabel(IconLoader.getIcon("go-previous.png"), JLabel.CENTER);
                collapsed.setIconTextGap(20);
                collapsed.setVerticalTextPosition(JLabel.BOTTOM);
                collapsed.setHorizontalTextPosition(JLabel.CENTER);
                collapsed.setToolTipText(I18N.getString("orbisgis.org.ui.sqlConsole.expand"));

                this.add(collapsed, BorderLayout.WEST);
                collapsed.addMouseListener(new MouseAdapter() {

                        @Override
                        public void mouseClicked(MouseEvent e) {
                                if (!list.isVisible()) {
                                        expand();
                                }
                        }
                });

                this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                this.setMinimumSize(new Dimension(100, 40));

                collapse();

        }

        @Override
        public void dragGestureRecognized(DragGestureEvent dge) {
                Transferable dragData = getDragData(dge);
                if (dragData != null) {
                        dragSource.startDrag(dge, DragSource.DefaultMoveDrop, dragData,
                                this);
                }

        }

        public String[] getSelectedSources() {
                Object[] selectedValues = list.getSelectedValues();
                String[] sources = new String[selectedValues.length];
                for (int i = 0; i < sources.length; i++) {
                        FunctionElement functionElement = (FunctionElement) selectedValues[i];
                        sources[i] = functionManager.getFunction(functionElement.getFunctionName()).getSqlOrder();
                }
                return sources;
        }

        public Transferable getDragData(DragGestureEvent dge) {
                String[] sources = getSelectedSources();
                if (sources.length > 0) {
                        return new TransferableSource(sources);
                } else {
                        return null;
                }
        }

        @Override
        public void dragEnter(DragSourceDragEvent dsde) {
        }

        @Override
        public void dragOver(DragSourceDragEvent dsde) {
        }

        @Override
        public void dropActionChanged(DragSourceDragEvent dsde) {
        }

        @Override
        public void dragExit(DragSourceEvent dse) {
        }

        @Override
        public void dragDropEnd(DragSourceDropEvent dsde) {
        }

        private void doFilter() {
                functionListModel.filter(txtFilter.getText());
        }

        private void collapse() {
                SQLFunctionsPanel.this.setPreferredSize(new Dimension(20, 0));
                list.setVisible(false);

                east.setVisible(false);
                collapsed.setVisible(true);
        }

        private void expand() {
                SQLFunctionsPanel.this.setPreferredSize(null);
                list.setVisible(true);
                east.setVisible(true);
                collapsed.setVisible(false);
        }

        private String getVertical(String string) {
                String ret = "<html>";
                for (int i = 0; i < string.length(); i++) {
                        ret += string.charAt(i) + "<br/>";
                }

                return ret + "</html>";
        }
}
