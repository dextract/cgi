import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.glu.GLU;

/**
 * @author
 */

public class Casa3Dswing implements GLEventListener, KeyListener { 

    private float zoom = 1.0f;
    private float moveSideways = 0.0f;
    private ArrayList<ArrayList<float[]>> faces;
    private HashMap<Integer, float[]> vertices;
    private ArrayList<float[]> texturesVt;
    private float[] minVertices;
    private float[] maxVertices;
    private boolean textureShown;

     GLU glu = new GLU();
   
    public void init(GLAutoDrawable gLDrawable) {
        glDraw = gLDrawable;
        ObjectLoader ol = new ObjectLoader();
        try {
            ol.load(new File("objects/chess-game.obj"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        faces = ol.getFaces();
        vertices = ol.getVertices();
        minVertices = ol.getMinVertices();
        maxVertices = ol.getMaxVertices();
        texturesVt = ol.getTexturesVt();
       
        GL gl = gLDrawable.getGL();
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        glDraw.addKeyListener(this);
    }
   
    public void display(GLAutoDrawable gLDrawable) {
         final GL gl = gLDrawable.getGL();
       
         gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        
        
         int height = gLDrawable.getHeight();
         int width = gLDrawable.getWidth();
         float aspect = (float)width / (float)height;
        
           float px = (minVertices[0] + maxVertices[0]) / 2.0f;
           float py = (minVertices[1] + maxVertices[1]) / 2.0f;
           float pz = (minVertices[2] + maxVertices[2]) / 2.0f;
           float r = (float)Math.sqrt(Math.pow(maxVertices[0]-px,2)+Math.pow(maxVertices[1]-py,2)+Math.pow(maxVertices[2]-pz,2));               
           float fDistance = (float)(r/Math.tan(Math.PI/5));
           
      	 gl.glMatrixMode(GL.GL_PROJECTION);
         gl.glLoadIdentity();
           
         
         // ortogonal
         if(tabOp == 0) {
	         // frente
	         if (width <= height)
	       
	        	 gl.glOrtho(-r*zoom, r*zoom, -r/aspect*zoom, r/aspect*zoom, -r, r);
	         else
	              gl.glOrtho(-r*aspect*zoom, r*aspect*zoom, -r*zoom, r*zoom, -r, r);
	         // planta
	         //gl.glRotatef(90, 1.0f, 0.0f, 0.0f);
	         // esquerda
	         //gl.glRotatef(90, 0.0f, 1.0f, 0.0f);
	         // direita
	         //gl.glRotatef(-90, 0.0f, 1.0f, 0.0f);
        	 
         } else if(tabOp == 1) {	         
	         //obliqua
        	 
	         if (width <= height)
	              gl.glOrtho(-r*zoom, r*zoom, -r/aspect*zoom, r/aspect*zoom, -r, r);
	         else
	              gl.glOrtho(-r*aspect*zoom, r*aspect*zoom, -r*zoom, r*zoom, -r, r);
	         double l = 0.5;
	         double alpha = Math.PI/4;
	         double[] m = {    1,0,-l*Math.cos(alpha),0,
	                         0,1,-l*Math.sin(alpha),0,
	                         0,0,0,0,
	                         0,0,0,1    };
	         gl.glMultTransposeMatrixd(m, 0);
	         
         } else if(tabOp == 2) {
         
         // axonometrica
	         if (width <= height)
	              gl.glOrtho(-r*zoom, r*zoom, -r/aspect*zoom, r/aspect*zoom, -r, r);
	         else
	              gl.glOrtho(-r*aspect*zoom, r*aspect*zoom, -r*zoom, r*zoom, -r, r);
	         double a = Math.PI/6;
	         double b = Math.PI/6;
	         double theta = (Math.atan(Math.sqrt(Math.tan(a)/Math.tan(b)))-Math.PI/2)*180/Math.PI;
	         double gamma = (Math.asin(Math.sqrt(Math.tan(a)*Math.tan(b))))*180/Math.PI;
	
	         gl.glRotated(gamma, 1.0f, 0.0f, 0.0f);
	         gl.glRotated(theta, 0.0f, 1.0f, 0.0f);
	         
         } else if(tabOp == 3) {
	         // perspectiva
	               	        
	           double dNear = fDistance - r;
	           double dFar = fDistance + r;
	           
	           if (width <= height)
	               gl.glFrustum(-r/5, r/5, -r/aspect/5, r/aspect/5, dNear, dFar*5);
	           else
	               gl.glFrustum(-r*aspect/5, r*aspect/5, -r/5, r/5, dNear, dFar*5);
           
         }
         
         gl.glMatrixMode(GL.GL_MODELVIEW);
         gl.glLoadIdentity();
       
         if(tabOp == 3)
        	 glu.gluLookAt(0.0f, 0.0f, fDistance*2*zoom, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
 
           gl.glColor3f(0.5f, 0.5f, 0.5f);
           // wireframe
           gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL.GL_LINE);
           // fill
           //gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL.GL_FILL);

           
           // center the model
           gl.glTranslatef(-px, -py, -pz);
           
           
           if(!textureShown) {
             
               File textureFile = new File("textures/leath01.png");
                BufferedImage img = null;
              
                try {
                  img = ImageIO.read(textureFile);
               } catch (IOException e) {
                  e.printStackTrace();
               }   
               WritableRaster raster = img.getRaster();   
              int widthR = raster.getWidth();   
              int heightR = raster.getHeight();   
              DataBuffer buf = raster.getDataBuffer();
             
               switch( buf.getDataType() ) {   
                   case DataBuffer.TYPE_BYTE:
                       DataBufferByte bb = (DataBufferByte) buf;   
                       byte im[] = bb.getData();
                       int[] textureId = new int[1];
                       gl.glActiveTexture(1);
                       gl.glGenTextures( 1, textureId, 0 );
                       gl.glBindTexture( GL.GL_TEXTURE_2D, textureId[0] );
                       gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_DECAL);
                       gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
                       gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
                       gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
                       gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
                       gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, widthR, heightR,    
                               0, GL.GL_BGR, GL.GL_UNSIGNED_BYTE, ByteBuffer.wrap(im));
                       gl.glEnable(GL.GL_TEXTURE_2D);
                      
                       break;
                   case DataBuffer.TYPE_UNDEFINED:
                       break;
               }
               
               textureShown=true;
           }
           
           Iterator<float[]> it;
           float[] pts = null;
           float[] ptsT = null;
           Iterator<ArrayList<float[]>> it1 = faces.iterator();
           while(it1.hasNext()) {
            it = it1.next().iterator();
            gl.glBegin(GL.GL_POLYGON);
            while(it.hasNext()) {
                float[] el = it.next();
//                if(el[1]!=0) {    // se textura aplicavel
//                    ptsT = texturesVt.get((int)el[1]-1);
//                    gl.glTexCoord2d(ptsT[0],ptsT[1]);
//                }
                pts = vertices.get((int)el[0]-1);
                gl.glVertex3f(pts[0],pts[1],pts[2]);
            }
            gl.glEnd(); 
         }
         gl.glFlush();
    }

    public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width,
            int height) {
        final GL gl = gLDrawable.getGL();
       
        gl.glViewport(0, 0, width, height);
       
       
    }
   
