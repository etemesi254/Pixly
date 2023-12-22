import androidx.compose.ui.res.loadImageBitmap
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
    } else if (byteArray.slice(0..1) == "P5".toList().map { it -> it.code.toUByte() }.toList()) {
        return ZilImageFormat.PPM
    } else if (byteArray.slice(0..1) == "P6".toList().map { it -> it.code.toUByte() }.toList()) {
        return ZilImageFormat.PPM
    } else if (byteArray.slice(0..1) == "P7".toList().map { it -> it.code.toUByte() }.toList()) {
        return ZilImageFormat.PPM
    } else if (byteArray.slice(0..1) == "Pf".toList().map { it -> it.code.toUByte() }.toList()) {
        return ZilImageFormat.PPM
    } else if (byteArray.slice(0..1) == "PF".toList().map { it -> it.code.toUByte() }.toList()) {
        return ZilImageFormat.PPM
    } else if (byteArray.slice(0..3) == "8BPS".toList().map { it -> it.code.toUByte() }.toList()) {
        return ZilImageFormat.PSD
    } else if (byteArray.slice(0..7) == "farbfeld".toList().map { it -> it.code.toUByte() }.toList()) {
        return ZilImageFormat.PSD
    } else if (byteArray.slice(0..3) == "8BPS".toList().map { it -> it.code.toUByte() }.toList()) {
        return ZilImageFormat.PSD
    } else if (byteArray.slice(0..3) == "qoif".toList().map { it -> it.code.toUByte() }.toList()) {
        return ZilImageFormat.PSD
    } else if (byteArray.slice(0..10) == "#?RADIANCE\n".toList().map { it -> it.code.toUByte() }.toList()) {
        return ZilImageFormat.HDR
    } else if (byteArray.slice(0..6) == "#?RGBE\n".toList().map { it -> it.code.toUByte() }.toList()) {
        return ZilImageFormat.HDR
    } else if (byteArray.slice(0..11) == ubyteArrayOf(
            0x00u, 0x00u, 0x00u, 0x0Cu, 0x4Au, 0x58u, 0x4Cu, 0x20u, 0x0Du, 0x0Au, 0x87u, 0x0Au
        ).toList()
    ) {
        return ZilImageFormat.JPEG_XL
    }
    else if (byteArray.slice(0..1)== ubyteArrayOf(0xFFu,0x0Au).toList()){
        return ZilImageFormat.JPEG_XL
    }

    return ZilImageFormat.UnknownFormat;
}