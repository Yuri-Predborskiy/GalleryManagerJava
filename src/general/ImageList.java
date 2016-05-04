package general;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import openSource.AlphanumComparator;

class ImageList {
	// thumbnail pattern: thumbnail.image.jpg
	enum mode { IMAGES, THUMBNAILS };
	final private mode m;
	final private String THUMBNAIL_PATTERN; // = "^thumbnail-.+$"
	final private String IMAGE_FILE_EXTENSIONS_PATTERN; // = "^(JPG)|(JPEG)|(PNG)$"
	final private String folder;
	private List<String> list = new ArrayList<String>();
	//private List<String> thumbnails = new ArrayList<String>();
	
	/* CONSTRUCTOR SECTION*/
	// set current mode to "images" if no mode is stated
	ImageList() {
		this(mode.IMAGES);
	}
	
	// use "images" mode if no mode is stated but folder is provided
	ImageList(String folder) {
		this(mode.IMAGES, folder);
	}
	
	// look for images in current folder if no folder is provided
	ImageList(mode m) {
		this(m, ""); // "" is default folder
	}
	
	// use default file extensions if none are provided
	ImageList(mode m, String folder) {
		this(m, folder, "^(JPG)|(JPEG)|(PNG)$");
	}
	
	// use default thumbnail pattern if none is provided
	ImageList(mode m, String folder, String file_extensions) {
		this(m, folder, file_extensions, "^thumbnail-.+$");
	}
	
	// default constructor
	ImageList(mode m, String folder, String file_extensions, String thumb_pattern) {
		this.folder = folder;
		this.m = m;
		this.THUMBNAIL_PATTERN = thumb_pattern;
		this.IMAGE_FILE_EXTENSIONS_PATTERN = file_extensions;
		try {
			getListOfImages();
		}
		catch(Exception e) {
			System.out.println("Error getting and sorting list of images at: " + folder + "," + e);
			e.printStackTrace();
		}
	}
	/* END OF CONSTRUCTOR SECTION*/
	
	private void getListOfImages() {
		File container = new File(folder.replace("/", "\\"));
		for (File f : container.listFiles()) {
	    	if (isImage(f)) {
	    		if (m == mode.IMAGES && !isThumbnail(f)) {
	    			list.add(f.getName());
	    		}
	    		else if (m == mode.THUMBNAILS && isThumbnail(f)) {
	    			list.add(f.getName());
	    		}
	    	}
	    }
		// if there are any list records, we should sort them
		if(list.size() > 0) {
			sort();
		}
	}
	
	public void update() {
		try {
			getListOfImages();
		}
		catch(Exception e) {
			System.out.println("Error getting and sorting list of images: " + e);
		}
	}

	// checks weather a file is an image (aims on JPEG and PNG)
	private boolean isImage(File f) {
		if(!f.isFile()) { 
			return false; 
		} // folder is not an image
		
		int dotIndex = f.getName().lastIndexOf(".");
		if(dotIndex==-1) { 
			return false; 
		} // file has no extension - not an image
		
		// get file extension and save it in upper case
		String ext = f.getName().substring(dotIndex+1).toUpperCase();
		if(ext.matches(IMAGE_FILE_EXTENSIONS_PATTERN)) {
			return true;
		}
		return false;
	}
	
	// checks weather file name matches thumbnail pattern
	private boolean isThumbnail(File f) {
		String test = f.getName();
		return test.substring(0, test.lastIndexOf(".")).matches(THUMBNAIL_PATTERN);
	}
	
	private void sort() {
		Collections.sort(list, new Comparator<String>() {
			public int compare(String s1, String s2) {
				return new AlphanumComparator().compare(s1.toLowerCase(), s2.toLowerCase());
			}
		});
	}
	
	/** @return unmodifiable list of images that are contained in current list
	 * 
	 * */
	public List<String> getImageList() {
		return Collections.unmodifiableList(list);
	}
	
	public String toString() {
		String result = "";
		for(String el : list) {
			result += el;
		}
		return result;
	}
	
	public int size() {
		return list.size();
	}
	
	public String get(int index) {
		return list.get(index);
	}
}
