/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.parameter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.xml.bind.JAXBElement;
import net.opengis.fes._2.LiteralType;
import net.opengis.fes._2.ObjectFactory;
import net.opengis.se._2_0.core.ParameterValueType;

/**
 * <code>Literal</code>s are the concrete realizations of <code>SeParameter</code>.
 * While when using <code>ValueReference</code> objects, data will be retrieved
 * from a GDMS table, <code>Literal</code>s will directly embed their datum.</p>
 * <p>A <code>Literal</code> is associated with a list of listeners, in order to 
 * simplify the propagation of changes that could occur in it.
 * @author maxence
 */
public abstract class Literal implements SeParameter {

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
        }

        @Override
        public HashSet<String> dependsOnFeature() {
                return new HashSet<String>();
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
}
