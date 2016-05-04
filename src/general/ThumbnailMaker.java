package general;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

class ThumbnailMaker {
	public static void MakeThumbnail(String name, int maxSize) throws Exception {
		Thumbnails.of(name)
			.size(maxSize, maxSize)
			.toFiles(Rename.PREFIX_HYPHEN_THUMBNAIL);
	}	
}
