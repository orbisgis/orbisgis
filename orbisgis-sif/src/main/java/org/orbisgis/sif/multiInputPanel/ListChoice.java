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
package org.orbisgis.sif.multiInputPanel;

import org.orbisgis.sif.common.ContainerItem;
import java.awt.Component;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JList;
import javax.swing.JScrollPane;

/**
 * A JList input.
 * Default value and value is the key of the selected {@link ContainerItem}
 */
public class ListChoice extends JList implements InputType {

        public static final String SEPARATOR = "#";

        /**
         * Generic constructor
         * @param choices List items
         */
        public ListChoice(Object... choices) {
                this(toContainerItems(choices));
        }

        private static ContainerItem[] toContainerItems(Object... choices) {
            ContainerItem[] items = new ContainerItem[choices.length];
            for(int index=0;index<choices.length;index++) {
                items[index] = new ContainerItem<Object>(choices[index],choices[index].toString());
            }
            return items;
        }

        /**
         * Constructor
         * @param items key,label pairs
         */
        public ListChoice(ContainerItem... items) {
            super(items);
        }
        @Override
        public Component getComponent() {
                return new JScrollPane(this);
        }

        @Override
        public String getValue() {
                final Object[] selectedValues = getSelectedValues();
                final StringBuilder sb = new StringBuilder();
                for (Object selectedValue : selectedValues) {
                    if (!(selectedValue instanceof ContainerItem)) {
                        sb.append(selectedValue);
                    } else {
                        sb.append(((ContainerItem) selectedValue).getKey());
                    }
                    sb.append(SEPARATOR);
                }
                return sb.toString();
        }
        private Map<Object,Integer> getItems() {
            Map<Object,Integer> items = new HashMap<Object, Integer>(getModel().getSize());
            for(int i=0;i<getModel().getSize();i++) {
                Object element = getModel().getElementAt(i);
                if(element instanceof ContainerItem) {
                    items.put(((ContainerItem) element).getKey(),i);
                } else {
                    items.put(element,i);
                }
            }
            return items;
        }
        @Override
        public void setValue(String value) {
                if (value!=null) {
                    //Set<Integer> selected = new HashSet<Integer>();
                    String[] content = value.split(SEPARATOR);
                    int[] selected = new int[content.length];
                    int curSelected = 0;
                    Map<Object,Integer> current = getItems();
                    for(String item : content) {
                        int selIndex = current.get(item);
                        if(selIndex!=-1) {
                            selected[curSelected++] = selIndex;
                        }
                    }
                    if(curSelected<selected.length) {
                        selected = Arrays.copyOf(selected,curSelected);
                    }
                    setSelectedIndices(selected);
                } else {
                    clearSelection();
                }

        }
}
