#pragma version(1)
#pragma rs java_package_name(com.example.myappimage.algorithm)

static const float4 weight = {0.299f, 0.587f, 0.114f, 0.0f};
int color;
int inter;
int intervalLeft;
int intervalRight;

uchar4 RS_KERNEL keepColor(uchar4 in) {
    const float4 pixelf = rsUnpackColor8888(in);

    float maximum = max(pixelf.r, max(pixelf.g, pixelf.b));
    float minimum = min(pixelf.r, min(pixelf.g, pixelf.b));
    float d = maximum - minimum;
    float h;

    if (maximum == minimum) {
        h = 0;
    } else if (maximum == pixelf.r) {
       h = (pixelf.g - pixelf.b) / d + (pixelf.g < pixelf.b ? 6 : 0);
    } else if (maximum == pixelf.g) {
        h = (pixelf.b - pixelf.r) / d + 2;
    } else {
        h = (pixelf.r - pixelf.g) / d + 4;
    }

    if (inter == 0) {
        if (h * 60 < intervalLeft || intervalRight < h * 60) {
            const float gray = dot(pixelf, weight);
            return rsPackColorTo8888(gray, gray, gray, pixelf.a);
         }
    } else {
        if (h * 60 > intervalLeft && intervalRight > h * 60) {
            const float gray = dot(pixelf, weight);
            return rsPackColorTo8888(gray, gray, gray, pixelf.a);
        }
    }

    return rsPackColorTo8888(pixelf.r, pixelf.g, pixelf.b, pixelf.a);
}