import javax.swing.JFrame;
import javax.swing.JPanel;

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
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import java.awt.event.WindowAdapter;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;

import javax.media.opengl.GLEventListener;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.GLUT;

/**
 * @author  M. Próspero
 */

public class Casa3Dswing implements GLEventListener, KeyListener {  

	private float rquad = 0.0f;
	private float xAxis = 0.0f;
	private float yAxis = 0.0f;
	private float zAxis = 0.0f;
	private float zoom = 1.0f;

    private GLU glu = new GLU();
	
    public void init(GLAutoDrawable gLDrawable) {
    	glDraw = gLDrawable;
    /*	ObjectLoader ol = new ObjectLoader();
    	try {
			ol.load(new File("objects/archer.obj"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    	GL gl = gLDrawable.getGL();
    	gl.glEnable(GL.GL_DEPTH_TEST);
    	gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
    	glDraw.addKeyListener(this);
    	// Podem adicionar-se outros listeners aqui (e.g. os do rato)
    }
    
    public void display(GLAutoDrawable gLDrawable) {
    	 final GL gl = gLDrawable.getGL();
    	
         gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
         
         
         int height = gLDrawable.getHeight();
         int width = gLDrawable.getWidth();
         float aspect = (float)width / (float)height;

         gl.glMatrixMode(GL.GL_PROJECTION);
         gl.glLoadIdentity();
         
         // if perspectiva
         //glu.gluPerspective(20.0f, aspect, 2.0, 50.0);
         
          //if ortogonal
          //frente default
         if (width <= height)
             gl.glOrtho(-2.0*zoom, 2.0*zoom, -2.0/aspect*zoom, 2.0/aspect*zoom, -1.0, 1.0);
         else
            gl.glOrtho(-2.0*aspect*zoom, 2.0*aspect*zoom, -2.0*zoom, 2.0*zoom, -1.0, 1.0);
         //if planta 
         //gl.glRotatef(90, 1.0f, 0.0f, 0.0f);
         // if esquerda
         //gl.glRotatef(90, 0.0f, 1.0f, 0.0f);
         // if direita
         //gl.glRotatef(-90, 0.0f, 1.0f, 0.0f);
         
         
         
         // if obliqua
         double l = 0.5;
         double alpha = Math.PI/4;
         double[] m = {	1,0,-l*Math.cos(alpha),0,
        		 		0,1,-l*Math.sin(alpha),0,
        		 		0,0,0,0,
        		 		0,0,0,1	};
         gl.glMultTransposeMatrixd(m, 0);
         
         
         
         gl.glMatrixMode(GL.GL_MODELVIEW);
         gl.glLoadIdentity();
         
         
         // perspectiva
        // glu.gluLookAt(0, 5, -25.0*-zoom, 0, 0, 0, 1, 1, 0);
         
         gl.glEnable(GL.GL_DEPTH_TEST);
    	 gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
    	 gl.glPolygonOffset(1.0f, 1.0f);
         
         gl.glBegin(GL.GL_QUADS);            // Draw A Quad
         gl.glColor3f(0.0f, 1.0f, 0.0f);     // Set The Color To Green
         gl.glVertex3f(1.0f, 1.0f, -1.0f);   // Top Right Of The Quad (Top)
         gl.glVertex3f(-1.0f, 1.0f, -1.0f);  // Top Left Of The Quad (Top)
         gl.glVertex3f(-1.0f, 1.0f, 1.0f);   // Bottom Left Of The Quad (Top)
         gl.glVertex3f(1.0f, 1.0f, 1.0f);    // Bottom Right Of The Quad (Top)

         gl.glColor3f(1.0f, 0.5f, 0.0f);     // Set The Color To Orange
         gl.glVertex3f(1.0f, -1.0f, 1.0f);   // Top Right Of The Quad (Bottom)
         gl.glVertex3f(-1.0f, -1.0f, 1.0f);  // Top Left Of The Quad (Bottom)
         gl.glVertex3f(-1.0f, -1.0f, -1.0f); // Bottom Left Of The Quad (Bottom)
         gl.glVertex3f(1.0f, -1.0f, -1.0f);  // Bottom Right Of The Quad (Bottom)

         gl.glColor3f(1.0f, 0.0f, 0.0f);     // Set The Color To Red
         gl.glVertex3f(1.0f, 1.0f, 1.0f);    // Top Right Of The Quad (Front)
         gl.glVertex3f(-1.0f, 1.0f, 1.0f);   // Top Left Of The Quad (Front)
         gl.glVertex3f(-1.0f, -1.0f, 1.0f);  // Bottom Left Of The Quad (Front)
         gl.glVertex3f(1.0f, -1.0f, 1.0f);   // Bottom Right Of The Quad (Front)

         gl.glColor3f(1.0f, 1.0f, 0.0f);     // Set The Color To Yellow
         gl.glVertex3f(1.0f, -1.0f, -1.0f);  // Bottom Left Of The Quad (Back)
         gl.glVertex3f(-1.0f, -1.0f, -1.0f); // Bottom Right Of The Quad (Back)
         gl.glVertex3f(-1.0f, 1.0f, -1.0f);  // Top Right Of The Quad (Back)
         gl.glVertex3f(1.0f, 1.0f, -1.0f);   // Top Left Of The Quad (Back)

         gl.glColor3f(0.0f, 0.0f, 1.0f);     // Set The Color To Blue
         gl.glVertex3f(-1.0f, 1.0f, 1.0f);   // Top Right Of The Quad (Left)
         gl.glVertex3f(-1.0f, 1.0f, -1.0f);  // Top Left Of The Quad (Left)
         gl.glVertex3f(-1.0f, -1.0f, -1.0f); // Bottom Left Of The Quad (Left)
         gl.glVertex3f(-1.0f, -1.0f, 1.0f);  // Bottom Right Of The Quad (Left)

         gl.glColor3f(1.0f, 0.0f, 1.0f);     // Set The Color To Violet
         gl.glVertex3f(1.0f, 1.0f, -1.0f);   // Top Right Of The Quad (Right)
         gl.glVertex3f(1.0f, 1.0f, 1.0f);    // Top Left Of The Quad (Right)
         gl.glVertex3f(1.0f, -1.0f, 1.0f);   // Bottom Left Of The Quad (Right)
         gl.glVertex3f(1.0f, -1.0f, -1.0f);  // Bottom Right Of The Quad (Right)
         gl.glEnd();                         // Done Drawing The Quad
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
		JPanel painelControlo = criarPainelControlo();
		JPanel painelAbout = criarPainelAbout();
		
		JPanel painel = new JPanel();
		painel.add(painelControlo);
		painel.add(painelAbout);
		return painel;
	}

	public static JPanel criarPainelControlo() {
		JPanel painelCor = criarCheckBoxes();
		JPanel painelPrimitivas = criarRadioButtons();
		
		JPanel painelControlo = new JPanel();
		painelControlo.setLayout(new GridLayout(2, 1));
		painelControlo.add(painelCor);
		painelControlo.add(painelPrimitivas);
		return painelControlo;
	}

	public static JPanel criarPainelAbout(){

		class BListener implements ActionListener {
			
			public void actionPerformed(ActionEvent event) {
				JOptionPane.showMessageDialog(null, 
						"Demo JOGL\n\nM. Pr\u00F3spero dos Santos\n");
			}
		}
		
		BListener listener = new BListener();

		JButton autor = new JButton("About"); 
		autor.addActionListener(listener);
				
		JPanel painel = new JPanel();
		painel.add(autor);
		return painel;
	}
	
	public static JPanel criarCheckBoxes(){

		class CBListener implements ActionListener {
			
			public void actionPerformed(ActionEvent event) {
				reDesenhar();
			}
		}
		
		CBListener listener = new CBListener();

		vermelho = new JCheckBox("Red");
		vermelho.addActionListener(listener);
		
		verde = new JCheckBox("Green");
		verde.addActionListener(listener);
		
		azul = new JCheckBox("Blue");
		azul.addActionListener(listener);
		
		JPanel painel = new JPanel();
		painel.add(vermelho);
		painel.add(verde);
		painel.add(azul);
		painel.setBorder(new TitledBorder(new EtchedBorder(), "Color"));
		return painel;
	}

	public static JPanel criarRadioButtons(){
		
		class RBListener implements ActionListener {
			
			public void actionPerformed(ActionEvent event) {
				reDesenhar();
			}
		}
		
		RBListener listener = new RBListener();
		
		paredes = new JRadioButton("Walls",true);
		paredes.addActionListener(listener);
		
		porta = new JRadioButton("Door");
		porta.addActionListener(listener);
		
		janela = new JRadioButton("Window");
		janela.addActionListener(listener);
		
		telhado = new JRadioButton("Roof");
		telhado.addActionListener(listener);

		ButtonGroup grupo = new ButtonGroup();
		grupo.add(paredes);
		grupo.add(porta);
		grupo.add(janela);
		grupo.add(telhado);

		JPanel painel = new JPanel();
		painel.add(paredes);
		painel.add(porta);
		painel.add(janela);
		painel.add(telhado);
		painel.setBorder(new TitledBorder(new EtchedBorder(), "Primitives"));
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
		else if(e.getKeyCode() == KeyEvent.VK_UP) {
			zoom -= 0.01f;
	        reDesenhar();
		}
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			zoom += 0.01f;
	        reDesenhar();
		}
	}

	public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
    	JFrame frame = new JFrame("My JOGL house");
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
    }
    
    private static GLAutoDrawable glDraw;
	private static JCheckBox vermelho;
	private static JCheckBox verde;
	private static JCheckBox azul;
	private static JRadioButton paredes;
	private static JRadioButton porta;
	private static JRadioButton janela;
	private static JRadioButton telhado;
	
	
}

