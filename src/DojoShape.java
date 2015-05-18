import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.media.opengl.GL;


public class DojoShape {
	double[][] vertices;
	double[][] normals;
	int[][] faces;
	public DojoShape(String s){
		Scanner sc=null;
		try {
			sc=new Scanner(new File(s));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<double[]> d=new ArrayList<double[]>();
		ArrayList<double[]> d2=new ArrayList<double[]>();
		ArrayList<int[]> d3=new ArrayList<int[]>();
		String[] data={};
		boolean there=false;
		while(!there){
			data=sc.nextLine().split(" ");
			if(data.length>0&&data[0].equals("v")){
				there=true;
			}
		}
		while(data.length!=0&&data[0].equals("v")){
			double[] temp=new double[3];
			temp[0]=Double.parseDouble(data[1]);
			temp[1]=Double.parseDouble(data[2]);
			temp[2]=Double.parseDouble(data[3]);
			d.add(temp);
			data=sc.nextLine().split(" ");
		}
		there=false;
//		while(!there){
//			data=sc.nextLine().split(" ");
//			if(data.length>0&&data[0].equals("vn")){
//				there=true;
//			}
//		}
//		while(data.length!=0&&data[0].equals("vn")){
//			double[] temp=new double[3];
//			temp[0]=Double.parseDouble(data[1]);
//			temp[1]=Double.parseDouble(data[2]);
//			temp[2]=Double.parseDouble(data[3]);
//			d2.add(temp);
//			data=sc.nextLine().split(" ");
//		}
		there=false;
		while(!there){
			data=sc.nextLine().split(" ");
			if(data.length>0&&data[0].equals("f")){
				there=true;
			}
		}
		while(data.length!=0&&data[0].equals("f")&&sc.hasNext()){
			int[] temp=new int[2];
			int[] temp2=new int[2];
			int[] temp3=new int[2];
			
			temp[0]=Integer.parseInt(data[1]);
			d3.add(temp);
			temp2[0]=Integer.parseInt(data[2]);
			d3.add(temp2);
			temp3[0]=Integer.parseInt(data[3]);
			d3.add(temp3);
			
	
//			String[] tempo=data[1].split("/");
//			String[] tempo2=data[2].split("/");
//			String[] tempo3=data[3].split("/");
//			for(String str:tempo)System.out.println(str);
//			temp[0]=Integer.parseInt(tempo[0]);
//			temp[1]=Integer.parseInt(tempo[2]);
//			d3.add(temp);
//			temp2[0]=Integer.parseInt(tempo2[0]);
//			temp2[1]=Integer.parseInt(tempo2[2]);
//			d3.add(temp2);
//			temp3[0]=Integer.parseInt(tempo3[0]);
//			temp3[1]=Integer.parseInt(tempo3[2]);
//			d3.add(temp3);
			
			
			data=sc.nextLine().split(" ");
		}
		vertices=new double[d.size()][3];
		normals=new double[d2.size()][3];
		faces=new int[d3.size()][2];
		
		for(int i=0;i<d.size();i++){
			vertices[i]=d.get(i);
		}for(int i=0;i<d2.size();i++){
			normals[i]=d2.get(i);
		}for(int i=0;i<d3.size();i++){
			faces[i]=d3.get(i);
		}	
	}
	
	
	
	public void draw(GL gl){
		//System.out.println("hi");
		//System.out.println(vertices.length+" "+normals.length+" "+faces.length);
		gl.glBegin(GL.GL_TRIANGLES);
		for(int i=0;i<faces.length;i++){
		//	System.out.println(vertices[faces[i][0]-1][0]+" "+vertices[faces[i][0]-1][1]+" "+ vertices[faces[i][0]-1][2]);
			gl.glVertex3d(vertices[faces[i][0]-1][0]*3-2.02, vertices[faces[i][0]-1][1]*3, vertices[faces[i][0]-1][2]*3-3.05*3-15);
		//	if(i%3==2)System.out.println();
		}
		gl.glEnd();
		//System.out.println("drawn");
	}

}
