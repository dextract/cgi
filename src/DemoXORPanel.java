import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
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
        setBackground(Color.LIGHT_GRAY);
                
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
                grabbedPoint = null;
               	editStartPoint = null;
            	move=false;
            	resize=false;
            	rotate=false;
            	editOn=false;
            	
            	if(boundingBox)
            		drawBoundingBox((Graphics2D) getGraphics());
            	
                setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            }
      
            public void mousePressed(MouseEvent e) {

                if(e.getButton() != MouseEvent.BUTTON1) return;
                if(e.getClickCount() != 1) return;
  
                // Obter as coordenadas actuais da posicao do rato
                int xPos = e.getX();
                int yPos = e.getY();
                
                //clicar numa vizinhanÃ§a de um ponto
                for(Point p: points)
                    if(Math.abs(xPos-p.getX())<=5 && Math.abs(yPos-p.getY())<=5)
                        grabbedPoint = p;
                
                if(grabbedPoint==null) {
		            if(move == true) {
		            	setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		            	editOn=true;
		            }
		            else if(resize == true) {
		                System.out.println("resizing box");
		                editOn=true;
		            }
		            else if(rotate == true) {
		            	System.out.println("rotating box");
		            	cX = (boxXmax + boxXmin) / 2;
	                	cY = (boxYmax + boxYmin) / 2;
		            	editOn=true;
		            }
	            	editStartPoint=new Point(xPos,yPos,false);
                }
                
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
                    if(bezierCurve)
                    	drawBezierCurve(points, g);
                
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
                }
                else if(move && !exited) {
                	for(Point p: points) {
                		p.setNewX(p.getX()+(x-editStartPoint.getX()));
                		p.setNewY(p.getY()+(y-editStartPoint.getY()));
                		repaint();
                	}
                	editStartPoint.setNewX(x);
                	editStartPoint.setNewY(y);
                }           
                else if(resize && !exited) {
                	switch(resizeSide) {
                		case 0: { // Resize cima
                			// Factor multiplicante com base no ponto com o maior y
                			double multFactor = boxYmax-y;
                			multFactor /= boxYmax-editStartPoint.getY();
		                	// Calculo do valor para substrair aos novos pontos de modo a que estes 
		                	// voltem aos sitios correctos
                			if(multFactor>=1) {
			                	int sub = ((int)(boxYmax*multFactor)-boxYmax);
		                		for(Point p: points) {
		                    		p.setNewY((int)(p.getY()*multFactor-sub));	
		                    		repaint();
		                		}
                			}
                			else {
                				int add = (boxYmax-(int)(boxYmax*multFactor));
                				for(Point p: points) {
                					p.setNewY((int)(p.getY()*multFactor)+add);
		                    		repaint();
		                		}
                			}
                			editStartPoint.setNewY(y);
		                	break;
                		}
                		case 1: { // Resize cima-direita
                			double multFactorX = x-boxXmin;
                			double multFactorY = boxYmax-y;
		                	multFactorX /= editStartPoint.getX()-boxXmin;
                			multFactorY /= boxYmax-editStartPoint.getY();
                			if(multFactorX>=1 && multFactorY>=1) {
                				int subX = ((int)(boxXmin*multFactorX)-boxXmin);
                				int subY = ((int)(boxYmax*multFactorY)-boxYmax);
		                		for(Point p: points) {
		                    		p.setNewX((int)(p.getX()*multFactorX-subX));	
		                    		p.setNewY((int)(p.getY()*multFactorY-subY));
		                    		repaint();
		                		}
                			}
                			else if(multFactorX>=1 && multFactorY<1) {
                				int subX = ((int)(boxXmin*multFactorX)-boxXmin);
                				int addY = (boxYmax-(int)(boxYmax*multFactorY));
                				for(Point p: points) {
		                    		p.setNewX((int)(p.getX()*multFactorX-subX));	
		                    		p.setNewY((int)(p.getY()*multFactorY)+addY);
		                    		repaint();
		                		}
                			}
                			else if(multFactorX<1 && multFactorY>=1) {
                				int addX = (boxXmin-(int)(boxXmin*multFactorX));
                				int subY = ((int)(boxYmax*multFactorY)-boxYmax);
                				for(Point p: points) {
                					p.setNewX((int)(p.getX()*multFactorX+addX));	
		                    		p.setNewY((int)(p.getY()*multFactorY-subY));
		                    		repaint();
		                		}
                			}
                			else {
                				int addX = (boxXmin-(int)(boxXmin*multFactorX));
                				int addY = (boxYmax-(int)(boxYmax*multFactorY));
                				for(Point p: points) {
                					p.setNewX((int)(p.getX()*multFactorX+addX));	
                					p.setNewY((int)(p.getY()*multFactorY)+addY);
		                    		repaint();
		                		}
                			}
                			editStartPoint.setNewX(x);
                        	editStartPoint.setNewY(y);
                		}
                		case 2: { // Resize direita
                			// Factor multiplicante com base no ponto com o menor x
		                	double multFactor = x-boxXmin;
		                	multFactor /= editStartPoint.getX()-boxXmin;
		                	// Calculo do valor para substrair aos novos pontos de modo a que estes 
		                	// voltem aos sitios correctos
		                	if(multFactor>=1) {
			                	int sub = ((int)(boxXmin*multFactor)-boxXmin);
		                		for(Point p: points) {
		                    		p.setNewX((int)(p.getX()*multFactor-sub));	
		                    		repaint();
		                		}
		                	}
		                	else {
		                		int add = (boxXmin-(int)(boxXmin*multFactor));
		                		for(Point p: points) {
		                    		p.setNewX((int)(p.getX()*multFactor+add));	
		                    		repaint();
		                		}
		                	}
		                	editStartPoint.setNewX(x);
		                	break;
                		}
                		case 3: { // Resize direita-baixo
                			double multFactorX = x-boxXmin;
                			double multFactorY = y-boxYmin;
                			multFactorX /= editStartPoint.getX()-boxXmin;
		                	multFactorY /= editStartPoint.getY()-boxYmin;
		                	
		                	if(multFactorX>=1 && multFactorY>=1) {
		                		int subX = ((int)(boxXmin*multFactorX)-boxXmin);
		                		int subY = ((int)(boxYmin*multFactorY)-boxYmin);
		                		for(Point p: points) {
		                			p.setNewX((int)(p.getX()*multFactorX-subX));	
		                			p.setNewY((int)(p.getY()*multFactorY-subY));
		                    		repaint();
		                		}
                			}
                			else if(multFactorX>=1 && multFactorY<1) {
                				int subX = ((int)(boxXmin*multFactorX)-boxXmin);
                				int addY = (boxYmin-(int)(boxYmin*multFactorY));
                				for(Point p: points) {
                					p.setNewX((int)(p.getX()*multFactorX-subX));	
                					p.setNewY((int)(p.getY()*multFactorY+addY));
		                    		repaint();
		                		}
                			}
                			else if(multFactorX<1 && multFactorY>=1) {
                				int addX = (boxXmin-(int)(boxXmin*multFactorX));
                				int subY = ((int)(boxYmin*multFactorY)-boxYmin);
                				for(Point p: points) {
                					p.setNewX((int)(p.getX()*multFactorX+addX));	
                					p.setNewY((int)(p.getY()*multFactorY-subY));
		                    		repaint();
		                		}
                			}
                			else {
                				int addX = (boxXmin-(int)(boxXmin*multFactorX));
                				int addY = (boxYmin-(int)(boxYmin*multFactorY));
                				for(Point p: points) {
                					p.setNewX((int)(p.getX()*multFactorX+addX));	
                					p.setNewY((int)(p.getY()*multFactorY+addY));
		                    		repaint();
		                		}
                			}
		                	
		                	editStartPoint.setNewX(x);
                        	editStartPoint.setNewY(y);
                		}
                		case 4: { // Resize baixo
                			// Factor multiplicante com base no ponto com o menor y
		                	double multFactor = y-boxYmin;
		                	multFactor /= editStartPoint.getY()-boxYmin;
		                	// Calculo do valor para substrair aos novos pontos de modo a que estes 
		                	// voltem aos sitios correctos
		                	if(multFactor>=1) {
			                	int sub = ((int)(boxYmin*multFactor)-boxYmin);
		                		for(Point p: points) {
		                    		p.setNewY((int)(p.getY()*multFactor-sub));	
		                    		repaint();
		                		}
		                	}
		                	else {
		                		int add = (boxYmin-(int)(boxYmin*multFactor));
		                		for(Point p: points) {
		                    		p.setNewY((int)(p.getY()*multFactor+add));	
		                    		repaint();
		                		}
		                	}
		                	editStartPoint.setNewY(y);
		                	break;
                		}
                		case 5: { // Resize baixo-esquerda
                			double multFactorX = boxXmax-x;
                			double multFactorY = y-boxYmin;
                			multFactorX /= boxXmax-editStartPoint.getX();
		                	multFactorY /= editStartPoint.getY()-boxYmin;
		                	
		                	if(multFactorX>=1 && multFactorY>=1) {
		                		int subX = ((int)(boxXmax*multFactorX)-boxXmax);
		                		int subY = ((int)(boxYmin*multFactorY)-boxYmin);
		                		for(Point p: points) {
		                			p.setNewX((int)(p.getX()*multFactorX-subX));	
		                			p.setNewY((int)(p.getY()*multFactorY-subY));
		                    		repaint();
		                		}
                			}
                			else if(multFactorX>=1 && multFactorY<1) {
                				int subX = ((int)(boxXmax*multFactorX)-boxXmax);
                				int addY = (boxYmin-(int)(boxYmin*multFactorY));
                				for(Point p: points) {
                					p.setNewX((int)(p.getX()*multFactorX-subX));	
                					p.setNewY((int)(p.getY()*multFactorY+addY));
		                    		repaint();
		                		}
                			}
                			else if(multFactorX<1 && multFactorY>=1) {
                				int addX = (boxXmax-(int)(boxXmax*multFactorX));
                				int subY = ((int)(boxYmin*multFactorY)-boxYmin);
                				for(Point p: points) {
                					p.setNewX((int)(p.getX()*multFactorX+addX));	
                					p.setNewY((int)(p.getY()*multFactorY-subY));
		                    		repaint();
		                		}
                			}
                			else {
                				int addX = (boxXmax-(int)(boxXmax*multFactorX));
                				int addY = (boxYmin-(int)(boxYmin*multFactorY));
                				for(Point p: points) {
                					p.setNewX((int)(p.getX()*multFactorX+addX));	
                					p.setNewY((int)(p.getY()*multFactorY+addY));
		                    		repaint();
		                		}
                			}
		                	editStartPoint.setNewX(x);
                        	editStartPoint.setNewY(y);
                        	break;
                		}
                		case 6: { // Resize esquerda
                			// Factor multiplicante com base no ponto com o maior x
		                	double multFactor = boxXmax-x;
		                	multFactor /= boxXmax-editStartPoint.getX();
		                	// Calculo do valor para substrair aos novos pontos de modo a que estes 
		                	// voltem aos sitios correctos
		                	if(multFactor>=1) {
			                	int sub = ((int)(boxXmax*multFactor)-boxXmax);
		                		for(Point p: points) {
		                    		p.setNewX((int)(p.getX()*multFactor-sub));	
		                    		repaint();
		                		}
		                	}
		                	else {
		                		int add = (boxXmax-(int)(boxXmax*multFactor));
		                		for(Point p: points) {
		                    		p.setNewX((int)(p.getX()*multFactor+add));	
		                    		repaint();
		                		}
		                	}
		                	editStartPoint.setNewX(x);
		                	break;
                		}
                		case 7: { // Resize esquerda-cima
                			double multFactorX = boxXmax-x;
                			double multFactorY = boxYmax-y;
                			multFactorX /= boxXmax-editStartPoint.getX();
                			multFactorY /= boxYmax-editStartPoint.getY();
		                	
		                	if(multFactorX>=1 && multFactorY>=1) {
		                		int subX = ((int)(boxXmax*multFactorX)-boxXmax);
		                		int subY = ((int)(boxYmax*multFactorY)-boxYmax);
		                		for(Point p: points) {
		                			p.setNewX((int)(p.getX()*multFactorX-subX));	
		                			p.setNewY((int)(p.getY()*multFactorY-subY));
		                    		repaint();
		                		}
                			}
                			else if(multFactorX>=1 && multFactorY<1) {
                				int subX = ((int)(boxXmax*multFactorX)-boxXmax);
                				int addY = (boxYmax-(int)(boxYmax*multFactorY));
                				for(Point p: points) {
                					p.setNewX((int)(p.getX()*multFactorX-subX));	
                					p.setNewY((int)(p.getY()*multFactorY)+addY);
		                    		repaint();
		                		}
                			}
                			else if(multFactorX<1 && multFactorY>=1) {
                				int addX = (boxXmax-(int)(boxXmax*multFactorX));
                				int subY = ((int)(boxYmax*multFactorY)-boxYmax);
                				for(Point p: points) {
                					p.setNewX((int)(p.getX()*multFactorX+addX));	
                					p.setNewY((int)(p.getY()*multFactorY-subY));
		                    		repaint();
		                		}
                			}
                			else {
                				int addX = (boxXmax-(int)(boxXmax*multFactorX));
                				int addY = (boxYmax-(int)(boxYmax*multFactorY));
                				for(Point p: points) {
                					p.setNewX((int)(p.getX()*multFactorX+addX));	
                					p.setNewY((int)(p.getY()*multFactorY)+addY);
		                    		repaint();
		                		}
                			}
		                	editStartPoint.setNewX(x);
                        	editStartPoint.setNewY(y);
                        	break;
                		}
                		default: break;
                	}
                } 
                else if (rotate) { 	
                	double u1 = editStartPoint.getX() - cX;
                	double u2 = editStartPoint.getY() - cY;
                	double v1 = e.getX() - cX;
                	double v2 = e.getY() - cY;
                	
                	double vA = Math.sqrt(Math.pow(editStartPoint.getX()-cX, 2) + Math.pow(editStartPoint.getY()-cY, 2));
                	double vB = Math.sqrt(Math.pow(e.getX()-cX, 2) + Math.pow(e.getY()-cY, 2));
                	 
                	
                	double cp = u1*v2 - u2 * v1;
                	
                			
                	double theta = Math.acos((u1 * v1 + u2 * v2) / (Math.sqrt(u1*u1 + u2*u2) * Math.sqrt(v1*v1 + v2*v2))); 
                

                	for(Point p: points) {
                		
                		double oldX = p.getX() - cX; 
                		double oldY = p.getY() - cY;
                		
                		double newX = oldX * Math.cos(theta) - oldY * Math.sin(theta);
                		double newY = oldX * Math.sin(theta) + oldY * Math.cos(theta); 
                		newX += cX;
                		newY += cY;
                		
                		p.setNewX((int) newX);
                		p.setNewY((int) newY);
                		
                		repaint();
                	}
                }
            }
            
            public void mouseMoved(MouseEvent e) {
                
                // Obter a posicao actual do cursor
                int xPos = e.getX();
                int yPos = e.getY();
                
                if(points.size() == nPoints) //linha já terminada
                        
                    if(boundingBox) {
                    	
                    	// Move & Resize
                    	if(	(boxXmin<=xPos && xPos<=boxXmax) &&
                     		(boxYmin<=yPos && yPos<=boxYmax) ) {
                    		
                    		rotate = false;
                            move = false;
                            resize = true;
                        	/*
                        	 * 0-N; 1-NE; 2-E; 3-SE; 4-S; 5-SW; 6-W; 7-NW
                        	 * 
                        	 */
                            if( (yPos-boxYmin<=5) && (boxXmax-xPos<=5) )
                        		resizeSide = 1;
                            else if( (boxYmax-yPos<=5) && (boxXmax-xPos<=5) )
                        		resizeSide = 3;
                            else if( (boxYmax-yPos<=5) && (xPos-boxXmin<=5) )
                        		resizeSide = 5;
                            else if( (yPos-boxYmin<=5) && (xPos-boxXmin<=5) )
                        		resizeSide = 7;
                            else if(yPos-boxYmin<=5)
                                resizeSide = 0;
                            else if(boxXmax-xPos<=5)
                        		resizeSide = 2;
                            else if(boxYmax-yPos<=5)
                        		resizeSide = 4;
                            else if(xPos-boxXmin<=5)
                                resizeSide = 6;
                        	else {
	                            rotate = false;
	                            move = true;
	                            resize = false;
                        	}
                        }
                    	// Rotate
                    	else if( 	((Math.abs(boxYmin-yPos)<=5) && (xPos-boxXmax<=5 && boxXmin-xPos<=5)) 
                    			|| 	((Math.abs(boxYmax-yPos)<=5) && (xPos-boxXmax<=5 && boxXmin-xPos<=5))
                    			||	((Math.abs(boxXmin-xPos)<=5) && (yPos-boxYmax<=5 && boxYmin-yPos<=5))
                    			|| 	((Math.abs(boxXmax-xPos)<=5) && (yPos-boxYmax<=5 && boxYmin-yPos<=5)) ) {
                    		rotate = true;
	                     	move = false;
	                     	resize = false;
	                        cX = (boxXmin+boxXmax) / 2;
	                        cY = (boxYmin+boxYmax) / 2;
	                    	/*
                        	 * 0-N; 1-NE; 2-E; 3-SE; 4-S; 5-SW; 6-W; 7-NW
                        	 * 
                        	 */
                            if( (boxYmin-yPos<=5) && (xPos-boxXmax<=5) ) {
                            	rotateSide = 1;
                            }
                            else if( (yPos-boxYmax<=5) && (xPos-boxXmax<=5) ) {
                            	rotateSide = 3;
                            }
                            else if( (yPos-boxYmax<=5) && (boxXmin-xPos<=5) ) {
                            	rotateSide = 5;
                            }
                            else if( (boxYmin-yPos<=5) && (boxXmin-xPos<=5) ) {
                            	rotateSide = 7;
                            }
                            else if(boxYmin-yPos<=5) {
                            	rotateSide = 0;
                            }
                            else if(xPos-boxXmax<=5) {
                            	rotateSide = 2;
                            }
                            else if(yPos-boxYmax<=5) {
                            	rotateSide = 4;
                            }
                            else if(boxXmin-xPos<=5) {
                            	rotateSide = 6;
                            }
                            else {
                            	rotate = false;
    	                     	move = false;
    	                     	resize = false;
                            }
                    	}
						else {
							rotate = false;
						 	move = false;
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
        	if(boundingBox) 
                drawBoundingBox(g2);
        	g2.setStroke(new BasicStroke(1));
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
           
            if(bezierCurve)
            	drawBezierCurve(points, g2);
            
        }

    }
    
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
                
                g.setColor(Color.black);
        		if( (move||resize||rotate) && (editOn) ) {
        			float dash[] = { 10.0f };
        		    g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
        		        BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
        		}
        		else
        			g.setStroke(new BasicStroke(2));
                g.drawRect(minX, minY, maxX-minX, maxY-minY);
                g.setStroke(new BasicStroke(1));
                g.setColor(Color.gray);
                g.fillRect(minX, minY, maxX-minX, maxY-minY);
                g.setColor(Color.black);
                
                Point fp;
                Point sp;
                
                for(int i = 0; i < points.size() - 1; i++) {
                    fp = points.get(i);
                    sp = points.get(i+1);
                    
                    g.setColor(Color.yellow);
                    g.drawLine(fp.getX(), fp.getY(), sp.getX(), sp.getY());
                    g.setColor(Color.black);
                    
                    drawSquare(fp.getX(), fp.getY(), g);
                    if(i==points.size()-2)
                            drawSquare(sp.getX(), sp.getY(), g);        
                }
                
                if(bezierCurve)
                	drawBezierCurve(points, g);
                
        }
    }
    
    public void setPointNumber(int n) {
        System.out.println(boundingBox);
        
        if(nPoints == n) {
        	System.out.println("Já existem " + n + " pontos.");
        }
        else {   		
	        nPoints = n;
	        limparDesenho();
        }
        
    }
        
    public void changeOption(int n) {
		Graphics2D g = (Graphics2D) getGraphics();
	    // 1 - bounding box
	    // 2 - bezier curve
		// 3 - polyline
		
	    if(n==1)
	    	boundingBox = (boundingBox) ? false : true;
	    if(n==2)
	    	bezierCurve = (bezierCurve) ? false : true;
	    if(n==3)
	    	polyline = (polyline) ? false : true;
	    
	    if(boundingBox)
	    	drawBoundingBox(g);
	    if(bezierCurve)
	    	drawBezierCurve(points, g);
	    
	     repaint();
    }
    
    
    public void drawBezierCurve(List<Point> points, Graphics2D g) {
    	
    	List<Point> firstGroup;
		List<Point> secondGroup;
		List<Point> thirdGroup;
    	
    	if(nPoints == 4) {
    		calculateCoeffs(points, g);
    	}
    	else if(nPoints == 7){
    		
    		firstGroup = new ArrayList<Point>();
    		secondGroup = new ArrayList<Point>();
    		
    		for(int i = 0; i < 4; i++) {
    			firstGroup.add(points.get(i));
    			secondGroup.add(points.get(i+3));
    		}
    		
    		calculateCoeffs(firstGroup, g);
    		g.setColor(Color.blue);
    		calculateCoeffs(secondGroup, g);
    		g.setColor(Color.black);
    	}
    	else if(nPoints == 10) {
    		
    		firstGroup = new ArrayList<Point>();
    		secondGroup = new ArrayList<Point>();
    		thirdGroup = new ArrayList<Point>();
    		
    		for(int i = 0; i < 4; i++) {
    			firstGroup.add(points.get(i));
    			secondGroup.add(points.get(i+3));
    			thirdGroup.add(points.get(i+6));
    		}
    		
    		calculateCoeffs(firstGroup, g);
    		g.setColor(Color.blue);
    		calculateCoeffs(secondGroup, g);
    		g.setColor(Color.red);
    		calculateCoeffs(thirdGroup, g);
    		g.setColor(Color.black);
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

       drawCurve(cx, cy, 1000, g);
        
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
    private boolean bezierCurve = false;
    private boolean polyline = true;
    private boolean existeLinha = false;
    private boolean linhaPresa = false;
    private boolean exited = false;
    private int resizeSide = 0;
    private int rotateSide = 0;
    private int nPoints = 4;
    private List<Point> points = new ArrayList<Point>();
    private int xPosInicial, yPosInicial;
    private int xPosAnterior, yPosAnterior;
    private Point editStartPoint = null;
    private Point grabbedPoint = null;
    private int boxXmin, boxXmax, boxYmin, boxYmax;
    private boolean rotate = false;
    private boolean resize = false;
    private boolean move = false;
    private boolean editOn = false;
    private double cX;
	private double cY;

}