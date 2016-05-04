package general;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

/** Contains information on an image (dimensions, file, thumbnail)
 * 
 * @author Yuri
 *
 */
class ImageInfo {
	ImageInfo() {
		
	}

	public static Dimension getImageDimension(String imgFile) throws IOException {
		String suffix = imgFile.substring(imgFile.lastIndexOf(".") + 1);
		Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
		if (iter.hasNext()) {
			ImageReader reader = iter.next();
			try {
				ImageInputStream stream = new FileImageInputStream(new File(imgFile));
				reader.setInput(stream);
				int width = reader.getWidth(reader.getMinIndex());
				int height = reader.getHeight(reader.getMinIndex());
				return new Dimension(width, height);
			} 
			catch (IOException e) {
				System.out.println("Error reading: " + imgFile + " " + e);
			} 
			finally {
				reader.dispose();
			}
		}

		throw new IOException("Not a known image file: " + imgFile);
	}
}
