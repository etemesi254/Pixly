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
    FineTunePanel,
    /**
     * Shows pre-configured image filters
     * */
    ImageFilters,

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
enum class ImageSpaceLayout {
    /**
     * Only use a single layout shows one image
     * */
    SingleLayout,

    /**
     * Use a two paned layout that shows the initial
     * image and editing step
     * */
    PanedLayout,
}

/**
 * Represents multiple canvas endpoints.
 *
 * ### Use case
 *  When rendering images, we may want to use different bitmaps
 *  for rendering, but we don't want to use an array to hold bitmaps
 *  since we may lose count of what each index represents
 *
 *  this is used to keep track, if you have a key ,you can get the canvas for that type
 *
 * */
enum class ImageContextBitmaps {
    /**
     * The current image used for most drawing
     * */
    CurrentCanvasImage,

    /**
     * The first canvas used for two paned layout for the original drawing,
     * this is usually the left pane
     * */
    FirstCanvasImage,

    /**
     * The filters displayed in the canvas
     * */
    FiltersCanvasImage
}

