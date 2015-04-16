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
 * Justin Shen
 * Vijay Upadhya
 * 
 * TODO
 * display enemy info, player info, traps
 * gl picking
 * improve invulnerability animation
 */
public class DojoRenderer extends GLCanvas{
    public static final float SENSITIVITY= 2; //higher number = more sensitive mouse movement
    public static final int NUM_SQUARE = 3; //number of smaller squares along the side of each wall
    public static final float SUN_SIZE= 10.0f; //size of the radius of the sun
    public static final float ENVIRONMENT_SIZE = 700f;//size of the game world
    public static int CENTER_X = 300; //x center of canvas
    public static int CENTER_Y = 300; //y center of canvas
    private Animator anim; //animator object used to animate the canvas
  //  private float player.getPos()[0],player.getPos()[1],player.getPos()[2]; //position of the camera
  //  private float player.getUp()[0],player.getUp()[1],player.getUp()[2]; //up position of the camera
  //  private float player.getDir()[0],player.getDir()[1],player.getDir()[2]; //camera look at position
    private float theta, phi; //angular position of the camera
    private float radius; //distant the camera is set from the camera look at position
    private int currentX; //current x mouse position
    private int currentY; //currnet y mouse position
    private static DojoMinimap mm;
    private Texture text;
    private int[] down=new int[0]; //stores keys pressed
    private KruskyKrab maze; //maze object drawn
    private DojoPlayer player;
    private ArrayList<DojoEnemy> enemies; //enemies in the maze
    private ArrayList<DojoTrap> traps;
    private float size; 
    private boolean isDay;
    private boolean gameWin = false;
    private boolean gameLost = false;
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
                myGLU.gluPerspective(60,1.0,player.getSize(), 2*ENVIRONMENT_SIZE);
            }
        });
        
        //start event detection
        this.addKeyListener(new KeyAdapter(){ //implement KeyAdapter to detect key events
          	@Override
          	public void keyPressed(KeyEvent e){ //stores keys pressed for processing
          		if(e.getKeyChar() ==' '){
          			if(anim.isAnimating()) 	anim.stop();
                	else anim.start();
				}
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
            	  if(Math.abs(currentX-me.getX()) >0){
            		  //pan horiplayer.getDir()[2]ntal
            		  System.out.print("x"+( currentX-me.getX()) +" ");
            		  theta+= (double)((currentX-me.getX()));//-Math.signum(currentX-me.getX())*100)/100 * 2.0);
            	  }
            	  if(Math.abs(currentY-me.getY()) >0){
            		  //pan vertical
            		  System.out.print("y"+ (currentY-me.getY())+" ");
            		  phi+= (double)(currentY-me.getY());//-Math.signum(currentY-me.getY())*100)/100 * 2.0;
            	  }
//            	  if(Math.abs(me.getX()-currentX) !=0)
//                      theta-=SENSITIVITY*(me.getX()-prevX)/Math.abs((me.getX()-prevX)); //increase the number of degrees to rotate
//                  prevX=me.getX();
//                  if(Math.abs(me.getY()-currentY) !=0)
//                      phi+=SENSITIVITY*(me.getY()-prevY)/Math.abs((me.getY()-prevY));
//                  prevY=me.getY();
                  updateCamera();
            	  currentX = me.getX(); 
                  currentY = me.getY();
//                  try{
//                	  Robot robot = new Robot();
//                	  if(Math.abs(currentX-CENTER_X) > 200){
//                		  System.out.print(currentX+" player.getDir()[1] "+currentY);
//                		  robot.mouseMove(CENTER_X,currentY);
//                		  currentX = CENTER_X;
//                	  }                		      
//                	  if( Math.abs(currentY-CENTER_Y) > 200){
//                		  System.out.print(currentX+" player.getDir()[1] "+currentY);
//                		  robot.mouseMove(currentX,CENTER_Y);
//                		  currentY= CENTER_Y;
//                	  }
//            		  System.out.println(currentX+" man "+currentY);
//                  }catch(Exception ex){
//                      ex.printStackTrace();
//                  }
              }
              
          });
          addMouseWheelListener(new MouseWheelListener(){
              @Override
              /**
               * mouseWheelMoved method used to detect the direction and amount of mouse scroll;
               * the method adjust the camera position (player.getDir()[2]om) according to the mouse scroll
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
        for(int i=0; i< 10; i++){
        	int rand= (int)(Math.random()*maze.n*maze.n);
        	System.out.println(maze.cells[rand].x+maze.cells[rand].width/2);
            enemies.add(new DojoEnemy(maze.size/4
            		,maze.cells[rand].x+maze.cells[rand].width/2-maze.n*maze.size/2
            		,maze.cells[rand].y+maze.cells[rand].height/2-maze.n*maze.size/2
            		,maze.size/2));
            //System.out.println(rand);
        }
        for(int i=0; i<5; i++){
        	int rand= (int)(Math.random()*maze.n*maze.n);
            traps.add(new DojoTrap(maze.size/4
            		,maze.cells[rand].x+maze.cells[rand].width/2-maze.n*maze.size/2
            		,maze.cells[rand].y+maze.cells[rand].height/2-maze.n*maze.size/2
            		,0));
            //System.out.println(rand);
        }
        
       // rand=(int)(Math.random()*maze.n*maze.n);
       // traps.add(new DojoTrap(maze.size, maze.cells[rand].x, maze.cells[rand].y, 10));
    }
    /**
     * (re)set to the initial settings of the camera
     */
    public void reset(){
    	//game state reset
    	gameWin=false;
    	gameLost= false;
    	player = new DojoPlayer();
    	//camera state reset
        radius = 25;
        theta=0; 
        phi= 270;    
        player.setPos(new double[]{0, 0, 15}); //set initial player and camera position
        //set camera orientation
        updateCamera();
        //initiate mouse location to center of screen
        currentX= 300;
        currentY = 300;
        
        //initiate game objects
        generateMaze();
        createObjects();
    }
    /**
     * returns the x coordinate in terms of the distance and angles given (spherical coordinate) 
     * offset by where the camera is looking at (player.getDir()[0],player.getDir()[1],player.getDir()[2])
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
     * offset by where the camera is looking at (player.getDir()[0],player.getDir()[1],player.getDir()[2])
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
     * offset by where the camera is looking at (player.getDir()[0],player.getDir()[1],player.getDir()[2])
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
        double xo=getCoordX(radius, phi, theta)+player.getPos()[0];
        double yo=getCoordY(radius,phi,theta)+player.getPos()[1];
        double zo=getCoordZ(radius,phi)+player.getPos()[2];
        double xUp=getCoordX(radius,phi+90,theta);
        double yUp=getCoordY(radius,phi+90,theta);
        double zUp=getCoordZ(radius,phi+90);
        
       //player.setPos(new double[]{player.getPos()[0], player.getPos()[1], player.getPos()[2]});
        player.setUp(new double[]{xUp, yUp, zUp});
        player.setDir(new double[]{xo,yo,zo});
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
        myGLU.gluLookAt(player.getPos()[0],player.getPos()[1],player.getPos()[2],player.getDir()[0],player.getDir()[1],player.getDir()[2], player.getUp()[0], player.getUp()[1], player.getUp()[2]); //the up x,y,z is 90 offset from the camera's position

        processEvents();
        if(Math.abs(player.getPos()[0])> maze.n*maze.size/2+5 || Math.abs(player.getPos()[1]) > maze.n*maze.size/2+5){
        //	switchto2D(myGL);
        	gameWin = true;
        	//displayMessage(myGL, "You Win!");
        	//reset();
        	//anim.stop();
        //	switchto3D(myGL);
        //	anim.
        }
        myGL.glPushMatrix();
        createEnvironment(myGL);
        drawMaze(myGL);
        updateObjects();
        drawObjects(myGL);
        
        myGL.glPopMatrix();
        
        switchto2D(myGL);
        player.draw(myGL);
        drawHUD(myGL);
		if(gameWin){
			displayMessage(myGL, "You Win! (Press Space to play again)");
			reset();
        	anim.stop();
		}
		if(gameLost){
			displayMessage(myGL, "You Lost :( (Press Space to play again)");
			reset();
        	anim.stop();
		}
		
        switchto3D(myGL);
        
    }
    public void displayMessage(GL gl, String msg){
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{0.0f,0.0f,0.0f,1f},0); 
		gl.glColor3f(0f, 0f, 0f);
		gl.glBegin(gl.GL_QUADS);
		gl.glVertex2d(0, 0);
		gl.glVertex2d(600, 0);
		gl.glVertex2d(600, 600);
		gl.glVertex2d(0,600);
		gl.glEnd();
		gl.glColor3f(1f, 1f, 1f);
		//gl.glRasterPos2f(CENTER_X-180, CENTER_Y);
		gl.glRasterPos2f(getWidth()/2-180, getHeight()/2);
		GLUT myGLUT= new GLUT();
		myGLUT.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_24, msg);
    }
    public static void main(String[] args){
        JFrame frame= new JFrame("Dojo Entertainment"); //create frame
        //create and add GLCanvas subclass to frame
        GLCapabilities cap= new GLCapabilities();
        DojoRenderer myCanvas = new DojoRenderer(cap);    
        frame.add(myCanvas);
        //set up frame
        frame.setSize(600, 600);
//        frame.setCursor(frame.getToolkit().createCustomCursor(
//                new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),
//                "null"));
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myCanvas.requestFocus();
    }
   
    /**
     * Process all key and mouse events
     */
    public void processEvents(){
    	//process mouse events
//        if(Math.abs(currentX-CENTER_X) >80){
//        	//pan horiplayer.getDir()[2]ntal
//        	theta-= (double)((currentX-CENTER_X-Math.signum(currentX-CENTER_X)*100)/100 * 2.0);
//        }
//        if(Math.abs(currentY-CENTER_Y) >80){
//        	//pan vertical
//        	phi-= (double)(currentY-CENTER_Y-Math.signum(currentY-CENTER_Y)*100)/100 * 2.0;
//        }
        updateCamera();
        //process key events
    	for(int i=0;i<down.length;i++){
    		if(down[i] == KeyEvent.VK_ESCAPE)//quit
    			System.exit(0); 
           	if(down[i] == KeyEvent.VK_R){ //reset the camera to original setting
                reset();
           	}
           	if(down[i] == KeyEvent.VK_Q){
           		player.jump();
           	}
            double dx=player.getDir()[0]-player.getPos()[0];//lets the camera move front and back around the room
            double dy=player.getDir()[1]-player.getPos()[1];
            double dz=player.getDir()[2]-player.getPos()[2];
            double dist=Math.sqrt(dx*dx+dy*dy+dz*dz);
            if(down[i]==KeyEvent.VK_W && !player.getState()){//move forward
				boolean moveX=true;
				boolean moveY=true;
//				//System.out.println((player.getPos()[0]+maze.n*maze.size/2)+" "+(player.getPos()[1]+maze.n*maze.size/2));
//				for(int k=0;k<maze.edges.length;k++){
//					if(maze.edges[k].x1==maze.edges[k].x2){
//						if(isBetween(player.getPos()[1]+maze.n*maze.size/2,maze.edges[k].y1,maze.edges[k].y2)){
//							if(switched(player.getPos()[0]+maze.n*maze.size/2,2*dx/dist*radius/30, maze.edges[k].x1)){
//								if(!maze.edges[k].joined){
//									moveX=false;
//								}
//							}
//						}
//					}else{
//						if(isBetween(player.getPos()[0]+maze.n*maze.size/2,maze.edges[k].x1,maze.edges[k].x2)){
//							if(switched(player.getPos()[1]+maze.n*maze.size/2,2*dy/dist*radius/30, maze.edges[k].y1)){
//								if(!maze.edges[k].joined){
//									moveY=false;
//								}
//							}
//						}
//					}
//				}
				if(moveX)player.getPos()[0]+=dx/dist*radius/30;
				if(moveY)player.getPos()[1]+=dy/dist*radius/30;

				updateCamera();
			}
			if(down[i]==KeyEvent.VK_S && !player.getState()){//move backward
				boolean moveX=true;
				boolean moveY=true;
//				//System.out.println((player.getPos()[0]+maze.n*maze.size/2)+" "+(player.getPos()[1]+maze.n*maze.size/2));
//				for(int k=0;k<maze.edges.length;k++){
//					if(maze.edges[k].x1==maze.edges[k].x2){
//						if(isBetween(player.getPos()[1]+maze.n*maze.size/2,maze.edges[k].y1,maze.edges[k].y2)){
//							if(switched(player.getPos()[0]+maze.n*maze.size/2,-2*dx/dist*radius/30, maze.edges[k].x1)){
//								if(!maze.edges[k].joined){
//									moveX=false;
//								}
//							}
//						}
//					}else{
//						if(isBetween(player.getPos()[0]+maze.n*maze.size/2,maze.edges[k].x1,maze.edges[k].x2)){
//							if(switched(player.getPos()[1]+maze.n*maze.size/2,-2*dy/dist*radius/30, maze.edges[k].y1)){
//								if(!maze.edges[k].joined){
//									moveY=false;
//								}
//							}
//						}
//					}
//				}
				if(moveX)player.getPos()[0]-=dx/dist*radius/30;
				if(moveY)player.getPos()[1]-=dy/dist*radius/30;
				updateCamera();
			}
			
            if(down[i]==KeyEvent.VK_ESCAPE){//quit
         	   System.exit(1);
            }
//            if(down[i] == KeyEvent.VK_SPACE){
//         	   isDay = !isDay;
//            	
// 			}
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
		mm.draw(myGL);
//		double px=(maze.n*maze.size/2+player.getPos()[0])*150d/(maze.n*maze.size)+450;
//		double py=(maze.n*maze.size/2+player.getPos()[1])*150d/(maze.n*maze.size)+420;
//		double pxo=(maze.n*maze.size/2+player.getDir()[0])*150d/(maze.n*maze.size)+450;
//		double pyo=(maze.n*maze.size/2+player.getDir()[1])*150d/(maze.n*maze.size)+420;
		double px=(player.getPos()[0]+maze.n*maze.size/2)*150d/(maze.n*maze.size)+450;
		double py=(player.getPos()[1]+maze.n*maze.size/2)*150d/(maze.n*maze.size)+420;
		double pxo=(player.getDir()[0]+maze.n*maze.size/2)*150d/(maze.n*maze.size)+450;
		double pyo=(player.getDir()[1]+maze.n*maze.size/2)*150d/(maze.n*maze.size)+420;
		//System.out.println(player.getPos()[0]+" "+player.getPos()[1]);
		double angle = Math.atan((pyo-py) / (pxo-px));
		double shift = 2.0*Math.PI/3.0;
		myGL.glBegin(GL.GL_TRIANGLES);//draws player
		if(pxo>=px){
			myGL.glColor3f(0f,1f,1f);
			myGL.glVertex2d(px+6*Math.cos(angle), py+6*Math.sin(angle)); //facing the direction of the camera
			myGL.glColor3f(1f,1f,1f);
			myGL.glVertex2d(px+4*Math.cos(angle-shift), py+4*Math.sin(angle-shift));
			myGL.glVertex2d(px+4*Math.cos(angle+shift), py+4*Math.sin(angle+shift));
		}
		else{
			myGL.glColor3f(0f,1f,1f);
			myGL.glVertex2d(px-6*Math.cos(angle), py-6*Math.sin(angle)); //facing the direction of the camera
			myGL.glColor3f(1f,1f,1f);
			myGL.glVertex2d(px-4*Math.cos(angle-shift), py-4*Math.sin(angle-shift));
			myGL.glVertex2d(px-4*Math.cos(angle+shift), py-4*Math.sin(angle+shift));
		}
//		myGL.glVertex2d(px-2.5, py-2.5);
//		myGL.glVertex2d(px-2.5, py+2.5);
//		myGL.glVertex2d(px+2.5, py+2.5);
//		myGL.glVertex2d(px+2.5, py-2.5);
		myGL.glEnd();
		for(int i=0;i<enemies.size();i++){
			//System.out.println(enemies.get(i).getPos()()[0]+" "+enemies.get(i).getPos()()[1]);
			px=(enemies.get(i).getPos()[0]+maze.n*maze.size/2)*150d/(maze.n*maze.size)+450;
			py=(enemies.get(i).getPos()[1]+maze.n*maze.size/2)*150d/(maze.n*maze.size)+420;
			myGL.glColor3f(1f,0f,0f);
			myGL.glBegin(GL.GL_QUADS);
			myGL.glVertex2d(px-2, py-2);
			myGL.glVertex2d(px-2, py+2);
			myGL.glVertex2d(px+2, py+2);
			myGL.glVertex2d(px+2, py-2);
			myGL.glEnd();
		}
		for(int i=0;i<traps.size();i++){
			px=(traps.get(i).getPos()[0]+maze.n*maze.size/2)*150d/(maze.n*maze.size)+450;
			py=(traps.get(i).getPos()[1]+maze.n*maze.size/2)*150d/(maze.n*maze.size)+420;
			myGL.glColor3f(1f,0f,1f);
			myGL.glBegin(GL.GL_QUADS);
			myGL.glVertex2d(px-2, py-2);
			myGL.glVertex2d(px-2, py+2);
			myGL.glVertex2d(px+2, py+2);
			myGL.glVertex2d(px+2, py-2);
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
		try {
			text=TextureIO.newTexture(new File("brickWall.jpg") , false);
		} catch (GLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Draw the maze on the canvas
	 * @param myGL gl object used to draw the maze
	 */
    public void drawMaze(GL myGL){
    	myGL.glPushMatrix();
    	//myGL.glTranslated(-maze.n*maze.size/2, -maze.n*maze.size/2,0);
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
    	//myGL.glTranslated(-maze.n*maze.size/2, -maze.n*maze.size/2,0);
    	myGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{0f,1f,1f,1f},0); //set the color maze
        for(int i=0; i< enemies.size(); i++){
        	enemies.get(i).draw(myGL);
        	//detects enemy-player collision
        	if(Math.abs(enemies.get(i).getPos()[0]-maze.n*maze.size/2-player.getPos()[0]) < enemies.get(i).getLength() + player.getSize() &&
        			Math.abs(enemies.get(i).getPos()[1]-player.getPos()[1]-maze.n*maze.size/2) < enemies.get(i).getLength()+player.getSize() ){
        		player.setHealth(player.getHealth()-enemies.get(i).getDamage()); //loses health from attack
        		double dx=player.getDir()[0]-player.getPos()[0];//simulates "bouncing back" reaction
    		   	double dy=player.getDir()[1]-player.getPos()[1];
    		   	double dz=player.getDir()[2]-player.getPos()[2];
    		   	double dist=Math.sqrt(dx*dx+dy*dy+dz*dz);
        		player.getPos()[0]-= dx/dist*player.getSize();
				player.getPos()[1]-= dy/dist*player.getSize();
        		if(player.getHealth() <= 0) gameLost= true;
        	}
        }
        for(int i=0; i<traps.size(); i++){
        	traps.get(i).draw(myGL);
        	//detects trap-player collision
        	if(Math.abs(traps.get(i).getPos()[0]-maze.n*maze.size/2-player.getPos()[0]) < traps.get(i).getSize()+player.getSize() &&
        			Math.abs(traps.get(i).getPos()[1]-player.getPos()[1]-maze.n*maze.size/2) < traps.get(i).getSize()+player.getSize() &&
        			Math.abs(traps.get(i).getPos()[2]-player.getPos()[2]) <= 15){
        		player.setHealth(player.getHealth()-traps.get(i).getDamage()); //loses health from trap
        		if(player.getHealth() <= 0) gameLost= true;
        	}
        }
        myGL.glPopMatrix();
    }
    public void updateObjects(){
    	player.update();
    	for(int i=0; i< enemies.size(); i++){
    		enemies.get(i).update();
        }
//          for(int i=0; i<traps.size(); i++){
//          	traps.get(i).update();
//          }
    	
    }
    public boolean isBetween(double x, double x1, double x2){
		double low=Math.min(x1, x2);
		double high=Math.max(x1, x2);
		if(low<=x&&x<=high)return true;
		return false;
	}
    public boolean switched(double x1, double dx, double x2){
		if(x1<x2){
			if(x1+dx>x2)return true;
			return false;
		}else{
			if(x1+dx<x2)return true;
			return false;
		}
	}

}