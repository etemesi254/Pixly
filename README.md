## Demo
Watch the video of demo (Youtube)
[![Watch the video](https://img.youtube.com/vi/bYdezBoQBOo/hqdefault.jpg)](https://youtu.be/bYdezBoQBOo)


## Features.
 - Load images in various formats - png, jpeg, ppm, jxl, bmp, qoi, pfm.
 - Save images in various formats- jpeg, png, webp, bmp, ico, gif.
 - Choose quality of saved output.
 - Multiple image editing options,  rotate, transpose, change brightness, blur add sepia to image, level adjustment etc
 - Undo history.
 - Responsive layout - Choose what widgets you want to use, rearrange them at will
 - Infinite zoom in and zoom out.
 - Double paned editing that shows you original vs edited picture side by side
 - Thumbnail generation.
 - Directory navigator (Desktop)
 - Light and dark theme options
 - Retrieves image information including exif data and shows image histogram 
 - Pre-configured filters such as `sepia` and `vivid`

## Building

You will need the following tools for building, the tools vary from host to host 

- A rust compiler for the target architecture you are running


### Android
- You will need the Android NDK tools for building the rust library

### Desktop
#### Windows
- A rust compiler, version `1.70` and above recommended
- Visual Studio C/C++ Development kit
- Rust `x86_64-pc-windows-gnu` target,  the `x86_64-pc-windows-msvc` was causing linker problems during testing

- #### Linux
- A rust compiler, version `1.70` and above recommended.
- Java/JVM toolkit for running
