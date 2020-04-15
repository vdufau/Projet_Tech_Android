#pragma version(1)
#pragma rs java_package_name(com.example.myappimage.algorithm)

uchar4 RS_KERNEL randomSnow(uchar4 in) {
    uchar4 out = in;
    int r = rsRand(255);
    if (in.r > r && in.g > r && in.b > r) {
         out.r = 255;
         out.g = 255;
         out.b = 255;
    }
    return out;
}