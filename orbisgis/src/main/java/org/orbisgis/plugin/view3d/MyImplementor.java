package org.orbisgis.plugin.view3d;

import org.orbisgis.plugin.TempPluginServices;

import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.Text;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jmex.awt.SimpleCanvasImpl;

/**
 * Class responsible for implementing the scene
 * 
 * @author Based on Joshua Slack's original Code
 * @author Samuel CHEMLA
 * 
 */
public class MyImplementor extends SimpleCanvasImpl {

	/**
	 * The root node of our text.
	 */
	protected Node fpsNode;

	/**
	 * Displays all the lovely information at the bottom.
	 */
	protected Text fps;

	/**
	 * This is used to recieve getStatistics calls.
	 */
	protected StringBuffer tempBuffer = new StringBuffer();

	/**
	 * This is used to display print text.
	 */
	protected StringBuffer updateBuffer = new StringBuffer(30);

	private LayerCollectionListener lcl = null;

	public MyImplementor(int width, int height) {
		super(width, height);
		lcl = new LayerCollectionListener(this);
		TempPluginServices.lc.addCollectionListener(lcl);
	}

	public void simpleSetup() {
		cam.setFrustumPerspective(45.0f, (float) width / (float) height, 1,
				10000);
		
		Vector3f location = new Vector3f(0, 0, 850);
		Vector3f left = new Vector3f(0, -1, 0);
		Vector3f up = new Vector3f(-1, 0, 0);
		Vector3f direction = new Vector3f(0, 0, -1f);
		cam.setFrame(location, left, up, direction);

		// Then our font Text object.
		/** This is what will actually have the text at the bottom. */
		fps = Text.createDefaultTextLabel("FPS label");
		fps.setCullMode(SceneElement.CULL_NEVER);
		fps.setTextureCombineMode(TextureState.REPLACE);

		// Finally, a stand alone node (not attached to root on purpose)
		fpsNode = new Node("FPS node");
		fpsNode.setRenderState(fps.getRenderState(RenderState.RS_ALPHA));
		fpsNode.setRenderState(fps.getRenderState(RenderState.RS_TEXTURE));
		fpsNode.attachChild(fps);
		fpsNode.setCullMode(SceneElement.CULL_NEVER);

		renderer.enableStatistics(true);

		GeomUtilities utils = new GeomUtilities();
		Geometry grid = utils.createGrid(50, 100f);
		rootNode.attachChild(grid);
		grid.updateRenderState();

		fpsNode.updateGeometricState(0, true);
		fpsNode.updateRenderState();

        // ---- LIGHTS
        /** Set up a basic, default light. */
        PointLight light = new PointLight();
        light.setDiffuse( new ColorRGBA( 0.75f, 0.75f, 0.75f, 0.75f ) );
        light.setAmbient( new ColorRGBA( 0.5f, 0.5f, 0.5f, 1.0f ) );
        light.setLocation( new Vector3f( 100, 100, 100 ) );
        light.setAttenuate(false);
        light.setEnabled( true );

        LightState lightState;
        
        /** Attach the light to a lightState and the lightState to rootNode. */
        lightState = this.getRenderer().createLightState();
        lightState.setEnabled( true );
        lightState.attach( light );
        rootNode.setRenderState( lightState );

        
        
	}

	public void simpleUpdate() {
		updateBuffer.setLength(0);
		updateBuffer.append("FPS: ").append((int) timer.getFrameRate()).append(
				" - ");
		updateBuffer.append(renderer.getStatistics(tempBuffer));
		/** Send the fps to our fps bar at the bottom. */
		fps.print(updateBuffer);
	}

	@Override
	public void simpleRender() {
		fpsNode.draw(renderer);
		renderer.clearStatistics();
	}

}