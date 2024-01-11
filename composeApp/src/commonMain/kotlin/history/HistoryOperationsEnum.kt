package history

enum class HistoryResponse {
    /**
     * Indicates this type of operation was repeated previously
     *
     * I.e. two similar brightness operations were carried out, for such we have a different
     * history layout*/
    SameAsLastOperation,

    /**
     * IT's the same as before, but the time it executed
     * take it as pressing a continuous slider
     * */
    SameAsLastOperationButExecutedTooQuickly,

    /**
     * A new operation different from the last type
     * */
    NewOperation,

    /**
     * Used when we indicate we don't care,
     * */
    DummyOperation
}

interface HistoryOperationsInterface {

    /** Return true if the buffer can be trivially undone,e.g bijection operations
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
    Rotate180(HistoryType.ImageFilter) {
        override fun trivialUndo(): Boolean = true
        override fun requiresValue(): Boolean = false

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

    },
    Hue(HistoryType.ImageFilter) {
        override fun requiresValue(): Boolean = true
        override fun trivialUndo(): Boolean = false
    },

    MedianBlur(HistoryType.ImageFilter) {
        override fun requiresValue(): Boolean = true
        override fun trivialUndo(): Boolean = false
    },
    BilateralBlur(HistoryType.ImageFilter) {
        override fun requiresValue(): Boolean = true
        override fun trivialUndo(): Boolean = false
    },
    ColorMatrix(HistoryType.ImageFilter) {
        override fun requiresValue(): Boolean = true
        override fun trivialUndo(): Boolean = false
    },
    Edges(HistoryType.ImageFilter) {
        override fun requiresValue(): Boolean = false
        override fun trivialUndo(): Boolean = false
    }


}

/**
 * Time between two common history operations
 * for which we consider them as separate operations
 *
 * E.g. if we have two contrast adjustment functions happening too fast, we don't store all
 * of them, just one that takes the longest
 * */
const val TIME_THRESHOLD = 400L

class HistoryOperations {
    private var history: MutableList<HistoryOperationsEnum> = mutableListOf()
    private var values: MutableList<Any> = mutableListOf()

    // records the last time something was added
    private var lastTimeAdded = 0L

    fun addHistory(historyEnum: HistoryOperationsEnum, passedValue: Any?): HistoryResponse {
        if (historyEnum.requiresValue() && passedValue == null) {
            throw Exception("History operation $historyEnum requires value");
        }
        val newValue = passedValue ?: 0
        if (history.lastOrNull() == historyEnum) {
            // we ran the same operation
            val currTime = System.currentTimeMillis()
            val diff = currTime - lastTimeAdded

//            if (diff < TIME_THRESHOLD) {
//                // just update previous with new value, don't update new value
//                // values[values.lastIndex] = newValue
//                lastTimeAdded = currTime
//                return HistoryResponse.SameAsLastOperationButExecutedTooQuickly;
//            }

            values.add(newValue)
            history.add(historyEnum)
            lastTimeAdded = System.currentTimeMillis()

            return HistoryResponse.SameAsLastOperation
        }
        values.add(newValue)
        history.add(historyEnum)
        lastTimeAdded = System.currentTimeMillis()
        return HistoryResponse.NewOperation
    }

    fun getHistory(): MutableList<HistoryOperationsEnum> {
        return history
    }

    fun pop() {
        history = history.dropLast(1).toMutableList()
        values = values.dropLast(1).toMutableList()
    }

    fun getValue(): MutableList<Any> {
        return values
    }

    fun reset() {
        values.clear()
        history.clear()
    }


}
