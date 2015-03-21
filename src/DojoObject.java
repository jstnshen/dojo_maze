import java.awt.Color;

import javax.media.opengl.GL;

import com.sun.opengl.util.texture.Texture;


public class DojoObject {
	private double[] position;
	private Color color;
	private Texture tex;
	private double size;
	public DojoObject(){
		position = new double[]{0,0,0};
		color= new Color(0,0,0);
		tex= null;
		size = 1;
	}
	public DojoObject(double[] newPos, Color newCol, Texture newTex, double newSize){
		position = newPos;
		color= newCol;
		tex= newTex;
		size = newSize;
	}
	public double[] getPosition() {
		return position;
	}

	public void setPosition(double[] position) {
		this.position = position;
	}
	
	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public Texture getTex() {
		return tex;
	}
	public void setTex(Texture tex) {
		this.tex = tex;
	}
	public boolean collision(DojoObject o){
		return false;
	}
	public void move(double[] direction, double magnitude){
		if(direction.length == 3){
			double normalize=0;
			for(int i=0; i<direction.length; i++)
				normalize+=direction[i];
			for(int i=0; i<direction.length; i++){
				position[i]+=direction[i]/normalize*magnitude;
			}
		}
		else System.out.println("direction vector is invalid (move(): DojoObject)");
		
	}
	public void draw(GL myGL){
		myGL.glPointSize((float) size);
		myGL.glBegin(GL.GL_POINT);
		myGL.glVertex3d(position[0], position[1], position[2]);
		myGL.glEnd();
	}
	
}
