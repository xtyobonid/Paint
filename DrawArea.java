import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.util.ArrayList;

public class DrawArea extends JComponent implements MouseMotionListener, MouseListener {
	private Color currentColor;
	private boolean dragging;
	private String drawType;
	private int toX;
	private int toY;
	private Point[] cubic;
	private int cubicCount;
	private ArrayList<Integer> polyX;
	private ArrayList<Integer> polyY;
	private int tempX;
	private int tempY;
	private int brushSize;
	private BufferedImage image;
	private static final int NEGLIGENT_CLICK_DISTANCE = 5;
	
	public DrawArea() {
		drawType = "Brush";
		currentColor = Color.BLACK;
		dragging = false;
		addMouseListener(this);
		addMouseMotionListener(this);
		cubic = new Point[4];
		cubicCount = 0;
		polyX = null;
		polyY = null;
		toX = -1;
		toY = -1;
		brushSize = 1;
		image = new BufferedImage(800,800, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.createGraphics();
		g.fillRect(0, 0, 1000, 1000);
		g.dispose();
	}
	
	public void setCurrentColor(Color newColor) {
		currentColor = newColor;
	}
	
	public void setBrushStroke(int size) {
		brushSize = size;
	}
	
	public void setDrawType(String s) {
		drawType = s;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, null);
		if ((dragging && toX != -1) || cubicCount != 0 || polyX != null) {
			Graphics2D g2 = (Graphics2D) g;
			if (drawType.substring(0,5).equals("Shape")) {
				g2.setColor(currentColor);
				g2.setStroke(new BasicStroke(brushSize));
				if (drawType.substring(5).equals("1")) {
					g2.drawLine(toX, toY, tempX, tempY);
				} else if (drawType.substring(5).equals("2")) {
					if (cubicCount == 1) {
						g2.drawOval((int)cubic[0].getX(), (int)cubic[0].getY(), 5, 5);
					} else if (cubicCount == 2) {
						g2.drawLine((int)cubic[0].getX(), (int)cubic[0].getY(), (int)cubic[1].getX(), (int)cubic[1].getY());
					} else if (cubicCount == 3) {
						QuadCurve2D q = new QuadCurve2D.Double();
						q.setCurve(cubic[0].getX(), cubic[0].getY(), cubic[1].getX(), cubic[1].getY(), cubic[2].getX(), cubic[2].getY());
						g2.draw(q);
					} else if (cubicCount == 4) {
						CubicCurve2D c = new CubicCurve2D.Double();
						c.setCurve(cubic[0].getX(), cubic[0].getY(), cubic[1].getX(), cubic[1].getY(), cubic[2].getX(), cubic[2].getY(), cubic[3].getX(), cubic[3].getY());
						g2.draw(c);
					}
				} else if (drawType.substring(5).equals("3")) {
					int x = tempX;
					int y = tempY;
					if (toX < x && toY < y)
						g2.drawOval(toX, toY, x - toX, y - toY);
					else if (x < toX && toY < y)
						g2.drawOval(x, toY, toX - x, y - toY);
					else if (toX < x && y < toY)
						g2.drawOval(toX, y, x - toX, toY - y);
					else
						g2.drawOval(x, y, toX - x, toY - y);
				} else if (drawType.substring(5).equals("4")) {
					int x = tempX;
					int y = tempY;
					if (toX < x && toY < y)
						g2.drawRect(toX, toY, x - toX, y - toY);
					else if (x < toX && toY < y)
						g2.drawRect(x, toY, toX - x, y - toY);
					else if (toX < x && y < toY)
						g2.drawRect(toX, y, x - toX, toY - y);
					else
						g2.drawRect(x, y, toX - x, toY - y);
				} else if (drawType.substring(5).equals("5")) {
					int x = tempX;
					int y = tempY;
					if (toX < x && toY < y)
						g2.drawRoundRect(toX, toY, x - toX, y - toY, 10, 10);
					else if (x < toX && toY < y)
						g2.drawRoundRect(x, toY, toX - x, y - toY, 10, 10);
					else if (toX < x && y < toY)
						g2.drawRoundRect(toX, y, x - toX, toY - y, 10, 10);
					else
						g2.drawRoundRect(x, y, toX - x, toY - y, 10, 10);
				} else if (drawType.substring(5).equals("6")) {
					if (polyX != null) {
						if (polyX.size() == 1) {
							g2.drawOval(polyX.get(0), polyY.get(0), 5, 5);
						} else {
							for (int i = 0; i < polyX.size() - 1; i++) {
								g2.drawLine(polyX.get(i), polyY.get(i), polyX.get(i+1), polyY.get(i+1));
							}
						}
					}
				} else if (drawType.substring(5).equals("7")) {
					int x = tempX;
					int y = tempY;
				
					int[] triX = {toX, (toX + x)/2, x};
					int[] triY = {y, toY, y};
					g2.drawPolygon(triX, triY, 3);
				}
			}
		}
		g.dispose();
	}
	
