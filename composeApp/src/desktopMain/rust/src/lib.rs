use std::alloc::{dealloc, Layout};
use std::ffi::c_void;
use jni::objects::{JByteArray, JByteBuffer, JClass, JIntArray, JObject, JString};
use jni::JNIEnv;
use jni::sys::{jfloat, jint, jlong};
use zune_core::colorspace::ColorSpace;
use zune_image::codecs::bmp::zune_core::bit_depth::BitDepth;
use zune_image::codecs::ImageFormat;
use zune_image::core_filters::colorspace::ColorspaceConv;
use zune_image::core_filters::depth::Depth;

use zune_image::image::Image;
use zune_image::traits::OperationsTrait;
use zune_imageprocs::bilateral_filter::BilateralFilter;
use zune_imageprocs::box_blur::BoxBlur;
use zune_imageprocs::brighten::Brighten;
use zune_imageprocs::contrast::Contrast;
use zune_imageprocs::crop::Crop;
use zune_imageprocs::exposure::Exposure;
use zune_imageprocs::flip::{Flip, VerticalFlip};
use zune_imageprocs::flop::Flop;
use zune_imageprocs::gamma::Gamma;
use zune_imageprocs::gaussian_blur::GaussianBlur;
use zune_imageprocs::histogram::ChannelHistogram;
use zune_imageprocs::hsv_adjust::HsvAdjust;
use zune_imageprocs::median::Median;
use zune_imageprocs::rotate::Rotate;
use zune_imageprocs::scharr::Scharr;
use zune_imageprocs::sobel::Sobel;
use zune_imageprocs::stretch_contrast::StretchContrast;
use zune_imageprocs::transpose::Transpose;


#[no_mangle]
pub extern "system" fn Java_image_desktop_ZilImageJni_createImagePtrNative(_env: JNIEnv, _class: JClass) -> jlong {
    let image = Image::new(vec![], BitDepth::Unknown, 1, 1, ColorSpace::Unknown);

    let c = Box::new(image);
    // convert it to a pointer
    Box::into_raw(c) as jlong
}

#[no_mangle]
pub unsafe extern "system" fn Java_image_desktop_ZilImageJni_destroyImagePtrNative(_env: JNIEnv, _class: JClass, ptr: jlong) {
    let ptr = ptr as *mut Image;
    ptr.drop_in_place();
    dealloc(ptr.cast(), Layout::new::<Image>())
}

#[no_mangle]
pub unsafe extern "system" fn Java_image_desktop_ZilImageJni_loadImageNative<'a>(mut env: JNIEnv<'a>, _class: JClass, image_ptr: jlong, filename: JString) {
    let img_ptr = image_ptr as *mut Image;
    let input_str: String = env.get_string(&filename).expect("Could not get input string").into();
    let image = Image::open(&input_str);
    match image {
        Ok(im) => {
            *img_ptr = im;
        }
        Err(e) => {
            env.throw(format!("Cannot load image {e}")).expect("Cannot throw exception");
        }
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_image_desktop_ZilImageJni_getImageWidthNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong) -> jlong {
    let img_ptr = image_ptr as *mut Image;
    if !img_ptr.is_null() {
        return unsafe { (*img_ptr).dimensions().0 } as jlong;
    }
    env.throw("Image ptr is null").expect("Cannot throw exception");
    0
}

#[no_mangle]
pub unsafe extern "system" fn Java_image_desktop_ZilImageJni_getImageHeightNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong) -> jlong {
    let img_ptr = image_ptr as *mut Image;
    if !img_ptr.is_null() {
        return unsafe { (*img_ptr).dimensions().1 } as jlong;
    }
    env.throw("Image ptr is null").expect("Cannot throw exception");
    0
}

#[no_mangle]
pub unsafe extern "system" fn Java_image_desktop_ZilImageJni_saveNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong, filename: JString) {
    let img_ptr = image_ptr as *const Image;
    if img_ptr.is_null() {
        env.throw("Image is null").expect("Could not throw exception");
        return;
    }
    let input_str: String = env.get_string(&filename).expect("Could not get input string").into();

    let img = &*img_ptr;
    if let Err(err) = img.save(input_str) {
        env.throw(err.to_string()).expect("Could not throw exception");
    }
}

