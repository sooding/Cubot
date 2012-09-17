package com.project;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

/**
 * This class is an object representation of 
 * a Cube containing the vertex information,
 * texture coordinates, the vertex indices
 * and drawing functionality, which is called 
 * by the renderer.

 */
public class Cube {

	/** The buffer holding the vertices */
	private FloatBuffer vertexBuffer;
	/** The buffer holding the texture coordinates */
	private FloatBuffer textureBuffer;
	/** The buffer holding the indices */
	private ByteBuffer indexBuffer0;
	private ByteBuffer indexBuffer1;
	private ByteBuffer indexBuffer2;
	private ByteBuffer indexBuffer3;
	private ByteBuffer indexBuffer4;
	private ByteBuffer indexBuffer5;
	/** The buffer holding the normals */
	private FloatBuffer normalBuffer;

	/** Our texture pointer */
	private int[] textures = new int[6];

	/** The initial vertex definition */	
	private float vertices[] = {
						//Vertices according to faces
						-1.0f, -1.0f, 1.0f, //v0
						1.0f, -1.0f, 1.0f, 	//v1
						-1.0f, 1.0f, 1.0f, 	//v2
						1.0f, 1.0f, 1.0f, 	//v3
			
						1.0f, -1.0f, 1.0f, 	// ...
						1.0f, -1.0f, -1.0f, 
						1.0f, 1.0f, 1.0f, 
						1.0f, 1.0f, -1.0f,
			
						1.0f, -1.0f, -1.0f, 
						-1.0f, -1.0f, -1.0f, 
						1.0f, 1.0f, -1.0f, 
						-1.0f, 1.0f, -1.0f,
			
						-1.0f, -1.0f, -1.0f, 
						-1.0f, -1.0f, 1.0f, 
						-1.0f, 1.0f, -1.0f, 
						-1.0f, 1.0f, 1.0f,
			
						-1.0f, -1.0f, -1.0f, 
						1.0f, -1.0f, -1.0f, 
						-1.0f, -1.0f, 1.0f, 
						1.0f, -1.0f, 1.0f,
			
						-1.0f, 1.0f, 1.0f, 
						1.0f, 1.0f, 1.0f, 
						-1.0f, 1.0f, -1.0f, 
						1.0f, 1.0f, -1.0f, 
											};

	/** 
	 * The initial normals for the lighting calculations 
	 * 
	 * The normals are not necessarily correct from a 
	 * real world perspective, as I am too lazy to write
	 * these all on my own. But you get the idea and see
	 * what I mean if you run the demo.
	 */	
	private float normals[] = {
						// Normals
						0.0f, 0.0f, 1.0f, 						
						0.0f, 0.0f, -1.0f, 
						0.0f, 1.0f, 0.0f, 
						0.0f, -1.0f, 0.0f, 
						
						0.0f, 0.0f, 1.0f, 
						0.0f, 0.0f, -1.0f, 
						0.0f, 1.0f, 0.0f, 
						0.0f, -1.0f, 0.0f,
						
						0.0f, 0.0f, 1.0f, 
						0.0f, 0.0f, -1.0f, 
						0.0f, 1.0f, 0.0f, 
						0.0f, -1.0f, 0.0f,
						
						0.0f, 0.0f, 1.0f, 
						0.0f, 0.0f, -1.0f, 
						0.0f, 1.0f, 0.0f, 
						0.0f, -1.0f, 0.0f,
						
						0.0f, 0.0f, 1.0f, 
						0.0f, 0.0f, -1.0f, 
						0.0f, 1.0f, 0.0f, 
						0.0f, -1.0f, 0.0f,
						
						0.0f, 0.0f, 1.0f, 
						0.0f, 0.0f, -1.0f, 
						0.0f, 1.0f, 0.0f, 
						0.0f, -1.0f, 0.0f,
											};

	/** The initial texture coordinates (u, v) */	
	private float texture[] = {
						//Mapping coordinates for the vertices
						0.0f, 0.0f, 
						0.0f, 1.0f, 
						1.0f, 0.0f, 
						1.0f, 1.0f,
			
						0.0f, 0.0f,
						0.0f, 1.0f, 
						1.0f, 0.0f,
						1.0f, 1.0f,
			
						0.0f, 0.0f, 
						0.0f, 1.0f, 
						1.0f, 0.0f, 
						1.0f, 1.0f,
			
						0.0f, 0.0f, 
						0.0f, 1.0f, 
						1.0f, 0.0f, 
						1.0f, 1.0f,
			
						0.0f, 0.0f, 
						0.0f, 1.0f, 
						1.0f, 0.0f, 
						1.0f, 1.0f,
			
						0.0f, 0.0f, 
						0.0f, 1.0f, 
						1.0f, 0.0f, 
						1.0f, 1.0f, 
									};

