package com.project;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;


public class Render extends GLSurfaceView implements Renderer {
	
	/** Cube instance */
	private Cube cube;	
	
	/* Rotation values */
	private float xrot;					//X Rotation
	private float yrot;					//Y Rotation

	/* Rotation speed values */
	private float xspeed;				//X Rotation Speed ( NEW )
	private float yspeed;				//Y Rotation Speed ( NEW )
	
	private float z = -5.0f;			//Depth Into The Screen ( NEW )
	
	private int filter = 0;				//Which texture filter? ( NEW )
	
	private GL10 mGL10;
	
	/** Is light enabled ( NEW ) */
	private boolean light = false;

	/* 
	 * The initial light values for ambient and diffuse
	 * as well as the light position ( NEW ) 
	 */
	private float[] lightAmbient = {0.5f, 0.5f, 0.5f, 1.0f};
	private float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};
	private float[] lightPosition = {0.0f, 0.0f, 2.0f, 1.0f};
		
	/* The buffers for our light values ( NEW ) */
	private FloatBuffer lightAmbientBuffer;
	private FloatBuffer lightDiffuseBuffer;
	private FloatBuffer lightPositionBuffer;
	
	//private Buffer pixelRead;
	
	private float x;
	private float y;
	
	/*
	 * These variables store the previous X and Y
	 * values as well as a fix touch scale factor.
	 * These are necessary for the rotation transformation
	 * added to this lesson, based on the screen touches. ( NEW )
	 */
	private float oldX;
    private float oldY;
	private final float TOUCH_SCALE = 0.2f;		//Proved to be good for normal rotation ( NEW )
	
	/** The Activity Context */
	private Context context;
	private boolean mHasPerformedLongPress;
	private CheckForLongPress mPendingCheckForLongPress;

    private boolean blend;
    
    public static int mWidth;
    public static int mHeight;

   // private Stars stars;

	private boolean rotFlag;
	private Vibrator vibrator;
    private int mFlag;
	/**
	 * Instance the Cube object and set the Activity Context 
	 * handed over. Initiate the light buffers and set this 
	 * class as renderer for this now GLSurfaceView.
	 * Request Focus and set if focusable in touch mode to
	 * receive the Input from Screen and Buttons  
	 * 
	 * @param context - The Activity Context
	 */
	public Render(Context context) {
		super(context);
		
		//Set this as Renderer
		this.setRenderer(this);
		//Request focus, otherwise buttons won't react
		this.requestFocus();
		this.setFocusableInTouchMode(true);
		
		this.setLongClickable(false);
		
		
		 vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
		//
		this.context = context;		
		
		//
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(lightAmbient.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightAmbientBuffer = byteBuf.asFloatBuffer();
		lightAmbientBuffer.put(lightAmbient);
		lightAmbientBuffer.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(lightDiffuse.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightDiffuseBuffer = byteBuf.asFloatBuffer();
		lightDiffuseBuffer.put(lightDiffuse);
		lightDiffuseBuffer.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(lightPosition.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		lightPositionBuffer = byteBuf.asFloatBuffer();
		lightPositionBuffer.put(lightPosition);
		lightPositionBuffer.position(0);
		
		//
		cube = new Cube();
	}

	/**
	 * The Surface is created/init()
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {		
		//And there'll be light!
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbientBuffer);		//Setup The Ambient Light ( NEW )
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuseBuffer);		//Setup The Diffuse Light ( NEW )
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPositionBuffer);	//Position The Light ( NEW )
		gl.glEnable(GL10.GL_LIGHT0);											//Enable Light 0 ( NEW )
		
		
	   // gl.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);               //Full Brightness. 50% Alpha ( NEW )
	    
	    gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE); 

		//Settings
		gl.glDisable(GL10.GL_DITHER);				//Disable dithering ( NEW )
		gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping
		gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); 	//Black Background
		gl.glClearDepthf(1.0f); 					//Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do
		
		//Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); 
				
		//Load the texture for the cube once during Surface creation
		cube.loadGLTexture(gl, this.context);
	}

	/**
	 * Here we do our drawing
	 */
	public void onDrawFrame(GL10 gl) {
		
		gl.glClearColor(0f,0f,.3f,.3f);
		//Clear Screen And Depth Buffer
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);	
		gl.glLoadIdentity();					//Reset The Current Modelview Matrix
		
		//Check if the light flag has been set to enable/disable lighting
		if(light) {
			gl.glEnable(GL10.GL_LIGHTING);
		} else {
			gl.glDisable(GL10.GL_LIGHTING);
		}
		if(blend){
		    gl.glEnable(GL10.GL_BLEND);           //Turn Blending On ( NEW )
		    gl.glDisable(GL10.GL_DEPTH_TEST);
		}
		else{
		    gl.glDisable(GL10.GL_BLEND);           //Turn Blending On ( NEW )
            gl.glEnable(GL10.GL_DEPTH_TEST);
		}
		
		//Drawing
		gl.glTranslatef(0.0f, 0.0f, z);			//Move z units into the screen
		gl.glScalef(0.8f, 0.8f, 0.8f); 			//Scale the Cube to 80 percent, otherwise it would be too large for the screen
		
		//Rotate around the axis based on the rotation matrix (rotation, x, y, z)
		gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);	//X
		gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);	//Y
				
		cube.draw(gl, filter);					//Draw the Cube	
		
		//Change rotation factors
		xrot += xspeed;
		yrot += yspeed;
	}		
	private void initVar(GL10 gl){
		mGL10=gl;
	}
	

	/**
	 * If the surface changes, reset the view
	 */
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if(height == 0) { 						//Prevent A Divide By Zero By
			height = 1; 						//Making Height Equal One
		}
		mWidth=width;
		mHeight=height;
		gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
		gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
		gl.glLoadIdentity(); 					//Reset The Projection Matrix

		//Calculate The Aspect Ratio Of The Window
		GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);

		gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
		gl.glLoadIdentity(); 					//Reset The Modelview Matrix
	}
	
