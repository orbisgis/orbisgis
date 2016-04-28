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
    private final static QName _FieldValue_QNAME = new QName("http://orbisgis.org", "FieldValue");
    private final static QName _Enumeration_QNAME = new QName("http://orbisgis.org", "Enumeration");
    private final static QName _GeometryData_QNAME = new QName("http://orbisgis.org", "Geometry");

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

    /**
     * Create an instance of {@link FieldValue }
     *
     */
    public FieldValue createFieldValue() { return new FieldValue(); }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FieldValue }{@code >}}
     *
     */
    @XmlElementDecl(namespace="http://orbisgis.org",
            name="FieldValue",
            substitutionHeadNamespace="http://www.opengis.net/wps/2.0",
            substitutionHeadName="DataDescription")
    public JAXBElement<FieldValue> createFieldValue(FieldValue fieldValue) {
        return new JAXBElement<>(_FieldValue_QNAME, FieldValue.class, fieldValue);
    }

    /**
     * Create an instance of {@link Enumeration }
     *
     */
    public Enumeration createEnumeration() { return new Enumeration(); }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Enumeration }{@code >}}
     *
     */
    @XmlElementDecl(namespace="http://orbisgis.org",
            name="Enumeration",
            substitutionHeadNamespace="http://www.opengis.net/wps/2.0",
            substitutionHeadName="DataDescription")
    public JAXBElement<Enumeration> createEnumeration(Enumeration enumeration) {
        return new JAXBElement<>(_Enumeration_QNAME, Enumeration.class, enumeration);
    }

    /**
     * Create an instance of {@link GeometryData }
     *
     */
    public GeometryData createGeometryData() { return new GeometryData(); }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Enumeration }{@code >}}
     *
     */
    @XmlElementDecl(namespace="http://orbisgis.org",
            name="Geometry",
            substitutionHeadNamespace="http://www.opengis.net/wps/2.0",
            substitutionHeadName="DataDescription")
    public JAXBElement<GeometryData> createGeometryData(GeometryData geometryData) {
        return new JAXBElement<>(_GeometryData_QNAME, GeometryData.class, geometryData);
    }
}
