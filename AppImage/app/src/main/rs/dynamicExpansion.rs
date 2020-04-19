#pragma version(1)
#pragma rs java_package_name(com.example.myappimage.algorithm)

int32_t LUTValue[101];
int maxValue;
int minValue;

void init() {
    maxValue = 0;
    minValue = 100;
    for (int i = 0; i < 101; i++) {
        LUTValue[i] = 0;
    }
}

void RS_KERNEL minMax(uchar4 in) {
    float4 pixelf = rsUnpackColor8888(in);

    float maximum = max(pixelf.r, max(pixelf.g, pixelf.b));
    float v = maximum * 100;
    maxValue = max(maxValue, (int)v);
    minValue = min(minValue, (int)v);
}

void createLUTExpanded() {
    for (int i = 0; i < 101; i++) {
        LUTValue[i] = 100 * (i - minValue) / (maxValue - minValue);
    }
}

uchar4 RS_KERNEL expansion(uchar4 in) {
    float4 pixelf = rsUnpackColor8888(in);

    float maximum = max(pixelf.r, max(pixelf.g, pixelf.b));
    float minimum = min(pixelf.r, min(pixelf.g, pixelf.b));
    float d = maximum - minimum;
    float h, s, v;

    if (maximum == minimum) {
        h = 0;
    } else if (maximum == pixelf.r) {
        h = fmod((60 * ((pixelf.g - pixelf.b) / d) + 360), 360);
    } else if (maximum == pixelf.g) {
        h = fmod((60 * ((pixelf.b - pixelf.r) / d) + 120), 360);
    } else {
        h = fmod((60 * ((pixelf.r - pixelf.g) / d) + 240), 360);
    }

    if (maximum == 0) {
        s = 0;
    } else {
        s = d / maximum;
    }

    v = maximum;
    float newV = LUTValue[(int)(v * 100)] / 100.0;

    float c = newV * s;
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

    float m = newV - c;
    pixelf.r += m;
    pixelf.g += m;
    pixelf.b += m;

    return rsPackColorTo8888(pixelf.r, pixelf.g, pixelf.b, pixelf.a);
}