import javax.media.opengl.GL;

import com.sun.opengl.util.GLUT;


public class DojoTrap extends DojoObject{
	public static final int DOJO_TRAP_BASIC = 0;
	public static final int DOJO_TRAP_SHOOTER = 1;
	public static final int DOJO_TRAP_SPIN = 2;
	
	private int type;
	private double activationZone;
	private long duration;
	
//	public DojoTrap(){
//		super();
//		type = DOJO_TRAP_BASIC;
//		duration = 0;
//		activationZone = 1;
//	}
	public DojoTrap(double size, double x, double y, double z){
		super();
		setPosition(new double[]{x,y,z}); //x,y,z is the corner of the trap
		setSize(size); //length of square
		duration = 0;
		activationZone = 1;
		type= DOJO_TRAP_BASIC;
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


	public void draw(GL myGL){ //TODO
		if(type == DOJO_TRAP_BASIC){
		//	System.out.println(getPosition()[0]+" "+getPosition()[1]+" "+ getPosition()[2]);
			myGL.glBegin(GL.GL_QUADS);
			myGL.glVertex3d(getPosition()[0],getPosition()[1], getPosition()[2]);
			myGL.glVertex3d(getPosition()[0]+getSize(),getPosition()[1], getPosition()[2]);
			myGL.glVertex3d(getPosition()[0]+getSize(),getPosition()[1]+getSize(), getPosition()[2]);
			myGL.glVertex3d(getPosition()[0],getPosition()[1]+getSize(), getPosition()[2]);
			
			myGL.glEnd();
	   //     myGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{1f,0f,0f,1f},0); 
			myGL.glPushMatrix();
		//	myGL.glScaled(25, 25, 25);
			myGL.glTranslated(getPosition()[0], getPosition()[1], getPosition()[2]);
			GLUT myGLUT = new GLUT();
			myGLUT.glutSolidSphere(getSize(), 100, 100);
			myGL.glPopMatrix();
		};
		if(type == DOJO_TRAP_SHOOTER);
		if(type == DOJO_TRAP_SPIN);
	}
}
