
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