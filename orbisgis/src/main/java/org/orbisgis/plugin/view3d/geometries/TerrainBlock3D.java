package org.orbisgis.plugin.view3d.geometries;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.nio.FloatBuffer;

import javax.media.jai.PlanarImage;

import org.geotools.coverage.grid.GridCoverage2D;
import org.orbisgis.plugin.view.layerModel.RasterLayer;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.SceneElement;
import com.jme.scene.VBOInfo;
import com.jme.scene.batch.TriangleBatch;
import com.jme.scene.lod.AreaClodMesh;
import com.jme.system.DisplaySystem;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;
import com.jmex.terrain.util.ImageBasedHeightMap;

/**
 * Use this class to create a terrain mesh from a Raster layer or a square image
 * 
 * Carefull : code mostly taken from TerrainBlock.java in jMonkey I swapped y
 * and z axis because javaMonkey used to make a confusion. TODO : re-do all
 * function to swap correctly y and z. TODO : this class needs further testing.
 * Maybe we will need to use TerrainPage because it supports larger terrains.
 * 
 * 
 * @author Samuel CHEMLA
 * 
 */
public class TerrainBlock3D extends AreaClodMesh {

	// size of the block, totalSize is the total size of the heightmap if this
	// block is just a small section of it.
	private int size;

	// the total size of the terrain. (Higher if the block is part of
	// a TerrainPage tree.
	private int totalSize;

	private short quadrant = 1;

	// x/y step (scale for the axis)
	private Vector3f stepScale;

	// use lod or not
	private boolean useClod;

	// center of the block in relation to (0,0,0)
	private Vector2f offset;

	// amount the block has been shifted.
	// Used for texture coordinates.
	private float offsetAmount;

	// heightmap values used to create this block (datas)
	private int[] heightMap;

	private int[] oldHeightMap;

	private static Vector3f calcVec1 = new Vector3f();

	private static Vector3f calcVec2 = new Vector3f();

	private static Vector3f calcVec3 = new Vector3f();

	/**
	 * Build a terrain from an image (usually a png image in grey levels). Only
	 * square images are currently supported. This feature is being developped
	 * by javaMonkey.
	 * 
	 * @param image
	 */
	public TerrainBlock3D(Image image) {
		super(image.toString());
		ImageBasedHeightMap heightMap = new ImageBasedHeightMap(image);
		Vector3f terrainScale = new Vector3f(1, 1, -1);
		heightMap.setHeightScale(0.001f);
		initialize(heightMap.getSize(), terrainScale, heightMap.getHeightMap(),
				new Vector3f(0, 0, 0), false, heightMap.getSize(),
				new Vector2f(), 0f);

		// This rotation is used because i was too tired to set correctly the
		// vertices
		Quaternion rotQuat = new Quaternion();
		float angle = 3.1415f;
		Vector3f axis = new Vector3f(1, 1, 0).normalizeLocal();
		rotQuat.fromAngleNormalAxis(angle, axis);
		setLocalRotation(rotQuat);
	}

	/**
	 * Build a terrain from a raster layer.
	 * 
	 * @param layer
	 */
	public TerrainBlock3D(RasterLayer layer) {
		super(layer.getName());

		GridCoverage2D gcin = (GridCoverage2D) layer.getGridCoverage();
		gcin.geophysics(false);
		PlanarImage plim = (PlanarImage) gcin.getRenderedImage();
		Image miche = Toolkit.getDefaultToolkit().createImage(
				plim.getAsBufferedImage().getSource());

		ImageBasedHeightMap heightMap = new ImageBasedHeightMap(miche);
		Vector3f terrainScale = new Vector3f(1, 1, -1);
		heightMap.setHeightScale(0.001f);

		initialize(heightMap.getSize(), terrainScale, heightMap.getHeightMap(),
				new Vector3f(0, 0, 0), false, heightMap.getSize(),
				new Vector2f(), 0f);

	}

