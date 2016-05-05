# GalleryManagerJava
Gallery manager Java application (including JAR file in root folder).
I wrote this java app to help generate image galleries for use with <strong><a href="http://photoswipe.com/">PhotoSwipe</a></strong> Javascript plugin.

<b>How to use:</b><br>
Launch the app with proper parameters to generate JSON text and write it on screen (print). JSON text contains basic required info for PhotoSwipe plugin (src, msrc, w and h). If thumbnails are not found, they will be generated (thanks to thumbnailator!). Image lists are sorted with AlphanumComparator sorting.

<b>Launch parameters:</b><br>
-help - writes available parameters and quits the app<br>
-fullPath:"path/to/images" - specify absolute path to images (if JAR file is in a separate folder) or relative path (if JAR file is in project folder). Default - "images\"<br>
-relPath:"path/to/images" - specify relative path to images from html file location. Default - same as fullPath<br>
-size:xxx - sets max size for images, xxx by xxx pixels (keeping aspect ratio)<br>
-update or -force - forces update of thumbnails, overwriting any existing ones (if you want to make new thumbnails with new -size

Supported image file extensions: JPG, JPEG, PNG
Thumbnail pattern: thumbnail-image.jpg
If respective thumbnail file is not found, a new file will be generated using Thumbnailator.
Max size for thumbnails is set with "-size" launch parameter, defaults to 600 x 600 px (keeping aspect ratio), these will be served on the page.html. Full image will be loaded when user clicks on thumbnail using PhotoSwipe javascript plugin.

<ul>This app uses:
<li><a href="https://github.com/coobird/thumbnailator">Thumbnailator</a></li>
<li><a href="http://www.davekoelle.com/alphanum.html">AlphanumComparator</a></li>
</ul>

Gallery page javascript code (written in jQuery):

        var gallerySelector = "#img-gallery";
        var imageListSelector = "#imageList";
        // parse server reply (JSON, imitated, saved into a tag)
        var itemArray = jQuery.parseJSON($(imageListSelector).html());

        // HTML structure of gallery image
        var imageHTML = "<figure class=\"col-xs-12 col-sm-6 col-md-4\" " + 
            "itemprop=\"associatedMedia\" itemscope " + 
            "itemtype=\"http://schema.org/ImageObject\">\n" +
            "<a href=\"{imageSource}\" itemprop=\"contentUrl\">\n" +
            "<img class=\"img-responsive\" src=\"{imageThumb}\" itemprop=\"thumbnail\" />\n" +
            "</a>\n</figure>";

        // generate images based on JSON request (imitated) and appended them to the page
        itemArray.forEach(function(item) {
            var imageTags = imageHTML.replace("{imageSource}", item.src);
            var imageTags = imageTags.replace("{imageThumb}", item.msrc);
            $(gallerySelector).append(imageTags);
            // save last added element for getThumbBoundsFn
            item.el = $(gallerySelector + " figure:last img")[0];
        });

        // bind click event to every gallery element
        $('.my-gallery').each( function() {
            var $pic = $(this);
            var $pswp = $('.pswp')[0];
            $pic.on('click', 'figure', function(event) {
                event.preventDefault();
                var $index = $(this).index();
                var options = {
                    index: $index,
                    getThumbBoundsFn: function(index) {
                        // get position of element relative to viewport
                        var rect = itemArray[index].el.getBoundingClientRect();
                        // get window scroll Y
                        var pageYScroll = window.pageYOffset ||
                            document.documentElement.scrollTop; 
                        return {x:rect.left, y:rect.top + pageYScroll, w:rect.width};
                    }
                }
                // Initialize PhotoSwipe
                var lightBox = new PhotoSwipe($pswp, PhotoSwipeUI_Default, itemArray, options);
                lightBox.init();
            });
        });
        
Gallery will be dynamically generated based on content of <b>#imageList</b> (which should contain only JSON text) and put into <b>.my-gallery</b>
