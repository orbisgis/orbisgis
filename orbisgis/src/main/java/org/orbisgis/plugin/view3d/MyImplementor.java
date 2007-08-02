package org.orbisgis.plugin.view3d;

import java.awt.Canvas;
import java.awt.Color;
import java.util.prefs.Preferences;

//import jmetest.effects.RenParticleEditor;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.Text;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jmex.awt.SimpleCanvasImpl;

/**
 * Class responsible for implementing the scene
 * 
 */
public class MyImplementor extends SimpleCanvasImpl {

	//private Preferences prefs = Preferences
	//		.userNodeForPackage(RenParticleEditor.class);

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
	
	private Canvas glCanvas;

	public MyImplementor(int width, int height, Canvas glCanvas) {
		super(width, height);
		this.glCanvas = glCanvas;
	}
	
	private ColorRGBA makeColorRGBA(Color color) {
        return new ColorRGBA(color.getRed() / 255f, color.getGreen() / 255f,
                color.getBlue() / 255f, color.getAlpha() / 255f);
    }

	public void simpleSetup() {
		//Color bg = new Color(prefs.getInt("bg_color", 0));
		//renderer.setBackgroundColor(makeColorRGBA(bg));
		cam.setFrustumPerspective(45.0f, (float) glCanvas.getWidth()
				/ (float) glCanvas.getHeight(), 1, 10000);

		Vector3f loc = new Vector3f(0, 850, -850);
		Vector3f left = new Vector3f(1, 0, 0);
		Vector3f up = new Vector3f(0, 0.7071f, 0.7071f);
		Vector3f dir = new Vector3f(0, -0.7071f, 0.7071f);
		cam.setFrame(loc, left, up, dir);

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

		//particleNode = new Node("particles");
		//root.attachChild(particleNode);

		ZBufferState zbuf = renderer.createZBufferState();
		zbuf.setWritable(false);
		zbuf.setEnabled(true);
		zbuf.setFunction(ZBufferState.CF_LEQUAL);

		//particleNode.setRenderState(zbuf);
		//particleNode.updateRenderState();

		fpsNode.updateGeometricState(0, true);
		fpsNode.updateRenderState();

		//createNewSystem();

	};

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