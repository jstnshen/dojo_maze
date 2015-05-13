import java.util.ArrayList;

import javax.media.opengl.GL;

import com.sun.opengl.util.GLUT;

/*
 * 
power up:
demolition, creation -> (world creation mode)
health
torches (sensor)
ammo
 */
public class DojoBonus extends DojoObject{
	public static final int DOJO_BONUS_BASIC = 0; //health
	public static final int DOJO_BONUS_DEMOLITION = 1; //ability to destroy wall
	public static final int DOJO_BONUS_CREATION = 2; //ability to create wall
	public static final int DOJO_BONUS_LIGHT = 3;
	public static final int DOJO_BONUS_AMMO = 4;
	
	private int type;
	private int health;
	private int bullet;

	
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
		health = 20;
		bullet = 5;
		//type= DOJO_BONUS_BASIC;
		type = DOJO_BONUS_AMMO;
	}
	public int getHealth(){
		return this.health;
	}
	public void setHealth(int newHealth){
		this.health= newHealth;
	}
	public int getBullet(){
		return this.bullet;
	}
	public void setBullet(int newBullet){
		this.bullet = newBullet;
	}
	public void activate(DojoPlayer p, KruskyKrab m, ArrayList<DojoProjectile> proj){
		switch(getType()){
		case DOJO_BONUS_BASIC:
			p.setHealth(p.getHealth()+ getHealth());
			break;
		case DOJO_BONUS_DEMOLITION :
			p.setDemoLeft(p.getDemoLeft()+ 1);
/*
 * 			

if(proj.get(i).getType() == DEMO && ! maze.edges[index].joined) {
         maze.edges[index].joined=true;
}
			if(maze.edges[index].joined){  //construct a new wall at the edge indicated with joined
	      		maze.edges[index].joined=false;
 */
			break;
		case DOJO_BONUS_CREATION:
			p.setCreationLeft(p.getCreationLeft()+ 1);
			break;
		case DOJO_BONUS_LIGHT:
			
			break;
		case DOJO_BONUS_AMMO:
			p.setBulletLeft(p.getBulletLeft()+bullet);
			
			break;
		}
	}
	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}

	public void draw(GL myGL){
		myGL.glPushMatrix();
		myGL.glTranslated(getPos()[0], getPos()[1], getPos()[2]);
		GLUT myGLUT = new GLUT();
		myGLUT.glutSolidSphere(getSize(), 50, 50);
		myGLUT.glutSolidTorus(getSize()*0.1, getSize(), 10, 10);

		myGL.glPopMatrix();
		
	}

//	public void draw(GL myGL){
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
