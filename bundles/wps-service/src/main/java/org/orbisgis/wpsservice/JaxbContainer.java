package org.orbisgis.wpsservice;

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
            JAXBCONTEXT = JAXBContext.newInstance(net.opengis.wps.v_2_0.ObjectFactory.class,
                    net.opengis.ows.v_2_0.ObjectFactory.class,
                    org.orbisgis.wpsservice.model.ObjectFactory.class);
        } catch (JAXBException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
}
