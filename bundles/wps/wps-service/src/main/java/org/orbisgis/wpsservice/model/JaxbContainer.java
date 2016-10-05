package org.orbisgis.wpsservice.model;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * Container class with the JAXB contexts
 *
 * @author Sylvain PALOMINOS
 */
public class JaxbContainer {
    //Make the JaxbContainer constructor private to avoid its instantiation.
    private JaxbContainer() {}
    //The JaxbContext
    public static final JAXBContext JAXBCONTEXT;
    static {
        try {
            JAXBCONTEXT = JAXBContext.newInstance(net.opengis.wps._2_0.ObjectFactory.class,
                    net.opengis.wms.ObjectFactory.class,
                    net.opengis.ows._2.ObjectFactory.class,
                    net.opengis.se._2_0.core.ObjectFactory.class,
                    org.orbisgis.wpsservice.model.ObjectFactory.class,
                    oasis.names.tc.ciq.xsdschema.xal._2.ObjectFactory.class);
        } catch (JAXBException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
}
