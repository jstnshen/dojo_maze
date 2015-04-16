import java.awt.Canvas;

import javax.media.opengl.GL;

import com.sun.opengl.util.texture.Texture;


public class DojoMinimap extends Canvas{
	
	KruskyKrab maze;
	double s=150;
	double scale;
	Thread runner;
	public DojoMinimap(KruskyKrab maze){
		super();
		this.maze=maze;
		s=maze.size*maze.n;
		scale=150d/s;
	}public void draw(GL gl){
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{0.0f,0.0f,0.0f,1f},0); 
		gl.glColor3f(0f, 0f, 0f);
		gl.glBegin(gl.GL_QUADS);
		gl.glVertex2d(450, 420);
		gl.glVertex2d(600, 420);
		gl.glVertex2d(600, 570);
		gl.glVertex2d(450, 570);
		gl.glEnd();
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{1f,1f,1f,1f},0); 
		for(int i=0;i<maze.edges.length;i++){
			if(!maze.edges[i].joined){
				//g.drawLine((int)(maze.edges[i].x1*scale),(int) (maze.edges[i].y1*scale),(int)( maze.edges[i].x2*scale), (int)(maze.edges[i].y2*scale));
				gl.glColor3f(1f, 1f, 1f);
				gl.glBegin(gl.GL_LINES);
				gl.glVertex2d((maze.edges[i].x2+maze.n*maze.size/2)*scale+450, (maze.edges[i].y2+maze.n*maze.size/2)*scale+420);
				gl.glVertex2d((maze.edges[i].x1+maze.n*maze.size/2)*scale+450, (maze.edges[i].y1+maze.n*maze.size/2)*scale+420);
				gl.glEnd();
			}
		}
	}
}
