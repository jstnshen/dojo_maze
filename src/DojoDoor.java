
public class DojoDoor extends DojoObject {
	public static final int DOJO_DOOR_BASIC = 0;
	public static final int DOJO_DOOR_SECURE = 1;
	
	private int type;
	private double length, width;
	
	public DojoDoor(){
		super();
		type = DOJO_DOOR_BASIC;
		length = 1;
		width = 1;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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
	
	public void open(DojoPlayer p){
		//TODO
	}
	public void close(){
		//TODO 
	}
}
