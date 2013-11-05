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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * @author Vladislav Pinzhuro, n. 34224
 * @author Joao Costa, n. 41726
 */
public class DemoXORPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public DemoXORPanel() {
		// Fundo de cor cinzenta
		setBackground(Color.LIGHT_GRAY);

		MouseListener ml = new MouseAdapter() {

			public void mouseEntered(MouseEvent e) {
				Graphics2D g = (Graphics2D) getGraphics();

				exited = false;

				if (linhaPresa) {
					g.setColor(Color.yellow);
					g.drawLine(xPosInicial, yPosInicial, xPosAnterior,
							yPosAnterior);
					g.setColor(g.getBackground());
				}

			}

			public void mouseExited(MouseEvent e) {
				Graphics2D g = (Graphics2D) getGraphics();

				exited = true;

				if (linhaPresa) {
					g.setColor(Color.yellow);
					g.drawLine(xPosInicial, yPosInicial, xPosAnterior,
							yPosAnterior);
					g.setColor(g.getBackground());
				}

			}

			public void mouseReleased(MouseEvent e) {
				grabbedPoint = null;
				editStartPoint = null;
				move = false;
				resize = false;
				rotate = false;
				editOn = false;

				if (boundingBox && points.size() == nPoints)
					drawBoundingBox((Graphics2D) getGraphics());

				if (rubberBanding) {
					rubberBanding = false;
					repaint();
					imprimir();
					rubberBandingPoint = null;
					rubberBandingTmpPoint = null;
				}

				setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			}

			public void mousePressed(MouseEvent e) {

				if (e.getButton() != MouseEvent.BUTTON1)
					return;
				if (e.getClickCount() != 1)
					return;

				// Obter as coordenadas actuais da posicao do rato
				int xPos = e.getX();
				int yPos = e.getY();

				// clicar numa vizinhanÃ§a de um ponto

				if (polyline) {
					for (Point p : points)
						if (Math.abs(xPos - p.getX()) <= 5
								&& Math.abs(yPos - p.getY()) <= 5)
							grabbedPoint = p;
				}

				if (grabbedPoint == null) {
					if (move == true) {
						setCursor(Cursor
								.getPredefinedCursor(Cursor.MOVE_CURSOR));
						editOn = true;
					} else if (resize == true) {
						System.out.println("resizing box");
						boxEdges[0] = boxXmax;
						boxEdges[1] = boxXmin;
						boxEdges[2] = boxYmax;
						boxEdges[3] = boxYmin;
						editOn = true;
					} else if (rotate == true) {
						System.out.println("rotating box");
						editOn = true;
					}
					editStartPoint = new Point(xPos, yPos, false);
					cX = (boxXmax + boxXmin) / 2;
					cY = (boxYmax + boxYmin) / 2;
					op = getPointsCopy(points);
				}

				Graphics2D g = (Graphics2D) getGraphics();

				if (linhaPresa && points.size() == nPoints - 1) { // Estamos a
																	// acabar
																	// uma
																	// linha...
					// Desenhar a linha nova (definitiva)

					g.setPaintMode();

					g.setColor(Color.yellow);
					g.drawLine(xPosInicial, yPosInicial, xPos, yPos);
					g.setColor(Color.black);

					linhaPresa = false;
					points.add(new Point(xPos, yPos, true));
					drawSquare(xPos, yPos, g);
					if (boundingBox && points.size() == nPoints)
						drawBoundingBox(g);
					if ((bezierCurve || bspline || catmull)
							&& points.size() == nPoints)
						drawCurves(points, g);
					repaint();

				} else if (polyline && points.size() < nPoints - 1) { // Estamos
																		// a
																		// iniciar
																		// uma
																		// linha

					linhaPresa = true;
					xPosInicial = xPosAnterior = xPos;
					yPosInicial = yPosAnterior = yPos;
					drawSquare(xPos, yPos, g);
					points.add(new Point(xPosInicial, yPosInicial, false));
				}

				if (rubberBanding && grabbedPoint == null && !editOn)
					rubberBandingPoint = new Point(xPos, yPos, false);
			}

		};