    public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged) {}

    public static void reDesenhar() {
        glDraw.display();
    }
   
    public static JPanel criarPainel() {

        painel = new JPanel();
        tabs = new JTabbedPane();
       
        tabOrto = new JPanel();
        tabOblq = new JPanel();
        tabAxon = new JPanel();
        tabPers = new JPanel();
       
        painel.setLayout(new GridLayout(1, 4));
        tabs.addTab("Ortogonal", tabOrto);
        tabs.addTab("Obliqua", tabOblq);
        tabs.addTab("Axonometrica", tabAxon);
        tabs.addTab("Perspectiva", tabPers);
   
       
        painel.add(tabs);
        return painel;
    }
   
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == '\u001B' ||  // escape
            e.getKeyChar() == 'Q' ||
            e.getKeyChar() == 'q') {   // quit
            System.exit(0);
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {

        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT) { }
        else if(e.getKeyCode() == KeyEvent.VK_UP) {
            zoom -= 0.01f;        
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT) { }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
            zoom += 0.01f;
        }
        else if(e.getKeyCode() == KeyEvent.VK_ADD) {
            zoom -= 0.01f;
        }
        else if(e.getKeyCode() == KeyEvent.VK_SUBTRACT) {
            zoom += 0.01f;
        }
        else if(e.getKeyCode() == KeyEvent.VK_NUMPAD0) {
            zoom = 1.0f;
        }
        else if(e.getKeyCode() == KeyEvent.VK_K) {
            moveSideways += 0.01f;
        }
        else if(e.getKeyCode() == KeyEvent.VK_J) {
            moveSideways -= 0.01f;
        }
        
        reDesenhar();
       
    }
    
    
    

    public void keyReleased(KeyEvent e) {}
    

    public static void main(String[] args) {
        JFrame frame = new JFrame("Projections");
        GLCanvas canvas = new GLCanvas();
        canvas.addGLEventListener(new Casa3Dswing());
        canvas.setSize(400, 280);
         frame.add(canvas, BorderLayout.CENTER);
        JPanel painel = criarPainel();
        frame.add(painel, BorderLayout.SOUTH);
        frame.pack();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
         canvas.requestFocusInWindow();
        frame.setVisible(true);
        
        tabs.addChangeListener(new ChangeListener() {
	        public void stateChanged(ChangeEvent e) {
	        	
	        	System.out.println(tabs.getSelectedIndex());
	        	
	        	if(tabs.getSelectedIndex() == 0)
	        		tabOp = 0;
	        	else if(tabs.getSelectedIndex() == 1)
	        		tabOp = 1;
	        	else if(tabs.getSelectedIndex() == 2)
	        		tabOp = 2;
	        	else if(tabs.getSelectedIndex() == 3)
	        		tabOp = 3;
	        	
	        	reDesenhar();
	        }
    	}
        );
    }
   
    private static GLAutoDrawable glDraw;
   
    private static JPanel painel;
    private static JTabbedPane tabs;
    private static JPanel tabOrto;
    private static JPanel tabOblq;
    private static JPanel tabAxon;
    private static JPanel tabPers;
    private static int tabOp = 0;
   
}