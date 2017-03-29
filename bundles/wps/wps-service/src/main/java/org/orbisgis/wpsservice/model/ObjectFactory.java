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
 * @author Erwan Bocher
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _JDBCTable_QNAME = new QName("http://orbisgis.org", "JDBCTable");
    private final static QName _JDBCColumn_QNAME = new QName("http://orbisgis.org", "JDBCColumn");
    private final static QName _JDBCValue_QNAME = new QName("http://orbisgis.org", "JDBCValue");
    private final static QName _Enumeration_QNAME = new QName("http://orbisgis.org", "Enumeration");
    private final static QName _GeometryData_QNAME = new QName("http://orbisgis.org", "Geometry");
    private final static QName _RawData_QNAME = new QName("http://orbisgis.org", "RawData");
    private final static QName _Password_QNAME = new QName("http://orbisgis.org", "Password");
    private final static QName _BoundingBoxData_QNAME = new QName("http://orbisgis.org", "BoundingBoxData");
    private final static QName _TranslatableString_QNAME = new QName("http://orbisgis.org", "TranslatableString");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.wps.v_2_0
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link JDBCTable }
     *
     */
    public JDBCTable createJDBCTable() { return new JDBCTable(); }


    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link JDBCTable }{@code >}}
     *
     * @param JDBCTable
     * @return 
     */
    @XmlElementDecl(namespace="http://orbisgis.org",
            name="JDBCTable",
            substitutionHeadNamespace="http://www.opengis.net/wps/2.0",
            substitutionHeadName="DataDescription")
    public JAXBElement<JDBCTable> createJDBCTable(JDBCTable JDBCTable) {
        return new JAXBElement<>(_JDBCTable_QNAME, JDBCTable.class, JDBCTable);
    }

    /**
     * Create an instance of {@link JDBCColumn }
     *
     * @return 
     */
    public JDBCColumn createJDBCColumn() { return new JDBCColumn(); }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link JDBCColumn }{@code >}}
     *
     * @param jdbcColumn
     * @return 
     */
    @XmlElementDecl(namespace="http://orbisgis.org",
            name="JDBCColumn",
            substitutionHeadNamespace="http://www.opengis.net/wps/2.0",
            substitutionHeadName="DataDescription")
    public JAXBElement<JDBCColumn> createJDBCColumn(JDBCColumn jdbcColumn) {
        return new JAXBElement<>(_JDBCColumn_QNAME, JDBCColumn.class, jdbcColumn);
    }

    /**
     * Create an instance of {@link JDBCValue }
     *
     * @return 
     */
    public JDBCValue createJDBCValue() { return new JDBCValue(); }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link JDBCValue }{@code >}}
     *
     * @param jdbcValue
     * @return 
     */
    @XmlElementDecl(namespace="http://orbisgis.org",
            name="JDBCValue",
            substitutionHeadNamespace="http://www.opengis.net/wps/2.0",
            substitutionHeadName="DataDescription")
    public JAXBElement<JDBCValue> createJDBCValue(JDBCValue jdbcValue) {
        return new JAXBElement<>(_JDBCValue_QNAME, JDBCValue.class, jdbcValue);
    }

    /**
     * Create an instance of {@link Enumeration }
     *
     * @return 
     */
    public Enumeration createEnumeration() { return new Enumeration(); }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Enumeration }{@code >}}
     *
     * @param enumeration
     * @return 
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
     * @return 
     */
    public GeometryData createGeometryData() { return new GeometryData(); }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GeometryData }{@code >}}
     *
     * @param geometryData
     * @return 
     */
    @XmlElementDecl(namespace="http://orbisgis.org",
            name="Geometry",
            substitutionHeadNamespace="http://www.opengis.net/wps/2.0",
            substitutionHeadName="DataDescription")
    public JAXBElement<GeometryData> createGeometryData(GeometryData geometryData) {
        return new JAXBElement<>(_GeometryData_QNAME, GeometryData.class, geometryData);
    }

    /**
     * Create an instance of {@link RawData }
     *
     * @return 
     */
    public RawData createRawData() { return new RawData(); }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RawData }{@code >}}
     *
     * @param rawData
     * @return 
     */
    @XmlElementDecl(namespace="http://orbisgis.org",
            name="RawData",
            substitutionHeadNamespace="http://www.opengis.net/wps/2.0",
            substitutionHeadName="DataDescription")
    public JAXBElement<RawData> createRawData(RawData rawData) {
        return new JAXBElement<>(_RawData_QNAME, RawData.class, rawData);
    }

    /**
     * Create an instance of {@link Password }
     *
     * @return 
     */
    public Password createPassword() { return new Password(); }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Password }{@code >}}
     *
     * @param password
     * @return 
     */
    @XmlElementDecl(namespace="http://orbisgis.org",
            name="Password",
            substitutionHeadNamespace="http://www.opengis.net/wps/2.0",
            substitutionHeadName="DataDescription")
    public JAXBElement<Password> createPassword(Password password) {
        return new JAXBElement<>(_Password_QNAME, Password.class, password);
    }

    /**
     * Create an instance of {@link BoundingBoxData }
     *
     * @return 
     */
    public BoundingBoxData createBoundingBoxData() { return new BoundingBoxData(); }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BoundingBoxData }{@code >}}
     *
     * @param boundingBoxData
     * @return 
     */
    @XmlElementDecl(namespace="http://orbisgis.org",
            name="BoundingBoxData",
            substitutionHeadNamespace="http://www.opengis.net/wps/2.0",
            substitutionHeadName="DataDescription")
    public JAXBElement<BoundingBoxData> createBoundingBoxData(BoundingBoxData boundingBoxData) {
        return new JAXBElement<>(_BoundingBoxData_QNAME, BoundingBoxData.class, boundingBoxData);
    }

    /**
     * Create an instance of {@link TranslatableString }
     *
     * @return 
     */
    public TranslatableString createTranslatableString() { return new TranslatableString(); }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TranslatableString }{@code >}}
     *
     * @param translatableString
     * @return 
     */
    @XmlElementDecl(namespace="http://orbisgis.org",
            name="TranslatableString")
    public JAXBElement<TranslatableString> createTranslatableString(TranslatableString translatableString) {
        return new JAXBElement<>(_TranslatableString_QNAME, TranslatableString.class, translatableString);
    }
}
