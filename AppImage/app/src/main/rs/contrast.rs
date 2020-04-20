#pragma version(1)
#pragma rs java_package_name(com.example.myappimage.algorithm)

double multiplier;

uchar4 RS_KERNEL changeContrast(uchar4 in) {
    uchar4 out = in;
    double red = multiplier * (out.r - 128) + 128;;
    double green = multiplier * (out.g - 128) + 128;;
    double blue = multiplier * (out.b - 128) + 128;;

    if (red > 255) red = 255;
    if (green > 255) green = 255;
    if (blue > 255) blue = 255;
    if (red < 0) red = 0;
    if (green < 0) green = 0;
    if (blue < 0) blue = 0;

    out.r = (int)red;
    out.g = (int)green;
    out.b = (int)blue;

    return out;
}