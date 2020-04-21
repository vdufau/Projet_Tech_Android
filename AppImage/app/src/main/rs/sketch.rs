#pragma version(1)
#pragma rs java_package_name(com.example.myappimage.algorithm)

rs_allocation gIn;

uchar4* copy;
uchar4* invert;
int choice;

static uint32_t width;
static uint32_t height;

uchar4 RS_KERNEL sketch(uchar4 in, uint32_t x, uint32_t y) {
    uchar4 out = in;

    uchar4 pixelGray = copy[x + width * y];
    int grayCopy = (0.3 * pixelGray.r + 0.59 * pixelGray.g + 0.11 * pixelGray.b);
    uchar4 pixelInvert = invert[x + width * y];
    int grayInvert = (0.3 * pixelInvert.r + 0.59 * pixelInvert.g + 0.11 * pixelInvert.b);

    int newPixel;

    if (grayInvert == 255) {
        newPixel = grayInvert;
    } else {
        newPixel = min(255, ((grayCopy * 256) / (255 - grayInvert)));
    }

    for (int j = 0; j < choice; j++) {
        newPixel = newPixel * newPixel / 255;
    }

    out.r = newPixel;
    out.g = newPixel;
    out.b = newPixel;

    return out;
}

void setup()
{
    width = rsAllocationGetDimX(gIn);
    height = rsAllocationGetDimY(gIn);
}