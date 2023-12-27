enum class ZilImageFormat {
    /**
     * Any unknown format
     */
    UnknownFormat,

    /**
     * Joint Photographic Experts Group
     */
    JPEG,

    /**
     * Portable Network Graphics
     */
    PNG,

    /**
     * Portable Pixel Map image
     */
    PPM,

    /**
     * Photoshop PSD component
     */
    PSD,

    /**
     * Farbfeld format
     */
    Farbfeld,

    /**
     * Quite Okay Image
     */
    QOI,

    /**
     * JPEG XL, new format
     */
    JPEG_XL,

    /**
     * Radiance HDR decoder
     */
    HDR,

    /**
     * Windows Bitmap Files
     */
    BMP
}

fun ZilImageFormat.hasEncoder(): Boolean {
    return when (this) {
        ZilImageFormat.UnknownFormat -> false
        ZilImageFormat.JPEG -> true
        ZilImageFormat.PNG -> true
        ZilImageFormat.PPM -> true
        ZilImageFormat.PSD -> false
        ZilImageFormat.Farbfeld -> true
        ZilImageFormat.QOI -> false
        ZilImageFormat.JPEG_XL -> true
        ZilImageFormat.HDR -> true
        ZilImageFormat.BMP -> false
    }
}