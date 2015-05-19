import javax.media.opengl.GL;

import com.sun.opengl.util.GLUT;

/**
 * Represents the traps in the game. Traps can deal touch damage to the player or shoot projectiles
 * @author Justin and Vijay
 *
 */
public class DojoTrap extends DojoObject{
	public static final int DOJO_TRAP_BASIC = 0;
	public static final int DOJO_TRAP_SHOOTER = 1;
	public static final int DOJO_TRAP_SPIN = 2;
	
	private int type;
	private int damage;
	private double activationZone;

	public DojoTrap(double size, double x, double y, double z){
		super();
		setPos(new double[]{x,y,z}); //x,y,z is the corner of the trap
		setSize(size); //length of square
		activationZone = size*20;
		damage = 10;
		int[] rand = {DOJO_TRAP_SHOOTER,DOJO_TRAP_BASIC};
		type= rand[(int)(Math.random()*rand.length)];
	}
	/**
	 * allow DojoTrap to attack the player if tghe player is in range
	 * @param p the DojoPlayer
	 * @return true if the player is in range, else false
	 */
	public boolean attack(DojoPlayer p){
		double dx = p.getPos()[0]-getPos()[0];
		double dy = p.getPos()[1]-getPos()[1];
		double dz = p.getPos()[2]-getPos()[2];
		double dist = Math.sqrt(dx*dx+dy*dy+dz*dz);
		if(dist<=activationZone && Math.random() > 0.99){
			return true;
		}
		return false;
	}
	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}

	public double getActivationZone() {
		return activationZone;
	}


	public void setActivationZone(double activationZone) {
		this.activationZone = activationZone;
	}
	public void setDamage(int newDamage){
		damage= newDamage;
	}
	public int getDamage(){
		return damage;
	}


	public void draw(GL myGL, boolean draw){
		if(draw){
			myGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{0f,1f,1f,1f},0);
			myGL.glPushMatrix();
			myGL.glTranslated(getPos()[0], getPos()[1], getPos()[2]);
			GLUT myGLUT = new GLUT();
			if(type == DOJO_TRAP_BASIC){
				myGLUT.glutSolidTorus(getSize()*0.1, getSize(), 10, 10);
			}
		
			if(type ==DOJO_TRAP_SHOOTER ){
				myGLUT.glutSolidTorus(getSize()*0.1, getSize(), 10, 10);
				myGL.glPushMatrix();
				myGL.glTranslated(0, 0, getSize());
				myGLUT.glutSolidSphere(getSize()*0.1, 50, 50);
				myGL.glPopMatrix();
			}
//			myGL.glBegin(GL.GL_QUADS);
//			myGL.glVertex3f((float) -getSize(), (float) -getSize(), 0);
//			myGL.glVertex3f((float) -getSize(), (float) getSize(), 0);
//			myGL.glVertex3f((float) getSize(), (float) getSize(), 0);
//			myGL.glVertex3f((float) getSize(), (float) -getSize(), 0);
//			myGL.glEnd();
			myGL.glPopMatrix();
		}
		
	}
}
