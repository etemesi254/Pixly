
enum class ZilColorspace {
    /// Red, Green , Blue
    RGB,

    /// Red, Green, Blue, Alpha
    RGBA,

    /// YUV colorspace
    YCbCr,

    /// Grayscale colorspace
    Luma,

    /// Grayscale with alpha colorspace
    LumaA,

    YCCK,

    /// Cyan , Magenta, Yellow, Black
    CMYK,

    /// Blue, Green, Red
    BGR,

    /// Blue, Green, Red, Alpha
    BGRA,

    /// ALpha, Red,Green, Blue
    ARGB,

    /// The colorspace is unknown
    Unknown;


    public fun components(): Int {
        return when (this) {
            RGB -> 3
            RGBA -> 4
            YCbCr -> 4
            Luma -> 1
            LumaA -> 2
            YCCK -> 4
            CMYK -> 4
            BGR -> 3
            BGRA -> 4
            ARGB -> 4
            Unknown -> 0
        }
    }
}
