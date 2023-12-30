import java.nio.ByteBuffer

interface ZilImageInterface {
    /**
     * Clone an image class returning a copy one can
     * modify independently
     */
    fun clone(): ZilImageInterface

    /**
     * Return the image depth
     *
     * This gives you the internal pixel representation
     * for the native image, e.g. F32 would use float type
     *
     * @return depth of image
     * */
    public fun depth(): ZilDepth

    /**
     * Return image width
     *
     * @return internal image width
     * */
    public fun width(): UInt

    /**
     * Return image height
     *
     * @return internal image height
     * */
    public fun height(): UInt

    /**
     * Return image colorspace.
     *
     * This also tells you the number of channel components of images
     *
     * @return image colorspace
     * */
    public fun colorspace(): ZilColorspace

    /**
     * Convert the image to a different colorspace
     *
     * @param to: The new colorspace to convert to
     * */
    public fun convertColorspace(to: ZilColorspace)

    /**
     * Convert image to a different depth
     *
     * @param to:  The new depth
     * */
    public fun convertDepth(to: ZilDepth)

    /**
     * Change the image brightness
     *
     * @param d: The new image brightness
     * */
    public fun brightness(d: Float)

    /**
     * Apply a bilateral filter to the image
     *
     * @param d Diameter of the bilateral filter
     * @param sigmaColor A larger value of the parameter means that farther colors within the pixel neighborhood (see sigmaSpace)
     *  will be mixed together, resulting in larger areas of semi-equal color.
     * @param sigmaSpace Filter sigma in the coordinate space.
     * A larger value of the parameter means that farther pixels will influence each other as
     * long as their colors are close enough (see sigma_color ).
     * When d>0, it specifies the neighborhood size regardless of sigma_space. Otherwise, d is proportional to sigma_space
     * */
    public fun bilateralFilter(d: Int, sigmaColor: Float, sigmaSpace: Float)

    /**
     * Change image contrast
     *
     * @param contrast: The contrast to change the image
     * */
    public fun contrast(contrast: Float)

    /**
     * Crop the image returning a smaller part of the image
     *
     * Origin is defined as the image top left corner.
     *
     * @param newWidth The width of the new cropped out image
     * @param newHeight The height of the new cropped out image.
     * @param x How far from the x origin the image should start from
     * @param y How far from the y origin the image should start from
     * */
    public fun crop(newWidth: UInt, newHeight: UInt, x: UInt, y: UInt)

    /**
     * Change the image exposure.
     *
     * Formula is `(pix - blackPoint)*exposure`
     *
     * @param exposure The new image exposure
     * @param blackPoint A number subrtracted from a pixel before exposure, should be between 0 and 1
     * */
    public fun exposure(exposure: Float, blackPoint: Float)

    /**
     * Adjust gamma of the image
     *
     * @param gamma: The new gamma, should be between 0.0 and 3.0
     * */
    public fun gamma(gamma: Float)

    /**
     * Save the image with a specific format
     *
     * @param file: The file name to save to
     * @param format: The image format to use, this will take precedence over file
     * extension.
     * */
    public fun save(file: String, format: ZilImageFormat)

    /**
     * Save the image, determining the image format from extension
     *
     * @param file:  The file name, the extension will determine the image format
     * */
    public fun save(file: String)

    /**
     * Load a new file for this image,
     * after this, the image information e.g. width and height
     * is from this image
     *
     * @param file: The image file to load
     * */
    fun loadFile(file: String);

    /**
     * Linearly stretches the contrast, sending lower to image minimum and upper to image maximum.
     *
     *
     * @param lower The lower minimum for which pixels below this value become 0
     * @param higher Upper maximum for which pixels above this value become the maximum value
     *
     * */
    fun stretchContrast(lower: Float, higher: Float)

    /**
     * Apply the scharr  filter over an image
     *
     * This is a 3x3 derivative of the sobel filter with a different matrix
     * */
    fun scharr()

