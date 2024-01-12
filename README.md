# Pixly - An image editor
![](./screenshots/ic_launcher-playstore.png)
Pixly is an image editor built in Compose Multiplatform and Rust that works on Windows, Linux and Android devices

- Compose makes the GUI 
- Rust via [zune-image](https://github.com/etemesi254/zune-image) deals with image manipulation operations.

## Architecture
I've written on how the two apps work in my blog


## Motivation
I wanted a simple lightroom alternative that works on Linux.

## Demo
Watch the video of demo (Youtube)

### Desktop (Linux)

https://github.com/etemesi254/Pixly/assets/24320659/780868d5-78b4-4ba9-91ee-6fc16980f58b

### Android

https://github.com/etemesi254/Pixly/assets/24320659/386746bd-a2ee-4b0b-b63f-a6a60a074c0b

## Pics

### Desktop

#![Desktop In full view](./screenshots/desktop_full.png)

### Android

#![Android In Full View](./screenshots/android_view.png)

## Features.

- Load images in various formats - png, jpeg, ppm, jxl, bmp, qoi, pfm.
- Save images in various formats- jpeg, png, webp, bmp, ico, gif.
- Choose quality of saved output.
- Tabbed-layout : Open multiple images at once.
- Multiple image editing options, rotate, transpose, change brightness, blur add sepia to image, level adjustment etc
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
- Rust `x86_64-pc-windows-gnu` target, the `x86_64-pc-windows-msvc` was causing linker problems during testing

- #### Linux
- A rust compiler, version `1.70` and above recommended.
- Java/JVM toolkit for running
