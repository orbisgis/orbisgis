package org.gdms.data.persistence;

import java.util.ArrayList;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/**
 * A memento from the operation layer
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class OperationLayerMemento implements Memento {
    private String sql;
    private ArrayList<Memento> mementos = new ArrayList<Memento>();
    private String name;
    private ContentHandler contentHandler;

    /**
     * Creates a new OperationLayerMemento.
     *
     * @param name DataSource name
     * @param mementos mementos of DataSources involved in the operation
     * @param sql sql query of the operation
     */
    public OperationLayerMemento(String name, Memento[] mementos, String sql) {
        this.sql = sql;
        this.mementos.clear();

        for (int i = 0; i < mementos.length; i++) {
            this.mementos.add(mementos[i]);
        }

        this.name = name;
    }

    /**
     * Crea un nuevo OperationLayerMemento.
     *
     * @param name DataSource name
     * @param sql sql that originated the DataSource
     */
    OperationLayerMemento(String name, String sql) {
        this.mementos.clear();
        this.name = name;
        this.sql = sql;
    }

    /**
     * Adds a memento of a DataSource used in the query of this memento
     * DataSource
     *
     * @param m memento
     */
    void addMemento(Memento m) {
        this.mementos.add(m);
    }

    /**
     * Return the number of childs of this memento
     *
     * @return int
     */
    public int getMementoCount() {
        return mementos.size();
    }

    /**
     * Returns the ith memento
     *
     * @param i index of the memento
     *
     * @return Memento
     */
    public Memento getMemento(int i) {
        return (Memento) mementos.get(i);
    }

    /**
     * Returns the SQL that created the DataSource
     *
     * @return Returns the sql.
     */
    public String getSql() {
        return sql;
    }

    /**
     * @see org.gdms.data.persistence.Memento#setContentHandler(org.xml.sax.ContentHandler)
     */
    public void setContentHandler(ContentHandler saxHandler) {
        contentHandler = saxHandler;
    }

    /**
     * @see org.gdms.data.persistence.Memento#getXML()
     */
    public void getXML() throws SAXException {
        AttributesImpl atts = new AttributesImpl();
        atts.addAttribute("", "dataSourceName", "dataSourceName", "string", name);
        atts.addAttribute("", "sql", "sql", "string", sql);
        contentHandler.startElement("", "operation", "operation", atts);

        Memento[] mementos = (Memento[]) this.mementos.toArray(new Memento[0]);

        for (int i = 0; i < mementos.length; i++) {
            mementos[i].setContentHandler(contentHandler);
            mementos[i].getXML();
        }

        contentHandler.endElement("", "operation", "operation");
    }
}