fn exec_imgproc<T>(env: &mut JNIEnv, image: jlong, filter: T)
    where
        T: OperationsTrait,
{
    let image = image as *mut Image;
    if image.is_null() {
        env.throw("Image is null").expect("Failed to throw exception");
        return;
    }
    let image = unsafe { &mut *image };

    if let Err(err) = filter.execute_impl(image) {
        env.throw(err.to_string()).expect("Could not throw exception");
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_image_desktop_ZilImageJni_exposureNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong, exposure: jfloat, black_point: jfloat) {
    let filter = Exposure::new(exposure, black_point);
    exec_imgproc(&mut env, image_ptr, filter);
}

#[no_mangle]
pub unsafe extern "system" fn Java_image_desktop_ZilImageJni_cropNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong, new_width: jlong, new_height: jlong, x: jlong, y: jlong) {
    let filter = Crop::new(new_width as usize, new_height as usize, x as usize, y as usize);
    exec_imgproc(&mut env, image_ptr, filter);
}

#[no_mangle]
pub unsafe extern "system" fn Java_image_desktop_ZilImageJni_contrastNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong, contrast: jfloat) {
    let filter = Contrast::new(contrast);
    exec_imgproc(&mut env, image_ptr, filter);
}

#[no_mangle]
pub unsafe extern "system" fn Java_image_desktop_ZilImageJni_bilateralFilterNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong, d: jint, sigma_space: jfloat, sigma_color: jfloat) {
    let filter = BilateralFilter::new(d, sigma_color, sigma_space);
    exec_imgproc(&mut env, image_ptr, filter)
}

#[no_mangle]
pub unsafe extern "system" fn Java_image_desktop_ZilImageJni_gammaNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong, gamma: jfloat) {
    let filter = Gamma::new(gamma);
    exec_imgproc(&mut env, image_ptr, filter);
}

#[no_mangle]
pub unsafe extern "system" fn Java_image_desktop_ZilImageJni_getImageOutBufferSizeNative(_env: JNIEnv, _class: JClass, image_ptr: jlong) -> jlong {
    let image = { &*(image_ptr as *const Image) };

    let (w, h) = image.dimensions();
    let colorspace = image.colorspace().num_components();
    let depth = image.depth().size_of();

    return (w * h * colorspace * depth) as _;
}

#[no_mangle]
pub unsafe extern "system" fn Java_image_desktop_ZilImageJni_allocByteMemoryNative(_env: JNIEnv, _class: JClass, size: jlong) -> jlong {
    let ptr = libc::malloc(size as _);
    if ptr.is_null() {
        return 0;
    }
    return ptr as _;
}

#[no_mangle]
pub unsafe extern "system" fn Java_image_desktop_ZilImageJni_resizeByteMemoryNative(_env: JNIEnv, _class: JClass, initial_ptr: jlong, new_size: jlong) -> jlong {
    let ptr = initial_ptr as *mut c_void;
    let c = libc::realloc(ptr, new_size as _);
    if c.is_null() {
        // free initial pointer
        libc::free(ptr);
        // then report we failed
        return 0;
    }
    c as _
}

#[no_mangle]
pub unsafe extern "system" fn Java_image_desktop_ZilImageJni_freeByteMemoryNative(_env: JNIEnv, _class: JClass, ptr: jlong) {
    let ptr = ptr as *mut c_void;
    libc::free(ptr);
}

