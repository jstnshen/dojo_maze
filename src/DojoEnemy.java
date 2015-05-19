import java.util.ArrayList;

import javax.media.opengl.GL;

import com.sun.opengl.util.GLUT;

/**
 * This class represents an enemy that can attack the player
 * @author Justin and Vijay
 *
 */
public class DojoEnemy extends DojoObject{
	public static final int DOJO_ENEMY_BASIC = 0;
	private double xDirection, yDirection, zDirection;
	private double speed;
	private double length, width, height;
	private int damage;
	private int health;
	private int type;
	private boolean enroute=false;
	private double destX=0;
	private double destY=0;
	DojoShape shape;
	private ArrayList<Integer> path;
	public DojoEnemy(double radius, double x, double y, double z, DojoShape shape){
		super();
		this.shape=shape;
		xDirection=1;
		yDirection =0 ;
		zDirection=0;
		setPos(new double[]{x,y,z});
		speed = .5;
		length= radius;//this will be default radus of the basic enemy
		width = 1;
		height= 1;
		damage=21;
		health= 10;
		type= DOJO_ENEMY_BASIC;
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

	public void draw(GL myGL, boolean draw){

		if(enroute){
			if(Math.sqrt(Math.pow(getPos()[0]-destX,2)+Math.pow(getPos()[1]-destY,2))<=speed){
				this.setPos(new double[]{destX, destY, getPos()[2]});
				enroute=false;
			}else{
				this.setPos(new double[]{getPos()[0]-Math.signum(getPos()[0]-destX)*speed,getPos()[1]-Math.signum(getPos()[1]-destY)*speed,getPos()[2]});
			}
//			if(Math.random()>.995){
//				this.setPos(new double[]{destX, destY, getPos()[2]});
//				enroute=false;
//			}
		}
		myGL.glPushMatrix();
		myGL.glTranslated(getPos()[0], getPos()[1], getPos()[2]);
		GLUT myGLUT = new GLUT();
		if(draw){
			if(health>=10)myGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{0f,1f,1f,1f},0);
			else myGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{1f,0f,0f,1f},0);
			shape.draw(myGL);
		}
		//myGLUT.glutSolidSphere(length, 50, 50);
		myGL.glPopMatrix();
		
	}

	/**
	 * update the path the enemy will take
	 * @param p Player the enemy is tracking
	 * @param k the map
	 */
	public void update(DojoPlayer p, KruskyKrab k){
		setPath(p,k);
	}

	/**
	 * set the path the enemy will take
	 * @param p Player the enemy is tracking
	 * @param k the map
	 */
	public void setPath(DojoPlayer p, KruskyKrab k){
		if(!enroute){
			if(Math.sqrt(Math.pow(p.getPos()[0]-this.getPos()[0], 2)+Math.pow(p.getPos()[1]-this.getPos()[1], 2))<k.size*k.n/5){
				int c1=findCell(p.getPos()[0], p.getPos()[1], k.cells);
				int c2=findCell(this.getPos()[0], this.getPos()[1], k.cells);
				int next = dijkstras(c1,c2,k.adj);
				//int next=10;
				setDest(k.cells[next].x+k.cells[next].width/2,k.cells[next].y+k.cells[next].height/2);	
			}
		}
	}public void setDest(double x, double y){
		destX=x;
		destY=y;
		enroute=true;
	}
	
	public static int findCell(double x, double y, DojoCell[] cells){
		for(int i=0;i<cells.length;i++){
			if(x>=cells[i].x&&cells[i].x+cells[i].width>=x&&y>=cells[i].y&&cells[i].y+cells[i].height>=y)return i;
		}
		return 0;
	}
	/**
	 * Use dijkstras to determine the path from the enemy to the player
	 * @param start location the
	 * @param end
	 * @param mat
	 * @return
	 */
	public int dijkstras(int start, int end, int[][] mat){
		boolean[] checked=new boolean[mat.length];
		int[] prev=new int[mat.length];
		double[] dists=new double[mat.length];
		int curr=start;
		checked[curr]=true;
		while(curr!=end){
			//System.out.println(curr);
			for(int i=0;i<mat.length;i++){
				if(mat[curr][i]!=0&&!checked[i]&&curr!=i){
					double temp=dists[curr]+mat[curr][i];
					if(dists[i]==0||temp<dists[i]){
						dists[i]=temp;
						prev[i]=curr;
					}
				}
			}
			double temp=Integer.MAX_VALUE;
			int index=curr;
			for(int i=0;i<dists.length;i++){
				if(!checked[i]&&dists[i]>0&&dists[i]<temp){
					temp=dists[i];
					index=i;
				}
				
			}
			curr=index;
			checked[curr]=true;
		}
		
		ArrayList<Integer> path=new ArrayList<Integer>();
		curr=end;
		path.add(curr);
		while(curr!=start){
			path.add(prev[curr]);
			curr=prev[curr];
		}
//		for(int i=0;i<path.size();i++)System.out.print(path.get(i)+" ");
//		System.out.println();
		if(path.size()>1)return path.get(1);
		return path.get(0);
		//this.path=path;
		//return start;
	}
}
