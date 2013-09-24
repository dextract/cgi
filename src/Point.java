public class Point {
	
	private int x, y;
	private boolean last;
	
	public Point(int x, int y, boolean last) {
		this.x = x;
		this.y = y;
		this.last = last;
	}
	
	public int getX() { return x; }
	
	public int getY() { return y; }
	
	public boolean isLast() { return last; }
	
	public void setNewX(int nX) { x = nX; }
	
	public void setNewY(int nY) { y = nY; }

}