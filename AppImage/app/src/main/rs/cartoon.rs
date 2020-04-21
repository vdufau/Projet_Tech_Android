#pragma version(1)
#pragma rs java_package_name(com.example.myappimage.algorithm)

uchar4 RS_KERNEL cartoonize(uchar4 in) {
    uchar4 out = in;
    int red = (out.r - (out.r % 64));
    int green = (out.g - (out.g % 64));
    int blue = (out.b - (out.b % 64));

    if (red > 255) red = 255;
    if (green > 255) green = 255;
    if (blue > 255) blue = 255;
    if (red < 0) red = 0;
    if (green < 0) green = 0;
    if (blue < 0) blue = 0;

    out.r = red;
    out.g = green;
    out.b = blue;

    return out;
}