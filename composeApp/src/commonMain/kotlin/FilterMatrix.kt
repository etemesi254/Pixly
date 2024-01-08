
data class FilterMatrixComponent(val name: String, val colorMatrix: FloatArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FilterMatrixComponent

        if (name != other.name) return false
        if (!colorMatrix.contentEquals(other.colorMatrix)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + colorMatrix.contentHashCode()
        return result
    }
};


fun colorMatricesPane(): List<FilterMatrixComponent> {
    // convert to grayscale
    return listOf(
        FilterMatrixComponent(
            "Grayscale", listOf(
                0.2F, 0.3F, 0.5F, 0.0F, 0.0F,
                0.2F, 0.3F, 0.5F, 0.0F, 0.0F,
                0.2F, 0.3F, 0.5F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F, 1.0F, 0.0F
            ).toFloatArray()
        ),
        //https://learn.microsoft.com/en-us/archive/msdn-magazine/2005/january/net-matters-sepia-tone-stringlogicalcomparer-and-more
        FilterMatrixComponent(
            "Sepia", listOf(
                0.393F, 0.769F, 0.189F, 0.0F, 0.0F,
                0.349F, 0.686F, 0.686F, 0.0F, 0.0F,
                0.272F, 0.534F, 0.534F, 0.0F, 0.0F,
                0.0F, 0.0F, 0.0F, 1.0F, 0.0F
            ).toFloatArray()
        ),

        FilterMatrixComponent(
            "Vivid", listOf(
                +1.2F, -0.1F, -0.1F, 0.0F, 0.0F,
                -0.1F, +1.2F, -0.1F, 0.0F, 0.0F,
                -0.1F, -0.1F, +1.2F, 0.0F, 0.0F,
                +0.0F, +0.0F, +0.0F, 1.0F, 0.0F
            ).toFloatArray()
        ),
        FilterMatrixComponent(
            "Polaroid", listOf(
                +1.438F, -0.122F, -0.016F, 0.0F, -0.03F,
                -0.062F, +1.378F, -0.016F, 0.0F, +0.00F,
                -0.062F, -0.122F, +1.483F, 0.0F, -0.02F,
                +0.0F, +0.0F, +0.0F, 1.0F, 0.0F
            ).toFloatArray()
        ),
        FilterMatrixComponent(
            "Lilac", listOf(
                1.2F, 0.00F, 0.00F, 0.0F, 0.00F,
                0.0F, 0.8F, 0.00F, 0.0F, 0.00F,
                0.0F, 0.00F, 1.2F, 0.0F, 0.00F,
                0.0F, +0.0F, +0.0F, 1.0F, 0.0F
            ).toFloatArray()
        ),
    )
}