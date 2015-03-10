import java.awt.Color;

import javax.media.opengl.GL;


public class DojoWall extends DojoObject{
	private double[] startCoord;
	private double[] endCoord;
	private Color color;
	public DojoWall(){
		super();
		startCoord = new double[]{0,0,0};
		endCoord = new double[]{0,0,0};
		color= new Color(1,1,1);
	}
	
	public double[] getStartCoord() {
		return startCoord;
	}

	public void setStartCoord(double[] startCoord) {
		this.startCoord = startCoord;
	}

	public double[] getEndCoord() {
		return endCoord;
	}

	public void setEndCoord(double[] endCoord) {
		this.endCoord = endCoord;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void draw(GL myGL){
		
	}
}
