package com.cs301w01.meatload.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.cs301w01.meatload.model.Picture;
import com.cs301w01.meatload.model.Tag;
import com.cs301w01.meatload.model.querygenerators.PictureQueryGenerator;

/**
 * Implements Controller logic for Picture objects.
 * @author Isaac Matichuk
 */
public class PictureManager implements FController {

	private Context context;
	private Picture picture;
    private String albumName;
	private Bitmap imgOnDisplay;
	
	public PictureManager(Context context, String albumName) {
		this.albumName = albumName;
	}
	
    public PictureManager(Context context, Picture picture) {
    	this.picture = picture;
    }
    
    public PictureManager(Context context) {
    	this.picture = null;
    }
    
//    public PictureManager(int pid) {
//    	photoID = pid;
//    }
    
//    public PictureManager(String albumName) {
//    	this.albumName = albumName;
//    }
    
    public void setContext(Context context){
    	this.context = context;
    }
    
    /**
     * Takes Picture, save it to file, pass Picture object to DBManager
     * @see <a href=http://stackoverflow.com/questions/649154/android-bitmap-save-to-location>
     * http://stackoverflow.com/questions/649154/android-bitmap-save-to-location</a>
     * @param path File directory where the Picture is to be saved
     */
    public Picture takePicture(File path) {
    	Calendar cal = Calendar.getInstance();
    	SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy_HH-mm-sss");
    	String timestamp = sdf.format(cal.getTime());
    	String fname = "img-" + timestamp + ".PNG";
    	String fpath = path.toString() + "/";

    	try {
    		OutputStream outStream = null;
    		File file = new File(path, fname);

    		outStream = new FileOutputStream(file);
    		imgOnDisplay.compress(Bitmap.CompressFormat.PNG, 100, outStream);
    		outStream.flush();
    		outStream.close();

            //adds the new picture to the db and returns a picture object
            return savePicture(fpath + fname, cal.getTime(), fname);

        } catch (IOException e) {
        	Log.d("ERROR", "Unable to write " + fpath + fname);
            return null;
        }
        
        // TODO: Move to EditPictureActivity after takePicture finishes.
    }
    
    
    /**
     * Generates a random bitmap
     * @return Bitmap of generated picture
     * @see <a href="http://developer.android.com/reference/android/graphics/Bitmap.html#createBitmap%28int[],%20int,%20int,%20int,%20int,%20android.graphics.Bitmap.Config%29">
     http://developer.android.com/reference/android/graphics/Bitmap.html#createBitmap%28int[],%20int,%20int,%20int,%20int,%20android.graphics.Bitmap.Config%29</a>
     * @see <a href="http://developer.android.com/reference/android/graphics/Color.html">
     http://developer.android.com/reference/android/graphics/Color.html</a>
     * @see <a href="http://developer.android.com/reference/android/widget/ImageView.html">
     http://developer.android.com/reference/android/widget/ImageView.html</a>
     * @see <a href="http://docs.oracle.com/javase/1.4.2/docs/api/java/util/Random.html">
     http://docs.oracle.com/javase/1.4.2/docs/api/java/util/Random.html</a>
     * @see <a href="http://developer.android.com/reference/android/graphics/BitmapFactory.html">
     http://developer.android.com/reference/android/graphics/BitmapFactory.html</a>
     */
    //Used http://developer.android.com/reference/android/graphics/Bitmap.html#createBitmap
    //%28int[],%20int,%20int,%20int,%20int,%20android.graphics.Bitmap.Config%29
    //http://developer.android.com/reference/android/graphics/Color.html
    //http://developer.android.com/reference/android/widget/ImageView.html
    //http://docs.oracle.com/javase/1.4.2/docs/api/java/util/Random.html
    //http://developer.android.com/reference/android/graphics/BitmapFactory.html
    public Bitmap generatePicture() {
    	int height = 200;
    	int width = 200;
    	int[] colors = new int[height*width];
    	int r,g,b;
    	int test = 0;
        
    	Random gen = new Random(System.currentTimeMillis());
		r = gen.nextInt(256);
		g = gen.nextInt(256);
		b = gen.nextInt(256);
		
        for (int i = 0; i < height; i++) {
        	if (test > 15) {
        		r = gen.nextInt(256);
        		g = gen.nextInt(256);
        		b = gen.nextInt(256);
        		test = 0;
        	} else {
        		test++;
        	}
        	
        	for (int j = 0; j < width; j++) {
        		colors[i * height + j] = Color.rgb(r,g,b);
        	}
        }
        
        imgOnDisplay = Bitmap.createBitmap(colors, width, height, Bitmap.Config.RGB_565);

        return imgOnDisplay;
    }
    
    private Picture savePicture(String fpath, Date date, String fname) {

        Picture newPic = new Picture(albumName + ":" + date.toString(), fpath, albumName, date, 
        		new ArrayList<Tag>());
        
        new PictureQueryGenerator(context).insertPicture(newPic);
        Log.d("SAVE", "Saving " + fpath + fname);

        return newPic;
    }
    
    /**
     * Get this picture
     * @return Picture associated with this PictureManager
     */
    public Picture getPicture() {
    	return picture;
    }

    /**
     * Delete the Picture associated with this PictureManager object in the database.
     * @see PictureQueryGenerator
     */
    public void deletePicture() {
        PictureQueryGenerator pQ = new PictureQueryGenerator(context);
        pQ.deletePictureByID(picture.getPictureID());
    }
    
    /**
     * Adds a new tag to a picture, using a String instead of a tag object.
     * @param tagName String object representing the tag to be added to this picture
     */
    public void addTag(String tagName) {
    	
    }
    
    /**
     * Sets the tags of the picture associated with this object to the passed Collection of tags
     * @param tags Collection of tags to set the picture's tags to
     */
    public void setTags(Collection<Tag> tags) {
    	// TODO: Set the picture's tags in the object
    }

    /**
     * Remove a tag from the picture associated with this PictureManager object.
     * @param tag Tag to be deleted.
     */
	public void deleteTag(Tag tag) {
		// TODO Auto-generated method stub
		
	}
}
