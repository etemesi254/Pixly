
enum class ImageExtensions {
    PNG,
    JPEG,
    JXL,
    PPM,

}

fun ImageExtensions.extensions(): List<String> {
    return when (this) {
        ImageExtensions.PNG -> listOf("png","PNG")
        ImageExtensions.JPEG -> listOf("jpeg", "jpg","JPG","JPEG")
        ImageExtensions.JXL -> listOf("jxl")
        ImageExtensions.PPM -> listOf("ppm", "pam", "pbm", "pfm")
    }
}

val SUPPORTED_EXTENSIONS: List<String> = ImageExtensions.values().flatMap { it.extensions() }