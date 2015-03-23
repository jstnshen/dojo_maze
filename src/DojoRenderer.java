import java.awt.Frame;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

/*
 * TODO update mouse movement and key movement at the same time
 * make enemy intelligent
 * design mini-puzzle
 * add inventory
 * add torch/ tracking
 * collision detection (learn gl pickering)
 */
public class DojoRenderer extends GLCanvas{
    public static final float SENSITIVITY= 2; //higher number = more sensitive mouse movement
    public static final int NUM_SQUARE = 3; //number of smaller squares along the side of each wall
    public static final float SUN_SIZE= 10.0f; //size of the radius of the sun
    public static final float ENVIRONMENT_SIZE = 700f;//size of the game world
    public static int CENTER_X = 300; //x center of canvas
    public static int CENTER_Y = 300; //y center of canvas
    private Animator anim; //animator object used to animate the canvas
    private float xPos,yPos,zPos; //position of the camera
    private float xUp,yUp,zUp; //up position of the camera
    private float xo,yo,zo; //camera look at position
    private float theta, phi; //angular position of the camera
    private float radius; //distant the camera is set from the camera look at position
    private int currentX; //current x mouse position
    private int currentY; //currnet y mouse position
    private static DojoMinimap mm;
    private Texture text;
    private int[] down=new int[0]; //stores keys pressed
    private KruskyKrab maze; //maze object drawn
    private ArrayList<DojoEnemy> enemies; //enemies in the maze
    private ArrayList<DojoTrap> traps;
    private float size; 
    private boolean isDay;
    /**
     * Constructor, initiate the settings the camera and the size of the objects
     * @param glCap
     */
    public DojoRenderer(GLCapabilities glCap){
        super(glCap);
        anim = new Animator(this);
    	isDay=true;
        reset(); //initiate camera settings
        generateMaze(); 
        createObjects(); //create objects in the game
        //start openGL settings
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
                myGL.glClearColor(0f,.5f,1f,1f); //set background color
                myGL.glEnable(GL.GL_LIGHTING);
                myGL.glEnable(GL.GL_LIGHT0);
                myGL.glEnable(GL.GL_LIGHT1);

                myGL.glShadeModel(GL.GL_SMOOTH);
                myGL.glEnable( GL.GL_DEPTH_TEST);
                myGL.glDepthFunc(GL.GL_LEQUAL);
                loadText(myGL);
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
        
        //start event detection
        this.addKeyListener(new KeyAdapter(){ //implement KeyAdapter to detect key events
          	@Override
          	public void keyPressed(KeyEvent e){ //stores keys pressed for processing
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
  			}
  			@Override
  			public void keyReleased(KeyEvent e){ 
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
          addMouseMotionListener(new MouseAdapter(){ //detect mouse motion for panning
              @Override
              public void mouseMoved(MouseEvent me){ //update current location of the mouse in the canvas	
            	  

                  
//            	  if(Math.abs(currentX-me.getX()) >0){
//            		  //pan horizontal
//            		  theta+= (double)((currentX-me.getX()));//-Math.signum(currentX-me.getX())*100)/100 * 2.0);
//            	  }
//            	  if(Math.abs(currentY-me.getY()) >0){
//            		  //pan vertical
//            		  phi+= (double)(currentY-me.getY());//-Math.signum(currentY-me.getY())*100)/100 * 2.0;
//            	  }
            	  currentX = me.getX(); 
                  currentY = me.getY();
//                  try{
//                	  if(Math.abs(currentX-CENTER_X) > 200 || Math.abs(currentY-CENTER_Y) > 200){
//                		  Robot robot = new Robot();
//                          robot.mouseMove(CENTER_X,CENTER_Y);
//                          currentX =CENTER_X;
//                          currentY= CENTER_Y;
//                	  }
//                    
//
//                  }catch(Exception ex){
//                      ex.printStackTrace();
//                  }
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

              }
          });

        
        anim.start(); //start animator
    }

	/**
     * initiate game objects
     */
    public void createObjects(){
        enemies = new ArrayList<DojoEnemy> ();
        traps = new ArrayList<DojoTrap>();
        int rand= (int)(Math.random()*maze.n*maze.n);
        enemies.add(new DojoEnemy(maze.size/4,maze.cells[rand].x+maze.cells[rand].width/2,maze.cells[rand].y+maze.cells[rand].height/2,maze.size/2));
        System.out.println(rand);
        
       // rand=(int)(Math.random()*maze.n*maze.n);
       // traps.add(new DojoTrap(maze.size, maze.cells[rand].x, maze.cells[rand].y, 10));
    }
    /**
     * (re)set to the initial settings of the camera
     */
    public void reset(){
        radius = 25;
        theta=0; 
        phi= 270;    
        xPos=0; yPos=0; zPos = 15; //set initial camera position
        //set camera look at location
        xo=getCoordX(radius, phi, theta)+xPos;
        yo=getCoordY(radius,phi,theta)+yPos;
        zo=getCoordZ(radius,phi)+zPos;

	    //set up vector for the camera 
        xUp=getCoordX(radius,phi+90,theta);
        yUp=getCoordY(radius,phi+90,theta);
        zUp=getCoordZ(radius,phi+90);
        //initiate mouse location to center of screen
        currentX= 300;
        currentY = 300;
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
 
    /**
     * Draws all the graphical elements of the game onto the canvas
     * @param myGL gl object used to draw objects
     */
    public void doDisplay(GL myGL){
        myGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); //clear background with background color
        myGL.glMatrixMode(GL.GL_MODELVIEW); 
        myGL.glLoadIdentity();
        GLU myGLU= new GLU();
        myGLU.gluLookAt(xPos,yPos,zPos,xo,yo,zo, xUp, yUp, zUp); //the up x,y,z is 90 offset from the camera's position
        

        
        processEvents();
        
        myGL.glPushMatrix();
        createEnvironment(myGL);
        drawMaze(myGL);
        drawObjects(myGL);
        
        myGL.glPopMatrix();
        
        switchto2D(myGL);
        drawHUD(myGL);
        switchto3D(myGL);
        
    }
    public static void main(String[] args){
        JFrame frame= new JFrame("Dojo Entertainment"); //create frame
        //create and add GLCanvas subclass to frame
        GLCapabilities cap= new GLCapabilities();
        DojoRenderer myCanvas = new DojoRenderer(cap);    
        frame.add(myCanvas);
        //set up frame
        frame.setSize(600, 600);
        frame.setCursor(frame.getToolkit().createCustomCursor(
                new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),
                "null"));
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myCanvas.requestFocus();
    }
   
    /**
     * Process all key and mouse events
     */
    public void processEvents(){
    	//process mouse events
        if(Math.abs(currentX-CENTER_X) >100){
        	//pan horizontal
        	theta-= (double)((currentX-CENTER_X-Math.signum(currentX-CENTER_X)*100)/100 * 2.0);
        }
        if(Math.abs(currentY-CENTER_Y) >100){
        	//pan vertical
        	phi-= (double)(currentY-CENTER_Y-Math.signum(currentY-CENTER_Y)*100)/100 * 2.0;
        }
        updateCamera();
        //process key events
    	for(int i=0;i<down.length;i++){
    		if(down[i] == KeyEvent.VK_ESCAPE)//quit
    			System.exit(0); 
           	if(down[i] == KeyEvent.VK_R){ //reset the camera to original setting
                reset();
           	}
            double dx=xo-xPos;//lets the camera move front and back around the room
            double dy=yo-yPos;
            double dz=zo-zPos;
            double dist=Math.sqrt(dx*dx+dy*dy+dz*dz);
            if(down[i]==KeyEvent.VK_W){//move forward
         	   xPos+=dx/dist*radius/30;
         	   yPos+=dy/dist*radius/30;
 				//zPos+=dz/dist*radius/30;
         	   updateCamera();
            }
            if(down[i]==KeyEvent.VK_S){//move backward
         	   xPos-=dx/dist*radius/30;
         	   yPos-=dy/dist*radius/30;
         	   //zPos-=dz/dist*radius/30;
         	   updateCamera();
            }
            if(down[i]==KeyEvent.VK_ESCAPE){//quit
         	   System.exit(1);
            }
            if(down[i] == KeyEvent.VK_SPACE){//turn sun on or off
         	   isDay = !isDay;
 			}
    	}
    }
  
    /**
     * create a new, random maze for the game 
     */
    public void generateMaze(){

    	maze= new KruskyKrab(30,30);//generate the initial maze
    	for(int i=0;i<2*maze.n;i++){ //remove some random walls in the existing maze
    		int ran=(int)(Math.random()*2*maze.n*(maze.n+1));
	      	if(!maze.edges[ran].joined){
	      		maze.edges[ran].joined=true;
	      	}else{
	      		i--;
	      	}
    	}
        mm=new DojoMinimap(maze);
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

//        myGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{0f,.5f,1f,1f},0); //set the color of the sky
//        myGLUT.glutSolidSphere(ENVIRONMENT_SIZE,100,100);//draw sky
        
        
        myGL.glTranslatef(ENVIRONMENT_SIZE * (float)Math.cos(Math.PI/3*2), 0, ENVIRONMENT_SIZE * (float)Math.sin(Math.PI/3*2)-SUN_SIZE);
        myGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{1f,1f,0f,1f},0); //set color of the sun to yellow
        myGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_EMISSION, new float[]{1f,1f,0f,1f}, 0); 
        myGLUT.glutSolidSphere(SUN_SIZE, 50, 50);//draw sun
        
        //reset emission property
        myGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_EMISSION, new float[]{0, 0, 0, 1f}, 0);
        myGL.glPopMatrix();
    }
    public void drawHUD(GL myGL) {
		// TODO Auto-generated method stub
		mm.draw(myGL);
		double px=(maze.n*maze.size/2+xPos)*150d/(maze.n*maze.size)+450;//draws player
		double py=(maze.n*maze.size/2+yPos)*150d/(maze.n*maze.size)+420;
		//System.out.println(xPos+" "+yPos);
		myGL.glColor3f(0f,1f,1f);
		myGL.glBegin(GL.GL_QUADS);
		myGL.glVertex2d(px-2.5, py-2.5);
		myGL.glVertex2d(px-2.5, py+2.5);
		myGL.glVertex2d(px+2.5, py+2.5);
		myGL.glVertex2d(px+2.5, py-2.5);
		myGL.glEnd();
		for(int i=0;i<enemies.size();i++){
			//System.out.println(enemies.get(i).getPosition()[0]+" "+enemies.get(i).getPosition()[1]);
			px=(enemies.get(i).getPosition()[0])*150d/(maze.n*maze.size)+450;
			py=(enemies.get(i).getPosition()[1])*150d/(maze.n*maze.size)+420;
			myGL.glColor3f(1f,0f,0f);
			myGL.glBegin(GL.GL_QUADS);
			myGL.glVertex2d(px-2.5, py-2.5);
			myGL.glVertex2d(px-2.5, py+2.5);
			myGL.glVertex2d(px+2.5, py+2.5);
			myGL.glVertex2d(px+2.5, py-2.5);
			myGL.glEnd();
		}
		
	}
    public void switchto2D(GL gl){
    	gl.glDisable(GL.GL_DEPTH_TEST);
    	gl.glMatrixMode(GL.GL_PROJECTION);
    	gl.glPushMatrix();
    	gl.glLoadIdentity();
    	gl.glDisable(gl.GL_LIGHTING);
    	gl.glOrtho(0, this.getWidth(), 0, this.getHeight(), -1, 1);
    	gl.glMatrixMode(GL.GL_MODELVIEW);
    	gl.glLoadIdentity();
	}

	public void switchto3D(GL gl){
    	gl.glEnable(GL.GL_DEPTH_TEST);
    	gl.glMatrixMode(GL.GL_PROJECTION);
    	gl.glEnable(GL.GL_LIGHTING);
    	gl.glPopMatrix();
    	gl.glMatrixMode(GL.GL_MODELVIEW);
	}
	public void loadText(GL gl) {
		// TODO Auto-generated method stub
		try {
			text=TextureIO.newTexture(new File("corn2.jpg") , false);
		} catch (GLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Draw the maze on the canvas
	 * @param myGL gl object used to draw the maze
	 */
    public void drawMaze(GL myGL){
    	myGL.glPushMatrix();
    	myGL.glTranslated(-maze.n*maze.size/2, -maze.n*maze.size/2,0);
    	myGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{1f,1f,1f,1f},0); //set the color maze
    	
    	for(int i=0;i<maze.edges.length;i++){
    		if(!maze.edges[i].joined){
    			maze.edges[i].draw(myGL,text);
			}
		}
    	myGL.glPopMatrix();
	}
    /**
     * draws all the objects in the maze, including enemies, traps, etc.
     * @param myGL gl object used to draw the game objects
     */
    public void drawObjects(GL myGL){
    	myGL.glPushMatrix();
    	myGL.glTranslated(-maze.n*maze.size/2, -maze.n*maze.size/2,0);
    	myGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{0f,1f,1f,1f},0); //set the color maze
        for(int i=0; i< enemies.size(); i++){
        	enemies.get(i).draw(myGL);
        }
        for(int i=0; i<traps.size(); i++){
        	//traps.get(i).draw(myGL);
        }
        myGL.glPopMatrix();
    }

}