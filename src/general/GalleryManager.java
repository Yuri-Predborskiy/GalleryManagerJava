package general;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryManager {
	private enum WorkMode { NORMAL, EXPLAIN, FORCE_UPDATE };
	private static WorkMode mode = WorkMode.NORMAL;
	static String folder = "images\\";
	static String relativePath = null;
	static int size = 600;
	static boolean questionMode = false;
	private static void print(String what) {
		System.out.println(what);
	}

	public static void main(String[] args) {
		parseParameters(args);
		
		if(mode == WorkMode.EXPLAIN) {
			showExplanation();
			return;
		}
		
		if(relativePath == null) {
			relativePath = folder;
		}
		ImageList images = new ImageList(folder);
		ImageList thumbs = new ImageList(ImageList.mode.THUMBNAILS, folder);
		
		List<ImagePair> pairs = assignPairs(images, thumbs);
		
		String json = "[\n";
		for(ImagePair pair : pairs) {
			json += parseJSON(pair);
		}
		if(json.lastIndexOf(",") > 0) {
			// has data
			json = json.substring(0, json.lastIndexOf(",")) + "\n]";
		}
		
		print(json);
	}
	
	private static void parseParameters(String[] args) {
		String folderPattern = "^fullPath:.+$";
		String relativeFolderPattern = "^relPath:.+$";
		String questionPattern = "^(-help)|(/help)$";
		String updatePattern = "^(-update)|(/update)|(-force)|(/force)$";
		String sizePattern = "^-size:\\d+$";
		for(String arg : args) {
			if(arg.matches(questionPattern)) {
				mode = WorkMode.EXPLAIN;
				return;
			}
			
			if(arg.matches(updatePattern)) {
				mode = WorkMode.FORCE_UPDATE;
			}
			
			if(arg.matches(folderPattern))
			{
				folder = arg.substring(9);
				if(!folder.substring(folder.length()-1, folder.length()).equals("\\")) {
					folder += "\\";
					folder = folder.replace("/", "\\");
				}
			}
			
			if(arg.matches(relativeFolderPattern))
			{
				relativePath = arg.substring(8);
				if(!relativePath.substring(relativePath.length()-1, relativePath.length()).equals("\\")) {
					relativePath += "\\";
				}
				relativePath = relativePath.replace("\\", "/");
			}
			
			if(arg.matches(sizePattern)) {
				size = Integer.parseInt(arg.substring(6), 10);
			}
		}
	}
	
	private static class ImagePair {
		final private String image;
		private String thumbnail;

		ImagePair(String image) {
			this(image, "");
		}
		
		ImagePair(String image, String thumbnail) {
			this.image = image;
			this.thumbnail = thumbnail;
		}
		
		public String createThumbnail(int size) {
			try {
				ThumbnailMaker.MakeThumbnail(folder + image, size);
			} catch (Exception e) {
				print("Error making thumbnail of " + image + ": " + e);
				e.printStackTrace();
				return null;
			}
			File test = new File(folder + "thumbnail-" + image);
			if(test.exists()) {
				thumbnail = test.getName();
				return thumbnail;
			}
			else {
				return null;
			}
		}
		
		public String getImage() {
			return image;
		}
		
		public String getThumbnail() {
			return thumbnail;
		}
	}
	
	private static List<ImagePair> assignPairs(ImageList imgList, ImageList thumbList) {
		List<ImagePair> pairs = new ArrayList<ImagePair>();
		for(String img : imgList.getImageList()) {
			String thumb = findThumbnail(img, thumbList);
			if(thumb.equals("") || mode == WorkMode.FORCE_UPDATE) {
				// create / overwrite thumbnail
				ImagePair newPair = new ImagePair(img);
				if(newPair.createThumbnail(size) != null) {
					pairs.add(newPair);
				} else {
					// thumbnail creation failed - skip image
					print("Failed to create thumbnail for " + img + ", skipping image.");
				}
			} else {
				// thumbnail already exists, mode is not set into force update
				pairs.add(new ImagePair(img, thumb));
			}
		}
		return pairs;
	}
	
	private static String findThumbnail(String name, ImageList thumbList) {
		for(String thumb : thumbList.getImageList()) {
			if(thumb.contains(name)) {
				return thumb;
			}
		}
		return "";
	}
	
	private static String parseJSON(ImagePair pair) {
		String image = pair.getImage();
		String thumbnail = pair.getThumbnail();
		Dimension id;
		try {
			id = ImageInfo.getImageDimension(folder + image);
		}
		catch (Exception e) {
			print("Error reading image dimensions (" + image + ") " + e);
			return "";
		}
		String tt = "\t\t"; // tab tab
		String json = "\t{\n"
				+ tt + "\"src\" : \"" + relativePath + image + "\",\n"
				+ tt + "\"msrc\" : \"" + relativePath + thumbnail + "\",\n"
				+ tt + "\"w\" : " + id.getWidth() + ",\n"
				+ tt + "\"h\" : " + id.getHeight() + "\n"
				+ "\t},\n";
		return json;
	}
	
	// print instructions with available parameters
	private static void showExplanation() {
		print("\t allowed parameters (no spaces allowed):\n"
				+ "-help - "
				+ "shows available parameters\n"
				+ "fullPath:\"path/to/images\" - "
				+ "absolute path to image folder\n"
				+ "relPath:\"path/to/images\" - "
				+ "path to image folder from page.html\n"
				+ "size:300 - "
				+ "sets max size in px for image thumbnails\n"
				+ "-update | -force - "
				+ "create new thumbnails with current params");
	}
}
