package history


interface HistoryOperationsInterface {

    /** Return true if the buffer can be trivially undone
     *
     * And we don't have to save information about it
     *
     * E.g. operations like flip and flop are easy to undo
     * but not operations like adjusting levels
     */
    fun trivialUndo(): Boolean;

    /**
     * Return true whether the enum should have a value
     *
     * Operations like contrast take up a value
     * while something like vertical flip doesn't
     * */
    fun requiresValue(): Boolean
}

enum class HistoryType {
    ImageFilter
}


/**
 * Maintains a combination of all possible operations that need
 * to implement an undo filter
 * */
enum class HistoryOperationsEnum(historyType: HistoryType) : HistoryOperationsInterface {

    Brighten(HistoryType.ImageFilter) {
        override fun trivialUndo(): Boolean = false
        override fun requiresValue(): Boolean = true
    },
    Contrast(HistoryType.ImageFilter) {
        override fun trivialUndo(): Boolean = false
        override fun requiresValue(): Boolean = true

    },
    Gamma(HistoryType.ImageFilter) {
        override fun trivialUndo(): Boolean = false
        override fun requiresValue(): Boolean = true

    },
    Exposure(HistoryType.ImageFilter) {
        override fun trivialUndo(): Boolean = false
        override fun requiresValue(): Boolean = true

    },
    Levels(HistoryType.ImageFilter) {
        override fun trivialUndo(): Boolean = false
        override fun requiresValue(): Boolean = true

    },
    BoxBlur(HistoryType.ImageFilter) {
        override fun trivialUndo(): Boolean = false
        override fun requiresValue(): Boolean = true

    },
    GaussianBlur(HistoryType.ImageFilter) {
        override fun trivialUndo(): Boolean = false
        override fun requiresValue(): Boolean = true

    },
    VerticalFlip(HistoryType.ImageFilter) {
        override fun trivialUndo(): Boolean = true
        override fun requiresValue(): Boolean = false

    },
    HorizontalFlip(HistoryType.ImageFilter) {
        override fun trivialUndo(): Boolean = true
        override fun requiresValue(): Boolean = false

    },

    Transposition(HistoryType.ImageFilter) {
        override fun trivialUndo(): Boolean = true
        override fun requiresValue(): Boolean = false

    }

}

class HistoryOperations {
    private val history: MutableList<HistoryOperationsEnum> = mutableListOf()
    private val values: MutableList<Any> = mutableListOf()

    fun addHistory(historyEnum: HistoryOperationsEnum, passedValue: Any?) {
        if (historyEnum.requiresValue() && passedValue == null) {
            throw Exception("History operation $historyEnum requires value");
        }
        val newValue = passedValue ?: 0
        values.add(newValue)
        history.add(historyEnum)
        println(history)
    }

    fun getHistory(): MutableList<HistoryOperationsEnum> {
        return history
    }
    fun getValue(): MutableList<Any> {
        return values
    }

    fun reset() {
        values.clear()
        history.clear()
    }


}