#[no_mangle]
pub unsafe extern "system" fn Java_image_desktop_ZilImageJni_writeToBufferNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong, native_out_ptr: jlong, native_out_length: jlong, array: JByteArray) {

    // create the slice
    let native_ptr = native_out_ptr as *mut u8;
    let slice = std::slice::from_raw_parts_mut(native_ptr, native_out_length as usize);
    let image = { &*(image_ptr as *const Image) };
    let colorspace = image.colorspace();

    match image.frames_ref().get(0) {
        None => {
            env.throw("No frames in image, did you load an image?").expect("Could not throw an exception");
        }
        Some(frame) => {
            // write it to output
            if let Err(e) = zune_image::utils::swizzle_channels(&frame.channels_ref(colorspace, false), slice) {
                env.throw(e.to_string()).expect("Could not throw exception");
            }
        }
    }
    let (_, b, _) = slice.align_to::<i8>();
    if let Err(e) = env.set_byte_array_region(&array, 0, b) {
        env.throw(e.to_string()).expect("Could not throw error");
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_image_desktop_ZilImageJni_writeToNioBufferNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong, buffer: JByteBuffer) {
    let buffer_ptr = env.get_direct_buffer_address(&buffer).expect("Could not get buffer address");
    let size = env.get_direct_buffer_capacity(&buffer).expect("Could not get buffer size");

    let new_buff = std::slice::from_raw_parts_mut(buffer_ptr, size);
    // create the slice
    let image = { &*(image_ptr as *const Image) };
    let colorspace = image.colorspace();

    match image.frames_ref().get(0) {
        None => {
            env.throw("No frames in image, did you load an image?").expect("Could not throw an exception");
        }
        Some(frame) => {
            // write it to output
            if let Err(e) = zune_image::utils::swizzle_channels(&frame.channels_ref(colorspace, false), new_buff) {
                env.throw(e.to_string()).expect("Could not throw exception");
            }
        }
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_image_desktop_ZilImageJni_getDepthNative(_env: JNIEnv, _class: JClass, image: jlong) -> jlong {
    let image = { &*(image as *const Image) };
    // nb should match with definition in ZilImage otherwise UB
    return match image.depth() {
        BitDepth::Eight => 1,
        BitDepth::Sixteen => 2,
        BitDepth::Float32 => 3,
        _ => 0
    };
}

#[no_mangle]
pub unsafe extern "system" fn Java_image_desktop_ZilImageJni_getColorSpaceNative(_env: JNIEnv, _class: JClass, image: jlong) -> jlong {
    // nb: should match the bindings in zilimage otherwise :)))
    let image = { &*(image as *const Image) };

    return match image.colorspace() {
        ColorSpace::RGB => 1,
        ColorSpace::RGBA => 2,
        ColorSpace::YCbCr => 3,
        ColorSpace::Luma => 4,
        ColorSpace::LumaA => 5,
        ColorSpace::YCCK => 6,
        ColorSpace::CMYK => 7,
        ColorSpace::BGR => 8,
        ColorSpace::BGRA => 9,
        ColorSpace::ARGB => 10,
        _ => 0
    };
}

fn im_long_to_depth(data: jlong) -> Option<BitDepth> {
    return match data {
        0 => None,
        1 => Some(BitDepth::Eight),
        2 => Some(BitDepth::Sixteen),
        3 => Some(BitDepth::Float32),
        _ => None
    };
}

fn im_long_to_colorspace(data: jlong) -> Option<ColorSpace> {
    match data {
        0 => Some(ColorSpace::Unknown),
        1 => Some(ColorSpace::RGB),
        2 => Some(ColorSpace::RGBA),
        3 => Some(ColorSpace::YCbCr),
        4 => Some(ColorSpace::Luma),
        5 => Some(ColorSpace::LumaA),
        6 => Some(ColorSpace::YCCK),
        7 => Some(ColorSpace::CMYK),
        8 => Some(ColorSpace::BGR),
        9 => Some(ColorSpace::BGRA),
        10 => Some(ColorSpace::ARGB),
        _ => None
    }
}

fn im_long_to_format(data: jlong) -> Option<ImageFormat> {
    match data {
        0 => Some(ImageFormat::Unknown),
        1 => Some(ImageFormat::JPEG),
        2 => Some(ImageFormat::PNG),
        3 => Some(ImageFormat::PPM),
        4 => Some(ImageFormat::PSD),
        5 => Some(ImageFormat::Farbfeld),
        6 => Some(ImageFormat::QOI),
        7 => Some(ImageFormat::JPEG_XL),
        8 => Some(ImageFormat::HDR),
        9 => Some(ImageFormat::BMP),
        _ => None,
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_image_desktop_ZilImageJni_saveToNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong, filename: JString, format: jlong) {
    let img_ptr = image_ptr as *const Image;
    if img_ptr.is_null() {
        env.throw("Image is null").expect("Could not throw exception");
        return;
    }
    let input_str: String = env.get_string(&filename).expect("Could not get input string").into();
    if let Some(format) = im_long_to_format(format) {
        let img = &*img_ptr;
        if let Err(err) = img.save_to(input_str, format) {
            env.throw(err.to_string()).expect("Could not throw exception");
        }
    } else {
        env.throw("Could not determine format specified").expect("Could not throw exception");
    }
}

#[no_mangle]
extern "system" fn Java_image_desktop_ZilImageJni_convertColorSpaceNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong, colorspace: jlong) {
    if let Some(colorspace) = im_long_to_colorspace(colorspace) {
        let filter = ColorspaceConv::new(colorspace);
        exec_imgproc(&mut env, image_ptr, filter)
    } else {
        env.throw("Could not convert colorspace ").expect("Could not throw error");
    }
}


#[no_mangle]
extern "system" fn Java_image_desktop_ZilImageJni_convertDepthNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong, depth: jlong) {
    if let Some(depth) = im_long_to_depth(depth) {
        let filter = Depth::new(depth);
        exec_imgproc(&mut env, image_ptr, filter)
    } else {
        env.throw("Could not convert depth").expect("Could not throw error");
    }
}

#[no_mangle]
extern "system" fn Java_image_desktop_ZilImageJni_stretchContrastNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong, lower: f32, higher: f32) {
    exec_imgproc(&mut env, image_ptr, StretchContrast::new(lower, higher));
}

#[no_mangle]
extern "system" fn Java_image_desktop_ZilImageJni_scharrNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong) {
    exec_imgproc(&mut env, image_ptr, Scharr);
}


