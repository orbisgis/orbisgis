package org.orbisgis.wpsservice.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each Java content interface and Java element interface generated in the
 * model package.
 * An ObjectFactory allows to programatically construct new instances of the Java representation for XML content.
 * The Java representation of XML content can consist of schema derived interfaces and classes representing the
 * binding of schema type definitions, element declarations and model groups.
 * Factory methods for each of these are provided in this class.
 *
 * @author Sylvain PALOMINOS
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _DataStore_QNAME = new QName("http://orbisgis.org", "DataStore");
    private final static QName _DataField_QNAME = new QName("http://orbisgis.org", "DataField");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.wps.v_2_0
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DataStore }
     *
     */
    public DataStore createDataStore() { return new DataStore(); }


    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataStore }{@code >}}
     *
     */
    @XmlElementDecl(namespace="http://orbisgis.org",
            name="DataStore",
            substitutionHeadNamespace="http://www.opengis.net/wps/2.0",
            substitutionHeadName="DataDescription")
    public JAXBElement<DataStore> createDataStore(DataStore dataStore) {
        return new JAXBElement<>(_DataStore_QNAME, DataStore.class, dataStore);
    }

    /**
     * Create an instance of {@link DataField }
     *
     */
    public DataField createDataField() { return new DataField(); }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataField }{@code >}}
     *
     */
    @XmlElementDecl(namespace="http://orbisgis.org",
            name="DataField",
            substitutionHeadNamespace="http://www.opengis.net/wps/2.0",
            substitutionHeadName="DataDescription")
    public JAXBElement<DataField> createDataField(DataField dataField) {
        return new JAXBElement<>(_DataField_QNAME, DataField.class, dataField);
    }
}
