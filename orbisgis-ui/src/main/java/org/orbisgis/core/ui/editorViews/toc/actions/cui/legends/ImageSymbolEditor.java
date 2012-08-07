/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.ui.editorViews.toc.actions.cui.legends;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.orbisgis.core.renderer.symbol.ImageSymbol;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.sif.CRFlowLayout;
import org.orbisgis.core.sif.CarriageReturn;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.SymbolEditorListener;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ISymbolEditor;

public class ImageSymbolEditor extends JPanel implements ISymbolEditor {

	private ImageSymbol symbol;
	private JTextField txtURL;
	private SymbolEditorListener listener;

	public ImageSymbolEditor() {
		this.setLayout(new BorderLayout());

		JPanel pnl = new JPanel();
		CRFlowLayout flowLayout = new CRFlowLayout();
		flowLayout.setAlignment(CRFlowLayout.LEFT);
		pnl.setLayout(flowLayout);
		pnl.setBorder(BorderFactory.createTitledBorder("Source"));
		txtURL = new JTextField(25);
		txtURL.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				updateSymbol();
			}

		});
		pnl.add(txtURL);
		pnl.add(new CarriageReturn());
		JButton btnChooseFile = new JButton("Select file...");
		btnChooseFile.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ImageFileChooser ifc = new ImageFileChooser(
						"Select the image for the symbol");
				if (UIFactory.showDialog(ifc)) {
					try {
						txtURL.setText(ifc.getSelectedFile().toURI().toURL()
								.toString());
						updateSymbol();
					} catch (MalformedURLException e1) {
						throw new RuntimeException("Wrong URL", e1);
					}
				}
			}

		});
		pnl.add(btnChooseFile);

		this.add(pnl, BorderLayout.CENTER);
	}

	private void updateSymbol() {
		try {
			symbol.setImageURL(new URL(txtURL.getText()));
		} catch (MalformedURLException e1) {
		} catch (IOException e1) {
		}
		listener.symbolChanged();
	}

	public boolean accepts(Symbol symbol) {
		return symbol instanceof ImageSymbol;
	}

	public Component getComponent() {
		return this;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public void setSymbol(Symbol symbol) {
		if (symbol instanceof ImageSymbol) {
			this.symbol = (ImageSymbol) symbol;
			txtURL.setText(this.symbol.getImageURL().toString());
		} else {
			throw new RuntimeException("ImageSymbol expected");
		}
	}

	public ISymbolEditor newInstance() {
		return new ImageSymbolEditor();
	}

	public void setSymbolEditorListener(SymbolEditorListener listener) {
		this.listener = listener;
	}
}