	/**
	 * This was the original constructor of the javaMonkey terrain.
	 * 
	 * @param size
	 * @param stepScale
	 * @param heightMap
	 * @param origin
	 * @param clod
	 * @param totalSize
	 * @param offset
	 * @param offsetAmount
	 */
	private void initialize(int size, Vector3f stepScale, int[] heightMap,
			Vector3f origin, boolean clod, int totalSize, Vector2f offset,
			float offsetAmount) {
		this.useClod = clod;
		this.size = size;
		this.stepScale = stepScale;
		this.totalSize = totalSize;
		this.offsetAmount = offsetAmount;
		this.offset = offset;
		this.heightMap = heightMap;

		// the origin offset of the block
		setLocalTranslation(origin);

		buildVertices();
		buildTextureCoordinates();
		buildNormals();
		buildColors();
		TriangleBatch batch = getBatch(0);

		VBOInfo vbo = new VBOInfo(true);
		batch.setVBOInfo(vbo);

		// Clod : true will use level of detail, false will not.
		if (useClod) {
			this.create(null);
			this.setTrisPerPixel(0.02f);
		}
	}

	public int getType() {
		return (SceneElement.GEOMETRY | SceneElement.TRIMESH | SceneElement.TERRAIN_BLOCK);
	}

	/**
	 * <code>chooseTargetRecord</code> determines which level of detail to
	 * use. If CLOD is not used, the index 0 is always returned.
	 * 
	 * @param r
	 *            the renderer to use for determining the LOD record.
	 * @return the index of the record to use.
	 */
	public int chooseTargetRecord(Renderer r) {
		if (useClod) {
			return super.chooseTargetRecord(r);
		}

		return 0;
	}

	/**
	 * <code>setDetailTexture</code> copies the texture coordinates from the
	 * first texture channel to another channel specified by unit, mulitplying
	 * by the factor specified by repeat so that the texture in that channel
	 * will be repeated that many times across the block.
	 * 
	 * @param unit
	 *            channel to copy coords to
	 * @param repeat
	 *            number of times to repeat the texture across and down the
	 *            block
	 */
	public void setDetailTexture(int unit, int repeat) {
		copyTextureCoords(0, 0, unit, repeat);
	}

	/**
	 * <code>getHeight</code> returns the height of an arbitrary point on the
	 * terrain. If the point is between height point values, the height is
	 * linearly interpolated. This provides smooth height calculations. If the
	 * point provided is not within the bounds of the height map, the NaN float
	 * value is returned (Float.NaN).
	 * 
	 * @param position
	 *            the vector representing the height location to check.
	 * @return the height at the provided location.
	 */
	public float getHeight(Vector2f position) {
		return getHeight(position.x, position.y);
	}

	/**
	 * <code>getHeight</code> returns the height of an arbitrary point on the
	 * terrain. If the point is between height point values, the height is
	 * linearly interpolated. This provides smooth height calculations. If the
	 * point provided is not within the bounds of the height map, the NaN float
	 * value is returned (Float.NaN).
	 * 
	 * @param position
	 *            the vector representing the height location to check. Only the
	 *            x and z values are used.
	 * @return the height at the provided location.
	 */
	public float getHeight(Vector3f position) {
		return getHeight(position.x, position.y);
	}

	/**
	 * <code>getHeight</code> returns the height of an arbitrary point on the
	 * terrain. If the point is between height point values, the height is
	 * linearly interpolated. This provides smooth height calculations. If the
	 * point provided is not within the bounds of the height map, the NaN float
	 * value is returned (Float.NaN).
	 * 
	 * @param x
	 *            the x coordinate to check.
	 * @param y
	 *            the y coordinate to check.
	 * @return the height at the provided location.
	 */
	public float getHeight(float x, float y) {
		x /= stepScale.x;
		y /= stepScale.y;
		float col = FastMath.floor(x);
		float row = FastMath.floor(y);

		if (col < 0 || row < 0 || col >= size - 1 || row >= size - 1) {
			return Float.NaN;
		}
		float intOnX = x - col, intOnY = y - row;

		float topLeft, topRight, bottomLeft, bottomRight;

		int focalSpot = (int) (col + row * size);

		// find the heightmap point closest to this position (but will always
		// be to the left ( < x) and above (< z) of the spot.
		topLeft = heightMap[focalSpot] * stepScale.y;

		// now find the next point to the right of topLeft's position...
		topRight = heightMap[focalSpot + 1] * stepScale.y;

		// now find the next point below topLeft's position...
		bottomLeft = heightMap[focalSpot + size] * stepScale.y;

		// now find the next point below and to the right of topLeft's
		// position...
		bottomRight = heightMap[focalSpot + size + 1] * stepScale.y;

		// Use linear interpolation to find the height.
		return FastMath.LERP(intOnY, FastMath.LERP(intOnX, topLeft, topRight),
				FastMath.LERP(intOnX, bottomLeft, bottomRight));
	}

