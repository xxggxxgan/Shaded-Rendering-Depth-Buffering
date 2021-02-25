//PA4
//Xiaoxin GAN
//U90812154


import javax.swing.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
//import java.io.File;
//import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

import javax.imageio.ImageIO;
import javax.lang.model.type.PrimitiveType;
//import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;//for new version of gl
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;

import com.jogamp.opengl.util.FPSAnimator;//for new version of gl
import com.sun.swing.internal.plaf.synth.resources.synth_zh_TW;

import apple.laf.JRSUIConstants.Direction;

public class PA4 extends JFrame
		implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 1L;
	private final int DEFAULT_WINDOW_WIDTH = 800;
	private final int DEFAULT_WINDOW_HEIGHT = 800;
	private final float DEFAULT_LINE_WIDTH = 1.0f;

	private GLCapabilities capabilities;
	private GLCanvas canvas;
	private FPSAnimator animator;

	final private int numTestCase;
	private int testCase;
	private BufferedImage buff;
	private ZBuffer Zbuff;
	@SuppressWarnings("unused")
	private ColorType color;
	private int counter = 3;

	
	// specular exponent for materials
	private int ns = 5;
	// this is the direction of variable when we change ka, kd, ks
	private int sign = 1;
	private float fmoveY = 0;
	private float fmoveX = 0;

	private ArrayList<Point2D> lineSegs;
	private ArrayList<Point2D> triangles;
	private int Nsteps;
	private SketchBase sketchBase = new SketchBase();

	/** The quaternion which controls the rotation of the world. */
	private Quaternion viewing_quaternion = new Quaternion();
	/** The quaternion which controls the rotation of the camera. */
	private Quaternion camera_quaternion = new Quaternion();
	/** The quaternion which controls the rotation of the chosen object. */
	private Quaternion[] object_quaternion = new Quaternion[5];

	/** parameter of all kinds of light */
	private Point3D InfiniteLightDirection;
	private Point3D AttenLightDirection;
	private ColorType AmbientLightColor;
	private ColorType InfiniteLightColor;
	private ColorType PointLightColor;
	private Point3D[] PointLightPosition = {new Point3D((DEFAULT_WINDOW_WIDTH / 2), (DEFAULT_WINDOW_HEIGHT / 2), 800),
											new Point3D(0, 0, 800),
											new Point3D(0, 800, 800)};
	private float AttenLightScope;
	private boolean AmbientLight = false;
	private boolean InfiniteLight = true;
	private boolean PointLight = false;
	private boolean AttenLight = false;
	
	// this used in rotete the world
	private Point3D viewing_center = new Point3D((float) (DEFAULT_WINDOW_WIDTH / 2),
			(float) (DEFAULT_WINDOW_HEIGHT / 2), (float) 0.0);
	// this used for camera rotation, all the objects and light rotation according to the position of camera
	private Point3D camera_center = new Point3D((float) (DEFAULT_WINDOW_WIDTH / 2),
			(float) (DEFAULT_WINDOW_HEIGHT / 2), 1000f);
	/** The last x and y coordinates of the mouse press. */
	private int last_x = 0, last_y = 0;
	/** Whether the world is being rotated. */
	private boolean rotate_world = false;
	// Whether dragging an object
	private boolean drag_object = false;
	private boolean Flat = false;
	private boolean Gouraud = true;
	private boolean Phong = false;
	private boolean cameraRotate = false;
	private boolean SpecularTerm = true;
	private boolean DiffuseTerm = true;
	private boolean AmbientTerm = true;

	private BufferedImage EnTexture, BumpTexture;
	public float object_translation_x = 0,object_translation_y = 0,object_translation_z = 0;
	public float camera_translation_x = 0,camera_translation_y = 0,camera_translation_z = 0;
	public PA4() {
		capabilities = new GLCapabilities(null);
		capabilities.setDoubleBuffered(true); // Enable Double buffering

		canvas = new GLCanvas(capabilities);
		canvas.addGLEventListener(this);
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addKeyListener(this);
		canvas.setAutoSwapBufferMode(true); // true by default. Just to be
											// explicit
		canvas.setFocusable(true);
		getContentPane().add(canvas);

		animator = new FPSAnimator(canvas, 60); // drive the display loop @ 60
												// FPS


		numTestCase = 3;
		testCase = 0;
		Nsteps = 24;

		setTitle("CS480");
		setSize(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);

		color = new ColorType(1.0f, 0.0f, 0.0f);
		lineSegs = new ArrayList<Point2D>();
		triangles = new ArrayList<Point2D>();

		try {
			//EnTexture = ImageIO.read(new File("En5.jpg"));
			EnTexture = ImageIO.read(new File("BU.png"));
		} catch (IOException e) {
			System.out.println("Error: reading texture image.");
			e.printStackTrace();
		}

		try {
			//BumpTexture = ImageIO.read(new File("Orange.png"));
			BumpTexture = ImageIO.read(new File("brick_bump.png"));
		} catch (IOException e) {
			System.out.println("Error: reading texture image.");
			e.printStackTrace();
		}

	}

	public void run() {
		animator.start();
	}

	public static void main(String[] args) {
		PA4 P = new PA4();
		P.run();
	}

	// ***********************************************
	// GLEventListener Interfaces
	// ***********************************************
	public void init(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glLineWidth(DEFAULT_LINE_WIDTH);
		Dimension sz = this.getContentPane().getSize();
		buff = new BufferedImage(sz.width, sz.height, BufferedImage.TYPE_3BYTE_BGR);
		Zbuff = new ZBuffer();
		Zbuff.InitialZBuffer();
		clearPixelBuffer();
		Zbuff.CleanZBuffer();
		for (int i = 0; i < 5; i++)
			object_quaternion[i] = new Quaternion();

	}

	// Redisplaying graphics
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		WritableRaster wr = buff.getRaster();
		DataBufferByte dbb = (DataBufferByte) wr.getDataBuffer();
		byte[] data = dbb.getData();

		gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 1);
		gl.glDrawPixels(buff.getWidth(), buff.getHeight(), GL2.GL_BGR, GL2.GL_UNSIGNED_BYTE, ByteBuffer.wrap(data));
		drawTestCase();
	}

	// Window size change
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
		// deliberately left blank
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
		// deliberately left blank
	}

	void clearPixelBuffer() {
		lineSegs.clear();
		triangles.clear();
		Graphics2D g = buff.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, buff.getWidth(), buff.getHeight());
		g.dispose();
	}

	// drawTest
	void drawTestCase() {
		/* clear the window and vertex state */
		clearPixelBuffer();
		Zbuff.CleanZBuffer();

		// System.out.printf("Test case = %d\n",testCase);

		shadeTest();
	}
	public Boolean radial = false, angular = false;
	// ***********************************************
	// KeyListener Interfaces
	// ***********************************************
	public void keyTyped(KeyEvent key) {
		// Q,q: quit
		// C,c: clear polygon (set vertex count=0)
		// R,r: randomly change the color
		// S,s: toggle the smooth shading
		// T,t: show testing examples (toggles between smooth shading and flat
		// shading test cases)
		// >: increase the step number for examples
		// <: decrease the step number for examples
		// +,-: increase or decrease spectral exponent

		switch (key.getKeyChar()) {
		case 'Q':
		case 'q':
			new Thread() {
				public void run() {
					animator.stop();
				}
			}.start();
			System.exit(0);
			break;
		case 'R':
		case 'r':
			viewing_quaternion.reset();
			break;
		case 'C':
		case 'c':
			counter += 1;
			if (counter == 4)
				counter = 0;
			if (counter < 3)
			break;
		case 'T':
		case 't':
			testCase = (testCase + 1) % numTestCase;
			viewing_quaternion.reset();
			drawTestCase();
			break;
		case 'G':
		case 'g':
			Gouraud = true;
			Phong = false;
			Flat = false;
			System.out.println("Gouraud rendering");
			drawTestCase();
			break;
		case 'P':
		case 'p':
			Phong = true;
			Flat = false;
			Gouraud = false;
			System.out.println("Phong rendering");
			drawTestCase();
			break;
		case 'F':
		case 'f':
			Flat = true;
			Gouraud = false;
			Phong = false;
			System.out.println("Flat rendering");
			drawTestCase();
			break;
		case 'm':
		case 'M':
			cameraRotate = !cameraRotate;
			System.out.println("Start rotate camera!");
			break;
		case 'A':
		case 'a':
			AmbientTerm = !AmbientTerm;
			System.out.println("AmbientTerm " + AmbientTerm);
			break;
		case 'D':
		case 'd':
			DiffuseTerm = !DiffuseTerm;
			break;
		case 'S':
		case 's':
			SpecularTerm = !SpecularTerm;
			break;
		
		case 'u':
		case 'U':
			cameraTranslate(1);
			object_translation_y+=5;
			// System.out.println(1);
			break;
		case 'j':
		case 'J':
			cameraTranslate(2);
			object_translation_y-=5;
			// System.out.println(1);
			break;
		case 'h':
		case 'H':
			cameraTranslate(3);
			object_translation_x-=5;
			// System.out.println(3);
			break;
		case 'k':
		case 'K':
			cameraTranslate(4);
			object_translation_x+=5;
			// System.out.println(4);
			break;

		case '1':
			AmbientLight = !AmbientLight;
			System.out.println("AmbientLight"+AmbientLight);
			break;
		case '2':
			InfiniteLight = !InfiniteLight;
			System.out.println("InfiniteLight"+InfiniteLight);
			break;
		case '3':
			PointLight = !PointLight;
			System.out.println("PointLight"+PointLight);
			break;
		case '4':
			AttenLight = !AttenLight;
			//PointLight = !PointLight;
			System.out.println("attunate"+AttenLight);
			break;
		case '0':
			radial = !radial;
			break;	
		case '5':
			ns++;
			drawTestCase();
			break;
		case '6':
			if (ns > 0)
				ns--;
			drawTestCase();
			break;
		case '7':
			sign = -sign;
			System.out.println("sign: "+sign);
			break;
		
	
		
		case '<':
			Nsteps = Nsteps < 4 ? Nsteps : Nsteps / 2;
			System.out.printf("Nsteps = %d \n", Nsteps);
			drawTestCase();
			break;
		case '>':
			Nsteps = Nsteps > 190 ? Nsteps : Nsteps * 2;
			System.out.printf("Nsteps = %d \n", Nsteps);
			drawTestCase();
			break;
		default:
			break;
		}
	}

	public void keyPressed(KeyEvent key) {
		switch (key.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			new Thread() {
				public void run() {
					animator.stop();
				}
			}.start();
			System.exit(0);
			break;
		default:
			break;
		}
	}

	public void keyReleased(KeyEvent key) {
		// deliberately left blank
	}

	// **************************************************
	// MouseListener and MouseMotionListener Interfaces
	// **************************************************
	public void mouseClicked(MouseEvent mouse) {
		// deliberately left blank
	}

	public void mousePressed(MouseEvent mouse) {
		int button = mouse.getButton();
		if (button == MouseEvent.BUTTON1) {
			last_x = mouse.getX();
			last_y = mouse.getY();
			rotate_world = true;
		}

		if (button == MouseEvent.BUTTON3) {
			last_x = mouse.getX();
			last_y = mouse.getY();
			drag_object = true;
		}

	}

	public void mouseReleased(MouseEvent mouse) {
		int button = mouse.getButton();
		if (button == MouseEvent.BUTTON1) {
			rotate_world = false;
		}

		if (button == MouseEvent.BUTTON3) {
			drag_object = false;
		}
	}



	public void mouseMoved(MouseEvent mouse) {
		if (this.cameraRotate) {
			// get the current position of the mouse
			final int x = mouse.getX();
			final int y = mouse.getY();

			// System.out.println(x+","+y);
			// get the change in position from the previous one
			final int dx = x - this.last_x;
			final int dy = y - this.last_y;

			// create a unit vector in the direction of the vector (dy, dx, 0)
			final float magnitude = (float) Math.sqrt(dx * dx + dy * dy);
			if (magnitude > 0.0001) {
				// define axis perpendicular to (dx,-dy,0)
				// use -y because origin is in upper lefthand corner of the
				// window
				final float[] axis = new float[] { -(float) (dy / magnitude), (float) (dx / magnitude), 0 };

				// calculate appropriate quaternion
				final float viewing_delta = 3.1415927f / 180.0f * 0.5f;
				final float s = (float) Math.sin(0.5f * viewing_delta);
				final float c = (float) Math.cos(0.5f * viewing_delta);
				final Quaternion Q = new Quaternion(c, s * axis[0], s * axis[1], s * axis[2]);

				if (counter >= 3) {
					this.camera_quaternion = Q.multiply(this.camera_quaternion);
					// normalize to counteract acccumulating round-off error
					this.camera_quaternion.normalize();
				}

				// save x, y as last x, y
				this.last_x = x;
				this.last_y = y;
				drawTestCase();
			}
		}
	}

	/**
	 * Updates the rotation quaternion as the mouse is dragged.
	 * 
	 * @param mouse
	 *            The mouse drag event object.
	 */
	public void mouseDragged(final MouseEvent mouse) {

		if (this.rotate_world) {
			// get the current position of the mouse
			final int x = mouse.getX();
			final int y = mouse.getY();

			// get the change in position from the previous one
			final int dx = x - this.last_x;
			final int dy = y - this.last_y;

			// create a unit vector in the direction of the vector (dy, dx, 0)
			final float magnitude = (float) Math.sqrt(dx * dx + dy * dy);
			if (magnitude > 0.0001) {
				// define axis perpendicular to (dx,-dy,0)
				// use -y because origin is in upper lefthand corner of the
				// window
				final float[] axis = new float[] { -(float) (dy / magnitude), (float) (dx / magnitude), 0 };

				// calculate appropriate quaternion
				final float viewing_delta = 3.1415927f / 180.0f * 2.5f;
				final float s = (float) Math.sin(0.5f * viewing_delta);
				final float c = (float) Math.cos(0.5f * viewing_delta);
				final Quaternion Q = new Quaternion(c, s * axis[0], s * axis[1], s * axis[2]);

				// when counter >= 3 we could control all objects in a scene 
				if (counter >= 3) {
					this.viewing_quaternion = Q.multiply(this.viewing_quaternion);
					// normalize to counteract acccumulating round-off error
					this.viewing_quaternion.normalize();
				}
				// when counter < 3 we just control the object be chosen
				else {
					this.object_quaternion[counter] = Q.multiply(this.object_quaternion[counter]);
					this.object_quaternion[counter].normalize();
				}

				// save x, y as last x, y
				this.last_x = x;
				this.last_y = y;
				drawTestCase();
			}
		}

		if (this.drag_object) {
			final int x = mouse.getX();
			final int y = mouse.getY();

			// get the change in position from the previous one
			final int dx = x - this.last_x;
			final int dy = y - this.last_y;
			// when counter < 3 we move the object be chosen
//			if (counter < 3) {
//				objectsPosition[testCase][counter].x += dx;
//				objectsPosition[testCase][counter].y += dy;
//			}
			this.last_x = x;
			this.last_y = y;
		}

	}

	public void mouseEntered(MouseEvent mouse) {
		// Deliberately left blank
	}

	public void mouseExited(MouseEvent mouse) {
		// Deliberately left blank
	}

	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	// **************************************************
	// Test Cases
	// Nov 9, 2014 Stan Sclaroff -- removed line and triangle test cases
	// **************************************************

	
	
	void shadeTest() {
		if (testCase == 0) {
			Sphere3D sphere = new Sphere3D(0, 400f+object_translation_x, 400f+object_translation_y, 128f,
					(float) 1.5 * 50f, Nsteps, Nsteps, new ColorType(1.0f, 1.0f, 1.0f), new ColorType(1.9f, 1.3f, 0.1f));
			Torus3D torus = new Torus3D(1, 128f+object_translation_x, 400f+object_translation_y, 128f,
					(float) 0.8 * 50f, (float) 1.25 * 50, Nsteps, Nsteps,
					new ColorType(1.0f, 1.0f, 1.0f), new ColorType(0.0f, 0.5f, 1.9f));
			Ellipse3D ellipse = new Ellipse3D(2, 628f+object_translation_x, 400f+object_translation_y, 128f, (float) 1.5 * 50, (float) 3 * 50,
					(float) 1.5 * 50, Nsteps, Nsteps, new ColorType(1.0f, 1.0f, 1.0f), new ColorType(0.3f, 1.6f, 0.3f));
			Box3D box = new Box3D(2, 400f+object_translation_x, 600f+object_translation_y, 128f,
					(float) 3 * 50f, new ColorType(1.0f, 1.0f, 1.0f), new ColorType(1.7f, 0.5f, 0.8f));

			//define the lights of the scene 
			InfiniteLightColor = new ColorType(1.0f, 1.0f, 1.0f);
			InfiniteLightDirection = new Point3D((float) 0.0, (float) (-1.0 / Math.sqrt(2.0)),
					(float) (1.0 / Math.sqrt(2.0)));

			
			AmbientLightColor = new ColorType(0.06f, 0.09f, 0.03f);
			
			PointLightColor = new ColorType(1f, 1f, 1f);
			AttenLightDirection = new Point3D(1,1,1000);
		    //AttenLightDirection = new Point3D(0,-200,200);
			AttenLightScope = (float) Math.toRadians(999);

			DrawObject(sphere);
			DrawObject(torus);
			DrawObject(ellipse);
			//DrawObject(box);
			

		}

		
		if (testCase == 1) {
			
			Cylinder3D cylinder = new Cylinder3D(0, 128f+object_translation_x, 400f+object_translation_y, 128f, (float) 1.2 * 50, (float) 1.2 * 50, 100,
					Nsteps, Nsteps, new ColorType(1.0f, 1.0f, 1.0f), new ColorType(0.6f, 1f, 1f));
			SuperEllipsoid3D superellipsoid = new SuperEllipsoid3D(1, 628f+object_translation_x, 400f+object_translation_y, 128f, (float) 1.5 * 70f, (float) 1.5 * 70f,
					(float) 1.5 * 70f, (float) 2.5, (float) 2.5, Nsteps, Nsteps,new ColorType(1.0f, 1.0f, 1.0f),
					new ColorType(1f, 1f, 0.8f));
			Box3D box = new Box3D(2, 400f+object_translation_x, 400f+object_translation_y, 128f,
					(float) 2 * 50f, new ColorType(1.0f, 1.0f, 1.0f), new ColorType(0.7f, 0.5f, 0.8f));

			Sphere3D sphere = new Sphere3D(0, 400f+object_translation_x, 200f+object_translation_y, 128f,
					(float) 1.5 * 50f, Nsteps, Nsteps, new ColorType(1.0f, 1.0f, 1.0f), new ColorType(1.1f, 1.3f, 0.1f));
			
			AmbientLightColor = new ColorType(0.04f, 0.02f, 0.03f);
			InfiniteLightColor = new ColorType(0.8f, 0.6f, 1.0f);
			InfiniteLightDirection = new Point3D((float) (1.0 / Math.sqrt(2.0)),(float) 0.0, 
					(float) (1.0 / Math.sqrt(2.0)));

			PointLightColor = new ColorType(1f, 1f, 1f);
			AttenLightDirection = new Point3D(0, 0, -1);
			AttenLightScope = (float) Math.toRadians(30);

			DrawObject(cylinder);
			DrawObject(superellipsoid);
			DrawObject(box);
			DrawObject(sphere);
		}
		


		if (testCase == 2) {
			SuperEllipsoid3D superellipsoid = new SuperEllipsoid3D(1, 400f+object_translation_x, 400f+object_translation_y, 128f, (float) 1.5 * 70f, (float) 1.5 * 70f,
					(float) 4.5 * 70f, (float) 2.5, (float) 4.5, Nsteps, Nsteps,new ColorType(1.0f, 1.0f, 1.0f),
					new ColorType(1f, 1f, 0.8f));

			Torus3D torus = new Torus3D(1, 128f+object_translation_x, 400f+object_translation_y, 128f,
					(float) 0.5 * 50f, (float) 1.25 * 50, Nsteps, Nsteps,
					new ColorType(1.0f, 1.0f, 1.0f), new ColorType(1.0f, 1.5f, 1.9f));
			
			TexEn enMapping = new TexEn(1, 150f+object_translation_x, 400f+object_translation_y, 128f, (float) 1.5 * 70, Nsteps, Nsteps,
					EnTexture);

			TexBump bumpMapping = new TexBump(2,650f+object_translation_x, 400f+object_translation_y, 128f, (float) 1.5 * 70, Nsteps, Nsteps,
					BumpTexture);

			bumpMapping.set_kd(new ColorType(1f, 1f, 0.8f));
			bumpMapping.set_ks(new ColorType(1.0f, 1.0f, 1.0f));

			AmbientLightColor = new ColorType(0.04f, 0.02f, 0.03f);

			InfiniteLightColor = new ColorType(1.0f, 1.0f, 1.0f);
			InfiniteLightDirection = new Point3D((float) (1.0 / Math.sqrt(2.0)),(float) 0.0, 
					(float) (1.0 / Math.sqrt(2.0)));


			PointLightColor = new ColorType(0.8f, 1f, 0.5f);
			AttenLightDirection = new Point3D(0, 0, 1);
			AttenLightScope = (float) Math.toRadians(5);

			DrawObject(superellipsoid);
			DrawObject(bumpMapping);
			DrawObject(torus);
		//	DoTexture(enMapping);
		}
	}

	private void DrawObject(Objects3D O) {
		// view vector is defined along z axis，othorgraphic projection
		// view vector ： calculating specular lighting contribution， backface culling / backface rejection
		// for certain case, view vector is used to calculating specular lighting contribution
		// and view vector2 is used to backface culling / backface rejection for calculating specular lighting contribution 
		Point3D view_vector = new Point3D((float) 0.0, (float) 0.0, (float) 1.0);
		Point3D view_vector2 = new Point3D((float) 0.0, (float) 0.0, (float) 1.0);

		// this is the simulate translation of position of camera, because when camera move, its view vector moves too
		view_vector.y += fmoveY;
		view_vector.x += fmoveX;
		view_vector.normalize();

		Material mats = new Material(O.kd, O.kd, O.ks, ns);
		// control specular term, diffuse term, ambient term
		mats.ambient = AmbientTerm;
		mats.diffuse = DiffuseTerm;
		mats.specular = SpecularTerm;

		// When camera rotation, we change the view vector for looks real
		if (cameraRotate)
			rotateView(camera_quaternion, camera_center, view_vector);

		// initialize the SLight
		AllLight SLight = new AllLight(AmbientLight, AmbientLightColor, InfiniteLight, InfiniteLightDirection,
				InfiniteLightColor, PointLight, AttenLight, AttenLightDirection, PointLightColor, PointLightPosition[testCase],
				AttenLightScope,radial,angular);
		// Instead of rotate camera, we rotate all objects and light in a scene to have equally effect
		if (cameraRotate) {
			SLight.rotateLight(camera_quaternion, camera_center);
		}

		// normal to the plane of a triangle to be used in backface culling / backface rejection
		Point3D triangle_normal = new Point3D();

		//triangle mesh
		Mesh3D mesh;
		// for the rendering method. 1 gouraud  2  flat, 3 phong, 4 texture
		int mode = 0;
		int i, j, n, m;

		// temporary variables for triangle 3D vertices and 3D normals
		Point3D v0, v1, v2, n0, n1, n2;
		// projected triangle, with vertex colors
		Point2D[] tri = { new Point2D(), new Point2D(), new Point2D() };

		mesh = O.mesh;
		n = O.get_n();
		m = O.get_m();
		// rotate the surface's 3D mesh 
		//mesh.rotateMesh(object_quaternion[O.ID], objectsPosition[testCase][O.ID]);
		mesh.rotateMesh(viewing_quaternion, viewing_center);
		mesh.rotateMesh(camera_quaternion, camera_center);

		// case for draw box
		if (n == 4) {
			for (i = 0; i <= m - 1; i++) {
				if (view_vector2.dotProduct(mesh.n[i][0]) <= 0)
					continue;
				// draw the first triangle of a plane
				tri[0].x = (int) mesh.v[i][0].x;
				tri[0].y = (int) mesh.v[i][0].y;
				tri[0].z = (int) mesh.v[i][0].z;
				tri[1].x = (int) mesh.v[i][1].x;
				tri[1].y = (int) mesh.v[i][1].y;
				tri[1].z = (int) mesh.v[i][1].z;
				tri[2].x = (int) mesh.v[i][2].x;
				tri[2].y = (int) mesh.v[i][2].y;
				tri[2].z = (int) mesh.v[i][2].z;

				if (Phong) {
					sketchBase.setPhong(SLight, mats, view_vector, mesh.n[i][0], mesh.n[i][1], mesh.n[i][2]);

					tri[0].c = SLight.applyLight(mats, view_vector, mesh.n[i][0], tri[0]);
					tri[1].c = SLight.applyLight(mats, view_vector, mesh.n[i][1], tri[1]);
					tri[2].c = SLight.applyLight(mats, view_vector, mesh.n[i][2], tri[2]);
					mode = 3;
				}

				else if (Gouraud) {
					// vertex colors for Gouraud shading
					n0 = mesh.n[i][0];
					tri[0].c = SLight.applyLight(mats, view_vector, n0, tri[0]);
					tri[1].c = SLight.applyLight(mats, view_vector, n0, tri[1]);
					tri[2].c = SLight.applyLight(mats, view_vector, n0, tri[2]);
					mode = 1;
				}

				else if (Flat) {
					Point2D everage1 = new Point2D();
					// flat shading: use the normal to the triangle itself
					n0 = mesh.n[i][0];
					everage1.x = (tri[0].x + tri[1].x + tri[2].x) / 3;
					everage1.y = (tri[0].y + tri[1].y + tri[2].y) / 3;
					everage1.z = (tri[0].z + tri[1].z + tri[2].z) / 3;
					tri[2].c = tri[1].c = tri[0].c = SLight.applyLight(mats, view_vector, n0, everage1);
					mode = 2;
				}

				sketchBase.drawTriangle(buff, Zbuff, tri[0], tri[1], tri[2], mode);
				// draw the second part of the plane
				tri[0].x = (int) mesh.v[i][0].x;
				tri[0].y = (int) mesh.v[i][0].y;
				tri[0].z = (int) mesh.v[i][0].z;
				tri[1].x = (int) mesh.v[i][2].x;
				tri[1].y = (int) mesh.v[i][2].y;
				tri[1].z = (int) mesh.v[i][2].z;
				tri[2].x = (int) mesh.v[i][3].x;
				tri[2].y = (int) mesh.v[i][3].y;
				tri[2].z = (int) mesh.v[i][3].z;

				if (Phong) {
					sketchBase.setPhong(SLight, mats, view_vector, mesh.n[i][0], mesh.n[i][2], mesh.n[i][3]);

					tri[0].c = SLight.applyLight(mats, view_vector, mesh.n[i][0], tri[0]);
					tri[1].c = SLight.applyLight(mats, view_vector, mesh.n[i][2], tri[1]);
					tri[2].c = SLight.applyLight(mats, view_vector, mesh.n[i][3], tri[2]);
					mode = 3;
				}

				else if (Gouraud) {
					// vertex colors for Gouraud shading
					n0 = mesh.n[i][0];
					tri[0].c = SLight.applyLight(mats, view_vector, n0, tri[0]);
					tri[1].c = SLight.applyLight(mats, view_vector, n0, tri[1]);
					tri[2].c = SLight.applyLight(mats, view_vector, n0, tri[2]);
					mode = 1;
				}

				else if (Flat) {
					Point2D everage1 = new Point2D();
					// flat shading: use the normal to the triangle itself
					n0 = mesh.n[i][0];
					everage1.x = (tri[0].x + tri[1].x + tri[2].x) / 3;
					everage1.y = (tri[0].y + tri[1].y + tri[2].y) / 3;
					everage1.z = (tri[0].z + tri[1].z + tri[2].z) / 3;
					tri[2].c = tri[1].c = tri[0].c = SLight.applyLight(mats, view_vector, n0, everage1);
					mode = 2;
				}

				sketchBase.drawTriangle(buff, Zbuff, tri[0], tri[1], tri[2], mode);
			}
		}

		else {
			//using vertex colors draw triangles for the current surface this works for Gouraud and flat shading only (not Phong)
			for (i = 0; i < m - 1; ++i) {
				for (j = 0; j < n - 1; ++j) {
					v0 = mesh.v[i][j];
					v1 = mesh.v[i][j + 1];
					v2 = mesh.v[i + 1][j + 1];
					triangle_normal = computeTriangleNormal(v0, v1, v2);

					if (view_vector2.dotProduct(triangle_normal) > 0.0)
					// front-facing triangle?
					{
						tri[0].x = (int) v0.x;
						tri[0].y = (int) v0.y;
						tri[0].z = (int) v0.z;
						tri[1].x = (int) v1.x;
						tri[1].y = (int) v1.y;
						tri[1].z = (int) v1.z;
						tri[2].x = (int) v2.x;
						tri[2].y = (int) v2.y;
						tri[2].z = (int) v2.z;

						if (Phong) {
							sketchBase.setPhong(SLight, mats, view_vector, mesh.n[i][j], mesh.n[i][j + 1],
									mesh.n[i + 1][j + 1]);

							tri[0].c = SLight.applyLight(mats, view_vector, mesh.n[i][j], tri[0]);
							tri[1].c = SLight.applyLight(mats, view_vector, mesh.n[i][j + 1], tri[1]);
							tri[2].c = SLight.applyLight(mats, view_vector, mesh.n[i + 1][j + 1], tri[2]);
							mode = 3;
						}

						else if (Gouraud) {
							// vertex colors for Gouraud shading
							n0 = mesh.n[i][j];
							n1 = mesh.n[i][j + 1];
							n2 = mesh.n[i + 1][j + 1];
							tri[0].c = SLight.applyLight(mats, view_vector, n0, tri[0]);
							tri[1].c = SLight.applyLight(mats, view_vector, n1, tri[1]);
							tri[2].c = SLight.applyLight(mats, view_vector, n2, tri[2]);
							mode = 1;
						}

						else if (Flat) {
							Point2D everage1 = new Point2D();
							// flat shading: use the normal to the triangle
							// itself
							n2 = n1 = n0 = triangle_normal;
							everage1.x = (tri[0].x + tri[1].x + tri[2].x) / 3;
							everage1.y = (tri[0].y + tri[1].y + tri[2].y) / 3;
							everage1.z = (tri[0].z + tri[1].z + tri[2].z) / 3;
							tri[2].c = tri[1].c = tri[0].c = SLight.applyLight(mats, view_vector, triangle_normal,
									everage1);
							mode = 2;
						}

						sketchBase.drawTriangle(buff, Zbuff, tri[0], tri[1], tri[2], mode);
					}

					v0 = mesh.v[i][j];
					v1 = mesh.v[i + 1][j + 1];
					v2 = mesh.v[i + 1][j];
					triangle_normal = computeTriangleNormal(v0, v1, v2);

					if (view_vector2.dotProduct(triangle_normal) > 0.0)
					// front-facing triangle?
					{
						tri[0].x = (int) v0.x;
						tri[0].y = (int) v0.y;
						tri[0].z = (int) v0.z;
						tri[1].x = (int) v1.x;
						tri[1].y = (int) v1.y;
						tri[1].z = (int) v1.z;
						tri[2].x = (int) v2.x;
						tri[2].y = (int) v2.y;
						tri[2].z = (int) v2.z;

						if (Phong) {
							sketchBase.setPhong(SLight, mats, view_vector, mesh.n[i][j], mesh.n[i + 1][j + 1],
									mesh.n[i + 1][j]);
							tri[0].c = SLight.applyLight(mats, view_vector, mesh.n[i][j], tri[0]);
							tri[1].c = SLight.applyLight(mats, view_vector, mesh.n[i + 1][j + 1], tri[1]);
							tri[2].c = SLight.applyLight(mats, view_vector, mesh.n[i + 1][j], tri[2]);
							mode = 3;
						}

						else if (Gouraud) {
							// vertex colors for Gouraud shading
							n0 = mesh.n[i][j];
							n1 = mesh.n[i + 1][j + 1];
							n2 = mesh.n[i + 1][j];
							tri[0].c = SLight.applyLight(mats, view_vector, n0, tri[0]);
							tri[1].c = SLight.applyLight(mats, view_vector, n1, tri[1]);
							tri[2].c = SLight.applyLight(mats, view_vector, n2, tri[2]);
							mode = 1;
						} else if (Flat) {
							Point2D everage = new Point2D();
							// flat shading: use the normal to the triangle
							// itself
							n2 = n1 = n0 = triangle_normal;
							everage.x = (tri[0].x + tri[1].x + tri[2].x) / 3;
							everage.y = (tri[0].y + tri[1].y + tri[2].y) / 3;
							everage.z = (tri[0].z + tri[1].z + tri[2].z) / 3;
							tri[2].c = tri[1].c = tri[0].c = SLight.applyLight(mats, view_vector, triangle_normal,
									everage);
							mode = 2;
						}

						sketchBase.drawTriangle(buff, Zbuff, tri[0], tri[1], tri[2], mode);
					}
				}
			}
		}

	}

	// this is used for draw object with environment texture
