import javax.media.opengl.GL;


public class DojoTrap extends DojoObject{
	public static final int DOJO_TRAP_BASIC = 0;
	public static final int DOJO_TRAP_SHOOTER = 1;
	public static final int DOJO_TRAP_SPIN = 2;
	
	private int type;
	private double length, width;
	private double activationZone;
	private long duration;
	
	public DojoTrap(){
		super();
		type = DOJO_TRAP_BASIC;
		length = 1;
		width = 1;
		duration = 0;
		activationZone = 1;
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


	public double getActivationZone() {
		return activationZone;
	}


	public void setActivationZone(double activationZone) {
		this.activationZone = activationZone;
	}


	public long getDuration() {
		return duration;
	}


	public void setDuration(long duration) {
		this.duration = duration;
	}

	public void activate(DojoPlayer p){ 
		//TODO
	}
	public void deactivate(){ //TODO
		
	}

	@Override
	public void draw(GL myGL){ //TODO
		if(type == DOJO_TRAP_BASIC);
		if(type == DOJO_TRAP_SHOOTER);
		if(type == DOJO_TRAP_SPIN);
	}
}