/* ***** Listener Events ( NEW ) ***** */	
	/**
	 * Override the key listener to receive keyUp events.
	 * 
	 * Check for the DPad presses left, right, up, down and middle.
	 * Change the rotation speed according to the presses
	 * or change the texture filter used through the middle press.
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.d("HHHHHHHHHHCCCCCCCCCCCC:","Keycode:"+keyCode);
		if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			yspeed -= 0.1f;
			blend = true;
			
		} else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			yspeed += 0.1f;
			blend = true;
			
		} else if(keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			xspeed -= 0.1f;
			blend = true;
			
		} else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			xspeed += 0.1f;
			blend = true;
			
		} else if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			/*filter += 1;
			if(filter > 2) {
				filter = 0;
			}*/
			blend = false;
			xspeed=0;
			yspeed=0;
		}

		//We handled the event
		return true;
	}
		
			
	/**
	 * Override the touch screen listener.
	 * 
	 * React to moves and presses on the touchscreen.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//
		 x = event.getX();
         y = event.getY();
        boolean flag = true;
        
        //If a touch is moved on the screen
        if(event.getAction() == MotionEvent.ACTION_MOVE) {
        	//Calculate the change
        	float dx = x - oldX;
	        float dy = y - oldY;
	        
        	//Define an upper area of 10% on the screen
        	int upperArea = this.getHeight() / 10;
        	ByteBuffer PixelBuffer = ByteBuffer.allocateDirect(4); 
        	PixelBuffer.order(ByteOrder.nativeOrder()); 
       
        	/*
        	//Zoom in/out if the touch move has been made in the upper
        	if(y < upperArea) {
        		z -= dx * TOUCH_SCALE / 2;
        	*/
        	//Rotate around the axis otherwise
	
    	        xrot += dy * TOUCH_SCALE;
    	        yrot += dx * TOUCH_SCALE;
    	        
    	        if(yrot >= 360) {
    	            yrot = yrot-360;
    	        }
    	        
    	        else if (yrot <= -360){
    	            yrot = yrot + 360;
    	        }
    	        
    	        if(xrot >= 360) {
    	            xrot = xrot-360;
    	        }
    	        
    	        else if (xrot <= -360){
    	            xrot = xrot + 360;
    	        }
    	        
    	        
    	   //     Log.v("QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQqq","YROT VALUE:" + yrot);
    	     //   Log.v("111111111111111111111111111111111111111111111","XROT VALUE:" + xrot);
    	        
    	       
    	        

        
        //A press on the screen
        } else if(event.getAction() == MotionEvent.ACTION_UP) {
        	//Define an upper area of 10% to define a lower area
        	int upperArea = this.getHeight() / 10;
        	int lowerArea = this.getHeight() - upperArea;
        	flag=false;
        	
        	
        	//for first face (Music)
        	if(yrot < 45 && xrot<45 && xrot>-45 && yrot > -45 || yrot>315 && xrot<45 && xrot>-45 ||yrot>135 &&yrot<225 && xrot>-225 && xrot<-135 ||
        			yrot>-225 &&yrot<-135 && xrot>-225 && xrot<-135 || yrot>135 &&yrot<225 && xrot>135 && xrot<225 || xrot>135 &&xrot<225 && yrot>-225 && yrot<-135 ){
        	    Log.v("QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQqq","YsdfdsfsdddddddddE:");
        	   // mGL10.glRotatef(200,1,1,1);
        	    if(yrot>135 &&yrot<225 && xrot>-225 && xrot<-135 || yrot>-225 &&yrot<-135 && xrot>-225 && xrot<-135 || yrot>135 &&yrot<225 && xrot>135 && xrot<225
        	    		|| xrot>135 &&xrot<225 && yrot>-225 && yrot<-135 ){
        	    	yrot=180;xrot=-180;
        	    	//vibrator.vibrate(1000);
        	    	this.performHapticFeedback(1);
        	    }
        	    else{
        	    	yrot=0;xrot=0;
        	    	//vibrator.vibrate(1000);
        	    	this.performHapticFeedback(1);
        	    }
        	}
        	//for second face (text)
        	if(yrot<135 && yrot > 45 && xrot<45 && xrot>-45 || yrot<-225 && yrot>-315 && xrot<45 && xrot>-45 || yrot>225 &&yrot<315 && xrot>-225 && xrot<-135 ||
        			yrot>-135 &&yrot<-45 && xrot>-225 && xrot<-135 || xrot>135&&xrot<225 && yrot>225 && yrot <315 || xrot>135&&xrot<225&& yrot<-45&&yrot>-135){
        		if(yrot>225 &&yrot<315 && xrot>-225 && xrot<-135 || yrot>-135 &&yrot<-45 && xrot>-225 && xrot<-135 || xrot>135&&xrot<225 && yrot>225 && yrot <315 ||
        				xrot>135&&xrot<225&& yrot<-45&&yrot>-135){
        			yrot=270;xrot=-180;
        			//vibrator.vibrate(1000);
        			this.performHapticFeedback(1);
        			
        		}
        		else{
        			yrot=90;xrot=0;
        			//vibrator.vibrate(1000);
        			this.performHapticFeedback(1);
        		}
        	}
        	//for third face (Google)
        	
        	if(yrot<225 && yrot>135 && xrot<45 && xrot>-45|| yrot<-135 && yrot>-225 &&xrot<45 && xrot>-45 ||yrot>315 && xrot>-225 && xrot<-135 ||
        			yrot>-45&&yrot<45&& xrot>-225 && xrot<-135 || yrot<-315&& xrot>-225 && xrot<-135 || xrot>135&&xrot<225&& (yrot>315 ||(yrot>-45&&yrot<45)) ){
        		if(yrot>315 && xrot>-225 && xrot<-135 || yrot>-45&&yrot<45&& xrot>-225 && xrot<-135|| yrot<-315&& xrot>-225 && xrot<-135 || xrot>135&&xrot<225&& (yrot>315 ||(yrot>-45&&yrot<45))){
        			yrot=0;xrot=-180;
        			//vibrator.vibrate(1000);
        			this.performHapticFeedback(1);
        			
        		}
        		else{
        			yrot = 180; xrot=0;
        			//vibrator.vibrate(1000);
        			this.performHapticFeedback(1);
        			
        		}
        	}
        	//for fourth face (Gallery)
        	
        	if(yrot<315 && yrot>225 && xrot<45 && xrot>-45|| yrot<-45 && yrot>-135 && xrot<45 && xrot>-45 || yrot>45 &&yrot<135 && xrot>-225 && xrot<-135 ||
        			yrot>-315 &&yrot<-225 && xrot>-225 && xrot<-135 || xrot>135&&xrot<225&&yrot>45&&yrot<135 || xrot>135&&xrot<225&&yrot>-315 &&yrot<225 ){
        		if(yrot>45 &&yrot<135 && xrot>-225 && xrot<-135 || yrot>-315 &&yrot<-225 && xrot>-225 && xrot<-135 || xrot>135&&xrot<225&&yrot>45&&yrot<135 ||
        				xrot>135&&xrot<225&&yrot>-315 &&yrot<225){
        			yrot=90;xrot=-180;
        			//vibrator.vibrate(1000);
        			this.performHapticFeedback(1);
        			
        		}
        		/*else if(yrot>-315 &&yrot<-225 && xrot>-225 && xrot<-135){
        			yrot=-270;xrot=-180;
        		}*/
        		else{
        			yrot = 270; xrot=0;
        			//vibrator.vibrate(1000);
        			this.performHapticFeedback(1);
        			
        		}
        	}
        	
        	//for top (fifth) face (Video)
        	if(xrot>45 &&xrot<135|| xrot>-315&&xrot<-225){
        		xrot = 90;yrot=0;
        		//vibrator.vibrate(1000);
        		this.performHapticFeedback(1);
        		
        	}
        	
        	//for bottom face (desktop screenshot)
        	if(xrot<-45&&xrot>-135 || xrot>225&&xrot<315 ){
        		xrot = -90;yrot=0;
        		//vibrator.vibrate(1000);
        		this.performHapticFeedback(1);
        		
        	}
        	
        /*	
        	if (mPendingCheckForLongPress != null) {
                removeCallbacks(mPendingCheckForLongPress);
            }
        	
        	//Change the light setting if the lower area has been pressed 
        /*	if(y > lowerArea) {
        		if(light) {
        			light = false;
        		} else {
        			light = true;
        		}
        	}*/
        }
        else if(event.getAction() == MotionEvent.ACTION_DOWN){
        	
            
            mFlag ++;
            
        	
        	Log.v("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@","x:"+x);
        	Log.v("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@","y:"+y);
        	//mGL10.glTranslatef(x, y, 0);
        	
        	if(x >= 36 && x <= 310){
        		if(y >= 179 && y <=460){
        			postCheckForLongClick();
        		}
        	}
	        	
        	if(x >= 36 && x <= 310){
        		if(y >= 179 && y <=460){
		        	if(mFlag == 2){
		        		if((yrot == 0 && xrot == 0) || (xrot ==-180 && yrot ==180)){
			        	    Log.v("gggggggggggggggggggg","kkkk");
			        	    mFlag = 0;
			        	    Intent intent =new Intent(context,FileBrowser.class);
			            	
			            	//Bundle b=new Bundle();
			            	//b.putInt("hi", 0);
			            	//intent.putExtras(b);
			        	    intent.putExtra("type", 0);
			            	context.startActivity(intent);
		        		}
	                    if((yrot == 90 && xrot == 0) || (yrot ==270 && xrot == -180) ){ 
	                    	Log.v("BOB","Second face");
	                    	Intent intent =new Intent(context,FileBrowser.class);
			            	
			            	//Bundle b=new Bundle();
			            	//b.putInt("hi", 0);
			            	//intent.putExtras(b);
			        	    intent.putExtra("type", 1);
			            	context.startActivity(intent);
	                    	
	                    	
	                    }
	                    if((yrot ==180 && xrot ==0) || (yrot == 0 && xrot ==-180)){
	                    	Log.v("BOB","Third face");
	                    	context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))); 
	                    }
	                    
	                    if((yrot ==270 && xrot == 0) || (yrot == 90 && xrot == -180 )){
	                    	Log.v("BOB","fourth face");
	                    	Intent intent =new Intent(context,FileBrowser.class);
			            	
			            	//Bundle b=new Bundle();
			            	//b.putInt("hi", 0);
			            	//intent.putExtras(b);
			        	    intent.putExtra("type", 3);
			            	context.startActivity(intent);
			            	Log.v("BOB","fourth face");
			            	
	                    	
	                    }
	                    if(xrot == 90 && yrot == 0){
	                    	Log.v("BOB","fifth face");
	                    	Intent intent =new Intent(context,FileBrowser.class);
			            	
			            	//Bundle b=new Bundle();
			            	//b.putInt("hi", 0);
			            	//intent.putExtras(b);
			        	    intent.putExtra("type", 4);
			            	context.startActivity(intent);
	                    	
	                    }
	                    if(xrot == -90 && yrot == 0){
	                    	Log.v("BOB", "sixth face");
	                    	context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com"))); 
	                    }
		        	}
        		}
        	}
	        	
        	
        	
        	
        }
        else if(event.getAction() == MotionEvent.ACTION_CANCEL){
        //	Toast.makeText(context,"hi", Toast.LENGTH_LONG).show();
        //	Log.v("hi","hi");
        }
        
        
        //Remember the values
        oldX = x;
        oldY = y;
        
        //We handled the event
		return true;
	}
	class CheckForLongPress implements Runnable {
        

        public void run() {
                    
                    mFlag = 0;
                    Log.v("seeeetttttttttttttttt","sssssssssssss");
                    /*
                    if(yrot == 0){
                        Log.v("BOB","first face");
                        Intent intent =new Intent(context,WidgetFileBrowser.class);
                    	
                    	Bundle b=new Bundle();
                    	b.putInt("hi", 0);
                    	intent.putExtras(b);
                    	context.startActivity(intent);
                    }
                    if(yrot == 90){
                    	Log.v("BOB","Second face");
                    	
                    }
                    if(yrot ==180){
                    	Log.v("BOB","Third face");
                    }
                    
                    if(yrot ==270){
                    	Log.v("BOB","fourth face");
                    }
                    int view[] = {0,0,mWidth,mHeight};
                    int[] v0={-1,1,1};
                    //mGL10.glGetIntegerv(GL_VIEWPORT, viewport); 
                    int v1[]={1,1,1};
                    int v2[]={-1,-1,1};
                    int v3[]={1,-1,1};
        		/*    if (PickingUtils.objectIntersectUnproject(x,y,mGL10,cube.mProjector,view)) {
        		        //if(PickingUtils.objectIntersectUnproject(x,y,mGL10,cube.mProjector,view,v1,v2,v3))
        		            Log.v("!!!!!!!!!!!!!!!!!!!!!!","HIT!!!");
        		    }*/
        		    
                	//Toast.makeText(context,"doing stuff", Toast.LENGTH_LONG).show();
                    mHasPerformedLongPress = true;
               
        }
     }

        
    

    private void postCheckForLongClick() {
        mHasPerformedLongPress = false;

        if (mPendingCheckForLongPress == null) {
            mPendingCheckForLongPress = new CheckForLongPress();
        }
        Log.v("hi","sending message");
        postDelayed(mPendingCheckForLongPress, 300);
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();

        mHasPerformedLongPress = false;
        if (mPendingCheckForLongPress != null) {
            removeCallbacks(mPendingCheckForLongPress);
        }
    }
}
