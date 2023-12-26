package image.desktop;

import java.lang.ref.Cleaner;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

class Zic implements Cleaner.Cleanable {

    static {
        System.loadLibrary("zune_jni_bindings");
    }

    Zic(String file) {
        imagePtr = createImagePtrNative();
        loadImageNative(imagePtr, file);
    }

    Zic() {
        imagePtr = createImagePtrNative();
    }

    private Zic(long imagePtr) {
        this.imagePtr = imagePtr;
    }

    private long imagePtr = 0;


    private native long createImagePtrNative();

    private native void destroyImagePtrNative(long imagePtr);

    private native void loadImageNative(long imagePtr, String fileName);

    private native long cloneNative(long imagePtr);

    private native long getImageWidthNative(long imagePtr);

    private native long getImageHeightNative(long imagePtr);

    private native long getDepthNative(long imagePtr);

    private native long getColorSpaceNative(long imagePtr);

    private native void saveNative(long imagePtr, String filename);

    private native void saveToNative(long imagePtr, String filename, long imageFormat);

    private native void contrastNative(long imagePtr, float value);

    private native void cropNative(long imagePtr, long newWidth, long newHeight, long x, long y);

    private native void exposureNative(long imagePtr, float exposure, float blackPoint);

    private native void gammaNative(long imageptr, float gamma);

    private native void bilateralFilterNative(long imagePtr, int d, float sigmaSpace, float sigmaColor);

    private native long allocByteMemoryNative(long nativeMemoryLength);

    private native long resizeByteMemoryNative(long nativeMemoryPtr, long size);

    private native void freeByteMemoryNative(long nativeMemoryPtr);

    private native long getImageOutBufferSizeNative(long nativeMemoryPtr);

    private native void writeToBufferNative(long imagePtr, long nativeMemoryPtr, long length, byte[] array);

    private native void convertColorSpaceNative(long imagePtr, long toColorspace);

    private native void convertDepthNative(long imagePtr, long toDepth);

    private native void brightenNative(long imagePtr, float by);

    private native void sobelNative(long imagePtr);

    private native void scharrNative(long imagePtr);

    private native void stretchContrastNative(long imagePtr, float lower, float higher);

    private native void flipNative(long imagePtr);

    private native void verticalFlipNative(long imagePtr);

    private native void flopNative(long imagePtr);

    private native void transposeNative(long imagePtr);

    private native void histogramNative(long imagePtr, Map<String, long[]> arrays);

    private native void exifMetadataNative(long imagePtr, Map<String, String> metadata);

    private native void boxBlurNative(long imagePtr, long radius);

    private native void gaussianBlurNative(long imagePtr, long radius);

    private native void hslAdjustNative(long imagePtr, float hue, float saturation, float lightness);

    private native void medianBlurNative(long imagePtr, long radius);

    //private native void rotate90Native(long imagePtr);

    /**
     * Write to native buffer allocated via bytebuffer direct
     */
    private native void writeToNioBufferNative(long imagePtr, ByteBuffer buffer);


    public long width() throws Exception {
        if (imagePtr == 0) {
            throw new Exception("Image ptr is zero, have you loaded an image?");
        }
        return this.getImageWidthNative(imagePtr);
    }

    public long height() throws Exception {
        if (imagePtr == 0) {
            throw new Exception("Image ptr is zero, have you loaded an image?");
        }
        return this.getImageHeightNative(imagePtr);
    }

    public void save(String filename) throws Exception {
        if (imagePtr == 0) {
            throw new Exception("Image ptr is zero, have you loaded an image?");
        }
        this.saveNative(imagePtr, filename);
    }

    public void contrast(float contrast) {
        this.contrastNative(imagePtr, contrast);
    }

    public void exposure(float exposure, float blackPoint) {
        this.exposureNative(imagePtr, exposure, blackPoint);
    }

    public void crop(long newWidth, long newHeight, long x, long y) {
        this.cropNative(imagePtr, newWidth, newHeight, x, y);
    }

    public void bilateralFilter(int d, float sigmaSpace, float sigmaColor) {
        this.bilateralFilterNative(imagePtr, d, sigmaSpace, sigmaColor);
    }

    public void gamma(float gamma) {
        this.gammaNative(imagePtr, gamma);
    }

    @Override
    public void clean() {
        this.destroyImagePtrNative(imagePtr);
    }

    public long getOutBufferSize() {
        return this.getImageOutBufferSizeNative(imagePtr);
    }


    public long getDepth() {
        return getDepthNative(imagePtr);
    }

    public long getColorSpace() {
        return getColorSpaceNative(imagePtr);
    }

    public void save(String filename, long format) {
        saveToNative(imagePtr, filename, format);
    }

    public void convertColorspace(long to) {
        convertColorSpaceNative(imagePtr, to);
    }

    public void convertDepth(long to) {
        convertDepthNative(imagePtr, to);
    }

    public void loadNewFile(String file) {
        loadImageNative(imagePtr, file);
    }

    public Zic clone() {
        var newPtr = cloneNative(imagePtr);
        return new Zic(newPtr);

    }

    public void brighten(float by) {
        brightenNative(imagePtr, by);
    }

    public void sobel() {
        sobelNative(imagePtr);
    }

    public void scharr() {
        scharrNative(imagePtr);
    }

    public void stretchContrast(float lower, float higher) {
        stretchContrastNative(imagePtr, lower, higher);
    }

    public void flip() {
        flipNative(imagePtr);
    }

    public void flop() {
        flopNative(imagePtr);
    }

    public void transpose() {
        transposeNative(imagePtr);
    }

    public void verticalFlip() {
        verticalFlipNative(imagePtr);
    }

    public Map<String, long[]> histogram() {
        Map<String, long[]> map = new HashMap<>();
        histogramNative(imagePtr, map);
        return map;
    }

    public Map<String, String> exifMetadata() {
        var map = new HashMap<String, String>();
        exifMetadataNative(imagePtr, map);
        return map;
    }

    public void gaussianBlur(Long radius) {
        gaussianBlurNative(imagePtr, radius);
    }

    public void boxBlur(Long radius) {
        boxBlurNative(imagePtr, radius);
    }

    public void writeToBuffer(ByteBuffer buf,  byte[] output) throws Exception {
        if (!buf.isDirect()) {
            throw new Exception("Native buffer should be direct");
        }
        if (buf.capacity() < getOutBufferSize()) {
            throw new Exception("The buffer capacity will not fit the array");
        }
        if (getOutBufferSize() > output.length) {
            throw new Exception("The output size will not fit the array");
        }

        writeToNioBufferNative(imagePtr, buf);
        // transfer that now to an output
        // the bytebuffer isn't backed by an array, so we can't peek into it
        // we just write the output to an array understood by java/jvm
       // buf.get(0, output);
    }

    public void rotate90() {
        //rotate90Native(imagePtr);
    }

    public void hslAdjust(float hue, float saturation, float lightness) {

        hslAdjustNative(imagePtr, hue, saturation, lightness);
    }

    public void medianBlur(long radius) {
        medianBlurNative(imagePtr, radius);
    }


}
