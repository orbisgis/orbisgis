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
import org.orbisgis.core.ui.editorViews.toc.actions.cui.SymbolEditorListener;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ISymbolEditor;
import org.orbisgis.sif.CRFlowLayout;
import org.orbisgis.sif.CarriageReturn;
import org.orbisgis.sif.UIFactory;

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
