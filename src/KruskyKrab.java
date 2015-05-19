import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JFrame;

/**
 * This class generates random mazes
 * @author Justin and Vijay
 *
 */
public class KruskyKrab extends Canvas{
	DojoWall[] edges;
	DojoCell[] cells;
	ArrayList<Integer> xs=new ArrayList<Integer>();
	ArrayList<Integer> ys=new ArrayList<Integer>();
	int[][] adj;
	double size=10;
	int n=10;
	public KruskyKrab(double size,int n){
		super();
		this.size=size;
		this.n=n;
		genMaze();
		this.addMouseListener(new MouseAdapter(){
			
			public void mouseDragged(MouseEvent e){
				xs.add(e.getX());
				ys.add(e.getY());
			}
			
		});
	}
	/**
	 * generate maze
	 */
	public void genMaze(){
		int counter=0;
		ArrayList<LinkedList<DojoCell>> joined=new ArrayList<LinkedList<DojoCell>>();
		cells=new DojoCell[n*n];
		edges=new DojoWall[2*n*(n+1)];
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				cells[n*i+j]=new DojoCell(size*i-n*size/2,size*j-n*size/2,size,size,n*j+i);
				LinkedList<DojoCell> l=new LinkedList<DojoCell>();
				l.add(cells[n*i+j]);
				joined.add(l);
				if(i!=0){
					edges[counter]=new DojoWall(size*j-n*size/2,size*i -n*size/2,size*j+size-n*size/2,size*i  -n*size/2,cells[n*i+j],cells[n*(i-1)+j],size);
					counter+=1;
					
				}else{
					edges[counter]=new DojoWall(size*j-n*size/2,size*i-n*size/2,size*j+size-n*size/2,size*i-n*size/2,cells[n*i+j],null,size);
					counter+=1;
				}
				
				if(j!=0){ 
					edges[counter]=new DojoWall(size*j-n*size/2
							,size*i-n*size/2
							,size*j-n*size/2
							,size*i+size-n*size/2
							,cells[n*i+j],cells[n*i+j-1],size);
					counter+=1;
				}else{
					edges[counter]=new DojoWall(size*j-n*size/2
							,size*i-n*size/2
							,size*j-n*size/2
							,size*i+size-n*size/2
							,cells[n*i+j],null,size);
					counter+=1;
				}
				
				if(i==n-1){
					edges[counter]=new DojoWall(size*j-n*size/2
							,size*(i+1)-n*size/2
							,size*j+size-n*size/2
							,size*(i+1)-n*size/2
							,cells[n*i+j],null,size);
					counter+=1;
				}if(j==n-1){
					edges[counter]=new DojoWall(size*(j+1)-n*size/2
							,size*i-n*size/2
							, size*(j+1)-n*size/2
							,size*i+size-n*size/2
							,cells[n*i+j],null,size);
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
			DojoCell c1=edges[index].c1;
			DojoCell c2=edges[index].c2;
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
		
		for(int i=0;i<2*n;i++){ //remove some random walls in the existing maze
    		int ran=(int)(Math.random()*2*n*(n+1));
	      	if(!edges[ran].joined){
	      		edges[ran].joined=true;
	      	}else{
	      		i--;
	      	}
    	}
		//System.out.println(n);
	//	System.out.println();
		adj=new int[n*n][n*n];
		for(int i=0;i<edges.length;i++){
			if(edges[i].joined&&edges[i].c1!=null&&edges[i].c2!=null){
				adj[edges[i].c1.id][edges[i].c2.id]=1;
				adj[edges[i].c2.id][edges[i].c1.id]=1;
				//System.out.println(edges[i].c1.id+" "+edges[i].c2.id);
			}
		}
//		for(int[] i:adj){
//			for(int j:i){
//				System.out.print(j+" ");
//			}
//			System.out.println();
//		}
	}

}