#[no_mangle]
extern "system" fn Java_image_desktop_ZilImageJni_sobelNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong) {
    exec_imgproc(&mut env, image_ptr, Sobel);
}

#[no_mangle]
extern "system" fn Java_image_desktop_ZilImageJni_brightenNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong, by: f32) {
    exec_imgproc(&mut env, image_ptr, Brighten::new(by));
}

#[no_mangle]
extern "system" fn Java_image_desktop_ZilImageJni_cloneNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong) -> jlong {
    let image = image_ptr as *mut Image;
    if image.is_null() {
        env.throw("Image is null").expect("Failed to throw exception");
        return 0 as _;
    }
    let image = unsafe { &*image };
    let new_clone = image.clone();

    let c = Box::new(new_clone);
    // convert it to a pointer
    Box::into_raw(c) as jlong
}


#[no_mangle]
extern "system" fn Java_image_desktop_ZilImageJni_transposeNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong) {
    exec_imgproc(&mut env, image_ptr, Transpose);
}


#[no_mangle]
extern "system" fn Java_image_desktop_ZilImageJni_flopNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong) {
    exec_imgproc(&mut env, image_ptr, Flop);
}

#[no_mangle]
extern "system" fn Java_image_desktop_ZilImageJni_flipNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong) {
    exec_imgproc(&mut env, image_ptr, Flip);
}


#[no_mangle]
extern "system" fn Java_image_desktop_ZilImageJni_verticalFlipNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong) {
    exec_imgproc(&mut env, image_ptr, VerticalFlip);
}

#[no_mangle]
extern "system" fn Java_image_desktop_ZilImageJni_boxBlurNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong, radius: jlong) {
    exec_imgproc(&mut env, image_ptr, BoxBlur::new(radius.clamp(0, 10000) as _));
}


#[no_mangle]
extern "system" fn Java_image_desktop_ZilImageJni_gaussianBlurNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong, radius: jlong) {
    exec_imgproc(&mut env, image_ptr, GaussianBlur::new(radius.clamp(0, 10000) as _));
}

