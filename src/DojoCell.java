
public class DojoCell extends DojoObject{
	private double[] startCoord;
	private double[] endCoord;
	public DojoCell(){
		super();
		startCoord = new double[]{0,0,0};
		endCoord = new double[] {1,1,1};
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
	
}
