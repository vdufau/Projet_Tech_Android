#pragma version(1)
#pragma rs java_package_name(com.example.myappimage.algorithm)

int color;

uchar4 RS_KERNEL colorize(uchar4 in) {
    const float4 pixelf = rsUnpackColor8888(in);

    float maximum = max(pixelf.r, max(pixelf.g, pixelf.b));
    float minimum = min(pixelf.r, min(pixelf.g, pixelf.b));
    float d = maximum - minimum;
    float h, s, v;

    h = color;

    if (maximum == 0) {
        s = 0;
    } else {
        s = d / maximum;
    }

    v = maximum;

    float c = v * s;
    float H = h / 60.0;
    float x = c * (1 - fabs(fmod(H, 2) - 1));

    if (H >= 0 && H < 1 ) {
        pixelf.r = c;
        pixelf.g = x;
        pixelf.b = 0;
    } else if (H >= 1 && H < 2) {
        pixelf.r = x;
        pixelf.g = c;
        pixelf.b = 0;
    } else if (H >= 2 && H < 3) {
        pixelf.r = 0;
        pixelf.g = c;
        pixelf.b = x;
    } else if (H >= 3 && H < 4) {
        pixelf.r = 0;
        pixelf.g = x;
        pixelf.b = c;
    } else if (H >= 4 && H < 5) {
        pixelf.r = x;
        pixelf.g = 0;
        pixelf.b = c;
    } else {
        pixelf.r = c;
        pixelf.g = 0;
        pixelf.b = x;
    }

    float m = v - c;
    pixelf.r += m;
    pixelf.g += m;
    pixelf.b += m;

    return rsPackColorTo8888(pixelf.r, pixelf.g, pixelf.b, pixelf.a);
}