#[no_mangle]
extern "system" fn Java_image_desktop_ZilImageJni_histogramNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong, histogram_map: JObject) {
    let histogram = ChannelHistogram::new();
    // exec filter
    let image = image_ptr as *mut Image;
    if image.is_null() {
        env.throw("Image is null").expect("Failed to throw exception");
        return;
    }
    let image = unsafe { &mut *image };

    if let Err(err) = histogram.execute_impl(image) {
        env.throw(err.to_string()).expect("Could not throw exception");
    }

    let map = env.get_map(&histogram_map).expect(" Could not convert ptr into map");
    let histograms = histogram.histogram().expect("Could not get histograms");

    for (c, histo) in histograms.iter().enumerate() {
        let arr = env.new_long_array(histo.len() as _).expect("Could not create array");
        let histo: Vec<i64> = histo.iter().map(|x| *x as i64).collect();
        env.set_long_array_region(&arr, 0, histo.as_ref()).expect("Could not write to array");
        let new_str = env.new_string(c.to_string()).expect("Could not create string");
        map.put(&mut env, &new_str, &arr).expect("Could not write to array");
    }
}

#[no_mangle]
extern "system" fn Java_image_desktop_ZilImageJni_exifMetadataNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong, metadata_map: JObject) {
    // exec filter
    let image = image_ptr as *mut Image;
    if image.is_null() {
        env.throw("Image is null").expect("Failed to throw exception");
        return;
    }
    let image = unsafe { &mut *image };


    let map = env.get_map(&metadata_map).expect(" Could not convert ptr into map");
    if let Some(meta) = image.metadata().exif() {
        for field in meta {
            let key = field.tag.to_string();

            // some tags may have leading quotes yet they
            // are enclosed in a string.
            // This helps remove them
            let value = field
                .display_value()
                .with_unit(field)
                .to_string()
                .trim_start_matches(|x| x == '\"')
                .trim_end_matches(|x| x == '\"')
                .to_string();

            if value.len() < 100 {
                // put small strings, some strings are too huge they mess up everything

                let new_str_k = env.new_string(key).unwrap();
                let new_str_v = env.new_string(value).unwrap();
                map.put(&mut env, &new_str_k, &new_str_v).expect("Could not put the string into map");
            }
        }
    }
}

#[no_mangle]
extern "system" fn Java_image_desktop_ZilImageJni_writeFourChannelToIntArrayNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong, native_ptr: jlong, native_length: jlong, array: JIntArray) {
    let env = &mut env;
    let image = image_ptr as *mut Image;
    if image.is_null() {
        env.throw("Image is null").expect("Failed to throw exception");
        return;
    }
    let image = unsafe { &mut *image };

    let native_ptr = native_ptr as *mut u8;
    let slice = unsafe { std::slice::from_raw_parts_mut(native_ptr, native_length as usize) };
    let colorspace = image.colorspace();
    if colorspace.num_components() != 4 {
        env.throw("The colorspace is not 4 component colorspace").expect("Could not throw exception");
    }
    assert_eq!(colorspace.num_components(), 4);
    // write to our output first

    match image.frames_ref().get(0) {
        None => {
            env.throw("No frames in image, did you load an image?").expect("Could not throw an exception");
        }
        Some(frame) => {
            // write it to output
            if let Err(e) = zune_image::utils::swizzle_channels(&frame.channels_ref(colorspace, false), slice) {
                env.throw(e.to_string()).expect("Could not throw exception");
            }
        }
    }

    // align to i32
    // so this will do it in native endian,assuming the image is BGRA, native endian becomes
    // argb
    let (a, output, c) = unsafe { slice.align_to::<i32>() };
    assert_eq!(a.len(), 0);
    assert_eq!(c.len(), 0);

    // write to native endian
    env.set_int_array_region(&array, 0, output).expect("Not an array region");
}

#[no_mangle]
extern "system" fn Java_image_desktop_ZilImageJni_rotate90Native(mut env: JNIEnv, _class: JClass, image_ptr: jlong) {
    exec_imgproc(&mut env, image_ptr, Rotate::new(90f32))
}


#[no_mangle]
extern "system" fn Java_image_desktop_ZilImageJni_hslAdjustNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong, hue: f32, saturation: f32, lightness: f32) {
    exec_imgproc(&mut env, image_ptr, HsvAdjust::new(hue, saturation, lightness))
}

#[no_mangle]
extern "system" fn Java_image_desktop_ZilImageJni_medianBlurNative(mut env: JNIEnv, _class: JClass, image_ptr: jlong, radius: jlong) {
    exec_imgproc(&mut env, image_ptr, Median::new(radius as _))
}