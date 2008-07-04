package org.orbisgis.renderer.symbol.collection;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.orbisgis.IncompatibleVersionException;
import org.orbisgis.renderer.symbol.Symbol;

public interface SymbolCollection {

	/**
	 * Create a xml file in the specified path (file) with all the values of the
	 * collection
	 *
	 * @param file
	 *
	 * @throws JAXBException
	 * @throws IOException
	 */
	void saveXML() throws JAXBException, IOException;

	/**
	 * Loads the symbol collection described the specified file.
	 *
	 * @throws JAXBException
	 * @throws IOException
	 * @throws IncompatibleVersionException
	 */
	void loadCollection() throws JAXBException, IOException,
			IncompatibleVersionException;

	/**
	 * Adds a new symbol to the collection
	 *
	 * @param sym
	 */
	void addSymbol(Symbol sym);

	/**
	 * Gets the specified index
	 *
	 * @param index
	 * @return
	 */
	Symbol getSymbol(int index);

	/**
	 * Gets the number of symbols in the collection
	 *
	 * @return
	 */
	int getSymbolCount();

}