    /**
     *  Apply the [sobel](https://en.wikipedia.org/wiki/Sobel_operator) filter on an image
     *
     *  This is a 3x3 separable filter applied both horizontally and vertically
     * */
    fun sobel()

    /**
     * Creates a vertical mirror image by reflecting
     * the pixels around the central x-axis.
     * # Example
     *
     * old image     new image
     * ```text
     * ┌─────────┐   ┌──────────┐
     * │a b c d e│   │j i h g f │
     * │f g h i j│   │e d c b a │
     * └─────────┘   └──────────┘
     * ```
     * */
    fun flip()

    /**
     * Creates a horizontal mirror image by reflecting the pixels around the central y-axis
     *
     * ```text
     * old image     new image
     * ┌─────────┐   ┌──────────┐
     * │a b c d e│   │e d b c a │
     * │f g h i j│   │j i h g f │
     * └─────────┘   └──────────┘
     * ````
     * */
    fun flop()

    /**
     * Transpose an image
     *
     * This mirrors the image along the image top left to bottom-right diagonal
     *
     * Done by swapping X and Y indices of the array representation
     * */
    fun transpose()

    /**
     * Flip the image vertically,(rotate image by 180 degrees)
     *
     * ```text
     * old image     new image
     * ┌─────────┐   ┌──────────┐
     * │a b c d e│   │f g h i j │
     * │f g h i j│   │a b c d e │
     * └─────────┘   └──────────┘
     *
     * */
    fun verticalFlip()

    /**
     * Returns a histogram of color occurences and frequencies
     *
     * @return histogram: Key is a string number while value is the length
     * map length maps to image colorspace channels.
     * */
    fun histogram(): Map<String, LongArray>

    /**
     * Return the image exif metadata
     *
     * This returns an empty map if the image doesn't have exif info,
     * otherwise returns exif information in key-value pair
     *
     * */
    fun exifMetadata(): Map<String, String>

    /**
     * Perform a gaussian blur on an image
     *
     * @param radius An influence on how strong the blur should be, larger values
     * cause more blurring, smaller values cause less
     * */
    fun gaussianBlur(radius: Long)

    /**
     * Perform a 2D box blur on an image
     *
     * @param radius: How big the radius is
     *
     * */
    fun boxBlur(radius: Long)

    /**
     * The minimum output buffer size that is required to write pixels into
     * */
    fun outputBufferSize(): Long


    /**
     * Write interleaved pixels into output  using tempBuf
     * as a temporary memory location between native memory and jni memory
     *
     * @param tempBuf: A ByteBuffer allocated using allocate_direct whose capacity is enough
     * to hold the current image pixels
     * @param output: A ByteArray with enough size for storing the current image pixels
     * this will not try to resize it, it's the work of the caller to handle resizing
     * */
    fun writeToBuffer(tempBuf: ByteBuffer, output: ByteArray)

    /**
     * Rotate an image by 90 degrees
     * */
    fun rotate90()

    /**
     * Adjust the hue, saturation and lightness of an image
     *
     * # Arguments
     * - hue: The new hue value, a value between 0 and 360, this is a cyclic rotation and hence 0 and 360
     * have the same effect
     *
     * - saturation: The new saturation value, 0 means grayscale, 1 has no effect, and other values either create a pastel or vibrant color
     *
     * - lightness: The brightness of an image, 0 creates a pitch black image, 1 has no effect while values greater than 1 increase brightness
     * */
    fun hslAdjust(hue: Float, saturation: Float, lightness: Float);

    /**
     * Applies a median filter of given dimensions to an image.
     *
     * Each output pixel is the median of the pixels in a (2 * radius + 1) * (2 * radius + 1)
     * kernel of pixels in the input image.
     * */
    fun medianBlur(radius: Long);

    fun colorMatrix(floatArray: FloatArray)

    fun resize(newWidth: Long, newHeight: Long)


}