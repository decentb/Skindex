package com.cs301w01.meatload.test.ActivityTests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import com.cs301w01.meatload.activities.ViewAlbumsActivity;
import com.cs301w01.meatload.model.Album;
import com.cs301w01.meatload.model.Picture;
import com.cs301w01.meatload.model.PictureGenerator;
import com.cs301w01.meatload.model.SQLiteDBManager;
import com.cs301w01.meatload.model.Tag;
import com.cs301w01.meatload.controllers.AlbumManager;
import com.cs301w01.meatload.controllers.GalleryManager;
import com.cs301w01.meatload.controllers.MainManager;
import com.cs301w01.meatload.controllers.PictureCreator;
import com.cs301w01.meatload.controllers.PictureManager;
import com.cs301w01.meatload.model.gallery.AlbumGallery;
import com.cs301w01.meatload.model.gallery.AllPicturesGallery;
import com.cs301w01.meatload.model.querygenerators.AlbumQueryGenerator;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

/**
 * 
 * 
 * 
 * @author dowling
 * 
 */
public class DatabaseTest extends ActivityInstrumentationTestCase2<ViewAlbumsActivity> {
	private Context mContext;
	private ViewAlbumsActivity mActivity;
	private AlbumManager albumMan;
	private MainManager mainMan;
	private PictureManager picMan;