		MouseMotionListener mml = new MouseMotionAdapter() {

			public void mouseDragged(MouseEvent e) {
				if (points.size() < nPoints)
					return; // ainda nÃ£o se finalizou a linha por isso nÃ£o
							// deve poder arrastar pontos

				// Obter a posicao actual do cursor
				int x = e.getX();
				int y = e.getY();

				if (grabbedPoint != null && polyline) { // se se estiver a pegar
														// num ponto
					if (!exited) {
						grabbedPoint.setNewX(x);
						grabbedPoint.setNewY(y);
						if (continuityClass == 1 && nPoints > 4) {
							Point pA;
							Point pB;
							Point pP;
							double alpha;
							double beta;
							double rA;
							double rB;
							double dxA;
							double dyA;
							double dxB;
							double dyB;
							for (int i = 0; i < nPoints; i++) {
								if ((i == 2 || (i == 5 && nPoints == 10))
										&& grabbedPoint.getX() == points.get(i)
												.getX()
										&& grabbedPoint.getY() == points.get(i)
												.getY()) {
									pA = points.get(i);
									pP = points.get(i + 1);
									pB = points.get(i + 2);

									dxA = pA.getX() - pP.getX();
									dyA = pP.getY() - pA.getY();
									dxB = pB.getX() - pP.getX();
									dyB = pP.getY() - pB.getY();
									rB = Math.sqrt(Math.pow(dxB, 2)
											+ Math.pow(dyB, 2));
									alpha = Math.atan(dyA / dxA);
									if (dxA < 0)
										alpha += Math.PI;
									else if (dxA >= 0 && dyA < 0)
										alpha += Math.PI * 2;
									beta = alpha + Math.PI;

									pB.setNewX((int) (rB * Math.cos(beta)
											+ pP.getX() + 0.5));
									pB.setNewY((int) (-rB * Math.sin(beta)
											+ pP.getY() + 0.5));

								} else if ((i == 4 || (i == 7 && nPoints == 10))
										&& grabbedPoint.getX() == points.get(i)
												.getX()
										&& grabbedPoint.getY() == points.get(i)
												.getY()) {
									pB = points.get(i);
									pP = points.get(i - 1);
									pA = points.get(i - 2);

									dxA = pA.getX() - pP.getX();
									dyA = pP.getY() - pA.getY();
									dxB = pB.getX() - pP.getX();
									dyB = pP.getY() - pB.getY();
									rA = Math.sqrt(Math.pow(dxA, 2)
											+ Math.pow(dyA, 2));
									alpha = Math.atan(dyB / dxB);
									if (dxB < 0)
										alpha += Math.PI;
									else if (dxB >= 0 && dyB < 0)
										alpha += Math.PI * 2;
									beta = alpha + Math.PI;

									pA.setNewX((int) (rA * Math.cos(beta)
											+ pP.getX() + 0.5));
									pA.setNewY((int) (-rA * Math.sin(beta)
											+ pP.getY() + 0.5));

								}
							}
						} else if (continuityClass == 2 && nPoints > 4) {
							Point pA;
							Point pB;
							Point pP;
							for (int i = 0; i < nPoints; i++) {
								if ((i == 2 || (i == 5 && nPoints == 10))
										&& grabbedPoint.getX() == points.get(i)
												.getX()
										&& grabbedPoint.getY() == points.get(i)
												.getY()) {
									pA = points.get(i);
									pP = points.get(i + 1);
									pB = points.get(i + 2);
									pB.setNewX(2 * pP.getX() - pA.getX());
									pB.setNewY(2 * pP.getY() - pA.getY());
								} else if ((i == 4 || (i == 7 && nPoints == 10))
										&& grabbedPoint.getX() == points.get(i)
												.getX()
										&& grabbedPoint.getY() == points.get(i)
												.getY()) {
									pB = points.get(i);
									pP = points.get(i - 1);
									pA = points.get(i - 2);
									pA.setNewX(2 * pP.getX() - pB.getX());
									pA.setNewY(2 * pP.getY() - pB.getY());
								}
							}
						}
					}
					repaint();
				} else if (move && !exited) {
					for (Point p : points) {
						p.setNewX(p.getX() + (x - editStartPoint.getX()));
						p.setNewY(p.getY() + (y - editStartPoint.getY()));
						repaint();
					}
					editStartPoint.setNewX(x);
					editStartPoint.setNewY(y);
				} else if (resize && !exited) {
					if (resizeType == 0) {

						double multFactor = 0;

						switch (resizeSide) {

						case 0:
							if (y > boxEdges[2])
								break;

							multFactor = (double) (y - boxEdges[2])
									/ (boxEdges[3] - boxEdges[2]);
							break;
						case 2:
							if (x < boxEdges[1])
								break;

							multFactor = (double) (x - boxEdges[1])
									/ (boxEdges[0] - boxEdges[1]);

							break;
						case 4:
							if (y < boxEdges[3])
								break;

							multFactor = (double) (y - boxEdges[3])
									/ (boxEdges[2] - boxEdges[3]);
							break;
						case 6:
							if (y > boxEdges[0])
								break;

							multFactor = (double) (x - boxEdges[0])
									/ (boxEdges[1] - boxEdges[0]);
							break;
						}

						int i = 0;
						for (Point p : points) {

							p.setNewX((int) ((op.get(i).getX() - cX)
									* multFactor + cX));
							p.setNewY((int) ((op.get(i).getY() - cY)
									* multFactor + cY));

							i++;
							repaint();
						}

					} else if (resizeType == 1) {
						switch (resizeSide) {
						case 0: { // Resize cima
							// Factor multiplicante com base no ponto com o
							// maior y
							double multFactor = boxYmax - y;
							multFactor /= boxYmax - editStartPoint.getY();
							// Calculo do valor para substrair aos novos pontos
							// de modo a que estes
							// voltem aos sitios correctos
							if (multFactor >= 1) {
								int sub = ((int) (boxYmax * multFactor) - boxYmax);
								for (Point p : points) {
									p.setNewY((int) (p.getY() * multFactor - sub));
									repaint();
								}
							} else {
								if (boxYmax - y >= 10) {
									int add = (boxYmax - (int) (boxYmax * multFactor));
									for (Point p : points) {
										p.setNewY((int) (p.getY() * multFactor)
												+ add);
										repaint();
									}
								} else
									break;
							}
							editStartPoint.setNewY(y);
							break;
						}
						case 1: { // Resize cima-direita
							double multFactorX = x - boxXmin;
							double multFactorY = boxYmax - y;
							multFactorX /= editStartPoint.getX() - boxXmin;
							multFactorY /= boxYmax - editStartPoint.getY();
							if (multFactorX >= 1 && multFactorY >= 1) {
								int subX = ((int) (boxXmin * multFactorX) - boxXmin);
								int subY = ((int) (boxYmax * multFactorY) - boxYmax);
								for (Point p : points) {
									p.setNewX((int) (p.getX() * multFactorX - subX));
									p.setNewY((int) (p.getY() * multFactorY - subY));
									repaint();
								}
							} else if (multFactorX >= 1 && multFactorY < 1) {
								if (boxYmax - y >= 10) {
									int subX = ((int) (boxXmin * multFactorX) - boxXmin);
									int addY = (boxYmax - (int) (boxYmax * multFactorY));
									for (Point p : points) {
										p.setNewX((int) (p.getX() * multFactorX - subX));
										p.setNewY((int) (p.getY() * multFactorY)
												+ addY);
										repaint();
									}
								} else
									break;
							} else if (multFactorX < 1 && multFactorY >= 1) {
								if (x - boxXmin >= 10) {
									int addX = (boxXmin - (int) (boxXmin * multFactorX));
									int subY = ((int) (boxYmax * multFactorY) - boxYmax);
									for (Point p : points) {
										p.setNewX((int) (p.getX() * multFactorX + addX));
										p.setNewY((int) (p.getY() * multFactorY - subY));
										repaint();
									}
								} else
									break;
							} else {
								if ((boxYmax - y >= 20) && (x - boxXmin >= 20)) {
									int addX = (boxXmin - (int) (boxXmin * multFactorX));
									int addY = (boxYmax - (int) (boxYmax * multFactorY));
									for (Point p : points) {
										p.setNewX((int) (p.getX() * multFactorX + addX));
										p.setNewY((int) (p.getY() * multFactorY)
												+ addY);
										repaint();
									}
								} else
									break;
							}
							editStartPoint.setNewX(x);
							editStartPoint.setNewY(y);
						}
						case 2: { // Resize direita
							// Factor multiplicante com base no ponto com o
							// menor x
							double multFactor = x - boxXmin;
							multFactor /= editStartPoint.getX() - boxXmin;
							// Calculo do valor para substrair aos novos pontos
							// de modo a que estes
							// voltem aos sitios correctos
							if (multFactor >= 1) {
								int sub = ((int) (boxXmin * multFactor) - boxXmin);
								for (Point p : points) {
									p.setNewX((int) (p.getX() * multFactor - sub));
									repaint();
								}
							} else {
								if (x - boxXmin >= 10) {
									int add = (boxXmin - (int) (boxXmin * multFactor));
									for (Point p : points) {
										p.setNewX((int) (p.getX() * multFactor + add));
										repaint();
									}
								} else
									break;
							}
							editStartPoint.setNewX(x);
							break;
						}
						case 3: { // Resize direita-baixo
							double multFactorX = x - boxXmin;
							double multFactorY = y - boxYmin;
							multFactorX /= editStartPoint.getX() - boxXmin;
							multFactorY /= editStartPoint.getY() - boxYmin;

							if (multFactorX >= 1 && multFactorY >= 1) {
								int subX = ((int) (boxXmin * multFactorX) - boxXmin);
								int subY = ((int) (boxYmin * multFactorY) - boxYmin);
								for (Point p : points) {
									p.setNewX((int) (p.getX() * multFactorX - subX));
									p.setNewY((int) (p.getY() * multFactorY - subY));
									repaint();
								}
							} else if (multFactorX >= 1 && multFactorY < 1) {
								if (y - boxYmin >= 10) {
									int subX = ((int) (boxXmin * multFactorX) - boxXmin);
									int addY = (boxYmin - (int) (boxYmin * multFactorY));
									for (Point p : points) {
										p.setNewX((int) (p.getX() * multFactorX - subX));
										p.setNewY((int) (p.getY() * multFactorY + addY));
										repaint();
									}
								} else
									break;
							} else if (multFactorX < 1 && multFactorY >= 1) {
								if (x - boxXmin >= 10) {
									int addX = (boxXmin - (int) (boxXmin * multFactorX));
									int subY = ((int) (boxYmin * multFactorY) - boxYmin);
									for (Point p : points) {
										p.setNewX((int) (p.getX() * multFactorX + addX));
										p.setNewY((int) (p.getY() * multFactorY - subY));
										repaint();
									}
								} else
									break;
							} else {
								if ((x - boxXmin >= 20) && (y - boxYmin >= 20)) {
									int addX = (boxXmin - (int) (boxXmin * multFactorX));
									int addY = (boxYmin - (int) (boxYmin * multFactorY));
									for (Point p : points) {
										p.setNewX((int) (p.getX() * multFactorX + addX));
										p.setNewY((int) (p.getY() * multFactorY + addY));
										repaint();
									}
								} else
									break;
							}
							editStartPoint.setNewX(x);
							editStartPoint.setNewY(y);
						}
						case 4: { // Resize baixo
							// Factor multiplicante com base no ponto com o
							// menor y
							double multFactor = y - boxYmin;
							multFactor /= editStartPoint.getY() - boxYmin;
							// Calculo do valor para substrair aos novos pontos
							// de modo a que estes
							// voltem aos sitios correctos
							if (multFactor >= 1) {
								int sub = ((int) (boxYmin * multFactor) - boxYmin);
								for (Point p : points) {
									p.setNewY((int) (p.getY() * multFactor - sub));
									repaint();
								}
							} else {
								if (y - boxYmin >= 10) {
									int add = (boxYmin - (int) (boxYmin * multFactor));
									for (Point p : points) {
										p.setNewY((int) (p.getY() * multFactor + add));
										repaint();
									}
								} else
									break;
							}
							editStartPoint.setNewY(y);
							break;
						}
						case 5: { // Resize baixo-esquerda
							double multFactorX = boxXmax - x;
							double multFactorY = y - boxYmin;
							multFactorX /= boxXmax - editStartPoint.getX();
							multFactorY /= editStartPoint.getY() - boxYmin;

							if (multFactorX >= 1 && multFactorY >= 1) {
								int subX = ((int) (boxXmax * multFactorX) - boxXmax);
								int subY = ((int) (boxYmin * multFactorY) - boxYmin);
								for (Point p : points) {
									p.setNewX((int) (p.getX() * multFactorX - subX));
									p.setNewY((int) (p.getY() * multFactorY - subY));
									repaint();
								}
							} else if (multFactorX >= 1 && multFactorY < 1) {
								if (y - boxYmin >= 10) {
									int subX = ((int) (boxXmax * multFactorX) - boxXmax);
									int addY = (boxYmin - (int) (boxYmin * multFactorY));
									for (Point p : points) {
										p.setNewX((int) (p.getX() * multFactorX - subX));
										p.setNewY((int) (p.getY() * multFactorY + addY));
										repaint();
									}
								} else
									break;
							} else if (multFactorX < 1 && multFactorY >= 1) {
								if (boxXmax - x >= 10) {
									int addX = (boxXmax - (int) (boxXmax * multFactorX));
									int subY = ((int) (boxYmin * multFactorY) - boxYmin);
									for (Point p : points) {
										p.setNewX((int) (p.getX() * multFactorX + addX));
										p.setNewY((int) (p.getY() * multFactorY - subY));
										repaint();
									}
								} else
									break;
							} else {
								if ((y - boxYmin >= 20) && (boxXmax - x >= 20)) {
									int addX = (boxXmax - (int) (boxXmax * multFactorX));
									int addY = (boxYmin - (int) (boxYmin * multFactorY));
									for (Point p : points) {
										p.setNewX((int) (p.getX() * multFactorX + addX));
										p.setNewY((int) (p.getY() * multFactorY + addY));
										repaint();
									}
								} else
									break;
							}
							editStartPoint.setNewX(x);
							editStartPoint.setNewY(y);
							break;
						}
						case 6: { // Resize esquerda
							// Factor multiplicante com base no ponto com o
							// maior x
							double multFactor = boxXmax - x;
							multFactor /= boxXmax - editStartPoint.getX();
							// Calculo do valor para substrair aos novos pontos
							// de modo a que estes
							// voltem aos sitios correctos
							if (multFactor >= 1) {
								int sub = ((int) (boxXmax * multFactor) - boxXmax);
								for (Point p : points) {
									p.setNewX((int) (p.getX() * multFactor - sub));
									repaint();
								}
							} else {
								if (boxXmax - x >= 10) {
									int add = (boxXmax - (int) (boxXmax * multFactor));
									for (Point p : points) {
										p.setNewX((int) (p.getX() * multFactor + add));
										repaint();
									}
								} else
									break;
							}
							editStartPoint.setNewX(x);
							break;
						}
						case 7: { // Resize esquerda-cima
							double multFactorX = boxXmax - x;
							double multFactorY = boxYmax - y;
							multFactorX /= boxXmax - editStartPoint.getX();
							multFactorY /= boxYmax - editStartPoint.getY();

							if (multFactorX >= 1 && multFactorY >= 1) {
								int subX = ((int) (boxXmax * multFactorX) - boxXmax);
								int subY = ((int) (boxYmax * multFactorY) - boxYmax);
								for (Point p : points) {
									p.setNewX((int) (p.getX() * multFactorX - subX));
									p.setNewY((int) (p.getY() * multFactorY - subY));
									repaint();
								}
							} else if (multFactorX >= 1 && multFactorY < 1) {
								if (boxYmax - y >= 10) {
									int subX = ((int) (boxXmax * multFactorX) - boxXmax);
									int addY = (boxYmax - (int) (boxYmax * multFactorY));
									for (Point p : points) {
										p.setNewX((int) (p.getX() * multFactorX - subX));
										p.setNewY((int) (p.getY() * multFactorY)
												+ addY);
										repaint();
									}
								} else
									break;
							} else if (multFactorX < 1 && multFactorY >= 1) {
								if (boxXmax - x >= 10) {
									int addX = (boxXmax - (int) (boxXmax * multFactorX));
									int subY = ((int) (boxYmax * multFactorY) - boxYmax);
									for (Point p : points) {
										p.setNewX((int) (p.getX() * multFactorX + addX));
										p.setNewY((int) (p.getY() * multFactorY - subY));
										repaint();
									}
								} else
									break;
							} else {
								if ((boxXmax - x >= 20) && (boxYmax - y >= 20)) {
									int addX = (boxXmax - (int) (boxXmax * multFactorX));
									int addY = (boxYmax - (int) (boxYmax * multFactorY));
									for (Point p : points) {
										p.setNewX((int) (p.getX() * multFactorX + addX));
										p.setNewY((int) (p.getY() * multFactorY)
												+ addY);
										repaint();
									}
								} else
									break;
							}
							editStartPoint.setNewX(x);
							editStartPoint.setNewY(y);
							break;
						}
						default:
							break;
						}
					}
				} else if (rotate && !exited) {

					double alpha;
					double beta;
					double theta;

					alpha = Math.atan2(editStartPoint.getX() - cX,
							editStartPoint.getY() - cY);
					beta = Math.atan2(x - cX, y - cY);

					theta = alpha - beta;

					int i = 0;

					for (Point p : points) {

						double oldX = op.get(i).getX() - cX;
						double oldY = op.get(i).getY() - cY;
						double newX = oldX * Math.cos(theta) - oldY
								* Math.sin(theta) + cX;
						double newY = oldX * Math.sin(theta) + oldY
								* Math.cos(theta) + cY;

						p.setNewX((int) newX);
						p.setNewY((int) newY);

						repaint();

						i++;
					}

				}
				if (rubberBanding && rubberBandingPoint != null && !exited) {
					rubberBandingTmpPoint = new Point(e.getX(), e.getY(), false);
					repaint();
				}
			}

			public void mouseMoved(MouseEvent e) {

				// Obter a posicao actual do cursor
				int xPos = e.getX();
				int yPos = e.getY();
				boolean overPoint = false;

				if (points.size() == nPoints) // linha já terminada

					for (Point p : points)
						if (Math.abs(xPos - p.getX()) <= 5
								&& Math.abs(yPos - p.getY()) <= 5) {
							overPoint = true;
							setCursor(Cursor
									.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
						}

				if ((boundingBox && !overPoint) || (!polyline && boundingBox)) {

					// Move & Resize
					if ((boxXmin <= xPos && xPos <= boxXmax)
							&& (boxYmin <= yPos && yPos <= boxYmax)) {

						rotate = false;
						move = false;
						resize = true;
						/*
						 * 0-N; 1-NE; 2-E; 3-SE; 4-S; 5-SW; 6-W; 7-NW
						 */
						if ((yPos - boxYmin <= 5) && (boxXmax - xPos <= 5) && (resizeType==1)) {
							resizeSide = 1;
							setCursor(Cursor
									.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
						} else if ((boxYmax - yPos <= 5)
								&& (boxXmax - xPos <= 5) && (resizeType==1)) {
							resizeSide = 3;
							setCursor(Cursor
									.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
						} else if ((boxYmax - yPos <= 5)
								&& (xPos - boxXmin <= 5) && (resizeType==1)) {
							resizeSide = 5;
							setCursor(Cursor
									.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
						} else if ((yPos - boxYmin <= 5)
								&& (xPos - boxXmin <= 5) && (resizeType==1)) {
							resizeSide = 7;
							setCursor(Cursor
									.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
						} else if (yPos - boxYmin <= 5) {
							resizeSide = 0;
							setCursor(Cursor
									.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
						} else if (boxXmax - xPos <= 5) {
							resizeSide = 2;
							setCursor(Cursor
									.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
						} else if (boxYmax - yPos <= 5) {
							resizeSide = 4;
							setCursor(Cursor
									.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
						} else if (xPos - boxXmin <= 5) {
							resizeSide = 6;
							setCursor(Cursor
									.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
						} else {
							rotate = false;
							move = true;
							resize = false;
							setCursor(Cursor
									.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
						}
					}
					// Rotate
					else if (((Math.abs(boxYmin - yPos) <= 5) && (xPos
							- boxXmax <= 5 && boxXmin - xPos <= 5))
							|| ((Math.abs(boxYmax - yPos) <= 5) && (xPos
									- boxXmax <= 5 && boxXmin - xPos <= 5))
							|| ((Math.abs(boxXmin - xPos) <= 5) && (yPos
									- boxYmax <= 5 && boxYmin - yPos <= 5))
							|| ((Math.abs(boxXmax - xPos) <= 5) && (yPos
									- boxYmax <= 5 && boxYmin - yPos <= 5))) {
						setCursor(Cursor
								.getPredefinedCursor(Cursor.HAND_CURSOR));
						rotate = true;
						move = false;
						resize = false;
						
					} else {
						rotate = false;
						move = false;
						resize = false;
						setCursor(Cursor
								.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
					}

				}

				if (!linhaPresa)
					return; // Nao estamos a meio da especificacao duma linha!

				existeLinha = true;

				// Colocar em modo de desenho XOR
				Graphics2D g = (Graphics2D) getGraphics();
				g.setXORMode(getBackground());

				if (optionChanged) {
					g.setColor(Color.yellow);
					g.drawLine(xPosInicial, yPosInicial, xPosAnterior,
							yPosAnterior);
					optionChanged = false;
				}

				g.setColor(Color.yellow);
				// Apagar a linha antiga
				g.drawLine(xPosInicial, yPosInicial, xPosAnterior, yPosAnterior);

				// Desenha a linha nova
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

	// Este metodo eh o responsavel por actualizar a area de desenho do
	// componente
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		Point fp;
		Point sp;

		if (existeLinha) {

			g2.setStroke(new BasicStroke(1));

			if (polyline) {
				for (int i = 0; i < points.size() - 1; i++) {
					fp = points.get(i);
					sp = points.get(i + 1);

					g2.setColor(Color.yellow);
					g2.drawLine(fp.getX(), fp.getY(), sp.getX(), sp.getY());
					g2.setColor(Color.black);

					drawSquare(fp.getX(), fp.getY(), g2);

				}

				drawSquare(points.get(points.size() - 1).getX(),
						points.get(points.size() - 1).getY(), g2);
			}

			if (boundingBox && points.size() == nPoints)
				drawBoundingBox(g2);

			if ((bezierCurve || bspline || catmull) && points.size() == nPoints)
				drawCurves(points, g2);

			if (rubberBanding && rubberBandingPoint != null)
				drawRubberBanding(g2);

		}

	}

	// Metodo adicionado para se poder receber, do exterior (neste caso do
	// Frame),
	// um pedido para limpar o conteudo
	public void limparDesenho() {
		// Mudar o estado da aplicacao
		existeLinha = false;
		linhaPresa = false;
		points.clear();
		// Force-se a actualizacao do componente (acabarah por invocar o
		// paintComponent)
		repaint();
	}

	public void drawSquare(int x, int y, Graphics2D g) {
		g.drawRect(x - 5, y - 5, 10, 10);
	}

	public void drawRubberBanding(Graphics2D g) {
		g.setColor(Color.black);
		if (rubberBandingTmpPoint.getX() >= rubberBandingPoint.getX()
				&& rubberBandingTmpPoint.getY() >= rubberBandingPoint.getY())
			g.drawRect(rubberBandingPoint.getX(), rubberBandingPoint.getY(),
					rubberBandingTmpPoint.getX() - rubberBandingPoint.getX(),
					rubberBandingTmpPoint.getY() - rubberBandingPoint.getY());
		else if (rubberBandingTmpPoint.getX() >= rubberBandingPoint.getX()
				&& rubberBandingTmpPoint.getY() < rubberBandingPoint.getY())
			g.drawRect(rubberBandingPoint.getX(), rubberBandingTmpPoint.getY(),
					rubberBandingTmpPoint.getX() - rubberBandingPoint.getX(),
					rubberBandingPoint.getY() - rubberBandingTmpPoint.getY());
		else if (rubberBandingTmpPoint.getX() < rubberBandingPoint.getX()
				&& rubberBandingTmpPoint.getY() >= rubberBandingPoint.getY())
			g.drawRect(rubberBandingTmpPoint.getX(), rubberBandingPoint.getY(),
					rubberBandingPoint.getX() - rubberBandingTmpPoint.getX(),
					rubberBandingTmpPoint.getY() - rubberBandingPoint.getY());
		else
			g.drawRect(rubberBandingTmpPoint.getX(),
					rubberBandingTmpPoint.getY(), rubberBandingPoint.getX()
							- rubberBandingTmpPoint.getX(),
					rubberBandingPoint.getY() - rubberBandingTmpPoint.getY());
	}

	public List<Point> getPointsCopy(List<Point> ps) {

		List<Point> copy = new ArrayList<Point>();
		Point cp;

		for (Point p : ps) {
			cp = new Point(p.getX(), p.getY(), p.isLast());
			copy.add(cp);
		}
		return copy;
	}

	public void drawBoundingBox(Graphics2D g) {
		if (!linhaPresa) {

			int minX = points.get(0).getX();
			int minY = points.get(0).getY();
			int maxX = points.get(0).getX();
			int maxY = points.get(0).getY();

			for (Point p : points) {
				if (p.getX() > maxX)
					maxX = p.getX();
				if (p.getY() > maxY)
					maxY = p.getY();
				if (p.getX() < minX)
					minX = p.getX();
				if (p.getY() < minY)
					minY = p.getY();
			}

			boxXmin = minX;
			boxXmax = maxX;
			boxYmin = minY;
			boxYmax = maxY;

			g.setColor(Color.black);
			if ((move || resize || rotate) && (editOn)) {
				float dash[] = { 10.0f };
				g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
						BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
			} else
				g.setStroke(new BasicStroke(2));
			g.drawRect(minX, minY, maxX - minX, maxY - minY);
			g.setStroke(new BasicStroke(1));
			g.setColor(Color.gray);
			g.fillRect(minX, minY, maxX - minX, maxY - minY);
			g.setColor(Color.black);

			Point fp;
			Point sp;

			if (polyline) {
				for (int i = 0; i < points.size() - 1; i++) {
					fp = points.get(i);
					sp = points.get(i + 1);

					g.setColor(Color.yellow);
					g.drawLine(fp.getX(), fp.getY(), sp.getX(), sp.getY());
					g.setColor(Color.black);

					drawSquare(fp.getX(), fp.getY(), g);
					if (i == points.size() - 2)
						drawSquare(sp.getX(), sp.getY(), g);
				}
			}

			if ((bezierCurve || bspline || catmull) && points.size() == nPoints)
				drawCurves(points, g);

		}
	}

	public void setPointNumber(int n) {

		if (nPoints == n) {
			System.out.println("Já existem " + n + " pontos.");
		} else {
			nPoints = n;
			limparDesenho();
		}

	}

	public void changeOption(int n) {
		Graphics2D g = (Graphics2D) getGraphics();

		if (n == 1)
			boundingBox = (boundingBox) ? false : true;
		if (n == 2)
			bezierCurve = (bezierCurve) ? false : true;
		if (n == 3)
			polyline = (polyline) ? false : true;
		if (n == 4)
			bspline = (bspline) ? false : true;
		if (n == 5)
			catmull = (catmull) ? false : true;

		if (boundingBox && points.size() == nPoints)
			drawBoundingBox(g);
		if ((bezierCurve || bspline || catmull) && points.size() == nPoints)
			drawCurves(points, g);

		optionChanged = (optionChanged) ? false : true;

		repaint();
	}

	public void drawCurves(List<Point> pts, Graphics2D g) {

		List<Point> firstGroup;
		List<Point> secondGroup;
		List<Point> thirdGroup;

		List<Point> bsplineGroup;
		List<Point> bsplineGroup1;
		List<Point> bsplineGroup2;
		List<Point> bsplineGroup3;

		List<Point> catmullGroup;
		List<Point> catmullGroup1;
		List<Point> catmullGroup2;
		List<Point> catmullGroup3;

		if (nPoints == 4)
			calculateCoeffs(pts, g, 0);
		else if (nPoints == 7) {

			firstGroup = new ArrayList<Point>();
			secondGroup = new ArrayList<Point>();

			bsplineGroup = new ArrayList<Point>();
			bsplineGroup1 = new ArrayList<Point>();

			catmullGroup = new ArrayList<Point>();
			catmullGroup1 = new ArrayList<Point>();

			for (int i = 0; i < 4; i++) {
				firstGroup.add(pts.get(i));
				if (bspline) {
					bsplineGroup.add(pts.get(i + 1));
					bsplineGroup1.add(pts.get(i + 2));
				}
				if (catmull) {
					catmullGroup.add(pts.get(i + 1));
					catmullGroup1.add(pts.get(i + 2));
				}
				secondGroup.add(pts.get(i + 3));
			}

			g.setColor(Color.black);
			calculateCoeffs(firstGroup, g, 0);
			g.setColor(Color.DARK_GRAY);
			calculateCoeffs(secondGroup, g, 0);
			g.setColor(Color.black);

			if (bspline) {
				calculateCoeffs(bsplineGroup, g, 1);
				calculateCoeffs(bsplineGroup1, g, 1);
			}
			if (catmull) {
				calculateCoeffs(catmullGroup, g, 2);
				calculateCoeffs(catmullGroup1, g, 2);
			}

		} else if (nPoints == 10) {

			firstGroup = new ArrayList<Point>();
			secondGroup = new ArrayList<Point>();
			thirdGroup = new ArrayList<Point>();

			bsplineGroup = new ArrayList<Point>();
			bsplineGroup1 = new ArrayList<Point>();
			bsplineGroup2 = new ArrayList<Point>();
			bsplineGroup3 = new ArrayList<Point>();

			catmullGroup = new ArrayList<Point>();
			catmullGroup1 = new ArrayList<Point>();
			catmullGroup2 = new ArrayList<Point>();
			catmullGroup3 = new ArrayList<Point>();

			for (int i = 0; i < 4; i++) {
				firstGroup.add(pts.get(i));
				if (bspline) {
					bsplineGroup.add(pts.get(i + 1));
					bsplineGroup1.add(pts.get(i + 2));
					bsplineGroup2.add(pts.get(i + 4));
					bsplineGroup3.add(pts.get(i + 5));
				}
				if (catmull) {
					catmullGroup.add(pts.get(i + 1));
					catmullGroup1.add(pts.get(i + 2));
					catmullGroup2.add(pts.get(i + 4));
					catmullGroup3.add(pts.get(i + 5));
				}
				secondGroup.add(pts.get(i + 3));
				thirdGroup.add(pts.get(i + 6));
			}

			g.setColor(Color.black);
			calculateCoeffs(firstGroup, g, 0);
			g.setColor(Color.DARK_GRAY);
			calculateCoeffs(secondGroup, g, 0);
			g.setColor(Color.black);
			calculateCoeffs(thirdGroup, g, 0);

			if (bspline) {
				calculateCoeffs(bsplineGroup, g, 1);
				calculateCoeffs(bsplineGroup1, g, 1);
				calculateCoeffs(bsplineGroup2, g, 1);
				calculateCoeffs(bsplineGroup3, g, 1);
			}
			if (catmull) {
				calculateCoeffs(catmullGroup, g, 2);
				calculateCoeffs(catmullGroup1, g, 2);
				calculateCoeffs(catmullGroup2, g, 2);
				calculateCoeffs(catmullGroup3, g, 2);
			}

		}
	}

	public void calculateCoeffs(List<Point> pts, Graphics2D g, int curve) {

		int[][] bezierM = { { -1, 3, -3, 1 }, { 3, -6, 3, 0 }, { -3, 3, 0, 0 },
				{ 1, 0, 0, 0 } };

		double[][] bSplineM = { { -0.1666, 0.5, -0.5, 0.1666 },
				{ 0.5, -1, 0.5, 0 }, { -0.5, 0, 0.5, 0 },
				{ 0.1666, 0.6666, 0.1666, 0 }, };

		double[][] catmullRomM = { { -0.5, 1.5, -1.5, 0.5 },
				{ 1, -2.5, 2, -0.5 }, { -0.5, 0, 0.5, 0 }, { 0, 1, 0, 0 }, };

		int[] x = new int[4];
		int[] y = new int[4];

		int[] cx = new int[4];
		int[] cy = new int[4];

		int i = 0;

		for (Point p : pts) {
			x[i] = p.getX();
			y[i] = p.getY();
			i++;
		}

		if (bezierCurve && curve != 1 && curve != 2) {
			for (i = 0; i < x.length; i++) {
				cx[i] = bezierM[i][0] * x[0] + bezierM[i][1] * x[1]
						+ bezierM[i][2] * x[2] + bezierM[i][3] * x[3];
				cy[i] = bezierM[i][0] * y[0] + bezierM[i][1] * y[1]
						+ bezierM[i][2] * y[2] + bezierM[i][3] * y[3];
			}
			drawCurve(pts, cx, cy, 200, g, 0);
		}
		if (bspline && curve != 2) {
			for (i = 0; i < x.length; i++) {
				cx[i] = (int) (bSplineM[i][0] * x[0] + bSplineM[i][1] * x[1]
						+ bSplineM[i][2] * x[2] + bSplineM[i][3] * x[3]);
				cy[i] = (int) (bSplineM[i][0] * y[0] + bSplineM[i][1] * y[1]
						+ bSplineM[i][2] * y[2] + bSplineM[i][3] * y[3]);
			}
			if (curve == 0)
				g.setColor(Color.blue);
			else if (curve == 1)
				g.setColor(Color.cyan);
			drawCurve(pts, cx, cy, 200, g, 1);
		}
		if (catmull && curve != 1) {
			for (i = 0; i < x.length; i++) {
				cx[i] = (int) (catmullRomM[i][0] * x[0] + catmullRomM[i][1]
						* x[1] + catmullRomM[i][2] * x[2] + catmullRomM[i][3]
						* x[3]);
				cy[i] = (int) (catmullRomM[i][0] * y[0] + catmullRomM[i][1]
						* y[1] + catmullRomM[i][2] * y[2] + catmullRomM[i][3]
						* y[3]);
			}
			if (curve == 0)
				g.setColor(Color.red);
			else if (curve == 2)
				g.setColor(Color.pink);
			drawCurve(pts, cx, cy, 200, g, 2);
		}

	}

	public void drawCurve(List<Point> pts, int[] cx, int[] cy, int n,
			Graphics2D g, int curve) {
		g.setStroke(new BasicStroke(2));
		double t = 0;
		double delta = 1.0 / n;
		double t2, t3;
		int x = 0, y = 0;
		int prevx = 0;
		int prevy = 0;

		for (int i = 0; i < n; i++) {
			t += delta;
			t2 = t * t;
			t3 = t2 * t;

			x = (int) (cx[0] * t3 + cx[1] * t2 + cx[2] * t + cx[3]);
			y = (int) (cy[0] * t3 + cy[1] * t2 + cy[2] * t + cy[3]);

			if (i == 0) {
				prevx = x;
				prevy = y;
			}

			g.drawLine(prevx, prevy, x, y);
			prevx = x;
			prevy = y;
		}
	}

	public void imprimir() {
		/*
		 * Dimensoes da folha pretendida: 21 x 28 cm (A4) O ficheiro PostScript
		 * fica gravado com o tipo de papel 'US Letter', e este tem as dimensoes
		 * 8.50 x 11.00 inch -> 21.59 x 27.94 cm, resultando assim em nao
		 * preenchimento do espaco em largura todo. Por isso observa-se cerca de
		 * 0.4 cm de margem branca do lado direito. Mudar 21cm para 21.6cm
		 * resolve o problema, mas o enunciado exige a utilizacao de 21 x 28 cm.
		 */
		if (!points.isEmpty()) {
			if (rubberBandingTmpPoint == null)
				rubberBanding = true;
			else
				try {
					int difX = Math.abs(rubberBandingTmpPoint.getX()
							- rubberBandingPoint.getX());
					int difY = Math.abs(rubberBandingTmpPoint.getY()
							- rubberBandingPoint.getY());
					int lowX = Math.min(rubberBandingTmpPoint.getX(),
							rubberBandingPoint.getX());
					int lowY = Math.min(rubberBandingTmpPoint.getY(),
							rubberBandingPoint.getY());
					int highX = lowX + difX;
					int highY = lowY + difY;

					int cX = difX / 2;
					int cY = difY / 2;

					double ratioSelection = difX / difY;
					double ratioOutput = 21.0 / 28;
					double ratio;

					if (ratioSelection > ratioOutput)
						ratio = 21.0 / difX;
					else
						ratio = 28.0 / difY;

					BufferedWriter bw = new BufferedWriter(new FileWriter(
							new File("outpus.ps")));
					StringBuilder sb = new StringBuilder();

					sb.append("%!PS\n");
					sb.append("/cm {28.35 mul} def\n");
					if ((polyline || bezierCurve)) {
						sb.append((points.get(0).getX() - lowX - cX)
								* ratio
								+ 10.5
								+ " cm "
								+ (-(points.get(0).getY() - lowY - cY) * ratio + 14)
								+ " cm moveto\n");
						sb.append("gsave\n");
						for (int i = 1; i < nPoints; i++)
							sb.append((points.get(i).getX() - lowX - cX)
									* ratio
									+ 10.5
									+ " cm "
									+ (-(points.get(i).getY() - lowY - cY)
											* ratio + 14) + " cm lineto\n");
						sb.append("[ 0.2 cm 0.2 cm ] 0 setdash\n");
						sb.append("0.02 cm setlinewidth\n");
						sb.append("0.02 cm setlinewidth\n");
						sb.append("1.0 0.0 0.0 setrgbcolor\n");
						sb.append("stroke\n");
						sb.append("grestore\n");
						for (int i = 1; i < nPoints; i++) {
							sb.append((points.get(i).getX() - lowX - cX)
									* ratio
									+ 10.5
									+ " cm "
									+ (-(points.get(i).getY() - lowY - cY)
											* ratio + 14) + " cm ");
							if ((i == 3) && ((nPoints - i) > 1)) {
								sb.append("curveto\n");
								sb.append((points.get(i).getX() - lowX - cX)
										* ratio
										+ 10.5
										+ " cm "
										+ (-(points.get(i).getY() - lowY - cY)
												* ratio + 14) + " cm moveto\n");
							}
							if ((i == 6) && ((nPoints - i) > 1)) {
								sb.append("curveto\n");
								sb.append((lowX + points.get(i).getX() - cX)
										* ratio
										+ 10.5
										+ " cm "
										+ (-(points.get(i).getY() - lowY - cY)
												* ratio + 14) + " cm moveto\n");
							}
						}
						sb.append("curveto\n");
						sb.append("stroke\n");
					}
					// Se nao houver nem a linha guia nem a curva visivel, �
					// desenhada apenas a fronteira.
					sb.append(10.5 + " cm " + 14
							+ " cm 1 0 360 arc closepath\n");
					sb.append("0.2 cm setlinewidth\n");
					sb.append((lowX - lowX - cX) * ratio + 10.5 + " cm "
							+ (-(lowY - lowY - cY) * ratio + 14)
							+ " cm moveto\n");
					sb.append((highX - lowX - cX) * ratio + 10.5 + " cm "
							+ (-(lowY - lowY - cY) * ratio + 14)
							+ " cm lineto\n");
					sb.append((highX - lowX - cX) * ratio + 10.5 + " cm "
							+ (-(highY - lowY - cY) * ratio + 14)
							+ " cm lineto\n");
					sb.append((lowX - lowX - cX) * ratio + 10.5 + " cm "
							+ (-(highY - lowY - cY) * ratio + 14)
							+ " cm lineto\n");
					sb.append((lowX - lowX - cX) * ratio + 10.5 + " cm "
							+ (-(lowY - lowY - cY) * ratio + 14)
							+ " cm lineto\n");
					sb.append("stroke\n");
					sb.append("showpage");
					bw.write(sb.toString());
					bw.flush();
					bw.close();
					System.out.println("output.ps created");
				} catch (IOException e) {
					System.err.println("IOException");
					System.exit(1);
				}
		}

	}

	public void setContinuity(int i) {

		switch (i) {
		case 1: { // C0G1
			continuityClass = 1;
			break;
		}
		case 2: { // C1G1
			continuityClass = 2;
			break;
		}
		case 0: // C0G0
		default: {
			continuityClass = 0;
			break;
		}
		}

	}

	public void changed() {
		optionChanged = true;
		System.out.println("changed");
	}

	public void setResize(int n) {
		resizeType = n;
	}

	private int resizeType = 0;
	private boolean optionChanged = false;
	private int continuityClass;
	private Point rubberBandingPoint;
	private Point rubberBandingTmpPoint;
	private boolean rubberBanding = false;
	private boolean boundingBox = false;
	private boolean bezierCurve = false;
	private boolean bspline = false;
	private boolean catmull = false;
	private boolean polyline = true;
	private boolean existeLinha = false;
	private boolean linhaPresa = false;
	private boolean exited = false;
	private int resizeSide = 0;
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
	private int[] boxEdges = new int[4];
	private double cX;
	private double cY;
	private List<Point> op = new ArrayList<Point>();

}