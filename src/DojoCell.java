import java.util.LinkedList;

public class DojoCell extends DojoObject implements Comparable{
	double x,y,width,height=0;
	LinkedList<DojoCell> list=new LinkedList<DojoCell>();
	public DojoCell(double x,double y, double width, double height){
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
	}public void create(){
		list.add(this);
	}
	@Override
	public int compareTo(Object o) {
		if(o==null)return 1;
		if(x==((DojoCell)o).x&&y==((DojoCell)o).y&&width==((DojoCell)o).width&&height==((DojoCell)o).height){
			return 0;
		}else return -1;
	}
}
	