	public void mousePressed(MouseEvent event) {
		dragging = true;
		toX = event.getX();
		toY = event.getY();
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void mouseReleased(MouseEvent event) {
		dragging = false;
		if (drawType.substring(0,5).equals("Shape")) {
			Graphics2D g2 = (Graphics2D) image.createGraphics();
			g2.setColor(currentColor);
			g2.setStroke(new BasicStroke(brushSize));
			if (drawType.substring(5).equals("1")) {
				g2.drawLine(toX, toY, event.getX(), event.getY());
			} else if (drawType.substring(5).equals("2")) {
				if (cubicCount < 4) {
					cubic[cubicCount++] = new Point(event.getX(), event.getY());
				} else if (cubicCount >= 4) {
					cubicCount = 0;
					CubicCurve2D c = new CubicCurve2D.Double();
					c.setCurve(cubic[0].getX(), cubic[0].getY(), cubic[1].getX(), cubic[1].getY(), cubic[2].getX(), cubic[2].getY(), cubic[3].getX(), cubic[3].getY());
					g2.draw(c);
				}
			} else if (drawType.substring(5).equals("3")) {
				int x = event.getX();
				int y = event.getY();
				if (toX < x && toY < y)
					g2.drawOval(toX, toY, x - toX, y - toY);
				else if (x < toX && toY < y)
					g2.drawOval(x, toY, toX - x, y - toY);
				else if (toX < x && y < toY)
					g2.drawOval(toX, y, x - toX, toY - y);
				else
					g2.drawOval(x, y, toX - x, toY - y);
			} else if (drawType.substring(5).equals("4")) {
				int x = event.getX();
				int y = event.getY();
				if (toX < x && toY < y)
					g2.drawRect(toX, toY, x - toX, y - toY);
				else if (x < toX && toY < y)
					g2.drawRect(x, toY, toX - x, y - toY);
				else if (toX < x && y < toY)
					g2.drawRect(toX, y, x - toX, toY - y);
				else
					g2.drawRect(x, y, toX - x, toY - y);
			} else if (drawType.substring(5).equals("5")) {
				int x = event.getX();
				int y = event.getY();
				if (toX < x && toY < y)
					g2.drawRoundRect(toX, toY, x - toX, y - toY, 10, 10);
				else if (x < toX && toY < y)
					g2.drawRoundRect(x, toY, toX - x, y - toY, 10, 10);
				else if (toX < x && y < toY)
					g2.drawRoundRect(toX, y, x - toX, toY - y, 10, 10);
				else
					g2.drawRoundRect(x, y, toX - x, toY - y, 10, 10);
			} else if (drawType.substring(5).equals("6")) {
				int x = event.getX();
				int y = event.getY();
				
				if (polyX != null) {
					//check collisions
					if (Math.abs(polyX.get(0) - x) <= NEGLIGENT_CLICK_DISTANCE && Math.abs(polyY.get(0) - y) <= NEGLIGENT_CLICK_DISTANCE) {
						int[] insX = listConvert(polyX);
						int[] insY = listConvert(polyY);
						g2.drawPolygon(insX, insY, insX.length);
						polyX = null;
						polyY = null;
					}
					
					if (polyX != null) {
						polyX.add(x);
						polyY.add(y);
					}
				} else {
					polyX = new ArrayList<Integer>();
					polyY = new ArrayList<Integer>();
					polyX.add(x);
					polyY.add(y);
				}
			} else if (drawType.substring(5).equals("7")) {
				int x = event.getX();
				int y = event.getY();
				
				int[] triX = {toX, (toX + x)/2, x};
				int[] triY = {y, toY, y};
				g2.drawPolygon(triX, triY, 3);
			}
			repaint();
		}
	}
	
	public static int[] listConvert(ArrayList<Integer> orig) {
		int[] ret = new int[orig.size()];
		for (int i = 0; i < orig.size(); i++) {
			ret[i] = orig.get(i);
		}
		return ret;
	}
	
	public void mouseDragged(MouseEvent event) {
		if (dragging) {
			if (drawType.equals("Brush")) {
				int x = event.getX();
				int y = event.getY();
				
				Graphics2D g2 = (Graphics2D) image.createGraphics();
				g2.setColor(currentColor);
				g2.setStroke(new BasicStroke(brushSize));
				g2.drawLine(toX, toY, x, y);
				toX = x;
				toY = y;
				repaint();
			} else if (drawType.substring(0,5).equals("Shape")) {
				tempX = event.getX();
				tempY = event.getY();
				repaint();
			}
		}
	}
	
	public void mouseClicked(MouseEvent event) {}
	public void mouseEntered(MouseEvent event) {}
	public void mouseExited(MouseEvent event) {}
	
	public void mouseMoved(MouseEvent event) {}
}