	public DatabaseTest() {
		super("com.cs301w01.meatload", ViewAlbumsActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mActivity = getActivity();
		mContext = mActivity.getBaseContext();

		albumMan = new AlbumManager(mContext);
		mainMan = new MainManager(mContext);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		if (mActivity != null) {
			mActivity.finish();
		}

	}

	public void test1stResetDB() {

		SQLiteDBManager db = new SQLiteDBManager(mContext);
		db.resetDB();
		db.close();

		assertTrue(albumMan.getAllAlbums().size() == 0);
	}

	public void testAddAlbum() {

		int oldNumAlbums = albumMan.getAllAlbums().size();
		int newNumAlbums;

		albumMan.addAlbum("Album 1");
		albumMan.addAlbum("Album 2");
		albumMan.addAlbum("Album 3");
		albumMan.addAlbum("Album 4");
		albumMan.addAlbum("Album 5");
		albumMan.addAlbum("Album 6");

		newNumAlbums = albumMan.getAllAlbums().size();

		assertTrue(newNumAlbums == oldNumAlbums + 6);
	}

	public void testDeleteAlbum() {
		int oldNumAlbums = albumMan.getAllAlbums().size();
		int newNumAlbums;

		albumMan.deleteAlbum((int) albumMan.getAlbumByName("Album 5").getID());

		newNumAlbums = albumMan.getAllAlbums().size();

		assertTrue(newNumAlbums == oldNumAlbums - 1);
	}

	public void testUpdateAlbum() {
		int aid;
		Album album = albumMan.getAlbumByName("Album 6");
		aid = (int) album.getID();
		albumMan.changeAlbumName("Album 42", album);

		assertFalse(albumMan.albumExists("Album 6"));
	}

	public void testAddPicture1() {
		Album testAlbum;
		int oldNumPics;
		int newNumPics;
		PictureGenerator picGen = new PictureGenerator();
		PictureCreator pictureCreator = new PictureCreator(mContext);

		testAlbum = albumMan.getAlbumByName("Album 1");
		oldNumPics = testAlbum.getPictureCount();

		pictureCreator.takePicture(mContext.getFilesDir(), picGen.generatePicture(), "Album 1");
		pictureCreator.takePicture(mContext.getFilesDir(), picGen.generatePicture(), "Album 1");
		pictureCreator.takePicture(mContext.getFilesDir(), picGen.generatePicture(), "Album 1");

		testAlbum = albumMan.getAlbumByName("Album 1");
		newNumPics = testAlbum.getPictureCount();

		assertTrue(newNumPics == oldNumPics + 3);
	}

	public void testAddPicture2() {
		int oldNumPics;
		int newNumPics;
		PictureGenerator picGen = new PictureGenerator();
		PictureCreator pictureCreator = new PictureCreator(mContext);

		oldNumPics = mainMan.getPictureCount();

		pictureCreator.takePicture(mContext.getFilesDir(), picGen.generatePicture(), "Album 2");
		pictureCreator.takePicture(mContext.getFilesDir(), picGen.generatePicture(), "Album 3");
		pictureCreator.takePicture(mContext.getFilesDir(), picGen.generatePicture(), "Album 3");
		pictureCreator.takePicture(mContext.getFilesDir(), picGen.generatePicture(), "Album 3");

		newNumPics = mainMan.getPictureCount();

		assertTrue(newNumPics == oldNumPics + 4);
	}

	public void testDeletePicture() {
		int oldNumPics;
		int newNumPics;
		oldNumPics = mainMan.getPictureCount();

		AlbumGallery albGallery = new AlbumGallery(albumMan.getAlbumByName("Album 3"));
		ArrayList<Picture> pics = (ArrayList<Picture>) albGallery.getPictureGallery(mContext);
		picMan = new PictureManager(mContext, pics.get(1));
		picMan.deletePicture();

		newNumPics = mainMan.getPictureCount();

		assertTrue(newNumPics == oldNumPics - 1);
	}

	public void testUpdatePicture() {
		AlbumGallery albGallery = new AlbumGallery(albumMan.getAlbumByName("Album 3"));
		ArrayList<Picture> pics = (ArrayList<Picture>) albGallery.getPictureGallery(mContext);
		Picture pic = pics.get(1);
		picMan = new PictureManager(mContext, pic);
		Picture pic2 = new Picture("Groovy", pic.getPath(), pic.getAlbumName(), pic.getDate(),
				pic.getTags());
		picMan.savePicture(pic2);

		pics = (ArrayList<Picture>) albGallery.getPictureGallery(mContext);
		pic = pics.get(1);

		assertTrue(pic.getName().equals("Groovy"));
	}

	/**
	 * Add tags to each picture.
	 */
	public void testAddTags() {

		int tagsAdded = 0;

		String[] tags = { "nasty zit", "scrumptious scar", "tasty rash", "succulent papule",
				"beautiful blister", "trapped fart bubble", "half picked scab", "stinky pustule",
				"beautiful bustule" };

		AllPicturesGallery aPG = new AllPicturesGallery();
		GalleryManager gMan = new GalleryManager(mContext, aPG);

		int tagsToAdd = 0;

		for (Picture p : gMan.getPictureGallery()) {

			picMan = new PictureManager(mContext, p);

			for (int i = 0; i < tagsToAdd; i++) {

				picMan.addTag(tags[i]);

			}

			picMan.savePicture(p);

			tagsAdded += tagsToAdd;

		}

		aPG = new AllPicturesGallery();
		gMan = new GalleryManager(mContext, aPG);
		int insertedTagCount = 0;

		for (Picture p : gMan.getPictureGallery()) {

			insertedTagCount += p.getTags().size();

		}

		assertTrue(insertedTagCount == tagsAdded);

	}

	public void testDeleteTags() {

		int originalTagCount = 0;

		// delete all tags for 3 pictures
		int picturesTagsToDelete = 3;

		// list of tag names
		String[] tags = { "nasty zit", "scrumptious scar", "tasty rash", "succulent papule",
				"beautiful blister", "trapped fart bubble", "half picked scab", "stinky pustule",
				"beautiful bustule" };

		AllPicturesGallery aPG = new AllPicturesGallery();
		GalleryManager gMan = new GalleryManager(mContext, aPG);
		ArrayList<Picture> pictures = new ArrayList<Picture>(gMan.getPictureGallery());

		for (Picture p : pictures) {

			originalTagCount += p.getTags().size();

		}

		int tagsDeleted = 0;
		// delete tags for first 3 pictures
		for (int picturesTagsDeleted = 0; picturesTagsDeleted < picturesTagsToDelete; 
				picturesTagsDeleted++) {

			picMan = new PictureManager(mContext, pictures.get(picturesTagsDeleted));

			for (int i = 0; i < picMan.getPicture().getTags().size(); i++) {

				picMan.deleteTag(tags[i]);
				tagsDeleted++;

			}

			picMan.savePicture(picMan.getPicture());

		}

		aPG = new AllPicturesGallery();
		gMan = new GalleryManager(mContext, aPG);
		int newTagCount = 0;

		for (Picture p : gMan.getPictureGallery()) {

			newTagCount += p.getTags().size();

		}

		assertTrue(newTagCount == originalTagCount - tagsDeleted);

	}

	public void testUpdateTags() {
		int picID;
		AlbumGallery albGallery = new AlbumGallery(albumMan.getAlbumByName("Album 3"));
		ArrayList<Picture> pics = (ArrayList<Picture>) albGallery.getPictureGallery(mContext);
		Picture pic = pics.get(1);
		picID = pic.getPictureID();
		int oldNumTags = pic.getTags().size();

		picMan = new PictureManager(mContext, picID);
		picMan.addTag("Taco");
		picMan.addTag("Fish");
		picMan.savePicture(pic);

		picMan = new PictureManager(mContext, picID);
		pic = picMan.getPicture();

		assertTrue(pic.getTags().size() == oldNumTags + 2);
	}

}
