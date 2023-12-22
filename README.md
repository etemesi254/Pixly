## Pixly 

An image editor built on kotlin and Rust


### Features
- Load and save in multiple image formats
  - JPEG
  - PNG
  - HDR
  - BMP
  - PPM
  - QOI
  - PSD (limited)
- Image filters and effects in real-time.
   - Rotate, transpose, adjust exposure, gamma, contrast, brightness in real time, no lagging no waiting.

- Adjustable panels
  - Move the whole UI around until you are satisfied, capture your taste
- Drag and drop filter panels to reorder them to better suit your workflow
- Zoom in and out capabilities
  - Zoom in on the image, pan around a zoomed image and see pixel effects in real time
- Image metadata information
  - View info such as file size, width height and even exif information where present
- Tab Based nagivation
  - Load multiple images in many tabs, edit, each image individually, and save.
  - Information is specific to each tab, and preserved across tab switches

### Shortcomings
- Skia can't let us tell it where to point to a native memory pointer
which means we waste 2x image memory
- Although there exists an `ImageBitmap` interface, it's a useless one since nothing will be drawn
unless its `SkiaBackedImageBitmap`,  see https://github.com/JetBrains/compose-multiplatform/issues/108