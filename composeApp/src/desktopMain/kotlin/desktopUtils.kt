

fun calcResize(image: ZilBitmapInterface, newW: Long, newH: Long): List<Long> {
    val oldW = image.innerInterface().width().toFloat()
    val oldH = image.innerInterface().height().toFloat()

    val ratioW = oldW / newW.toFloat()
    val ratioH = oldH / newH.toFloat()

    val percent = if (ratioH < ratioW) {
        ratioW
    } else {
        ratioH
    };
    val t = (oldW / percent).toLong()
    val u = (oldH / percent).toLong()
    return listOf(t, u)

}