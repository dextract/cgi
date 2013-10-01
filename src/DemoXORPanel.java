import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;


public class DemoXORPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    
    public DemoXORPanel() {
        // Fundo de cor cinzenta
        setBackground(Color.blue);
                
        MouseListener ml = new MouseAdapter() {
                        
            public void mouseEntered(MouseEvent e) {
                Graphics2D g = (Graphics2D) getGraphics();
                
                exited = false;
                
                if(linhaPresa)  {
                    g.setColor(Color.yellow);
                    g.drawLine(xPosInicial, yPosInicial, xPosAnterior, yPosAnterior);
                    g.setColor(g.getBackground());
                }   
            }
                        
            public void mouseExited(MouseEvent e) {
                Graphics2D g = (Graphics2D) getGraphics();
                
                exited = true;
                
                if(linhaPresa) {
                        g.setColor(Color.yellow);
                        g.drawLine(xPosInicial, yPosInicial, xPosAnterior, yPosAnterior);
                        g.setColor(g.getBackground());
                }           
            }
            
            public void mouseReleased(MouseEvent e) {
                    if(grabbedPoint!=null) {
                            grabbedPoint = null;
                          //  calculateCoeffs(points);
                    }
                    
                    
            }
      
            public void mousePressed(MouseEvent e) {

                if(e.getButton() != MouseEvent.BUTTON1) return;
                if(e.getClickCount() != 1) return;
                

                // Obter as coordenadas actuais da posicao do rato
                int xPos = e.getX();
                int yPos = e.getY();
                
                
                if(move == true)
                        System.out.println("moving box");
                else if(resize == true)
                        System.out.println("resizing box");
                else if(rotate == true)
                        System.out.println("rotating box");
                        

                //clicar numa vizinhanÃ§a de um ponto
                for(Point p: points)
                    if(Math.abs(xPos-p.getX())<=5 && Math.abs(yPos-p.getY())<=5)
                        grabbedPoint = p;
                
                Graphics2D g = (Graphics2D) getGraphics();
                
                
                if(linhaPresa && points.size()==nPoints-1) {  // Estamos a acabar uma linha...
                // Desenhar a linha nova (definitiva)
                
                    g.setPaintMode();
                    
                    g.setColor(Color.yellow);
                    g.drawLine(xPosInicial, yPosInicial, xPos, yPos);
                    g.setColor(Color.black);
                    
                    linhaPresa = false;
                    points.add(new Point(xPos, yPos, true));
                    drawSquare(xPos, yPos, g);
                    if(boundingBox)
                        drawBoundingBox(g);
                    calculateCoeffs(points, g);
                
                }
                else if(points.size()<nPoints-1){   // Estamos a iniciar uma linha
                                                        
                    linhaPresa = true;
                    xPosInicial = xPosAnterior = xPos;
                    yPosInicial = yPosAnterior = yPos;
                    drawSquare(xPos, yPos, g);
                    points.add(new Point(xPosInicial, yPosInicial, false));
                }
            }                
        
        };
        
        MouseMotionListener mml = new MouseMotionAdapter() {
                
            public void mouseDragged(MouseEvent e) {
                if(points.size() < nPoints) return; //ainda nÃ£o se finalizou a linha por isso nÃ£o deve poder arrastar pontos
                  
                // Obter a posicao actual do cursor
                int x = e.getX();
                int y = e.getY();
                
                if(grabbedPoint != null) { //se se estiver a pegar num ponto
                    if(!exited) {
                        grabbedPoint.setNewX(x);
                        grabbedPoint.setNewY(y);
                    }
                    repaint();
                   // calculateCoeffs(points);
                    
                }
                    
            }
            
            public void mouseMoved(MouseEvent e) {
                
                // Obter a posicao actual do cursor
                int xPos = e.getX();
                int yPos = e.getY();
                
                if(points.size() == nPoints) //linha já terminada
                        
                    if(boundingBox) {
                        
                       if(Math.abs(xPos-boxXmin)<=5 && Math.abs(yPos-boxYmin)<=5 ||
                          Math.abs(xPos-boxXmax)<=5 && Math.abs(yPos-boxYmax)<=5 ||
                          Math.abs(xPos-boxXmax)<=5 && Math.abs(yPos-boxYmin)<=5 ||
                          Math.abs(xPos-boxXmin)<=5 && Math.abs(yPos-boxYmax)<=5) 
                       {
                           rotate = true;
                           move = false;
                           resize = false;
                       }                          
                       else if(Math.abs(xPos-boxXmin)<=5 ||
                                   Math.abs(yPos-boxYmin)<=5 || 
                                   Math.abs(xPos-boxXmax)<=5 ||
                                   Math.abs(yPos-boxYmax)<=5)
                       {
                           rotate = false;
                           move = false;
                           resize = true;
                       }   
                       else if (Math.abs(xPos-boxXmax)<= boxXmax - boxXmin && Math.abs(yPos-boxYmax)<= boxYmax - boxYmin)
                       {
                           rotate = false;
                           move = true;
                           resize = false;
                       }
                     }
                
                
                if(!linhaPresa) return;         // Nao estamos a meio da especificacao duma linha!
               
                existeLinha = true;
    
                
                // Colocar em modo de desenho XOR
                Graphics2D g = (Graphics2D) getGraphics();
                g.setXORMode(getBackground());
                
                g.setColor(Color.yellow);
                // Apagar a linha antiga
                g.drawLine(xPosInicial, yPosInicial, xPosAnterior, yPosAnterior);
                //Desenha a linha nova
                g.drawLine(xPosInicial, yPosInicial, xPos, yPos);
                g.setColor(Color.black);
                    
                // Memorizar a posicao actual
                xPosAnterior = xPos;
                yPosAnterior = yPos;    
                    
            }                                       
                
        };
        
        addMouseListener(ml);
        addMouseMotionListener(mml);    
    }

    // Este metodo eh o responsavel por actualizar a area de desenho do componente
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
       
        Graphics2D g2 = (Graphics2D) g;
        Point fp;
        Point sp;
        
        if(existeLinha) {
            for(int i = 0; i < points.size() - 1; i++) {
                fp = points.get(i);
                sp = points.get(i+1);
                
                g2.setColor(Color.yellow);
                g2.drawLine(fp.getX(), fp.getY(), sp.getX(), sp.getY());
                g2.setColor(Color.black);
                
                drawSquare(fp.getX(), fp.getY(), g2);
                if(i==points.size()-2)
                        drawSquare(sp.getX(), sp.getY(), g2);        
            }
            if(boundingBox)   
                drawBoundingBox(g2);
            calculateCoeffs(points, g2);
            
        }

    }
    
    //public void draw
    
    // Metodo adicionado para se poder receber, do exterior (neste caso do Frame), 
    // um pedido para limpar o conteudo
    public void limparDesenho() {
        // Mudar o estado da aplicacao
        existeLinha = false;
        linhaPresa = false;
        points.clear();
        // Force-se a actualizacao do componente (acabarah por invocar o paintComponent)
        repaint();
    }
    
    public void drawSquare(int x, int y, Graphics2D g) {
            g.drawRect(x - 5, y - 5, 10, 10);       
    }
    
    public void drawBoundingBox(Graphics2D g) {
        if(!linhaPresa) {
                int minX = points.get(0).getX();
                int minY = points.get(0).getY();
                int maxX = points.get(0).getX();
                int maxY = points.get(0).getY();
                
                for(Point p:points) {
                if(p.getX()>maxX)
                        maxX = p.getX();
                if(p.getY()>maxY)
                        maxY = p.getY();
                if(p.getX()<minX)
                        minX = p.getX();
                if(p.getY()<minY)
                        minY = p.getY();
                }
                
                boxXmin = minX;
                boxXmax = maxX;
                boxYmin = minY;
                boxYmax = maxY;
                
                g.drawRect(minX, minY, maxX-minX, maxY-minY);
        }
    }
    
    public void setPointNumber(int n) {
        
        nPoints = n;
        limparDesenho();
    }
        
    public void changeOption(int n) {
    	Graphics2D g = (Graphics2D) getGraphics();
        // 1 - bounding box
        // 
        if(n==1)
                boundingBox = (boundingBox) ? false : true;
        
        if(boundingBox)
                drawBoundingBox(g);
        else {
                g.setXORMode(getBackground());
                drawBoundingBox(g);
                g.setPaintMode();
        }
            
    }

    public void calculateCoeffs(List<Point> points, Graphics2D g) {
            
        int[][] bezierM = 
                {       {-1,3,-3,1},
                        {3,-6,3,0},
                        {-3,3,0,0},
                        {1,0,0,0}       };
        
        int[] x = new int[4];
        int[] y = new int[4];
        
        int[] cx = new int[4];
        int[] cy = new int[4];

        int i = 0;

        for(Point p: points) {
            x[i]=p.getX();
            y[i]=p.getY();
            i++;
        }
        
        for(i=0;i<x.length;i++) {
            cx[i]=bezierM[i][0]*x[0]+bezierM[i][1]*x[1]+bezierM[i][2]*x[2]+bezierM[i][3]*x[3];
            cy[i]=bezierM[i][0]*y[0]+bezierM[i][1]*y[1]+bezierM[i][2]*y[2]+bezierM[i][3]*y[3];
        }
        
        /*System.out.println();
        for(i=0;i<cx.length;i++) 
                System.out.print(cx[i]+" ");
        System.out.println();
        for(i=0;i<cy.length;i++) 
                System.out.print(cy[i]+" ");*/

        drawCurve(cx, cy, 50, g);
            
    }         
        
    /**
     * @param cx Coefficients for x(t): Cx=M*Gx
     * @param cy Coefficients for y(t): Cy=M*Gy
     * @param n Number of steps
     */
    public void drawCurve(int[] cx, int[] cy, int n, Graphics2D g) {
        g.setStroke(new BasicStroke(2));
        double t = 0;
        double delta = 1.0/n;
        double t2, t3;
        int x, y;
        int prevx = points.get(0).getX();
        int prevy = points.get(0).getY();
        
        for(int i=0;i<n;i++) {  
            t+=delta;
            t2=t*t;
            t3=t2*t;
            x=(int) (cx[0]*t3+cx[1]*t2+cx[2]*t+cx[3]);
            y=(int) (cy[0]*t3+cy[1]*t2+cy[2]*t+cy[3]);
            g.drawLine(prevx, prevy, x, y);
            prevx=x;
            prevy=y;
        }
    }
    
    private boolean boundingBox = false;
    private boolean existeLinha = false;
    private boolean linhaPresa = false;
    private boolean exited = false;
    private int nPoints = 4;
    private List<Point> points = new ArrayList<Point>();
    private int xPosInicial, yPosInicial;
    private int xPosAnterior, yPosAnterior;
    private Point grabbedPoint = null;
    private int boxXmin, boxXmax, boxYmin, boxYmax;
    private boolean rotate = false;
    private boolean resize = false;
    private boolean move = false;

}