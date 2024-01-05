package image

import ZilColorspace
import ZilDepth
import ZilImageFormat

fun ZilDepth.toNum(): UInt {
    return when (this) {
        ZilDepth.Unknown -> 0u
        ZilDepth.U8 -> 1u
        ZilDepth.U16 -> 2u
        ZilDepth.F32 -> 3u
    }
}

fun zilDepthFromNum(value: UInt): ZilDepth {
    // nb should match rust definition, otherwise bad
    // things will happen
    return when (value) {
        0u -> ZilDepth.Unknown
        1u -> ZilDepth.U8
        2u -> ZilDepth.U16
        3u -> ZilDepth.F32
        else -> throw Exception("Unknown depth")

    }
}

fun ZilColorspace.toNum(): UInt {
    return when (this) {
        ZilColorspace.Unknown -> 0u
        ZilColorspace.RGB -> 1u
        ZilColorspace.RGBA -> 2u
        ZilColorspace.YCbCr -> 3u
        ZilColorspace.Luma -> 4u
        ZilColorspace.LumaA -> 5u
        ZilColorspace.YCCK -> 6u
        ZilColorspace.CMYK -> 7u
        ZilColorspace.BGR -> 8u
        ZilColorspace.BGRA -> 9u
        ZilColorspace.ARGB -> 10u
    }
}

fun zilColorSpaceFromInt(int: UInt): ZilColorspace {
    return when (int) {
        0u -> ZilColorspace.Unknown
        1u -> ZilColorspace.RGB
        2u -> ZilColorspace.RGBA
        3u -> ZilColorspace.YCbCr
        4u -> ZilColorspace.Luma
        5u -> ZilColorspace.LumaA
        6u -> ZilColorspace.YCCK
        7u -> ZilColorspace.CMYK
        8u -> ZilColorspace.BGR
        9u -> ZilColorspace.BGRA
        10u -> ZilColorspace.ARGB
        else -> ZilColorspace.Unknown

    }
}

fun ZilImageFormat.toNum(): UInt {
    return when (this) {
        ZilImageFormat.UnknownFormat -> 0u
        ZilImageFormat.JPEG -> 1u
        ZilImageFormat.PNG -> 2u
        ZilImageFormat.PPM -> 3u
        ZilImageFormat.PSD -> 4u
        ZilImageFormat.Farbfeld -> 5u
        ZilImageFormat.QOI -> 6u
        ZilImageFormat.JPEG_XL -> 7u
        ZilImageFormat.HDR -> 8u
        ZilImageFormat.BMP -> 9u

    }
}