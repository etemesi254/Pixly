
/**
 * Various image depths
 *
 * This also tell us pixel sample, i.e how many pixels
 * */
enum class ZilDepth {
    Unknown,
    U8,
    U16,
    F32;


    /**
     * Return the size of a single sample pixel of a certain depth
     *
     * E.g.
     * - U8 has a value of 1, meaning each pixel occupies one byte
     * - F32 has a  value of 4 meaning each pixel occupies 4 bytes
     * - Unknown returns zero
     *
     * */
    fun sizeOf(): Int {
        return when (this) {
            Unknown -> 0
            U8 -> 1
            U16 -> 2
            F32 -> 4
        }
    }
}