	/**
	 * <code>getHeightFromWorld</code> returns the height of an arbitrary
	 * point on the terrain when given world coordinates. If the point is
	 * between height point values, the height is linearly interpolated. This
	 * provides smooth height calculations. If the point provided is not within
	 * the bounds of the height map, the NaN float value is returned
	 * (Float.NaN).
	 * 
	 * @param position
	 *            the vector representing the height location to check.
	 * @return the height at the provided location.
	 */
	public float getHeightFromWorld(Vector3f position) {
		Vector3f locationPos = calcVec1.set(position).subtractLocal(
				localTranslation);

		return getHeight(locationPos.x, locationPos.y);
	}

	/**
	 * <code>getSurfaceNormal</code> returns the normal of an arbitrary point
	 * on the terrain. The normal is linearly interpreted from the normals of
	 * the 4 nearest defined points. If the point provided is not within the
	 * bounds of the height map, null is returned.
	 * 
	 * @param position
	 *            the vector representing the location to find a normal at.
	 * @param store
	 *            the Vector3f object to store the result in. If null, a new one
	 *            is created.
	 * @return the normal vector at the provided location.
	 */
	public Vector3f getSurfaceNormal(Vector2f position, Vector3f store) {
		return getSurfaceNormal(position.x, position.y, store);
	}

	/**
	 * <code>getSurfaceNormal</code> returns the normal of an arbitrary point
	 * on the terrain. The normal is linearly interpreted from the normals of
	 * the 4 nearest defined points. If the point provided is not within the
	 * bounds of the height map, null is returned.
	 * 
	 * @param position
	 *            the vector representing the location to find a normal at. Only
	 *            the x and z values are used.
	 * @param store
	 *            the Vector3f object to store the result in. If null, a new one
	 *            is created.
	 * @return the normal vector at the provided location.
	 */
	public Vector3f getSurfaceNormal(Vector3f position, Vector3f store) {
		return getSurfaceNormal(position.x, position.z, store);
	}

	/**
	 * <code>getSurfaceNormal</code> returns the normal of an arbitrary point
	 * on the terrain. The normal is linearly interpreted from the normals of
	 * the 4 nearest defined points. If the point provided is not within the
	 * bounds of the height map, null is returned.
	 * 
	 * @param x
	 *            the x coordinate to check.
	 * @param z
	 *            the z coordinate to check.
	 * @param store
	 *            the Vector3f object to store the result in. If null, a new one
	 *            is created.
	 * @return the normal unit vector at the provided location.
	 */
	public Vector3f getSurfaceNormal(float x, float z, Vector3f store) {
		x /= stepScale.x;
		z /= stepScale.z;
		float col = FastMath.floor(x);
		float row = FastMath.floor(z);

		if (col < 0 || row < 0 || col >= size - 1 || row >= size - 1) {
			return null;
		}
		float intOnX = x - col, intOnZ = z - row;

		if (store == null)
			store = new Vector3f();

		Vector3f topLeft = store, topRight = calcVec1, bottomLeft = calcVec2, bottomRight = calcVec3;

		int focalSpot = (int) (col + row * size);
		TriangleBatch batch = getBatch(0);

		// find the heightmap point closest to this position (but will always
		// be to the left ( < x) and above (< z) of the spot.
		BufferUtils.populateFromBuffer(topLeft, batch.getNormalBuffer(),
				focalSpot);

		// now find the next point to the right of topLeft's position...
		BufferUtils.populateFromBuffer(topRight, batch.getNormalBuffer(),
				focalSpot + 1);

		// now find the next point below topLeft's position...
		BufferUtils.populateFromBuffer(bottomLeft, batch.getNormalBuffer(),
				focalSpot + size);

		// now find the next point below and to the right of topLeft's
		// position...
		BufferUtils.populateFromBuffer(bottomRight, batch.getNormalBuffer(),
				focalSpot + size + 1);

		// Use linear interpolation to find the height.
		topLeft.interpolate(topRight, intOnX);
		bottomLeft.interpolate(bottomRight, intOnX);
		topLeft.interpolate(bottomLeft, intOnZ);
		return topLeft.normalizeLocal();
	}

