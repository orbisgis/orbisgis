package org.orbisgis.core.renderer.symbol;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.RenderPermission;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class ShadingPolygon extends AbstractPolygonSymbol implements StandardPolygonSymbol{




	// position of the sun
	private double azimuthAngle = 315 ;	// in degree clockwise from the North
	private double altitudeAngle = 35 ;	// in degree from ground zero
	Coordinate sunDir = new Coordinate(0,0,0);	// direction of the sun (from Earth)

	//TODO: revoir la definition du contraste et la formule de calcul
	//contrast : 0 none, 1 full
	private double contrast = 0.20 ;



	public double getAzimuthAngle() {
		return azimuthAngle;
	}

	public void setAzimuthAngle(double azimuthAngle) {
		this.azimuthAngle = azimuthAngle;
		setSunDirection();
	}

	public double getAltitudeAngle() {
		return azimuthAngle;
	}

	public void setAltitude(double altitudeAngle) {
		this.altitudeAngle = altitudeAngle;
		setSunDirection();
	}

	private void setSunDirection(){
		// North corresponds to Y axis
		// Est to X axis
		sunDir.x = Math.sin(azimuthAngle * Math.PI/180.0) * Math.cos(altitudeAngle* Math.PI/180.0);
		sunDir.y = Math.cos(azimuthAngle* Math.PI/180.0) * Math.cos(altitudeAngle* Math.PI/180.0);
		sunDir.z = Math.sin(altitudeAngle* Math.PI/180.0);
/*			double norme = Math.sqrt(sunDir.x*sunDir.x + sunDir.y*sunDir.y + sunDir.z*sunDir.z);
		sunDir.x = sunDir.x /norme;
		sunDir.y = sunDir.y /norme;
		sunDir.z = sunDir.z /norme;*/
	}


	// return the normal vector of the triangle defined by the first 3
	// coordinates of the geometry, no test on geometry are made
	private Coordinate NormalOfGeometry(Geometry g) {
		Coordinate n = new Coordinate();
		// vecteurs directeurs du triangle
		Coordinate v1 =  new Coordinate(g.getCoordinates()[1].x - g.getCoordinates()[0].x,
			g.getCoordinates()[1].y - g.getCoordinates()[0].y,
			g.getCoordinates()[1].z - g.getCoordinates()[0].z);
		Coordinate v2 =  new Coordinate(g.getCoordinates()[2].x - g.getCoordinates()[0].x,
			g.getCoordinates()[2].y - g.getCoordinates()[0].y,
			g.getCoordinates()[2].z - g.getCoordinates()[0].z);
		// calcul de la normale par produit vectoriel
		n.x = v1.y*v2.z - v1.z*v2.y;
		n.y = v1.z*v2.x - v1.x*v2.z;
		n.z = v1.x*v2.y - v1.y*v2.x;
		// normage du vecteur
		double norme = Math.sqrt(n.x*n.x + n.y*n.y + n.z*n.z);
		n.x = n.x / norme;
		n.y = n.y / norme;
		n.z = n.z / norme;
		// on veut que la normale pointe vers le ciel
		if (n.z<0) {
			n.x = -n.x;
			n.y = -n.y;
			n.z = -n.z;
		}
		return n;
	}

	private Color calculateShadeColor(Geometry f){
		int alphaValue;
		Coordinate normal = NormalOfGeometry(f);
		double coeff;
		contrast = 0.25;
		coeff = (double)(normal.x*sunDir.x + normal.y*sunDir.y + normal.z*sunDir.z);//*contrast;
		coeff = Math.max(coeff, 0.5 - contrast/2);
		coeff = Math.min(coeff, 0.5 + contrast/2);

		//System.out.println("coeff : " + coeff);

		alphaValue = (int)(255 * (contrast - (coeff -(0.5 - contrast/2)))*1.0/contrast);
		//alphaValue = (int)(255 * ((coeff -(0.5 - contrast/2.0))));
		//alphaValue = (int) (255.0  * (1.0 - coeff));
		//System.out.println("alphaValue : " + alphaValue);
		Color shade = new Color(0,0,0,alphaValue);
		return shade;
/*
//		Luminosit� :
	  R := round(R * ( 1 + intens / 100));
	  G := round(G * ( 1 + intens / 100));
	  B := round(B * ( 1 + intens / 100));

//		Contraste:
	  R := round(R + intens / 100 * (R-127));
	  G := round(G + intens / 100 * (G-127));
	  B := round(B + intens / 100 * (B-127));
*/
	}


	public ShadingPolygon(Color outline, int lineWidth, Color fillColor) {
		super(outline, lineWidth, fillColor);
	}


	@Override
	public Symbol cloneSymbol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Symbol deriveSymbol(Color color) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Envelope draw(Graphics2D g, Geometry geom, AffineTransform at,
			RenderPermission permission) throws DriverException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getClassName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

}
