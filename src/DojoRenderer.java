import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.GLUT;

/*
 * TODO update mouse movement and key movement at the same time
 * make enemy intelligent
 * put traps
 * design mini-puzzle
 * add inventory
 * add torch/ tracking
 * add minimap
 * get rid of mouse drag
 * restrict z movement
 * collision detection (learn gl pickering)
 * continuous movement
 * add normal to the wall
 */
public class DojoRenderer extends GLCanvas{
    public static final float SENSITIVITY= 2; //higher number = more sensitive mouse movement
    public static final int NUM_SQUARE = 3; //number of smaller squares along the side of each wall
    public static final float SUN_SIZE= 10.0f; //size of the radius of the sun
    public static final float ENVIRONMENT_SIZE = 500f;//size of the "radius" of the sky
    public static int CENTER_X = 300;
    public static int CENTER_Y = 300;
    private Animator anim; //animator object used to animate the canvas
    private float xPos,yPos,zPos; //position of the camera
    private float xUp,yUp,zUp; //up position of the camera
    private float xo,yo,zo; //camera look at position
    private float prevX, prevY; //position of previous mouse location
    private float theta, phi; //angular position of the camera
    private float radius; //distant the camera is set from the camera look at position
    private int currentX; 
    private int currentY;
    private int[] down=new int[0];
    private KruskyKrab maze;
    private ArrayList<DojoEnemy> enemies;
    private float size; //size of the terrain
    private boolean isDay;
    /**
     * Constructor, initiate the settings the camera and the size of the objects
     * @param glCap
     */
    public DojoRenderer(GLCapabilities glCap){
        super(glCap);
        anim = new Animator(this);
        reset();
        generateMaze();
        //ez
        for(int i=0;i<2*maze.n;i++){
        	int ran=(int)(Math.random()*2*maze.n*(maze.n+1));
        	if(!maze.edges[ran].joined){
        		maze.edges[ran].joined=true;
        	}else{
        		i--;
        	}
        }
        //
        enemies = new ArrayList<DojoEnemy> ();
        int rand=(int)(Math.random()*maze.n*maze.n);
        enemies.add(new DojoEnemy(maze.size/4,maze.cells[rand].x+maze.cells[rand].width/2,maze.cells[rand].y+maze.cells[rand].height/2,maze.size/2));
        System.out.println(rand);
//        addKeyListener(new KeyAdapter(){//create a new KeyAdapter to add as a KeyListener
//            @Override
//            public void keyPressed(KeyEvent e){
//                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)//quit
//                    System.exit(0); 
//                if(e.getKeyChar() =='r'){ //reset the camera to original setting
//                    reset();
//                }
//                double dx=xo-xPos;//lets the camera move front and back around the room
//				double dy=yo-yPos;
//				double dz=zo-zPos;
//				System.out.println(dx+"dx");
//				double dist=Math.sqrt(dx*dx+dy*dy+dz*dz);
//				if(e.getKeyCode()==KeyEvent.VK_W){//move forward
//					xPos+=dx/dist*radius/3;
//					yPos+=dy/dist*radius/3;
//					zPos+=dz/dist*radius/3;
//					updateCamera();
//				}
//				if(e.getKeyCode()==KeyEvent.VK_S){//move backward
//					xPos-=dx/dist*radius/3;
//					yPos-=dy/dist*radius/3;
//					zPos-=dz/dist*radius/3;
//					updateCamera();
//				}
//				if(e.getKeyCode()==KeyEvent.VK_ESCAPE){//quit
//					System.exit(1);
//				}
//                
//                
//                
//                if(e.getKeyChar() == 'q'){ //move camera in the z direction
//                    if(zo-zPos != 0) zo-= (zo-zPos) / Math.abs(zo-zPos);
//                    updateCamera();
//                    System.out.println("zo: "+ zo);
//                }
//                if(e.getKeyChar() == 'e'){//move camera in the z direction
//                    if(zo-zPos != 0) zo+= (zo-zPos) / Math.abs(zo-zPos);
//                    updateCamera();
//                    System.out.println("zo: "+ zo);
//                }
//                if(e.getKeyChar() == ' '){//turn sun on or off
//                	isDay = !isDay;
//                	System.out.println("isDay: "+isDay);
//                }
//            }
//        });
			this.addKeyListener(new KeyAdapter(){
						
				public void keyPressed(KeyEvent e){
					int[] ndown=new int[down.length+1];
					boolean adder=true;
					for(int i=0;i<down.length;i++){
						if(down[i]==e.getKeyCode()){
							adder=false;
						}
						ndown[i]=down[i];
					}
					if(adder){
						ndown[down.length]=e.getKeyCode();
						down=ndown;
					}
					
				}public void keyReleased(KeyEvent e){
					int[] ndown= new int[down.length];
					int loc=0;
					for(int i=0;i<down.length;i++){
						if(e.getKeyCode()!=down[i]){
							ndown[loc]=down[i];
							loc++;
						}
					}
					down=ndown;
				}
			});
        addMouseListener(new MouseAdapter(){ //add mouse listener to the canvas
            @Override
            /**
             * set the color of the GLUT objects to a random color each time the mouse is clicked
             */
            public void mouseClicked(MouseEvent e){
                if(e.getClickCount() != 0){

                }
            }
        });
        addMouseMotionListener(new MouseAdapter(){
            @Override
            /**
             * mouseDragged method used to detect mouse drag to adjust the camera position about the center
             */
 
            
            //
            public void mouseMoved(MouseEvent me){
            	
            	currentX = me.getX();
            	currentY = me.getY();
//	            if(Math.abs(me.getX()-prevX) !=0)
//	                theta-=SENSITIVITY*(me.getX()-prevX)/Math.abs((me.getX()-prevX)); //increase the number of degrees to rotate
//	            prevX=me.getX();
//	            if(Math.abs(me.getY()-prevY) !=0)
//	                phi-=SENSITIVITY*(me.getY()-prevY)/Math.abs((me.getY()-prevY));
//	            prevY=me.getY();
//	            updateCamera();
        }
        });
        addMouseWheelListener(new MouseWheelListener(){
            @Override
            /**
             * mouseWheelMoved method used to detect the direction and amount of mouse scroll;
             * the method adjust the camera position (zoom) according to the mouse scroll
             */
            public void mouseWheelMoved(MouseWheelEvent e) {
                int change = e.getWheelRotation(); //how much the mouse wheel is scrolled (and direction)
                if(change !=0) radius+=change /Math.abs(change); 
                //update camera position
                updateCamera();
                System.out.println(radius);
            }
        });
        addGLEventListener(new GLEventListener(){ //add GL event listener to the canvas
            @Override
            /**
             * display objects on the canvas
             */
            public void display(GLAutoDrawable drawable) {
                GL myGL = drawable.getGL(); //get graphics object
                doDisplay(myGL);
            }
            @Override
            public void displayChanged(GLAutoDrawable drawable,
                    boolean modeChanged, boolean deviceChanged) {
            }
            @Override
            /**
             * initialize GL and game settings
             */
            public void init(GLAutoDrawable drawable) {
                GL myGL = drawable.getGL(); //get graphics object
                myGL.glClearColor(0f,0.0f,0.0f,0.0f);
                myGL.glEnable(GL.GL_LIGHTING);
                myGL.glEnable(GL.GL_LIGHT0);
                myGL.glEnable(GL.GL_LIGHT1);

                myGL.glShadeModel(GL.GL_SMOOTH);
                myGL.glEnable( GL.GL_DEPTH_TEST);
                myGL.glDepthFunc(GL.GL_LEQUAL);

            }
            @Override
            public void reshape(GLAutoDrawable drawable, int x, int y,int width, int height) {
                GL myGL = drawable.getGL(); //get graphics object
                myGL.glMatrixMode(GL.GL_PROJECTION);
                myGL.glLoadIdentity(); //load GL projection
                GLU myGLU= new GLU();
                myGLU.gluPerspective(60,1.0,1.0, 2*ENVIRONMENT_SIZE);
            }
        });
        anim.start(); //start animator
    }
    /**
     * (re)set to the initial settings
     */
    public void reset(){
    	isDay=true;
        radius = 25;
        theta=0;
        phi= 270;
        xPos=0; yPos=0; zPos = 15;
        xo=getCoordX(radius, phi, theta)+xPos;
        yo=getCoordY(radius,phi,theta)+yPos;
        zo=getCoordZ(radius,phi)+zPos;
        currentX= 300;
        currentY = 300;
	     
        xUp=getCoordX(radius,phi+90,theta);
        yUp=getCoordY(radius,phi+90,theta);
        zUp=getCoordZ(radius,phi+90);
        prevX=0; prevY=0;

    }
    /**
     * returns the x coordinate in terms of the distance and angles given (spherical coordinate) 
     * offset by where the camera is looking at (xo,yo,zo)
     * @param radius the distance the point is from the origin
     * @param phi the angle the point is from the y axis 
     * @param theta the angle the point is from the x axis
     * @return the x coordinate corresponding to the input
     */
    public float getCoordX(float radius, float phi, float theta){
        return (float) (radius*Math.sin(phi*Math.PI/180)*Math.cos(theta/180*Math.PI));
    }
    /**
     * returns the y coordinate in terms of the distance and angles given (spherical coordinate) 
     * offset by where the camera is looking at (xo,yo,zo)
     * @param radius the distance the point is from the origin
     * @param phi the angle the point is from the y axis 
     * @param theta the angle the point is from the x axis
     * @return the y coordinate corresponding to the input
     */
    public float getCoordY(float radius, float phi, float theta){
        return (float) (radius*Math.sin(phi*Math.PI/180)*Math.sin(theta/180*Math.PI));
    }
    /**
     * returns the z coordinate in terms of the radius and the angle given (spherical coordinate) 
     * offset by where the camera is looking at (xo,yo,zo)
     * @param radius the distance the point is from the origin
     * @param phi the angle the point is from the y axis 
     * @return the z coordinate corresponding to the input
     */
    public float getCoordZ(float radius, float phi){
        return (float) (radius*Math.cos(phi/180*Math.PI));
    }
    /**
     * update the position vector and up vector of the camera 
     */
    public void updateCamera(){
        xo=getCoordX(radius, phi, theta)+xPos;
        yo=getCoordY(radius,phi,theta)+yPos;
        zo=getCoordZ(radius,phi)+zPos;
        xUp=getCoordX(radius,phi+90,theta);
        yUp=getCoordY(radius,phi+90,theta);
        zUp=getCoordZ(radius,phi+90);
    }
 
   
    public void doDisplay(GL myGL){
        myGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); //clear background with background color
        myGL.glMatrixMode(GL.GL_MODELVIEW); 
        myGL.glLoadIdentity();
        GLU myGLU= new GLU();

