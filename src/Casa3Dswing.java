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
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.glu.GLU;

/**
 * @author  M. Próspero
 */

public class Casa3Dswing implements GLEventListener, KeyListener {  

	private float rquad = 0.0f;
	private float xAxis = 0.0f;
	private float yAxis = 0.0f;
	private float zAxis = 0.0f;

    private GLU glu = new GLU();
	
    public void init(GLAutoDrawable gLDrawable) {
    	glDraw = gLDrawable;
    	GL gl = gLDrawable.getGL();
    	gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
    	glDraw.addKeyListener(this);
    	// Podem adicionar-se outros listeners aqui (e.g. os do rato)
    }
    
    public void display(GLAutoDrawable gLDrawable) {
    	 final GL gl = gLDrawable.getGL();
         gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        
         gl.glLoadIdentity();
         gl.glTranslatef(0.0f, 0.0f, -6.0f);
         gl.glRotatef(rquad, xAxis, yAxis, zAxis);
         
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
    
  /*  public void display(GLAutoDrawable gLDrawable) {
      GL gl = gLDrawable.getGL();

      gl.glClear(GL.GL_COLOR_BUFFER_BIT);
      gl.glMatrixMode(GL.GL_PROJECTION);
      gl.glLoadIdentity();
      gl.glOrtho(0.0, 400.0, -280.0, 0.0, -100.0, 100.0); // janela fixa
      gl.glMatrixMode(GL.GL_MODELVIEW);
      gl.glLoadIdentity();

      double red = 0.0, green = 0.0, blue = 0.0;
      if (vermelho.isSelected()) 
    	  red = 1.0;
      if (verde.isSelected()) 
    	  green = 1.0;
      if (azul.isSelected()) 
    	  blue = 1.0;
      
      if (paredes.isSelected())
    	  gl.glColor3d(red, green, blue);
      else
    	  gl.glColor3d(0.0, 0.0, 0.0);  // paredes:
      	gl.glBegin(GL.GL_LINES);
      	gl.glVertex2d(120.0,-220.0);   // z=0 para glVertex2d()
      	gl.glVertex2d(120.0,-140.0);
      	gl.glVertex2d(280.0,-220.0);
      	gl.glVertex2d(280.0,-140.0);
      gl.glEnd();

      if (porta.isSelected())
    	  gl.glColor3d(red, green, blue);
      else
    	  gl.glColor3d(0.0, 0.0, 1.0);  // porta:
    	gl.glBegin(GL.GL_LINE_STRIP);
    	gl.glVertex2d(140.0, -220.0);
    	gl.glVertex2d(140.0, -160.0);
    	gl.glVertex2d(180.0, -160.0);
    	gl.glVertex2d(180.0, -220.0);
      gl.glEnd();

      if (janela.isSelected())
    	  gl.glColor3d(red, green, blue);
      else
    	  gl.glColor3d(1.0, 0.0, 1.0);  // janela:
  		gl.glBegin(GL.GL_LINE_LOOP);
  		gl.glVertex2d(200.0, -160.0);
  		gl.glVertex2d(260.0, -160.0);
  		gl.glVertex2d(260.0, -200.0);
  		gl.glVertex2d(200.0, -200.0);
  	  gl.glEnd();
      
  	  if (telhado.isSelected())
    	  gl.glColor3d(red, green, blue);
  	  else
    	  gl.glColor3d(1.0, 0.0, 0.0);  // telhado:
    	gl.glBegin(GL.GL_TRIANGLES);
    	gl.glVertex3d(100.0, -140.0, 50.0);  // 3D
    	gl.glVertex3d(200.0, -60.0, -50.0);
    	gl.glVertex3d(300.0, -140.0, 50.0);
      gl.glEnd();
    }
*/
    /*public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) {
    	GL gl = gLDrawable.getGL();
    	gl.glViewport(0, 0, width, height); // altera-se o visor
    } */
    
    public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, 
            int height) {
        final GL gl = gLDrawable.getGL();

        if (height <= 0) // avoid a divide by zero error!
            height = 1;
        final float h = (float) width / (float) height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, h, 1.0, 20.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
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
			yAxis += 1.0f;
	        rquad -= 0.2f;
			reDesenhar();
		}
		else if(e.getKeyCode() == KeyEvent.VK_UP) {
			//yAxis = 0.0f;
			xAxis += 1.0f;
	        rquad -= 0.2f;
	        reDesenhar();
		}
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			yAxis -= 1.0f;
	        rquad += 0.2f;
	        reDesenhar();
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			xAxis += 1.0f;
	        rquad += 0.2f;
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