	/**
	 * <code>buildVertices</code> sets up the vertex and index arrays of the
	 * TriMesh.
	 */
	private void buildVertices() {
		TriangleBatch batch = getBatch(0);
		batch.setVertexCount(heightMap.length);
		batch.setVertexBuffer(BufferUtils.createVector3Buffer(batch
				.getVertexBuffer(), batch.getVertexCount()));
		Vector3f point = new Vector3f();
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {

				point.set(x * stepScale.x, y * stepScale.y, heightMap[x
						+ (y * size)]
						* stepScale.z);
				// System.err.println(point.x +" "+point.y+" "+point.z);
				BufferUtils.setInBuffer(point, batch.getVertexBuffer(),
						(x + (y * size)));
			}
		}

		// set up the indices
		batch.setTriangleQuantity(((size - 1) * (size - 1)) * 2);
		batch.setIndexBuffer(BufferUtils.createIntBuffer(batch
				.getTriangleCount() * 3));

		// go through entire array up to the second to last column.
		for (int i = 0; i < (size * (size - 1)); i++) {
			// we want to skip the top row.
			if (i % ((size * (i / size + 1)) - 1) == 0 && i != 0) {
				continue;
			}
			// set the top left corner.
			batch.getIndexBuffer().put(i);
			// set the bottom right corner.
			batch.getIndexBuffer().put((1 + size) + i);
			// set the top right corner.
			batch.getIndexBuffer().put(1 + i);
			// set the top left corner
			batch.getIndexBuffer().put(i);
			// set the bottom left corner
			batch.getIndexBuffer().put(size + i);
			// set the bottom right corner
			batch.getIndexBuffer().put((1 + size) + i);

		}
	}

	/**
	 * <code>buildTextureCoordinates</code> calculates the texture coordinates
	 * of the terrain.
	 */
	public void buildTextureCoordinates() {
		float offsetX = offset.x + (offsetAmount * stepScale.x);
		float offsetY = offset.y + (offsetAmount * stepScale.y);
		TriangleBatch batch = getBatch(0);

		FloatBuffer texs = BufferUtils.createVector2Buffer(batch
				.getTextureBuffers().get(0), batch.getVertexCount());
		batch.getTextureBuffers().set(0, texs);
		texs.clear();

		batch.getVertexBuffer().rewind();

		System.out.println(batch.getVertexCount() + " size : " + size);

		float[] xtab = new float[batch.getVertexCount()];
		float[] ytab = new float[batch.getVertexCount()];

		for (int i = 0; i < batch.getVertexCount(); i++) {

			xtab[i] = (batch.getVertexBuffer().get() + offsetX)
					/ (stepScale.x * (totalSize - 1));
			ytab[i] = (batch.getVertexBuffer().get() + offsetY)
					/ (stepScale.y * (totalSize - 1));

			// ignore vert z coord.
			batch.getVertexBuffer().get();

			// System.out.println(xx + " " + yy + " " + zz);
		}

		for (int i = 0; i < batch.getVertexCount(); i++) {
			texs.put(xtab[i]);
			texs.put(ytab[batch.getVertexCount() - 1 - i]);

		}
	}

	/**
	 * <code>buildNormals</code> calculates the normals of each vertex that
	 * makes up the block of terrain.
	 */
	private void buildNormals() {
		TriangleBatch batch = getBatch(0);
		batch.setNormalBuffer(BufferUtils.createVector3Buffer(batch
				.getNormalBuffer(), batch.getVertexCount()));
		Vector3f oppositePoint = new Vector3f();
		Vector3f adjacentPoint = new Vector3f();
		Vector3f rootPoint = new Vector3f();
		Vector3f tempNorm = new Vector3f();
		int adj = 0, opp = 0, normalIndex = 0;
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				BufferUtils.populateFromBuffer(rootPoint, batch
						.getVertexBuffer(), normalIndex);
				if (row == size - 1) {
					if (col == size - 1) { // last row, last col
						// up cross left
						adj = normalIndex - size;
						opp = normalIndex - 1;
					} else { // last row, except for last col
						// right cross up
						adj = normalIndex + 1;
						opp = normalIndex - size;
					}
				} else {
					if (col == size - 1) { // last column except for last row
						// left cross down
						adj = normalIndex - 1;
						opp = normalIndex + size;
					} else { // most cases
						// down cross right
						adj = normalIndex + size;
						opp = normalIndex + 1;
					}
				}
				BufferUtils.populateFromBuffer(adjacentPoint, batch
						.getVertexBuffer(), adj);
				BufferUtils.populateFromBuffer(oppositePoint, batch
						.getVertexBuffer(), opp);
				tempNorm.set(adjacentPoint).subtractLocal(rootPoint)
						.crossLocal(oppositePoint.subtractLocal(rootPoint))
						.normalizeLocal();
				BufferUtils.setInBuffer(tempNorm, batch.getNormalBuffer(),
						normalIndex);
				normalIndex++;
			}
		}
	}

	/**
	 * Sets the colors for each vertex to the color white.
	 */
	private void buildColors() {
		setDefaultColor(ColorRGBA.white);
	}

	/**
	 * Returns the height map this terrain block is using.
	 * 
	 * @return This terrain block's height map.
	 */
	public int[] getHeightMap() {
		return heightMap;
	}

	/**
	 * Returns the offset amount this terrain block uses for textures.
	 * 
	 * @return The current offset amount.
	 */
	public float getOffsetAmount() {
		return offsetAmount;
	}

	/**
	 * Returns the step scale that stretches the height map.
	 * 
	 * @return The current step scale.
	 */
	public Vector3f getStepScale() {
		return stepScale;
	}

	/**
	 * Returns the total size of the terrain.
	 * 
	 * @return The terrain's total size.
	 */
	public int getTotalSize() {
		return totalSize;
	}

	/**
	 * Returns the size of this terrain block.
	 * 
	 * @return The current block size.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * If true, the terrain is created as a ClodMesh. This is only usefull as a
	 * call after the default constructor.
	 * 
	 * @param useClod
	 */
	public void setUseClod(boolean useClod) {
		this.useClod = useClod;
	}

	/**
	 * Returns the current offset amount. This is used when building texture
	 * coordinates.
	 * 
	 * @return The current offset amount.
	 */
	public Vector2f getOffset() {
		return offset;
	}

	/**
	 * Sets the value for the current offset amount to use when building texture
	 * coordinates. Note that this does <b>NOT </b> rebuild the terrain at all.
	 * This is mostly used for outside constructors of terrain blocks.
	 * 
	 * @param offset
	 *            The new texture offset.
	 */
	public void setOffset(Vector2f offset) {
		this.offset = offset;
	}

	/**
	 * Returns true if this TerrainBlock was created as a clod.
	 * 
	 * @return True if this terrain block is a clod. False otherwise.
	 */
	public boolean isUseClod() {
		return useClod;
	}

	/**
	 * Sets the size of this terrain block. Note that this does <b>NOT </b>
	 * rebuild the terrain at all. This is mostly used for outside constructors
	 * of terrain blocks.
	 * 
	 * @param size
	 *            The new size.
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * Sets the total size of the terrain . Note that this does <b>NOT </b>
	 * rebuild the terrain at all. This is mostly used for outside constructors
	 * of terrain blocks.
	 * 
	 * @param totalSize
	 *            The new total size.
	 */
	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	/**
	 * Sets the step scale of this terrain block's height map. Note that this
	 * does <b>NOT </b> rebuild the terrain at all. This is mostly used for
	 * outside constructors of terrain blocks.
	 * 
	 * @param stepScale
	 *            The new step scale.
	 */
	public void setStepScale(Vector3f stepScale) {
		this.stepScale = stepScale;
	}

	/**
	 * Sets the offset of this terrain texture map. Note that this does <b>NOT
	 * </b> rebuild the terrain at all. This is mostly used for outside
	 * constructors of terrain blocks.
	 * 
	 * @param offsetAmount
	 *            The new texture offset.
	 */
	public void setOffsetAmount(float offsetAmount) {
		this.offsetAmount = offsetAmount;
	}

	/**
	 * Sets the terrain's height map. Note that this does <b>NOT </b> rebuild
	 * the terrain at all. This is mostly used for outside constructors of
	 * terrain blocks.
	 * 
	 * @param heightMap
	 *            The new height map.
	 */
	public void setHeightMap(int[] heightMap) {
		this.heightMap = heightMap;
	}

	/**
	 * This apply a coeff on z axis and then rebuild the terrain
	 * 
	 * @param scale :
	 *            the coeff to apply
	 */
	public void setHeightMap(float scale) {
		if (scale!=0) {
			float exScale = stepScale.z;
			stepScale.z = scale;
			float coeff = scale / exScale;
			for (int i = 0; i < heightMap.length; i++) {
				heightMap[i] = Math.round(heightMap[i] * coeff);
			}
			updateFromHeightMap();
		}		
	}

	/**
	 * Updates the block's vertices and normals from the current height map
	 * values.
	 */
	public void updateFromHeightMap() {
		if (!hasChanged())
			return;
		TriangleBatch batch = getBatch(0);

		Vector3f point = new Vector3f();
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				point.set(x * stepScale.x, y * stepScale.y, heightMap[x
						+ (y * size)]
						* Math.abs(stepScale.z));
				BufferUtils.setInBuffer(point, batch.getVertexBuffer(),
						(x + (y * size)));
			}
		}
		buildNormals();

		if (batch.getVBOInfo() != null) {
			batch.getVBOInfo().setVBOVertexID(-1);
			batch.getVBOInfo().setVBONormalID(-1);
			DisplaySystem.getDisplaySystem().getRenderer().deleteVBO(
					getVertexBuffer(0));
			DisplaySystem.getDisplaySystem().getRenderer().deleteVBO(
					getNormalBuffer(0));
		}
	}

	/**
	 * <code>setHeightMapValue</code> sets the value of this block's height
	 * map at the given coords
	 * 
	 * @param x
	 * @param y
	 * @param newVal
	 */
	public void setHeightMapValue(int x, int y, int newVal) {
		heightMap[x + (y * size)] = newVal;
	}

	/**
	 * <code>setHeightMapValue</code> adds to the value of this block's height
	 * map at the given coords
	 * 
	 * @param x
	 * @param y
	 * @param toAdd
	 */
	public void addHeightMapValue(int x, int y, int toAdd) {
		heightMap[x + (y * size)] += toAdd;
	}

	/**
	 * <code>setHeightMapValue</code> multiplies the value of this block's
	 * height map at the given coords by the value given.
	 * 
	 * @param x
	 * @param y
	 * @param toMult
	 */
	public void multHeightMapValue(int x, int y, int toMult) {
		heightMap[x + (y * size)] *= toMult;
	}

	protected boolean hasChanged() {
		boolean update = false;
		if (oldHeightMap == null) {
			oldHeightMap = new int[heightMap.length];
			update = true;
		}

		for (int x = 0; x < oldHeightMap.length; x++)
			if (oldHeightMap[x] != heightMap[x] || update) {
				update = true;
				oldHeightMap[x] = heightMap[x];
			}

		return update;
	}

	/**
	 * @return Returns the quadrant.
	 */
	public short getQuadrant() {
		return quadrant;
	}

	/**
	 * @param quadrant
	 *            The quadrant to set.
	 */
	public void setQuadrant(short quadrant) {
		this.quadrant = quadrant;
	}

	public void write(JMEExporter e) throws IOException {
		super.write(e);
		OutputCapsule capsule = e.getCapsule(this);
		capsule.write(size, "size", 0);
		capsule.write(totalSize, "totalSize", 0);
		capsule.write(quadrant, "quadrant", (short) 1);
		capsule.write(stepScale, "stepScale", Vector3f.ZERO);
		capsule.write(useClod, "useClod", false);
		capsule.write(offset, "offset", new Vector2f());
		capsule.write(offsetAmount, "offsetAmount", 0);
		capsule.write(heightMap, "heightMap", null);
		capsule.write(oldHeightMap, "oldHeightMap", null);
	}

	public void read(JMEImporter e) throws IOException {
		super.read(e);
		InputCapsule capsule = e.getCapsule(this);
		size = capsule.readInt("size", 0);
		totalSize = capsule.readInt("totalSize", 0);
		quadrant = capsule.readShort("quadrant", (short) 1);
		stepScale = (Vector3f) capsule.readSavable("stepScale", new Vector3f(
				Vector3f.ZERO));
		useClod = capsule.readBoolean("useClod", false);
		offset = (Vector2f) capsule.readSavable("offset", new Vector2f());
		offsetAmount = capsule.readFloat("offsetAmount", 0);
		heightMap = capsule.readIntArray("heightMap", null);
		oldHeightMap = capsule.readIntArray("oldHeightMap", null);
	}
}