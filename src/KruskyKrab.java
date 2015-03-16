import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JFrame;


public class KruskyKrab extends Canvas{
	Edge[] edges;
	Cell[] cells;
	ArrayList<Integer> xs=new ArrayList<Integer>();
	ArrayList<Integer> ys=new ArrayList<Integer>();
	Thread thread;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame f=new JFrame();
		f.setVisible(true);
		f.setSize(500,500);
		//f.setResizable(false);
		f.setDefaultCloseOperation(3);
		f.add(new KruskyKrab());
	
	}public KruskyKrab(){
		super();
		genMaze();
		thread=new Thread(new Runnable(){
			public void run(){
				while(thread==Thread.currentThread()){
					try {
						repaint();
						thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();
		this.addMouseListener(new MouseAdapter(){
			
			public void mouseDragged(MouseEvent e){
				xs.add(e.getX());
				ys.add(e.getY());
			}
			
		});
	}class Edge{
		double x1,x2,y1,y2=0;
		Cell c1,c2;
		boolean joined=false;
		public Edge(double x, double y, double x2, double y2,Cell c1, Cell c2){
			x1=x;
			this.x2=x2;
			y1=y;
			this.y2=y2;
			this.c1=c1;this.c2=c2;
		}
	}class Cell implements Comparable{
		double x,y,width,height=0;
		LinkedList<Cell> list=new LinkedList<Cell>();
		public Cell(double x,double y, double width, double height){
			this.x=x;
			this.y=y;
			this.width=width;
			this.height=height;
		}public void create(){
			list.add(this);
		}
		@Override
		public int compareTo(Object o) {
			// TODO Auto-generated method stub
			if(o==null)return 1;
			if(x==((Cell)o).x&&y==((Cell)o).y&&width==((Cell)o).width&&height==((Cell)o).height){
				return 0;
			}else return -1;
		}
	}public void genMaze(){
		int n=75;
		int counter=0;
		ArrayList<LinkedList<Cell>> joined=new ArrayList<LinkedList<Cell>>();
		cells=new Cell[n*n];
		edges=new Edge[2*n*(n+1)];
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				cells[n*i+j]=new Cell(10*i,10*j,10,10);
				LinkedList<Cell> l=new LinkedList<Cell>();
				l.add(cells[n*i+j]);
				joined.add(l);
				if(i!=0){
					edges[counter]=new Edge(10*j,10*i,10*j+10,10*i,cells[n*i+j],cells[n*(i-1)+j]);
					counter+=1;
					
				}else{
					edges[counter]=new Edge(10*j,10*i,10*j+10,10*i,cells[n*i+j],null);
					counter+=1;
				}
				
				if(j!=0){
					edges[counter]=new Edge(10*j,10*i,10*j,10*i+10,cells[n*i+j],cells[n*i+j-1]);
					counter+=1;
				}else{
					edges[counter]=new Edge(10*j,10*i,10*j,10*i+10,cells[n*i+j],null);
					counter+=1;
				}
				
				if(i==n-1){
					edges[counter]=new Edge(10*j,10*(i+1), 10*j+10,10*(i+1),cells[n*i+j],null);
					counter+=1;
				}if(j==n-1){
					edges[counter]=new Edge(10*(j+1),10*i, 10*(j+1),10*i+10,cells[n*i+j],null);
					counter+=1;
				}
				
			}
		}
		
		ArrayList<Integer> nums=new ArrayList<Integer>();
		for(int i=0;i<edges.length;i++){
			nums.add(i);
		}
		while(nums.size()>0){
			int lol=(int)(Math.random()*nums.size());
			int index=nums.get(lol);
			Cell c1=edges[index].c1;
			Cell c2=edges[index].c2;
			int index1=-1;
			int index2=-1;
			for(int i=0;i<joined.size();i++){
				if(c1==null||c2==null);
				else if(joined.get(i).contains(c1)&&joined.get(i).contains(c2));
				else{
					if(joined.get(i).contains(c1)){
						index1=i;
					}if(joined.get(i).contains(c2)){
						index2=i;
					}
				}
			}
			if(index1>=0&&index2>=0&&index1!=index2){
				joined.get(index1).addAll(joined.get(index2));
				joined.remove(index2);
				edges[index].joined=true;
			}
			nums.remove(lol);
		}
		
		
		
	}public void paint(Graphics g){
		g.setColor(Color.black);
		for(int i=0;i<edges.length;i++){
			if(!edges[i].joined){
				g.drawLine((int)edges[i].x1, (int)edges[i].y1, (int)edges[i].x2, (int)edges[i].y2);
			}
		}
		g.setColor(Color.red);
		for(int i=0;i<xs.size();i++){
			g.fillOval(xs.get(i)-2, ys.get(i)-2, 4, 4);
		}
	}

}
