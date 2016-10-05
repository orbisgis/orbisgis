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

package org.orbisgis.wpsclient.view.utils.Filter;

import org.orbisgis.sif.components.filter.DefaultActiveFilter;
import org.orbisgis.sif.components.filter.FilterFactory;
import org.orbisgis.sif.components.filter.TextFieldDocumentListener;
import org.orbisgis.wpsclient.view.utils.TreeNodeWps;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.*;

/**
 * FilterFactory for filtering by char sequence contained.
 *
 * @author Sylvain PALOMINOS
 **/

public class SearchFilter implements FilterFactory<IFilter,DefaultActiveFilter> {

    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(SearchFilter.class);

    /**
     * Returns the factory ID.
     * @return Internal name of the filter type
     */
    @Override
    public String getFactoryId() {
        return "search_filter";
    }

    /**
     * Returns the filter label seen by the user.
     * @return The displayable filter label.
     */
    @Override
    public String getFilterLabel() {
        return I18N.tr("Search ");
    }

    /**
     * Returns a TextFilter instance with the filterValue.
     * @param filterValue The new value fired by PropertyChangeEvent
     * @return TextFilter instance.
     */
    @Override
    public IFilter getFilter(DefaultActiveFilter filterValue) {
        return new TextFilter(filterValue.getCurrentFilterValue());
    }


    /**
     * The DataSourceFilterFactory build the component that let the user to
     * define the filter parameters.
     * @param filterValue When the control change the ActiveFilter value must be updated
     * @return The swing component.
     */
    @Override
    public Component makeFilterField(DefaultActiveFilter filterValue) {
        JTextField filterField = new JTextField(filterValue.getCurrentFilterValue());
        //Update the field at each modification
        //The lifetime of the listener has the same lifetime than the ActiveFilter,
        //then this is useless to remove the listener.
        filterField.getDocument().addDocumentListener(new TextFieldDocumentListener(filterField,filterValue));
        return filterField;
    }

    @Override
    public DefaultActiveFilter getDefaultFilterValue() {
        return new DefaultActiveFilter(getFactoryId(), "");
    }

    /**
     * Filter associated to this factory
     */
    private class TextFilter implements IFilter{
        /** Text use to filter **/
        private final String textFilter;
        /**
         * Constructor
         * @param textFilter The filter value
         */
        public TextFilter(String textFilter) {
            this.textFilter = textFilter;
        }

        @Override
        public boolean accepts(TreeNodeWps node) {
            return node.getUserObject().toString().toLowerCase().contains(textFilter);
        }

        @Override
        public boolean acceptsAll() {
            return textFilter.equals("");
        }
    }
}
