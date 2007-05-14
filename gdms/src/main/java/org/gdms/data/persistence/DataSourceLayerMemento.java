package org.gdms.data.persistence;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Memento of the DataSource layer
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class DataSourceLayerMemento implements Memento {
    private String tableName;

    private String tableAlias;

    private ContentHandler contentHandler;

    /**
     * Creates a new DataSourceLayerMemento.
     * 
     * @param tableName
     *            table name
     * @param tableAlias
     *            table alias
     */
    public DataSourceLayerMemento(String tableName, String tableAlias) {
        this.tableName = tableName;
        this.tableAlias = tableAlias;
    }

    /**
     * Returns the tableAlias.
     * 
     * @return Returns the tableAlias.
     */
    public String getTableAlias() {
        return tableAlias;
    }

    /**
     * Returns the table name.
     * 
     * @return Returns the tableName.
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @see org.gdms.data.persistence.Memento#setContentHandler(org.xml.sax.ContentHandler)
     */
    public void setContentHandler(ContentHandler saxHandler) {
        this.contentHandler = saxHandler;
    }

    /**
     * @see org.gdms.data.persistence.Memento#getXML()
     */
    public void getXML() throws SAXException {
        AttributesImpl atts = new AttributesImpl();
        atts.addAttribute("", "table-name", "table-name", "string", tableName);
        atts.addAttribute("", "table-alias", "table-alias", "string",
                tableAlias);
        contentHandler.startElement("", "table", "table", atts);
        contentHandler.endElement("", "table", "table");
    }
}
