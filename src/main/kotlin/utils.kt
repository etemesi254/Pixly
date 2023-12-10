import androidx.compose.ui.res.loadImageBitmap
import components.loadAndDecodeImage
import java.io.File

@OptIn(ExperimentalUnsignedTypes::class)
fun isImage(file: File): Boolean {

    if (file.isFile) {
        // read 100 bytes from file
        val byteArray = ByteArray(100);
        val stream = file.inputStream()
        val bytesRead = stream.read(byteArray);
        if (bytesRead > 0) {
            // match whatever zune does because I'm too lazy
            // to make this a native call
            if (imageFormat(byteArray.asUByteArray()) != ZilImageFormat.UnknownFormat){
                return true;
            }

        }

    }
    return false;
}

@OptIn(ExperimentalUnsignedTypes::class)
fun imageFormat(byteArray: UByteArray): ZilImageFormat {
    if (byteArray.slice(0..1) == ubyteArrayOf(0xFFu, 0xD8u).asList()) {
        return ZilImageFormat.JPEG;
    } else if (byteArray.slice(0..7) == ubyteArrayOf(137u, 80u, 78u, 71u, 13u, 10u, 26u, 10u).asList()) {
        return ZilImageFormat.PNG
    } else if (byteArray.slice(0..1) == "P5".toList().map { it -> it.code.toByte() }.toList()) {
        return ZilImageFormat.PPM
    } else if (byteArray.slice(0..1) == "P6".toList().map { it -> it.code.toByte() }.toList()) {
        return ZilImageFormat.PPM
    } else if (byteArray.slice(0..1) == "P7".toList().map { it -> it.code.toByte() }.toList()) {
        return ZilImageFormat.PPM
    } else if (byteArray.slice(0..1) == "Pf".toList().map { it -> it.code.toByte() }.toList()) {
        return ZilImageFormat.PPM
    } else if (byteArray.slice(0..1) == "PF".toList().map { it -> it.code.toByte() }.toList()) {
        return ZilImageFormat.PPM
    } else if (byteArray.slice(0..3) == "8BPS".toList().map { it -> it.code.toByte() }.toList()) {
        return ZilImageFormat.PSD
    } else if (byteArray.slice(0..7) == "farbfeld".toList().map { it -> it.code.toByte() }.toList()) {
        return ZilImageFormat.PSD
    } else if (byteArray.slice(0..3) == "8BPS".toList().map { it -> it.code.toByte() }.toList()) {
        return ZilImageFormat.PSD
    } else if (byteArray.slice(0..3) == "qoif".toList().map { it -> it.code.toByte() }.toList()) {
        return ZilImageFormat.PSD
    } else if (byteArray.slice(0..10) == "#?RADIANCE\n".toList().map { it -> it.code.toByte() }.toList()) {
        return ZilImageFormat.HDR
    } else if (byteArray.slice(0..6) == "#?RGBE\n".toList().map { it -> it.code.toByte() }.toList()) {
        return ZilImageFormat.HDR
    } else if (byteArray.slice(0..11) == arrayOf(
            0x00, 0x00, 0x00, 0x0C, 0x4A, 0x58, 0x4C, 0x20, 0x0D, 0x0A, 0x87, 0x0A
        )
    ) {
        return ZilImageFormat.JPEG_XL
    }
    else if (byteArray.slice(0..1)== arrayOf(0xFF,0x0A)){
        return ZilImageFormat.JPEG_XL
    }

    return ZilImageFormat.UnknownFormat;
}