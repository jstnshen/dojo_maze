import javax.media.opengl.GL;

import com.sun.opengl.util.GLUT;


public class DojoProjectile extends DojoObject {
	public static final int DOJO_PROJ_BASIC = 0;
	public static final int DOJO_PROJ_DEMO = 1;

	private double[] direction;
	private double speed;
	private int damage;
	private int type;
	public DojoProjectile(double size, double x, double y, double z, double[] dir, double newSpeed, int newType){
		super();
		setPos(new double[]{x,y,z});
		double dx =(dir[0]-getPos()[0]);
		double dy =(dir[1]-getPos()[1]);
		double dz =(dir[2]-getPos()[2]);
		double mag = Math.sqrt(dx*dx+dy*dy+dz*dz);
		setDir(new double[]{dx/mag,dy/mag,dz/mag});
		setSize(size);
		speed=newSpeed;
		damage = 5;
		type = newType;

	}
	public int getType(){
		return this.type;
	}
	public void setType(int newType){
		type = newType;
	}
	public double getSpeed() {
		return speed;
	}
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	public double[] getDir() {
		return direction;
	}
	public void setDir(double[] newDirection) {
		this.direction = newDirection;
	}
	
	public void draw(GL myGL){
		myGL.glPushMatrix();
		myGL.glTranslated(getPos()[0], getPos()[1], getPos()[2]);
		GLUT myGLUT = new GLUT();
		myGLUT.glutSolidSphere(getSize(), 10, 10);
		myGL.glPopMatrix();
	}
	public void update(){
		setPos(new double[]{getPos()[0]+getDir()[0]*speed, getPos()[1]+getDir()[1]*speed, getPos()[2]+getDir()[2]*speed});
	}
}