//	private void DoTexture(TexEn O) {
//		// normal to the plane of a triangle
//		Point3D triangle_normal = new Point3D();
//		Point3D v0, v1, v2;
//		Mesh3D mesh;
//		Point3D view_vector = new Point3D((float) 0.0, (float) 0.0, (float) 1.0);
//		Point2D[] tri = { new Point2D(), new Point2D(), new Point2D() };
//		// set texture to sketch base
//		sketchBase.setTexture(EnTexture);
//
//		mesh = O.mesh;
//		int n = O.get_n();
//		int m = O.get_m();
//		// rotate the surface's 3D mesh using quaternion
//		mesh.rotateMesh(viewing_quaternion, viewing_center);
//
//		for (int i = 0; i < m - 1; ++i) {
//			for (int j = 0; j < n - 1; ++j) {
//				if (i == 9 && j == 6) {
//					v0 = mesh.v[i][j];
//				}
//				v0 = mesh.v[i][j];
//				v1 = mesh.v[i][j + 1];
//				v2 = mesh.v[i + 1][j + 1];
//				triangle_normal = computeTriangleNormal(v0, v1, v2);
//
//				if (view_vector.dotProduct(triangle_normal) > 0.0) {
//	
//					tri[0].x = (int) v0.x;
//					tri[0].y = (int) v0.y;
//					tri[0].z = (int) v0.z;
//					tri[1].x = (int) v1.x;
//					tri[1].y = (int) v1.y;
//					tri[1].z = (int) v1.z;
//					tri[2].x = (int) v2.x;
//					tri[2].y = (int) v2.y;
//					tri[2].z = (int) v2.z;
//
//					tri[0].u = O.p[i][j].u;
//					tri[0].v = O.p[i][j].v;
//
//					tri[1].u = O.p[i][j + 1].u;
//					tri[1].v = O.p[i][j + 1].v;
//
//					tri[2].u = O.p[i + 1][j + 1].u;
//					tri[2].v = O.p[i + 1][j + 1].v;
//
//					sketchBase.drawTriangle(buff, Zbuff, tri[0], tri[1], tri[2], 4);
//				}
//
//				v0 = mesh.v[i][j];
//				v1 = mesh.v[i + 1][j + 1];
//				v2 = mesh.v[i + 1][j];
//				triangle_normal = computeTriangleNormal(v0, v1, v2);
//
//				if (view_vector.dotProduct(triangle_normal) > 0.0) {
//	
//					tri[0].x = (int) v0.x;
//					tri[0].y = (int) v0.y;
//					tri[0].z = (int) v0.z;
//					tri[1].x = (int) v1.x;
//					tri[1].y = (int) v1.y;
//					tri[1].z = (int) v1.z;
//					tri[2].x = (int) v2.x;
//					tri[2].y = (int) v2.y;
//					tri[2].z = (int) v2.z;
//
//					tri[0].u = O.p[i][j].u;
//					tri[0].v = O.p[i][j].v;
//
//					tri[1].u = O.p[i + 1][j + 1].u;
//					tri[1].v = O.p[i + 1][j + 1].v;
//
//					tri[2].u = O.p[i + 1][j].u;
//					tri[2].v = O.p[i + 1][j].v;
//
//					sketchBase.drawTriangle(buff, Zbuff, tri[0], tri[1], tri[2], 4);
//				}
//			}
//		}
//	}

	// helper method that computes the unit normal to the plane of the triangle
	// degenerate triangles yield normal that is numerically zero
	private Point3D computeTriangleNormal(Point3D v0, Point3D v1, Point3D v2) {
		Point3D e0 = v1.minus(v2);
		Point3D e1 = v0.minus(v2);
		Point3D norm = e0.crossProduct(e1);

		if (norm.magnitude() > 0.000001)
			norm.normalize();
		else // detect degenerate triangle and set its normal to zero
			norm.set((float) 0.0, (float) 0.0, (float) 0.0);

		return norm;
	}

	// help function to rotate view vector when camera rotation
	public void rotateView(Quaternion q, Point3D center, Point3D view_vector) {
		Quaternion q_inv = q.conjugate();

		Quaternion p;
		p = new Quaternion((float) 0.0, view_vector);
		p = q_inv.multiply(p);
		p = p.multiply(q);
		view_vector.x = p.get_v().x;
		view_vector.y = p.get_v().y;
		view_vector.z = p.get_v().z;
	}
	
// help function for camera translate
	public void cameraTranslate(int direction) {
		if (direction == 1) {
			for (int i = 0; i < 3; i++)
			
			PointLightPosition[testCase].y += 5;
			fmoveY -= 0.05;
		}

		if (direction == 2) {
			for (int i = 0; i < 3; i++)
				
			PointLightPosition[testCase].y -= 5;
			fmoveY += 0.05;
		}

		if (direction == 3) {
			for (int i = 0; i < 3; i++)
				
			PointLightPosition[testCase].x -= 5;
			fmoveX += 0.05;
		}

		if (direction == 4) {
			for (int i = 0; i < 3; i++)
				
			PointLightPosition[testCase].x += 5;
			fmoveX -= 0.05;
		}
	}


}