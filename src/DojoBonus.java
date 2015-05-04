import javax.media.opengl.GL;

import com.sun.opengl.util.GLUT;


public class DojoBonus extends DojoObject{
	public static final int DOJO_BONUS_BASIC = 0;
	public static final int DOJO_BONUS_SHOOTER = 1;
	public static final int DOJO_BONUS_SPIN = 2;
	
	private int type;
	private int damage;
	private double activationZone;
	private long duration;
	
//	public DojoTrap(){
//		super();
//		type = DOJO_BONUS_BASIC;
//		duration = 0;
//		activationZone = 1;
//	}
	public DojoBonus(double size, double x, double y, double z){
		super();
		setPos(new double[]{x,y,z}); //x,y,z is the corner of the trap
		setSize(size); //length of square
		duration = 0;
		activationZone = 1;
		damage = 10;
		type= DOJO_BONUS_BASIC;
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

	public long getDuration() {
		return duration;
	}


	public void setDuration(long duration) {
		this.duration = duration;
	}

	public void activate(DojoPlayer p){ 
		//TODO
	}
	public void deactivate(){ //TODO
		
	}

	public void draw(GL myGL){
		myGL.glPushMatrix();
		myGL.glTranslated(getPos()[0], getPos()[1], getPos()[2]);
		GLUT myGLUT = new GLUT();
		myGLUT.glutSolidSphere(getSize(), 50, 50);
		myGLUT.glutSolidTorus(getSize()*0.1, getSize(), 10, 10);
		
//		myGL.glBegin(GL.GL_QUADS);
//		myGL.glVertex3f((float) -getSize(), (float) -getSize(), 0);
//		myGL.glVertex3f((float) -getSize(), (float) getSize(), 0);
//		myGL.glVertex3f((float) getSize(), (float) getSize(), 0);
//		myGL.glVertex3f((float) getSize(), (float) -getSize(), 0);
//		myGL.glEnd();
		myGL.glPopMatrix();
		
	}

//	public void draw(GL myGL){ //TODO
//		if(type == DOJO_BONUS_BASIC){
//		//	System.out.println(getPos()[0]+" "+getPos()[1]+" "+ getPos()[2]);
//			myGL.glBegin(GL.GL_QUADS);
//			myGL.glVertex3d(getPos()[0],getPos()[1], getPos()[2]);
//			myGL.glVertex3d(getPos()[0]+getSize(),getPos()[1], getPos()[2]);
//			myGL.glVertex3d(getPos()[0]+getSize(),getPos()[1]+getSize(), getPos()[2]);
//			myGL.glVertex3d(getPos()[0],getPos()[1]+getSize(), getPos()[2]);
//			
//			myGL.glEnd();
//	   //     myGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{1f,0f,0f,1f},0); 
//			myGL.glPushMatrix();
//		//	myGL.glScaled(25, 25, 25);
//			myGL.glTranslated(getPos()[0], getPos()[1], getPos()[2]);
//			GLUT myGLUT = new GLUT();
//			myGLUT.glutSolidSphere(getSize(), 100, 100);
//			myGL.glPopMatrix();
//		};
//		if(type == DOJO_BONUS_SHOOTER);
//		if(type == DOJO_BONUS_SPIN);
//	}
}
