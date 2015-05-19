import java.util.ArrayList;

import javax.media.opengl.GL;

import com.sun.opengl.util.GLUT;

/**
 * This class stores information about the bonuses in the game, including the bonus type, effects, and graphical info.
 * @author Justin and Vijay
 *
 */
public class DojoBonus extends DojoObject{
	public static final int DOJO_BONUS_BASIC = 0; //health
	public static final int DOJO_BONUS_AMMO = 4; //ammunition
	//not implemented
	public static final int DOJO_BONUS_DEMOLITION = 1; //ability to destroy wall
	public static final int DOJO_BONUS_CREATION = 2; //ability to create wall
	public static final int DOJO_BONUS_LIGHT = 3; //torch

	
	private int type;
	private int health;
	private int bullet;

	public DojoBonus(double size, double x, double y, double z){
		super();
		setPos(new double[]{x,y,z}); //x,y,z is the corner of the trap
		setSize(size); //length of square
		health = 20;
		bullet = 5;
		//type= DOJO_BONUS_BASIC;
		int[] rand = {DOJO_BONUS_AMMO,DOJO_BONUS_BASIC};
		type = rand[(int)(Math.random()*rand.length)];
	}
	/**
	 * 
	 * @return the amount of health the bonus restores
	 */
	public int getHealth(){
		return this.health;
	}
	/**
	 * set the amount of health the bonus restores
	 * @param newHealth amount of health the bonus will restore
	 */
	public void setHealth(int newHealth){
		this.health= newHealth;
	}
	public int getBullet(){
		return this.bullet;
	}
	public void setBullet(int newBullet){
		this.bullet = newBullet;
	}
	/**
	 * activate the effect of the bonus
	 * @param p the player that got the bonus
	 * @param m the map the player is in
	 * @param proj list of projectiles in the map
	 */
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
		if(getType() == DOJO_BONUS_BASIC) myGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{0f,1f,0f,1f},0);
		myGL.glPushMatrix();
		myGL.glTranslated(getPos()[0], getPos()[1], getPos()[2]);
		GLUT myGLUT = new GLUT();
		myGLUT.glutSolidSphere(getSize(), 50, 50);
		myGLUT.glutSolidTorus(getSize()*0.1, getSize(), 10, 10);

		myGL.glPopMatrix();
		
	}

}
