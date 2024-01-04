## Rust JVM bindings to zune-image

This contains the rust bindings to [zune-image] used by Pixly as the 
native backend for image processing (loading, saving, processing)

They are needed by the Jvm endpoints which include Desktop and Android.

So here is how to build for those endpoints

### Building

#### Prerequisites
- We assume you have installed rust and cargo, if not please see how to do that 
[here](https://www.rust-lang.org/tools/install)
- We assume you have installed Android NDK tools, if not see [here](https://developer.android.com/studio/projects/install-ndk)
on how to do that.
- 

### Steps to compile

I'm on Linux, so  I'll be explaining how to build Android and Linux from there, there is a guide explaining
how to build Windows dlls later on

#### Building Android
1. Get rust endpoints by running the following 
   ```shell
    rustup target add aarch64-linux-android armv7-linux-androideabi i686-linux-android
    ```
2. Depending on where your ndk files are stored, modify `./cargo/config` to change the linkers, the current base linker
is `opt/android-sdk/ndk/26.1.10909125/` for my machine, so this is the only thing that needs to change

3.  Run the commands to build for each target
    ```shell
    cargo build --target aarch64-linux-android --release
    cargo build --target armv7-linux-androideabi --release
    cargo build --target i686-linux-android --release
    ```
    
    In case you get linker errors like
    ```text
    error adding symbols: file in wrong format
          collect2: error: ld returned 1 exit status
    ```
    Means step 2  was done wrongly, repeat that

4. Copy the generated files to `composeApp/src/androidMain/jniLibs/{ARCH}`
    
    We assume you are in the directory `rust`
    ```shell
    cp ./target/aarch64-linux-android/release/libzune_jni_bindings.so ../composeApp/src/androidMain/jniLibs/arm64-v8a/
    cp ./target/armv7-linux-androideabi/release/libzune_jni_bindings.so ../composeApp/src/androidMain/jniLibs/armeabi-v7a/
    cp ./target/i686-linux-android/release/libzune_jni_bindings.so ../composeApp/src/androidMain/jniLibs/x86/
    ```
   
5. You are all done, the libraries should be picked up by Gradle and android automatically

[zune-image]: https://github.com/etemesi254/zune-image 