        myGLU.gluLookAt(xPos,yPos,zPos,xo,yo,zo, xUp, yUp, zUp); //the up x,y,z is 90 offset from the camera's position
        myGL.glPushMatrix();
        createEnvironment(myGL);
        myGL.glPushMatrix();
    	myGL.glTranslated(-maze.n*maze.size/2, -maze.n*maze.size/2,0);
        drawMaze(myGL);
        myGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{0f,1f,1f,1f},0); //set the color maze
        for(int i=0; i<enemies.size(); i++){
        	enemies.get(i).draw(myGL);
        }
        myGL.glPopMatrix();
        
        if(Math.abs(currentX-CENTER_X) >100){
        	//pan horizontal
        	theta-= (double)((currentX-CENTER_X-Math.signum(currentX-CENTER_X)*100)/100 * 2.0);
        }
        if(Math.abs(currentY-CENTER_Y) >100){
        	//pan vertical
        	phi-= (double)(currentY-CENTER_Y-Math.signum(currentY-CENTER_Y)*100)/100 * 2.0;
        }
        for(int i=0;i<down.length;i++){
        	if(down[i] == KeyEvent.VK_ESCAPE)//quit
               System.exit(0); 
          	if(down[i] == KeyEvent.VK_R){ //reset the camera to original setting
               reset();
          	}
           double dx=xo-xPos;//lets the camera move front and back around the room
			double dy=yo-yPos;
			double dz=zo-zPos;
			System.out.println(dx+"dx");
			double dist=Math.sqrt(dx*dx+dy*dy+dz*dz);
			if(down[i]==KeyEvent.VK_W){//move forward
				xPos+=dx/dist*radius/30;
				yPos+=dy/dist*radius/30;
				zPos+=dz/dist*radius/30;
				updateCamera();
			}
			if(down[i]==KeyEvent.VK_S){//move backward
				xPos-=dx/dist*radius/30;
				yPos-=dy/dist*radius/30;
				zPos-=dz/dist*radius/30;
				updateCamera();
			}
			if(down[i]==KeyEvent.VK_ESCAPE){//quit
				System.exit(1);
			}
			if(down[i] == KeyEvent.VK_SPACE){//turn sun on or off
				isDay = !isDay;
				System.out.println("isDay: "+isDay);
			}
        }
        updateCamera();
        
        
    }
    public static void main(String[] args){
        JFrame frame= new JFrame("Dojo Entertainment"); //create frame
        //create and add GLCanvas subclass to frame
        GLCapabilities cap= new GLCapabilities();
        DojoRenderer myCanvas = new DojoRenderer(cap);    
        frame.add(myCanvas);
        //set up frame
        frame.setSize(600, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myCanvas.requestFocus();
    }
    /**
     * Coordinate class: holds the x,y,z coordinate of a point
     *
     */
    class Coord{
        public float x,y,z;
        /**
         * Constructor of the Coord class
         * @param newX the x coordinate
         * @param newY the y coordinate
         * @param newZ the z coordinate
         */
        public Coord(float newX, float newY, float newZ){
            x= newX;
            y= newY;
            z= newZ;
        }
        /**
         * returns the distance (Cartesian) between the given Coord and the current Coord
         * @param p2 the given Coord to find the distance to
         * @return the distance between p2 and the current Coord point
         */
        public float getDistance(Coord p2){
            float xDist= p2.x - x;
            float yDist= p2.y - y;
            float zDist= p2.z - z;
            return (float) Math.sqrt(Math.pow(xDist,2)+ Math.pow(yDist,2)+ Math.pow(zDist,2));
        }
    }
   
  
    /**
     * create new maze
     */
    public void generateMaze(){
    	maze= new KruskyKrab(30,30);
    }

    /**
     * create the basic game environment
     * @param myGL GL object used to draw the components in the game
     */
    public void createEnvironment(GL myGL){
        myGL.glPushMatrix();
        
        //create a light source where the sun is
        if(!isDay) myGL.glDisable(GL.GL_LIGHT0);
        else{
        	myGL.glEnable(GL.GL_LIGHT0);
	        myGL.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR,new float[]{1f, 1f, 1f, 1f} , 0); 
	        myGL.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT,new float[]{1f, 1f, 1f, 1f} , 0);
	        myGL.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE,new float[]{1f, 1f, 1f, 1f} , 0);
	        myGL.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION,
	                new float[]{ENVIRONMENT_SIZE * (float)Math.cos(Math.PI/3*2), 0, ENVIRONMENT_SIZE * (float)Math.sin(Math.PI/3*2)-SUN_SIZE, 1f}, 0);
	        myGL.glLightf(GL.GL_LIGHT0, GL.GL_QUADRATIC_ATTENUATION, (float)Math.pow(ENVIRONMENT_SIZE, -2)); //smaller value = less dramatic attenuation
        }
        
        GLUT myGLUT= new GLUT();
        //create the ground using a large thin cylinder
        myGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{0.7f,0.5f,0.4f,1f},0); //set color of the ground to brown
        myGL.glTranslatef(0,0,-size-1);
        myGLUT.glutSolidCylinder(ENVIRONMENT_SIZE, size/2, 50, 50);
        myGL.glPopMatrix();
        myGL.glPushMatrix();
        //create a spherical sun
        myGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{0f,.5f,1f,1f},0); //set the color of the sky
        myGLUT.glutSolidSphere(ENVIRONMENT_SIZE,100,100);//draw sky
        myGL.glTranslatef(ENVIRONMENT_SIZE * (float)Math.cos(Math.PI/3*2), 0, ENVIRONMENT_SIZE * (float)Math.sin(Math.PI/3*2)-SUN_SIZE);
        //create a sky using sphere
        myGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{1f,1f,0f,1f},0); //set color of the sun to yellow
        myGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_EMISSION, new float[]{1f,1f,0f,1f}, 0); 
        myGLUT.glutSolidSphere(SUN_SIZE, 50, 50);//draw sun
        
        //reset emission property
        myGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_EMISSION, new float[]{0, 0, 0, 1f}, 0);
        myGL.glPopMatrix();
    	}
    	public void drawMaze(GL myGL){
    		myGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{0f,1f,0f,1f},0); //set the color maze
    	
    		for(int i=0;i<maze.edges.length;i++){
    			if(!maze.edges[i].joined){
    				maze.edges[i].draw(myGL);
			}
		}
	}

}