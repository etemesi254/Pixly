/**
 * List the ordering of the panels in the image
 * editor panel
 * */
enum class FiltersPaneOrdering {
    LightFilters,
    OrientationFilters,
    Levels,
    BlurFilters,
    HSLFilters
}

/**
 * Indicates which right pane item was clicked
 * and is currently opened
 * */
enum class RightPaneOpened {
    /**
     * No panel opened
     */
    None,

    /**
     * The information panel containing image information
     * and exif data is opened
     */
    InformationPanel,

    /**
     * The filters panel which contains the image filters
     * like levels and such is opened
     * */
    FiltersPanel,

    /**
     * History panel ,contains previous commands performed
     * by operations that can be undone
     * */
    HistoryPanel
}

/**
 * Indicates which left pane item was clicked
 * and is currently opened
 * */
enum class LeftPaneOpened {
    /**
     * No panel opened
     */
    None,

    /**
     * Folders panel was opened
     * which gives you the open file stuff
     */
    DirectoryViewer

}
/**
 * Layout for the image pane
 * can either be single or two paned
 * where two paned shows original/edited image
 * and single pane just shows currently edited images
 * */
enum class ImageSpaceLayout{
    SingleLayout,
    PanedLayout,
}