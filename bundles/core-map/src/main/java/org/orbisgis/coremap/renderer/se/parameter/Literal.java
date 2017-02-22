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
package org.orbisgis.coremap.renderer.se.parameter;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import net.opengis.fes._2.LiteralType;
import net.opengis.fes._2.ObjectFactory;
import net.opengis.se._2_0.core.ParameterValueType;
import org.orbisgis.coremap.renderer.se.AbstractSymbolizerNode;
import org.orbisgis.coremap.renderer.se.SymbolizerNode;

/**
 * <code>Literal</code>s are the concrete realizations of <code>SeParameter</code>.
 * While when using <code>ValueReference</code> objects, data will be retrieved
 * from a table, <code>Literal</code>s will directly embed their datum.</p>
 * <p>A <code>Literal</code> is associated with a list of listeners, in order to 
 * simplify the propagation of changes that could occur in it.
 * @author Maxence Laurent
 * @author Bocher Erwan
 */
public abstract class Literal extends AbstractSymbolizerNode implements Comparable, SeParameter {

        private List<LiteralListener> listeners;

        /**
         * Create a new <code>Literal</code>, with an empty list of listeners
         */
        public Literal() {
                listeners = new ArrayList<LiteralListener>();
        }

        /**
         * Add a <code>LiteralListener</code> to the list of listeners.
         * @param l 
         */
        public void register(LiteralListener l) {
                if (!listeners.contains(l)) {
                        listeners.add(l);
                }
        }

        /**
         * Notify a change to the listeners associated to this <code>Literal</code>.
         */
        public void fireChange() {
                for (LiteralListener l : listeners) {
                        l.literalChanged();
                }
            update();
        }

        @Override
        public ParameterValueType getJAXBParameterValueType() {
                ParameterValueType pvt = new ParameterValueType();
                pvt.getContent().add(this.toString());
                return pvt;
        }

        /**
         * As these literals can be seen as independants from ParameterValue, we
         * provide a way to retrieve them as {@code LiteralType} instances.
         * @return
         */
        public LiteralType getJAXBLiteralType() {
                LiteralType t = new LiteralType();
                t.getContent().add(this.toString());
                return t;
        }

        @Override
        public JAXBElement<?> getJAXBExpressionType() {
                LiteralType l = new LiteralType();
                l.getContent().add(this.toString());
                ObjectFactory of = new ObjectFactory();
                return of.createLiteral(l);
        }

        @Override
        public List<SymbolizerNode> getChildren() {
                return new ArrayList<SymbolizerNode>();
        }
}
