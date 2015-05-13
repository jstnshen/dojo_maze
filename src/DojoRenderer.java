import java.awt.BorderLayout;
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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

/*
 * Justin Shen
 * Vijay Upadhya
 * 
 * TODO
Goal by next week: traps, enemies, progression, shooting (everything but themes and graphics and UI)
				new types of traps (projectile), set up dojoBonus
Player ability:
shoot (limited amount of ammo), jump, shield, special actions

 */
public class DojoRenderer extends GLCanvas{
    public static final float SENSITIVITY= 10.0f; //higher number = more sensitive mouse movement
    public static final int NUM_SQUARE = 3; //number of smaller squares along the side of each wall
    public static final float SUN_SIZE= 10.0f; //size of the radius of the sun
    public static final float ENVIRONMENT_SIZE = 700f;//size of the game world
    public static int REFERENCE_X = 300; //x center of canvas
    public static int REFERENCE_Y = 300; //y center of canvas
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
    private ArrayList<DojoProjectile> proj;
    private ArrayList<DojoBonus> bonus;
    private float size; 
    private boolean isDay;
    private boolean mazeWin = false;
    private boolean gameLost = false;
    private boolean gameWin= false;
    private boolean intro=true;
    DojoShape shape=new DojoShape("cube.txt");
    private boolean isShooting = false;
    private double minimapSize = 200;
    private int level;
    /**
     * Constructor, initiate the settings the camera and the size of the objects
     * @param glCap
     */
    public DojoRenderer(GLCapabilities glCap){
        super(glCap);
        anim = new Animator(this);
    	isDay=true;
    	level = 1;
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
          		if(e.getKeyChar() =='p'){
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
                	  if(!intro){
                		  isShooting=true;
                	  }
                  }
              }
          });
          addMouseMotionListener(new MouseAdapter(){ //detect mouse motion for panning
              @Override
              public void mouseMoved(MouseEvent me){ //update current location of the mouse in the canvas	
            	  if(Math.abs(currentX-me.getX()) >0){
            		  //pan horiplayer.getDir()[2]
            		  theta+= (double)((currentX-me.getX()))/SENSITIVITY;//-Math.signum(currentX-me.getX())*100)/100 * 2.0);
            	  }
            	  if(Math.abs(currentY-me.getY()) >0){
            		  //pan vertical
            		  phi+= (double)(currentY-me.getY())/SENSITIVITY;//-Math.signum(currentY-me.getY())*100)/100 * 2.0;
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
                  try{
                	  if(currentX==me.getX() && currentY==me.getY()){
                		  Robot robot = new Robot();
                    	  robot.mouseMove(REFERENCE_X,REFERENCE_Y+45);
                		  currentX = REFERENCE_X;
                		  currentY=REFERENCE_Y;
                	  }
                	  
//                	  if(Math.abs(currentX-REFERENCE_X) > -){
//                		  System.out.print(currentX+" player.getDir()[1] "+currentY);
//                		  robot.mouseMove(REFERENCE_X,currentY);
//                		  currentX = REFERENCE_X;
//                	  }                		      
//                	  if( Math.abs(currentY-REFERENCE_Y) > -){
//                		  System.out.print(currentX+" player.getDir()[1] "+currentY);
//                		  robot.mouseMove(currentX,REFERENCE_Y);
//                		  currentY= REFERENCE_Y;
//                	  }
//            		  System.out.println(currentX+" man "+currentY);
                  }catch(Exception ex){
                      ex.printStackTrace();
                  }
              }
              
          });
          addMouseWheelListener(new MouseWheelListener(){
              @Override
              /**
               * mouseWheelMoved method used to detect the direction and amount of mouse scroll;
               * the method adjust the camera position (player.getDir()[2]) according to the mouse scroll
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
     * (re)set to the initial settings of the camera
     */
    public void reset(){
    	//game state reset
    	gameWin=false;
    	mazeWin = false;
    	gameLost= false;
    	intro=true;
    //	level=1;
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
        //myGLU.gluLookAt(2*player.getPos()[0]-player.getDir()[0],2*player.getPos()[1]-player.getDir()[1],2*player.getPos()[2]-player.getDir()[2],player.getPos()[0],player.getPos()[1],player.getPos()[2], player.getUp()[0], player.getUp()[1], player.getUp()[2]); 
        processEvents();
        if(!intro){
	        if(Math.abs(player.getPos()[0])> maze.n*maze.size/2+5 || Math.abs(player.getPos()[1]) > maze.n*maze.size/2+5){
	        //	switchto2D(myGL);
	        	//gameWin = true;
	        	if(level<5) mazeWin=true; //finished maze
	        	else gameWin= true; //finish game
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
        }
        switchto2D(myGL);
        player.displayStatus(myGL);
        drawHUD(myGL);
        if(intro){
        	displayMessage(myGL, "WELCOME!");
			//reset();
        	//anim.stop();
        }
		if(gameWin){
			displayMessage(myGL, "You Win! (Press 'p' to play again)");
			level = 1;
			reset();
        	anim.stop();
		}
		if(mazeWin){
			displayMessage(myGL, "You solved the maze! But can you solve the next one? (Press 'p' to continue)");
			level++;
			reset();
        	anim.stop();
		}
		if(gameLost){
			displayMessage(myGL, "You Lost :( (Press 'p' to play again)");
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
		gl.glVertex2d(getWidth(), 0);
		gl.glVertex2d(getWidth(), getHeight());
		gl.glVertex2d(0,getHeight());
		gl.glEnd();
		gl.glColor3f(1f, 1f, 1f);
		//gl.glRasterPos2f(REFERENCE_X-180, REFERENCE_Y);
		gl.glRasterPos2f(getWidth()/2-180, getHeight()/2);
		GLUT myGLUT= new GLUT();
		myGLUT.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_24, msg);
	//	System.out.println(myGLUT.glutStrokeLength(GLUT.BITMAP_TIMES_ROMAN_24, msg));
    }
    public static void main(String[] args){
        JFrame frame= new JFrame("Dojo Entertainment"); //create frame
        GLCapabilities cap= new GLCapabilities();
        DojoRenderer myCanvas = new DojoRenderer(cap);    
        JPanel panel= new JPanel();
        panel.setLayout(new BorderLayout());
//        panel.add(new JButton(),BorderLayout.PAGE_END);
//        panel.add(new JButton(),BorderLayout.PAGE_START);
//        panel.add(new JButton(),BorderLayout.LINE_END);
//        panel.add(new JButton(),BorderLayout.LINE_START);
        panel.add(myCanvas, BorderLayout.CENTER);
        frame.add(panel);
        //set up frame
        frame.setSize(1000, 1000);
        frame.setCursor(frame.getToolkit().createCustomCursor( //make mouse invisible
                new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),
                "null"));
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myCanvas.requestFocus();
    }
	/**
     * initiate game objects
     */
    public void createObjects(){
        enemies = new ArrayList<DojoEnemy> ();
        traps = new ArrayList<DojoTrap>();
        bonus = new ArrayList<DojoBonus>();
        proj = new ArrayList<DojoProjectile>();
        for(int i=0; i< 10*level; i++){
        	int rand= (int)(Math.random()*maze.n*maze.n);
            enemies.add(new DojoEnemy(maze.size/4
            		,maze.cells[rand].x+maze.cells[rand].width/2
            		,maze.cells[rand].y+maze.cells[rand].height/2
            		,maze.size/2, shape));
            //System.out.println(rand);
        }
        for(int i=0; i<5*level; i++){
        	int rand= (int)(Math.random()*maze.n*maze.n);
            traps.add(new DojoTrap(maze.size/4
            		,maze.cells[rand].x+maze.cells[rand].width/2
            		,maze.cells[rand].y+maze.cells[rand].height/2
            		,0));
            //System.out.println(rand);
        }
        for(int i=0; i<10*level; i++){
        	int rand= (int)(Math.random()*maze.n*maze.n);
            bonus.add(new DojoBonus(maze.size/4
            		,maze.cells[rand].x+maze.cells[rand].width/2
            		,maze.cells[rand].y+maze.cells[rand].height/2 
            		,0));
        }
    }
    /**
     * Process all key and mouse events
     */
    public void processEvents(){
    	//process mouse events
//        if(Math.abs(currentX-REFERENCE_X) >80){
//        	//pan horiplayer.getDir()[2]ntal
//        	theta-= (double)((currentX-REFERENCE_X-Math.signum(currentX-REFERENCE_X)*100)/100 * 2.0);
//        }
//        if(Math.abs(currentY-REFERENCE_Y) >80){
//        	//pan vertical
//        	phi-= (double)(currentY-REFERENCE_Y-Math.signum(currentY-REFERENCE_Y)*100)/100 * 2.0;
//        }
        updateCamera();
        //process key events
    	for(int i=0;i<down.length;i++){
    		if(down[i] == KeyEvent.VK_ESCAPE)//quit
    			System.exit(0); 
           	if(down[i] == KeyEvent.VK_R){ //reset the camera to original setting
                reset();
           	}
           	if(down[i] == KeyEvent.VK_SPACE){
           		player.jump();
           	}
            double dx=player.getDir()[0]-player.getPos()[0];//lets the camera move front and back around the room
            double dy=player.getDir()[1]-player.getPos()[1];
            double dz=player.getDir()[2]-player.getPos()[2];
            double dist1=Math.sqrt(dx*dx+dy*dy);
			double x = player.getUp()[1] * player.getDir()[2] - player.getUp()[2] * dy;
			double y = player.getUp()[2] * dx - player.getUp()[0] * player.getDir()[2];
			double z = player.getUp()[0] * dy - player.getUp()[1] * dx;
			double dist2 = Math.sqrt((x-player.getPos()[0])*(x-player.getPos()[0])+(y-player.getPos()[1])*(y-player.getPos()[1]));
            if(down[i]==KeyEvent.VK_W && !player.getState()){//move forward
				boolean moveX=true;
				boolean moveY=true;
//				//System.out.println((player.getPos()[0]+maze.n*maze.size/2)+" "+(player.getPos()[1]+maze.n*maze.size/2));
				for(int k=0;k<maze.edges.length;k++){
					if(maze.edges[k].x1==maze.edges[k].x2){
						if(isBetween(player.getPos()[1],maze.edges[k].y1,maze.edges[k].y2)){
							if(switched(player.getPos()[0],2*dx/dist1, maze.edges[k].x1)){
								if(!maze.edges[k].joined){
									moveX=false;
								}
							}
						}
					}else{
						if(isBetween(player.getPos()[0],maze.edges[k].x1,maze.edges[k].x2)){
							if(switched(player.getPos()[1],2*dy/dist1, maze.edges[k].y1)){
								if(!maze.edges[k].joined){
									moveY=false;
								}
							}
						}
					}
				}
				if(moveX)player.getPos()[0]+=dx/dist1;
				if(moveY)player.getPos()[1]+=dy/dist1;
				System.out.println(Math.pow(dx/dist1,2) + Math.pow(dy/dist1,2));//TODO
				updateCamera();
			}
			if(down[i]==KeyEvent.VK_S && !player.getState()){//move backward
				boolean moveX=true;
				boolean moveY=true;
				for(int k=0;k<maze.edges.length;k++){
					if(maze.edges[k].x1==maze.edges[k].x2){
						if(isBetween(player.getPos()[1],maze.edges[k].y1,maze.edges[k].y2)){
							if(switched(player.getPos()[0],-2*dx/dist1, maze.edges[k].x1)){
								if(!maze.edges[k].joined){
									moveX=false;
								}
							}
						}
					}else{
						if(isBetween(player.getPos()[0],maze.edges[k].x1,maze.edges[k].x2)){
							if(switched(player.getPos()[1],-2*dy/dist1, maze.edges[k].y1)){
								if(!maze.edges[k].joined){
									moveY=false;
								}
							}
						}
					}
				}
				if(moveX)player.getPos()[0]-=dx/dist1;
				if(moveY)player.getPos()[1]-=dy/dist1;
				System.out.println(Math.pow(dx/dist1,2) + Math.pow(dy/dist1,2));//TODO
				updateCamera();
			}
			if(down[i]==KeyEvent.VK_D && !player.getState()){//move right
				boolean moveX=true;
				boolean moveY=true;

				if(moveX){
					player.getPos()[0]-=x/dist2;
				//	player.getDir()[0]-=x/100*radius/30;
				}
				if(moveY){
					player.getPos()[1]-=y/dist2;
				//	player.getDir()[1]-=y/100*radius/30;
				}
				updateCamera();
			}
			if(down[i]==KeyEvent.VK_A && !player.getState()){//move right
				boolean moveX=true;
				boolean moveY=true;

				if(moveX){
					player.getPos()[0]+=x/dist2;
				//	player.getDir()[0]+=x/100*radius/30;
				}
				if(moveY){
					player.getPos()[1]+=y/dist2;
				//	player.getDir()[1]+=y/100*radius/30;
				}
				updateCamera();
			}
			if(down[i]==KeyEvent.VK_ENTER){
				reset();
				intro=false;
			}
            if(down[i]==KeyEvent.VK_ESCAPE){//quit
         	   System.exit(1);
            }

    	}
    }
  
    /**
     * create a new, random maze for the game 
     */
    public void generateMaze(){
    	maze= new KruskyKrab(30,5*(level-1)+15);//generate the initial maze
    	for(int i=0;i<2*maze.n;i++){ //remove some random walls in the existing maze
    		int ran=(int)(Math.random()*2*maze.n*(maze.n+1));
	      	if(!maze.edges[ran].joined){
	      		maze.edges[ran].joined=true;
	      	}else{
	      		i--;
	      	}
    	}

        mm=new DojoMinimap(maze,getWidth()-minimapSize-1,getHeight()-minimapSize-1,minimapSize);
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
//		double pyo=(maze.n*maze.xsize/2+player.getDir()[1])*150d/(maze.n*maze.size)+420;
		double px=(player.getPos()[0]+maze.n*maze.size/2)*minimapSize/(maze.n*maze.size)+getWidth()-minimapSize;
		double py=(player.getPos()[1]+maze.n*maze.size/2)*minimapSize/(maze.n*maze.size)+getHeight()-minimapSize;
		double pxo=(player.getDir()[0]+maze.n*maze.size/2)*minimapSize/(maze.n*maze.size)+getWidth()-minimapSize;
		double pyo=(player.getDir()[1]+maze.n*maze.size/2)*minimapSize/(maze.n*maze.size)+getHeight()-minimapSize;
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
			px=(enemies.get(i).getPos()[0]+maze.n*maze.size/2)*minimapSize/(maze.n*maze.size)+getWidth()-minimapSize;
			py=(enemies.get(i).getPos()[1]+maze.n*maze.size/2)*minimapSize/(maze.n*maze.size)+getHeight()-minimapSize;
			myGL.glColor3f(1f,0f,0f);
			myGL.glBegin(GL.GL_QUADS);
			myGL.glVertex2d(px-2, py-2);
			myGL.glVertex2d(px-2, py+2);
			myGL.glVertex2d(px+2, py+2);
			myGL.glVertex2d(px+2, py-2);
			myGL.glEnd();
		}
		for(int i=0;i<traps.size();i++){
			px=(traps.get(i).getPos()[0]+maze.n*maze.size/2)*minimapSize/(maze.n*maze.size)+getWidth()-minimapSize;
			py=(traps.get(i).getPos()[1]+maze.n*maze.size/2)*minimapSize/(maze.n*maze.size)+getHeight()-minimapSize;
			myGL.glColor3f(1f,0f,1f);
			myGL.glBegin(GL.GL_QUADS);
			myGL.glVertex2d(px-2, py-2);
			myGL.glVertex2d(px-2, py+2);
			myGL.glVertex2d(px+2, py+2);
			myGL.glVertex2d(px+2, py-2);
			myGL.glEnd();
		}
		for(int i=0;i<proj.size();i++){
			px=(proj.get(i).getPos()[0]+maze.n*maze.size/2)*minimapSize/(maze.n*maze.size)+getWidth()-minimapSize;
			py=(proj.get(i).getPos()[1]+maze.n*maze.size/2)*minimapSize/(maze.n*maze.size)+getHeight()-minimapSize;
			myGL.glColor3f(1f,1f,1f);
			myGL.glBegin(GL.GL_QUADS);
			myGL.glVertex2d(px-2, py-2);
			myGL.glVertex2d(px-2, py+2);
			myGL.glVertex2d(px+2, py+2);
			myGL.glVertex2d(px+2, py-2);
			myGL.glEnd();
		}
		for(int i=0;i<bonus.size();i++){
			px=(bonus.get(i).getPos()[0]+maze.n*maze.size/2)*minimapSize/(maze.n*maze.size)+getWidth()-minimapSize;
			py=(bonus.get(i).getPos()[1]+maze.n*maze.size/2)*minimapSize/(maze.n*maze.size)+getHeight()-minimapSize;
			myGL.glColor3f(1f,1f,0f);
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

    	myGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{1f,1f,0f,1f},0);
    	for(int i=0; i< bonus.size();i++){
    		bonus.get(i).draw(myGL);
        	//detects enemy-bonus collision
        	if(Math.abs(bonus.get(i).getPos()[0]-player.getPos()[0]) < bonus.get(i).getSize() + player.getSize() &&
        			Math.abs(bonus.get(i).getPos()[1]-player.getPos()[1]) < bonus.get(i).getSize()+player.getSize() ){
        		bonus.get(i).activate(player,maze, proj);
        		bonus.remove(i);
        		if(player.getHealth() <= 0) gameLost= true;
        	}
    	}
    	myGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{0f,1f,1f,1f},0); 
        for(int i=0; i< enemies.size(); i++){
        	enemies.get(i).draw(myGL);
        	//detects enemy-player collision
        	if(Math.abs(enemies.get(i).getPos()[0]-player.getPos()[0]) < enemies.get(i).getLength() + player.getSize() &&
        			Math.abs(enemies.get(i).getPos()[1]-player.getPos()[1]) < enemies.get(i).getLength()+player.getSize() ){
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
        	if(Math.abs(traps.get(i).getPos()[0]-player.getPos()[0]) < traps.get(i).getSize()+player.getSize() &&
        			Math.abs(traps.get(i).getPos()[1]-player.getPos()[1]) < traps.get(i).getSize()+player.getSize() &&
        			Math.abs(traps.get(i).getPos()[2]-player.getPos()[2]) <= 15){
        		player.setHealth(player.getHealth()-traps.get(i).getDamage()); //loses health from trap
        		if(player.getHealth() <= 0) gameLost= true;
        	}
        }
        
        for(int i=0;i<proj.size();i++){
        	 double dx=proj.get(i).getDir()[0];
             double dy=proj.get(i).getDir()[1];
             double dz=proj.get(i).getDir()[2];;
             double dist1=Math.sqrt(dx*dx+dy*dy);
// 			double x = proj.get(i).getUp()[1] * proj.get(i).getDir()[2] - proj.get(i).getUp()[2] * dy;
// 			double y = proj.get(i).getUp()[2] * dx -proj.get(i).getUp()[0] * proj.get(i).getDir()[2];
// 			double z = proj.get(i).getUp()[0] * dy - proj.get(i).getUp()[1] * dx;
 			//double dist2 = Math.sqrt((x-player.getPos()[0])*(x-player.getPos()[0])+(y-player.getPos()[1])*(y-player.getPos()[1]));
 			for(int k=0;k<maze.edges.length;k++){
				if(maze.edges[k].x1==maze.edges[k].x2){
					if(isBetween(proj.get(i).getPos()[1],maze.edges[k].y1,maze.edges[k].y2)){
						if(switched(proj.get(i).getPos()[0],2*dx/dist1, maze.edges[k].x1)){
							if(!maze.edges[k].joined){
								proj.remove(i);
								break;
							}
						}
					}
				}else{
					if(isBetween(proj.get(i).getPos()[0],maze.edges[k].x1,maze.edges[k].x2)){
						if(switched(proj.get(i).getPos()[1],2*dy/dist1, maze.edges[k].y1)){
							if(!maze.edges[k].joined){
								proj.remove(i);
								break;
							}
						}
					}
				}
			}
        }
        for(int i=0;i<proj.size();i++){
        	 double dx=proj.get(i).getPos()[0]-player.getPos()[0];
             double dy=proj.get(i).getPos()[1]-player.getPos()[1];
             double dz=proj.get(i).getPos()[2]-player.getPos()[2];;
             double dist1=Math.sqrt(dx*dx+dy*dy+dz*dz);
        	if(dist1<proj.get(i).getSize()+player.getSize())player.setHealth(player.getHealth()-5);
        }
        myGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{0.6f,0.6f,0.6f,1f},0);
    	for(int i=0; i<proj.size(); i++){
    		proj.get(i).draw(myGL);
    	}
//        myGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{0f,0f,0f,1f},0); //color of the player
//        player.draw(myGL);
        myGL.glPopMatrix();
    }
    public void updateObjects(){
    	player.update();
    	if(player.getBulletLeft()<=0) isShooting = false;
    	if(isShooting){
    		isShooting = false;
    		player.attack();
    		proj.add(new DojoProjectile(2, player.getPos()[0], player.getPos()[1], player.getPos()[2],player.getDir(), 5));

    	}
    	for(int i=0; i< enemies.size(); i++){
    		enemies.get(i).update(player, maze);
        }
        for(int i=0; i<traps.size(); i++){
         	if(traps.get(i).getType() == DojoTrap.DOJO_TRAP_SHOOTER && traps.get(i).attack(player)){
         		double[] dir= { player.getPos()[0], player.getPos()[1], player.getPos()[2]-traps.get(i).getSize()};
         		proj.add(new DojoProjectile(2, traps.get(i).getPos()[0], traps.get(i).getPos()[1], traps.get(i).getPos()[2],dir, 2.5));
         	}
         	
        }
//      for(int i=0; i<bonus.size(); i++){
//  	bonus.get(i).update();
//  }
    	for(int i=0; i<proj.size(); i++){
    		proj.get(i).update();
    		
    	}
    	
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