	/** The initial indices definition */
/*	private byte indices[] = {
						// Faces definition
						0, 1, 3, 0, 3, 2, 		// Face front
						4, 5, 7, 4, 7, 6, 		// Face right
						8, 9, 11, 8, 11, 10, 	// ...
						12, 13, 15, 12, 15, 14, 
						16, 17, 19, 16, 19, 18, 
						20, 21, 23, 20, 23, 22, 
												};*/
	private byte indices0[] = { 0, 1, 3, 0, 3, 2 };
	private byte indices1[] = { 4, 5, 7, 4, 7, 6 };
	private byte indices2[] = { 8, 9, 11, 8, 11, 10 };
	private byte indices3[] = { 12, 13, 15, 12, 15, 14 };
	private byte indices4[] = { 16, 17, 19, 16, 19, 18 };
	private byte indices5[] = { 20, 21, 23, 20, 23, 22 };
    
	/**
	 * The Cube constructor.
	 * 
	 * Initiate the buffers.
	 */
	public Cube() {
		//
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		vertexBuffer = byteBuf.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);

		//
		byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		textureBuffer = byteBuf.asFloatBuffer();
		textureBuffer.put(texture);
		textureBuffer.position(0);

		//
		byteBuf = ByteBuffer.allocateDirect(normals.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		normalBuffer = byteBuf.asFloatBuffer();
		normalBuffer.put(normals);
		normalBuffer.position(0);

		//
		indexBuffer0 = ByteBuffer.allocateDirect(indices0.length);
		indexBuffer0.put(indices0);
		indexBuffer0.position(0);
		
		indexBuffer1 = ByteBuffer.allocateDirect(indices1.length);
        indexBuffer1.put(indices1);
        indexBuffer1.position(0);
        
        indexBuffer2 = ByteBuffer.allocateDirect(indices2.length);
        indexBuffer2.put(indices2);
        indexBuffer2.position(0);
        
        indexBuffer3 = ByteBuffer.allocateDirect(indices3.length);
        indexBuffer3.put(indices3);
        indexBuffer3.position(0);
        
        indexBuffer4 = ByteBuffer.allocateDirect(indices4.length);
        indexBuffer4.put(indices4);
        indexBuffer4.position(0);
        
        indexBuffer5 = ByteBuffer.allocateDirect(indices5.length);
        indexBuffer5.put(indices5);
        indexBuffer5.position(0);
		
		
		
		
		
		
		
		
	}

	/**
	 * The object own drawing function.
	 * Called from the renderer to redraw this instance
	 * with possible changes in values.
	 * 
	 * @param gl - The GL Context
	 * @param filter - Which texture filter to be used
	 */
	public void draw(GL10 gl, int filter) {
		//Bind the texture according to the set texture filter
		//gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[filter]);

		//Enable the vertex, texture and normal state
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

		//Set the face rotation
		gl.glFrontFace(GL10.GL_CCW);
		
		//Point to our buffers
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
		gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer);
		
		//Draw the vertices as triangles, based on the Index Buffer information
		//gl.glColor4f(1,1,0,0);
		gl.glDrawElements(GL10.GL_TRIANGLES, indices0.length, GL10.GL_UNSIGNED_BYTE, indexBuffer0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		
	//	mProjector.getCurrentModelView(gl);
      //  mProjector.getCurrentProjection(gl);
		
		
		//gl.glColor4f(1,0,0,0);
		gl.glDrawElements(GL10.GL_TRIANGLES, indices1.length, GL10.GL_UNSIGNED_BYTE, indexBuffer1);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[1]);
        
     //   mProjector.getCurrentModelView(gl);
     //   mProjector.getCurrentProjection(gl);
        
