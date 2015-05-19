import java.awt.Color;

import javax.media.opengl.GL;

import com.sun.opengl.util.texture.Texture;


/**
 * Represent the walls in the maze. Contain information on size and location
 * @author Justin and Vijay
 *
 */
public class DojoWall extends DojoObject{

	Color color;
	double x1,x2,y1,y2,length;
	DojoCell c1,c2;
	boolean joined=false;
	public DojoWall(double x, double y, double x2, double y2,DojoCell c1, DojoCell c2,double length){
		x1=x;
		this.x2=x2;
		y1=y;
		this.y2=y2;
		this.c1=c1;this.c2=c2;
		color = new Color(0,0,0);
		this.length=length;
		setPos(new double[]{(x2-x1)/2 , (y2-y1)/2, length/2});
	}
	

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void draw(GL myGL,Texture text, boolean draw){
		//g.drawLine((int)maze.edges[i].x1, (int)maze.edges[i].y1, (int)maze.edges[i].x2, (int)maze.edges[i].y2);	
		 //calculate the normal vector for each vertices of the side
		if(draw){
			float[] u = new float[]{0,-1,0};
			float[] v = new float[]{-1,0,0};
			myGL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
			myGL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
			text.enable();text.bind();
	 		myGL.glBegin(GL.GL_QUADS);
	 		if(x1==x2){
	        	myGL.glNormal3fv(u,0);
	        }else{
	        	myGL.glNormal3fv(v,0);
	        }
	 		myGL.glTexCoord2d(1,1);
			myGL.glVertex3d(x1, y1, 0);
			myGL.glTexCoord2d(1,0);
			myGL.glVertex3d(x1, y1, length);
			myGL.glTexCoord2d(0,0);
			myGL.glVertex3d(x2, y2, length);
			myGL.glTexCoord2d(0,1);
			myGL.glVertex3d(x2, y2, 0);
			myGL.glEnd();
			text.disable();
		}
	}
}
