/**
 * List the ordering of the panels in the image
 * editor panel
 * */
enum class FiltersPaneOrdering {
    LightFilters,
    OrientationFilters,
    HistogramFilters,
    Levels,
}

/**
 * Indicates which right pane item was clicked
 * and is currently opened
 * */
enum class RightPaneOpened {
    None,
    InformationPanel,
    FiltersPanel
}