		//gl.glColor4f(0,1,0,0);
        gl.glDrawElements(GL10.GL_TRIANGLES, indices2.length, GL10.GL_UNSIGNED_BYTE, indexBuffer2);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[2]);
        
      //  mProjector.getCurrentModelView(gl);
      //  mProjector.getCurrentProjection(gl);
        
        //gl.glColor4f(0,1,1,0);
        gl.glDrawElements(GL10.GL_TRIANGLES, indices3.length, GL10.GL_UNSIGNED_BYTE, indexBuffer3);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[3]);
        
       // mProjector.getCurrentModelView(gl);
      //  mProjector.getCurrentProjection(gl);
        
        //gl.glColor4f(1,1,1,0);
        gl.glDrawElements(GL10.GL_TRIANGLES, indices4.length, GL10.GL_UNSIGNED_BYTE, indexBuffer4);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[4]);
        
      //  mProjector.getCurrentModelView(gl);
      //  mProjector.getCurrentProjection(gl);
        
       // gl.glColor4f(1,0,1,0);
        gl.glDrawElements(GL10.GL_TRIANGLES, indices5.length, GL10.GL_UNSIGNED_BYTE, indexBuffer5);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[5]);
         
       // mProjector.getCurrentModelView(gl);
       // mProjector.getCurrentProjection(gl);
        
       
    /*    gl.glMatrixMode(GL10.GL_PROJECTION);    //Select The Projection Matrix
        gl.glLoadIdentity();                    //Reset The Projection Matrix

        //Calculate The Aspect Ratio Of The Window
        GLU.gluPerspective(gl, 45.0f, (float)Lesson07.mWidth / (float)Lesson07.mHeight, 0.1f, 100.0f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);     //Select The Modelview Matrix
        gl.glLoadIdentity();*/
		
		//Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
	}

	/**
	 * Load the textures
	 * 
	 * @param gl - The GL Context
	 * @param context - The Activity context
	 */
	public void loadGLTexture(GL10 gl, Context context) {
		//Get the texture from the Android resource directory
		InputStream is0 = context.getResources().openRawResource(R.drawable.gallery);
		InputStream is1 = context.getResources().openRawResource(R.drawable.goog1);
		InputStream is2 = context.getResources().openRawResource(R.drawable.text);
		InputStream is3 = context.getResources().openRawResource(R.drawable.face);
		InputStream is4 = context.getResources().openRawResource(R.drawable.video);
		InputStream is5 = context.getResources().openRawResource(R.drawable.music);
		
		
		Bitmap bitmap0,bitmap1,bitmap2,bitmap3,bitmap4,bitmap5 = null;
		try {
			//BitmapFactory is an Android graphics utility for images
			bitmap0 = BitmapFactory.decodeStream(is0);
			bitmap1 = BitmapFactory.decodeStream(is1);
			bitmap2 = BitmapFactory.decodeStream(is2);
			bitmap3 = BitmapFactory.decodeStream(is3);
			bitmap4 = BitmapFactory.decodeStream(is4);
			bitmap5 = BitmapFactory.decodeStream(is5);
			

		} finally {
			//Always clear and close
			try {
				is0.close();
				is0 = null;
				is1.close();
				is1=null;
				is2.close();
				is2=null;
				is3.close();
				is3=null;
				is4.close();
				is4=null;
				is5.close();
				is5=null;
				
			} catch (IOException e) {
			}
		}

		//Generate there texture pointer
		gl.glGenTextures(6, textures, 0);

		//Create Nearest Filtered Texture and bind it to texture 0
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap0, 0);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[1]);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap1, 0);
        
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[2]);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap2, 0);
        
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[3]);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap3, 0);
        
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[4]);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap4, 0);
        
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[5]);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap5, 0);
		
		
		
		
		
		/*
		//Create Linear Filtered Texture and bind it to texture 1
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[1]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		//Create mipmapped textures and bind it to texture 2
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[2]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR_MIPMAP_NEAREST);
		/*
		 * This is a change to the original tutorial, as buildMipMap does not exist anymore
		 * in the Android SDK.
		 * 
		 * We check if the GL context is version 1.1 and generate MipMaps by flag.
		 * Otherwise we call our own buildMipMap implementation
		 */
	/*	if(gl instanceof GL11) {
			gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
			
		//
		} else {
			buildMipmap(gl, bitmap);
		}*/		
		
		//Clean up
		bitmap0.recycle();
		bitmap1.recycle();
		bitmap2.recycle();
		bitmap3.recycle();
		bitmap4.recycle();
		bitmap5.recycle();
	}
	
	/**
	 * Our own MipMap generation implementation.
	 * Scale the original bitmap down, always by factor two,
	 * and set it as new mipmap level.
	 * 
	 * Thanks to Mike Miller (with minor changes)!
	 * 
	 * @param gl - The GL Context
	 * @param bitmap - The bitmap to mipmap
	 */
	private void buildMipmap(GL10 gl, Bitmap bitmap) {
		//
		int level = 0;
		//
		int height = bitmap.getHeight();
		int width = bitmap.getWidth();

		//
		while(height >= 1 || width >= 1) {
			//First of all, generate the texture from our bitmap and set it to the according level
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, level, bitmap, 0);
			
			//
			if(height == 1 || width == 1) {
				break;
			}

			//Increase the mipmap level
			level++;

			//
			height /= 2;
			width /= 2;
			Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, width, height, true);
			
			//Clean up
			bitmap.recycle();
			bitmap = bitmap2;
		}
	}
}
