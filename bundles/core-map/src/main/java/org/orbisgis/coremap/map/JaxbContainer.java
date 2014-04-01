package org.orbisgis.coremap.map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * Hold an instance of the JAXBContext, moved from old Services class.
 * @author Nicolas Fortin
 */
public final class JaxbContainer {
    private JaxbContainer() {
    }

    public static final JAXBContext JAXBCONTEXT;
    static {
        try {
            JAXBCONTEXT = JAXBContext.newInstance("net.opengis.ows_context:net.opengis.se._2_0.core:net.opengis.wms:oasis.names.tc.ciq.xsdschema.xal._2");
        } catch (JAXBException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
}
