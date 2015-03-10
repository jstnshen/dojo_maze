
public class DojoProjectile extends DojoObject {
	double xDirection, yDirection, zDirection;
	double speed;
	double locX, locY, locZ;
	double radius;
	public DojoProjectile(){
		super();
		xDirection=0; yDirection=0; zDirection=0;
		locX=0; locY=0; locZ=0;
		radius=1;
	}
	public double getxDirection() {
		return xDirection;
	}
	public void setxDirection(double xDirection) {
		this.xDirection = xDirection;
	}
	public double getyDirection() {
		return yDirection;
	}
	public void setyDirection(double yDirection) {
		this.yDirection = yDirection;
	}
	public double getzDirection() {
		return zDirection;
	}
	public void setzDirection(double zDirection) {
		this.zDirection = zDirection;
	}
	public double getSpeed() {
		return speed;
	}
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	public double getLocX() {
		return locX;
	}
	public void setLocX(double locX) {
		this.locX = locX;
	}
	public double getLocY() {
		return locY;
	}
	public void setLocY(double locY) {
		this.locY = locY;
	}
	public double getLocZ() {
		return locZ;
	}
	public void setLocZ(double locZ) {
		this.locZ = locZ;
	}
	public double getRadius() {
		return radius;
	}
	public void setRadius(double radius) {
		this.radius = radius;
	}
	
}
