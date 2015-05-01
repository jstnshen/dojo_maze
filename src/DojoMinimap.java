import java.awt.Canvas;

import javax.media.opengl.GL;

import com.sun.opengl.util.texture.Texture;


public class DojoMinimap extends Canvas{
	
	KruskyKrab maze;
	double s=150;
	double scale;
	double xPos, yPos;
	double size;
	Thread runner;
	public DojoMinimap(KruskyKrab maze, double newX, double newY, double size){
		super();
		this.maze=maze;
		xPos = newX;
		yPos = newY;
		this.size =size;
		s=maze.size*maze.n;
		scale=size/s;
	}public void draw(GL gl){
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{0.0f,0.0f,0.0f,1f},0); 
		gl.glColor3f(0f, 0f, 0f);
		gl.glBegin(gl.GL_QUADS);
		gl.glVertex2d(xPos, yPos);
		gl.glVertex2d(xPos+size, yPos);
		gl.glVertex2d(xPos+size, yPos+size);
		gl.glVertex2d(xPos, yPos+size);
		gl.glEnd();
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{1f,1f,1f,1f},0); 
		for(int i=0;i<maze.edges.length;i++){
			if(!maze.edges[i].joined){
				//g.drawLine((int)(maze.edges[i].x1*scale),(int) (maze.edges[i].y1*scale),(int)( maze.edges[i].x2*scale), (int)(maze.edges[i].y2*scale));
				gl.glColor3f(1f, 1f, 1f);
				gl.glBegin(gl.GL_LINES);
				gl.glVertex2d((maze.edges[i].x2+maze.n*maze.size/2)*scale+xPos, (maze.edges[i].y2+maze.n*maze.size/2)*scale+yPos);
				gl.glVertex2d((maze.edges[i].x1+maze.n*maze.size/2)*scale+xPos, (maze.edges[i].y1+maze.n*maze.size/2)*scale+yPos);
				gl.glEnd();
			}
		}
	}
}
