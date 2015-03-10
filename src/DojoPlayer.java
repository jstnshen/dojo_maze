import javax.media.opengl.GL;


public class DojoPlayer extends DojoObject{
	private double xDirection, yDirection, zDirection;
	private double speed;
	private double length, width, height;
	private int damage;
	private int health;
	private int type;
	public DojoPlayer(){
		super();
		xDirection=1;
		yDirection =0 ;
		zDirection=0;
		speed = 1;
		length= 1;
		width = 1;
		height= 1;
		damage=9001;
		health= 42;
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
	public double getLength() {
		return length;
	}
	public void setLength(double length) {
		this.length = length;
	}
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	public int getDamage() {
		return damage;
	}
	public void setDamage(int damage) {
		this.damage = damage;
	}
	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = health;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public void attack(DojoPlayer p){
		
	}
	@Override
	public void draw(GL myGL){
		
	}

	public void update(){
		
	}
}
