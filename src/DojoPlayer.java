import javax.media.opengl.GL;

import com.sun.opengl.util.GLUT;

/**
 * Stores information about the player
 * @author Justin and Vijay
 *
 */
public class DojoPlayer extends DojoObject{
	private double[] direction; //direction the player is facing
	private double[] up; //vector indicating the up vector for the player
	private double speed;
	private double accel;
	private double length, width, height;
	private int damage;
	private int health;
	private int type;
	private int bulletLeft;
	private int demolitionLeft;
	private int creationLeft;
	private boolean isAnimated; //true if the player is in an animated sequence (not controlled by keyboard)
	private boolean isJumping; //true if the player is in a jumping sequence
	private boolean isInvulnerable; //true if the player just got damaged 
	public DojoPlayer(){
		super();
		speed = 10;
		accel = -9;
		length= 5;
		width = 1;
		height= 1;
		damage=9001;
		health= 100;
		bulletLeft=10;
		demolitionLeft=1;
		creationLeft =1;
		setSize(length);
		isAnimated= false;
		isJumping = false;
		isInvulnerable =false;
	}
	public double[] getDir() {
		return direction;
	}
	public void setDir(double[] newDirection) {
		this.direction = newDirection;
	}
	public double[] getUp(){
		return up;
	}
	public void setUp(double[] newUp){
		this.up= newUp;
	}
	public boolean getState(){
		return isAnimated;
	}
	public void switchState(){
		isAnimated = !isAnimated;
	}
	public void jump(){
		isJumping= true;
		
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
		if(! isInvulnerable){
			this.health = health;
			isInvulnerable = true;
		}
		
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public void setBulletLeft(int bullet){
		bulletLeft=bullet;
	}
	public int getBulletLeft(){
		return bulletLeft;
	}
	public int getDemoLeft(){
		return demolitionLeft;
	}
	public void setDemoLeft(int n){
		demolitionLeft=n;
	}
	public void setCreationLeft(int n){
		creationLeft=n;
	}
	public int getCreationLeft(){
		return creationLeft;
	}
	public void attack(){
		if(bulletLeft>0){
			bulletLeft--;
		}
		
	}
	public void destroy(){
		if(demolitionLeft>0){
			demolitionLeft--;
		}	
	}
	public void create(){
		if(creationLeft>0){
			creationLeft--;
		}
	}
	@Override
	public void draw(GL myGL){
		myGL.glPushMatrix();
		myGL.glTranslated(getPos()[0], getPos()[1], getPos()[2]);
		GLUT myGLUT = new GLUT();
		myGLUT.glutSolidSphere(getSize(), 50, 50);
		myGL.glPopMatrix();
	}
	/**
	 * display player related status on the canvas such as health and ammo left
	 * @param myGL
	 */
	public void displayStatus(GL myGL){ 
		//draw health bar
		myGL.glColor3f(1f,0f,0f);
		if(isInvulnerable) myGL.glColor3f(1,1,0);
		myGL.glBegin(GL.GL_QUADS);
		myGL.glVertex2d(0, 0);
		myGL.glVertex2d(0, 20);
		myGL.glVertex2d(health*2, 20);
		myGL.glVertex2d(health*2, 0);
		myGL.glEnd();

		myGL.glRasterPos2f(health*2, 0);
		GLUT myGLUT= new GLUT();
		myGLUT.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_24, "HP: "+health +" Bullet Left:"+bulletLeft);
	}
	
	/**
	 * update the status of the player when it is invulnerable or jumping
	 */
	public void update(){
//		if(isAnimated && isJumping){
//			
//		}
		if(isJumping){
			double dt =0.05;
			speed += accel*dt; 
			double dz = speed*dt;
			setPos(new double[]{getPos()[0], getPos()[1], getPos()[2]+dz});
			if(getPos()[2] < 15){
				setPos(new double[]{getPos()[0], getPos()[1], 15});
				speed = 10;
				isJumping = false;
			}
		}
		if(isInvulnerable){
			double dt =0.1;
			speed += accel*dt; 
			double dz = speed*dt;
			setPos(new double[]{getPos()[0], getPos()[1], getPos()[2]+dz});
			if(getPos()[2] < 15){
				setPos(new double[]{getPos()[0], getPos()[1], 15});
				speed = 10;
				isInvulnerable = false;
			}
		}
	}
}
