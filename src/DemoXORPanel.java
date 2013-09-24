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
                setBackground(Color.DARK_GRAY);
 
                
                MouseListener ml = new MouseAdapter() {
                        
                        public void mouseEntered(MouseEvent e) {
                                Graphics2D g = (Graphics2D) getGraphics();
                                
                                exited = false;
                                if( (linhaPresa) || (existeLinha && grabbedPoint !=null) )  {
                                        g.setColor(Color.yellow);
                                        g.drawLine(xPosInicial, yPosInicial, xPosAnterior, yPosAnterior);
                                        g.setColor(Color.black);
                                }
                                
                        }
                        
                        public void mouseExited(MouseEvent e) {
                                Graphics2D g = (Graphics2D) getGraphics();
                                
                                exited = true;
                                if(existeLinha && grabbedPoint !=null) {
                                        g.setColor(Color.yellow);
                                        g.drawLine(xPosInicial, yPosInicial, xPosAnterior, yPosAnterior);
                                        g.setColor(Color.black);
                                }
                                
                        }
                        
                        
                        public void mouseReleased(MouseEvent e) {
                                grabbedPoint = null;
                        }

                        
                        public void mousePressed(MouseEvent e) {

                                if(e.getButton() != MouseEvent.BUTTON1) return;
                                if(e.getClickCount() != 1) return;
                                
        
                                // Obter as coordenadas actuais da posicao do rato
                                int xPos = e.getX();
                                int yPos = e.getY();
                                

                                //clicar numa vizinhanÃ§a de um ponto
                                for(Point p: points)
                                        if(     Math.abs(xPos-p.getX())<=5 && 
                                                Math.abs(yPos-p.getY())<=5 )
                                                        grabbedPoint = p;
                                
                                Graphics2D g = (Graphics2D) getGraphics();
                                
                                
                                if(linhaPresa && nPoints==1) {  // Estamos a acabar uma linha...
                                // Desenhar a linha nova (definitiva)
                                
                                        g.setPaintMode();
                                        
                                        g.setColor(Color.yellow);
                                        g.drawLine(xPosInicial, yPosInicial, xPos, yPos);
                                        g.setColor(Color.black);
                                        
                                
                                        linhaPresa = false;
                                        points.add(new Point(xPos, yPos, true));
                                        drawSquare(xPos, yPos, g);
                                        drawBoundingBox(g);
                                
                                }
                                else if(nPoints > 1){   // Estamos a iniciar uma linha
                                                                        
                                        linhaPresa = true;
                                        xPosInicial = xPosAnterior = xPos;
                                        yPosInicial = yPosAnterior = yPos;
                                        drawSquare(xPos, yPos, g);
                                        points.add(new Point(xPosInicial, yPosInicial, false));
                                        nPoints--;

                                }
                        }                       
                };
                
                MouseMotionListener mml = new MouseMotionAdapter() {
                        
                        
                        public void mouseDragged(MouseEvent e) {
                                
                                if(nPoints > 1) return; //ainda nÃ£o se finalizou a linha por isso nÃ£o deve poder arrastar pontos
                                
                                Graphics2D g = (Graphics2D) getGraphics();
                                
                                // Obter a posicao actual do cursor
                                int x = e.getX();
                                int y = e.getY();
                                
                                if(grabbedPoint != null) { //se se estiver a pegar num ponto
        
                                        if(!exited) {
                                                grabbedPoint.setNewX(x);
                                                grabbedPoint.setNewY(y);
                                        }
                                    
                                        paintComponent(g);
                                }
        
                                
                        }
                                        
                        
                        public void mouseMoved(MouseEvent e) {
                                
                                if(!linhaPresa) return;         // Nao estamos a meio da especificacao duma linha!
                                
                                existeLinha = true;
                                
                                // Obter a posicao actual do cursor
                                int xPos = e.getX();
                                int yPos = e.getY();

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
        public void paintComponent(Graphics g)
        {
                super.paintComponent(g);
                
                //g.setXORMode(getBackground());
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
                        
                        drawBoundingBox(g2);
                        
                }
                
        }
        
        //public void draw
        
        // Metodo adicionado para se poder receber, do exterior (neste caso do Frame), 
        // um pedido para limpar o conteudo
        public void limparDesenho()
        {
                // Mudar o estado da aplicacao
                existeLinha = false;
                linhaPresa = false;
                nPoints = 4;
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
                
                g.drawRect(minX, minY, maxX-minX, maxY-minY);
                }
                
        }
        
		public void setPointNumber(int n) {
			nPoints = n;
		}


        private boolean existeLinha = false;
        private boolean linhaPresa = false;
        private boolean exited = false;
        private int nPoints = 0;
        private List<Point> points = new ArrayList<Point>();
        private int xPosInicial, yPosInicial;
        private int xPosAnterior, yPosAnterior;
        private Point grabbedPoint = null; 

}