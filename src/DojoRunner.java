import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.media.opengl.GLCapabilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;


public class DojoRunner extends JPanel implements Runnable {
	private Thread myThread;
	private BufferedImage title;
	private BufferedImage play;
	private BufferedImage help;
	private BufferedImage build;
	private BufferedImage present;
	private BufferedImage background;
	private Image bgBuffer; // second image for double buffering
	private Rectangle2D.Double[] buttons;
	public static final long SLEEP_START=100; //sleep time when the mode is MODE_START
	public static final long SLEEP_PAUSE=2000; //sleep time when the mode is MODE_END
	public static final long SLEEP_DISPLAY=200; //sleep time when the mode is MODE_DISPLAY
	public static final int MODE_START=1; //mode signifying the starting animation to run
	public static final int MODE_PAUSE=-1; //mode signifying the ending animation to run
	public static final int MODE_DISPLAY=0; //mode signifying data displaying
	public static final int MESSAGE_SIZE =20;
	private int mode;
	private int clicked;
	private String instruction;
	private JScrollPane sp;
	/*
	 * preserve original aspect ratio
	 */
	public BufferedImage resize(BufferedImage pic, int newWidth){
		double scale = (double)newWidth / (double)pic.getWidth();
		BufferedImage tmp=  new BufferedImage((int)(pic.getWidth() *scale) ,(int)(pic.getHeight() *scale),BufferedImage.TYPE_INT_ARGB);
		tmp.getGraphics().drawImage(pic, 0, 0, tmp.getWidth(), tmp.getHeight(), 0, 0, pic.getWidth(), pic.getHeight(), null);
		return tmp;
	}
	public int getMode(){
		return mode;
	}
	public void setMode(int newMode){
		mode= newMode;
	}
	//public DojoUI(){
	public DojoRunner(){
		mode = MODE_START;
		clicked = -1;
		sp = new JScrollPane();
		try {
			title= ImageIO.read(new File("title.jpg"));//"Colorful_Balloons_Clipart.png"));
			present =  ImageIO.read(new File("present.png"));
			play =  ImageIO.read(new File("play.png"));
			build =  ImageIO.read(new File("build.png"));
			help =  ImageIO.read(new File("help.png"));
			background = ImageIO.read(new File("background.jpg"));
//			background = ImageIO.read(new File("maze.jpg"));
			instruction = "\n\n\n";
			Scanner sc = new Scanner(new File("Instruction"));
			while(sc.hasNextLine()){
				instruction+=("\t"+sc.nextLine()+"\n");
			}

			//BufferedImage tmp= ImageIO.read(new File("Clouds.jpg")); //holds the background image picture
			//background= new BufferedImage(tmp.getWidth(),tmp.getHeight(),BufferedImage.TYPE_INT_ARGB); //allow the alpha to be manipulated
			//background.getGraphics().drawImage(tmp, 0, 0, background.getWidth(), background.getHeight(), 0, 0, tmp.getWidth(), tmp.getHeight(), null);
//			for(int i=0; i<scopes.length; i++){
//				scopes[i]=ImageIO.read(new File("scope.png"));
//				transparent(scopes[i]);
//			}
//				transparent(background);
		} catch (IOException e) {
			e.printStackTrace();
		}
//			readData();
		addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent ke){
				if(ke.getKeyCode()==KeyEvent.VK_ESCAPE){
					mode= MODE_PAUSE;
					
				}
			}
		});
		addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent me){
				//chooseGraph();
				int x=me.getX();
				int y= me.getY();
				if(clicked == 3){
					clicked = -1;
					mode = MODE_DISPLAY;
				}
				else
					for(int i =0 ; i< buttons.length; i++){
						if(buttons[i].contains(x,y)){
	//						x=getWidth();
	//						y=getHeight();
							clicked = i;
						}
					}
				if(clicked ==3) mode = MODE_PAUSE;
			}
		});
		myThread= new Thread(this);
		myThread.start();
	}

	public void paint(Graphics g){
		createBGBuffer();
		paintOntoSomethingElse(bgBuffer.getGraphics());
		g.drawImage(bgBuffer, 0, 0, null);
	}
	/**
	* update but don't clear the primary buffer
	*/
	public void update(Graphics g){
		paint(g);
	}
	public void createBGBuffer(){
		if(bgBuffer==null
				||bgBuffer.getWidth(null) != getWidth()
				|| bgBuffer.getHeight(null) != getHeight()){
			bgBuffer= createImage(getWidth(),getHeight());
		}
	}
	/**
	 * Draws all the graphical elements of the project onto the bgBuffer
	 * @param g graphics object used to draw on the canvas 
	 */
	public void paintOntoSomethingElse(Graphics g){
		g.clearRect(0,0,getWidth(),getHeight());
		//setBackground(Color.black);
		Graphics2D g2 = (Graphics2D)g;
		Font messageFont = new Font("Verdana", Font.BOLD, MESSAGE_SIZE);
		g2.setFont(messageFont);
		g2.setColor(Color.CYAN);
		String message = "";
		int xPos = 0;
		int yPos = 0 ;
		if(!message.equals(""))g2.drawString(message, xPos, yPos+g2.getFontMetrics().getMaxAscent());

		switch(clicked){
		case 0 :
			clicked = -1;
			break;
		case 1 : //start the game
			clicked = -1;
	        GLCapabilities cap= new GLCapabilities();
	        DojoRenderer game = new DojoRenderer(cap);
			JPanel panel = (JPanel) getParent();
			panel.getParent().setCursor(panel.getParent().getToolkit().createCustomCursor( //make mouse invisible
	                new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),
	                "null"));
			panel.removeAll();
			//panel.remove(sp);
			panel.add(game,BorderLayout.CENTER);
			panel.getParent().validate();
			game.requestFocus();
			break;
		case 2 :
			clicked = -1;
			
			break;
		case 3 : //display instruction
//			clicked = -1;
			g2.setColor(Color.black);
			g2. setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //enable anti-aliasing 
			g2.drawRect(0, 0, getWidth(), getHeight());
			JTextArea ta = new JTextArea();
			ta.setEditable(false);
			ta.setFont(new Font("Serif", Font.ITALIC, 16));
			ta.append(instruction);
			
			sp = new JScrollPane(ta);
			sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			sp.setPreferredSize(new Dimension(getWidth(), getHeight()/2));
			getParent().add(sp,BorderLayout.CENTER);
			getParent().validate();
			break;
			default:
				g2. setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //enable anti-aliasing 
				g2.drawImage(background, 0, 0, getWidth(), getHeight(), 0, 0, background.getWidth(), background.getHeight(), null);
				g2.drawImage(title, (getWidth()-title.getWidth())/2, getHeight()/4, null);
				g2.drawImage(present , (getWidth()-title.getWidth())/2, getHeight()/4-present.getHeight()-5, null);
				g2.drawImage(play, (getWidth()-play.getWidth())/2, getHeight()/2, null);
				g2.drawImage(build, (getWidth()-build.getWidth())/2, getHeight()/2+play.getHeight(), null);
				g2.drawImage(help, (getWidth()-help.getWidth())/2, getHeight()/2+play.getHeight()+build.getHeight(), null);
				break;
		}
	}
	public void run() {
		while(Thread.currentThread() == myThread){
			repaint();
			try {
				if(mode==MODE_DISPLAY){
					Thread.sleep(SLEEP_DISPLAY);

				}
				else if(mode ==MODE_START && getWidth() != 0 && getHeight()!=0){
					Thread.sleep(SLEEP_START);
					title = resize(title, getWidth()/2);
					present = resize(present, getWidth()/4);
					play = resize(play,getWidth()/8);
					build = resize(build,getWidth()/8);
					help = resize(help,getWidth()/8);

					buttons = new Rectangle2D.Double[4];
					buttons[0] = new Rectangle2D.Double((double)(getWidth()-title.getWidth())/2, (double)(getHeight()/4-present.getHeight()-5), present.getWidth(), present.getHeight());
					buttons[1] =new Rectangle2D.Double( (double)(getWidth()-play.getWidth())/2, (double) getHeight()/2, play.getWidth(), play.getHeight());
					buttons[2] =new Rectangle2D.Double((double)(getWidth()-build.getWidth())/2, (double)getHeight()/2+play.getHeight(), build.getWidth(), build.getHeight());
					buttons[3] =new Rectangle2D.Double( (double)(getWidth()-help.getWidth())/2, (double)getHeight()/2+play.getHeight()+build.getHeight(), help.getWidth(), help.getHeight());
					mode = MODE_DISPLAY;
				}
				else if(mode == MODE_PAUSE){
					Thread.sleep(SLEEP_PAUSE);
//						endingAnimation();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
//	}
	  public static void main(String[] args){
	        JFrame frame= new JFrame("Dojo Entertainment"); //create frame
//	        GLCapabilities cap= new GLCapabilities();
//	        DojoRenderer myCanvas = new DojoRenderer(cap);  
//	        myCanvas.start();
	        JPanel panel= new JPanel();
	        panel.setLayout(new BorderLayout());
//	        panel.add(new JButton(),BorderLayout.PAGE_END);
//	        panel.add(new JButton(),BorderLayout.PAGE_START);
//	        panel.add(new JButton(),BorderLayout.LINE_END);
//	        panel.add(new JButton(),BorderLayout.LINE_START);
	        

	        DojoRunner myUI= new DojoRunner();
	        panel.add(myUI,BorderLayout.CENTER);
	        frame.add(panel);
	        //set up frame
	        int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	        int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight(); 
	        frame.setSize(width, height-35);
//	        panel.remove(myUI);
//	        panel.add(myCanvas, BorderLayout.CENTER);
//	        frame.setCursor(frame.getToolkit().createCustomCursor( //make mouse invisible
//	                new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),
//	                "null"));
	        frame.setVisible(true);
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    //    myCanvas.requestFocus();
	    }
}
