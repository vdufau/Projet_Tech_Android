#pragma version(1)
#pragma rs java_package_name(com.example.myappimage.algorithm)

int32_t histV[101];
float histVC[101];
int size;

void init() {
    for (int i = 0; i < 101; i++) {
        histV[i] = 0;
        histVC[i] = 0.0f;
    }
}

void RS_KERNEL incHisto(uchar4 in) {
    const float4 pixelf = rsUnpackColor8888(in);

    float maximum = max(pixelf.r, max(pixelf.g, pixelf.b));
    int v = (int)(maximum * 100);
    rsAtomicInc(&histV[v]);
}

void createHistoCumul() {
    histVC[0] = histV[0];
    for (int i = 1; i < 101; i++) {
        histVC[i] = histVC[i - 1] + histV[i];
    }
}

uchar4 RS_KERNEL equalization(uchar4 in) {
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
    float newV = histVC[(int)(v * 100)] / size;

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