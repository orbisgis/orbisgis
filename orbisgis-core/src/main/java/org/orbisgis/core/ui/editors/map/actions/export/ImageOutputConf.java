package org.orbisgis.core.ui.editors.map.actions.export;

import java.awt.Component;
import java.net.URL;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.orbisgis.core.ui.components.preview.JNumericSpinner;
import org.orbisgis.core.ui.components.preview.SizeSelector;
import org.orbisgis.core.ui.components.preview.UnitSelector;
import org.orbisgis.sif.CRFlowLayout;
import org.orbisgis.sif.CarriageReturn;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;

public class ImageOutputConf extends JPanel implements UIPanel {

	private SizeSelector widthSize;
	private SizeSelector heightSize;
	private JNumericSpinner spnDPI;

	public ImageOutputConf() {
		this.setLayout(new CRFlowLayout(CRFlowLayout.LEFT));
		UnitSelector unitSelector = new UnitSelector();
		this.add(new JLabel("Width:"));
		widthSize = new SizeSelector(5, unitSelector);
		widthSize.setValue(10);
		widthSize.setMin(0);
		this.add(widthSize);
		this.add(unitSelector);
		this.add(new CarriageReturn());
		this.add(new JLabel("Height:"));
		heightSize = new SizeSelector(5, unitSelector);
		heightSize.setValue(10);
		heightSize.setMin(0);
		this.add(heightSize);
		this.add(new CarriageReturn());
		this.add(new JLabel("Resolution (dpi):"));
		spnDPI = new JNumericSpinner(5);
		spnDPI.setInc(1);
		spnDPI.setValue(300);
		spnDPI.setMin(0);
		spnDPI.setNumberFormat(NumberFormat.getIntegerInstance());
		this.add(spnDPI);
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public URL getIconURL() {
		return UIFactory.getDefaultIcon();
	}

	@Override
	public String getInfoText() {
		return "Select the size and resolution of the output image";
	}

	@Override
	public String getTitle() {
		return "Image configuration";
	}

	@Override
	public String initialize() {
		return null;
	}

	@Override
	public String postProcess() {
		return null;
	}

	@Override
	public String validateInput() {
		if ((widthSize.getValue() <= 0) || (heightSize.getValue() <= 0)) {
			return "Image size must be greater than zero";
		}
		double dpi = spnDPI.getValue();
		if (dpi <= 0) {
			return "Image resolution must be greater than zero";
		}
		if (dpi != (int) dpi) {
			return "Image resolution must be an integer";
		}
		return null;
	}

	public double getImageWidth() {
		return widthSize.getValue();
	}

	public double getImageHeight() {
		return heightSize.getValue();
	}

	public int getImageResolution() {
		return (int) spnDPI.getValue();
	}

}
