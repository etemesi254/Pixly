/**
 * Load the image specified by appCtx.imFile
 *
 * @param appCtx: Application context
 * @param forceReload: Reload the image even if it's currently loaded
 *
 * NB: DO NOT RUN THIS ON THE MAIN THREAD AS IT WILL BLOCK
 * OTHER I/0, RUN IT ON IO THREAD
 * */
expect fun loadImage(appCtx: AppContext, forceReload: Boolean)