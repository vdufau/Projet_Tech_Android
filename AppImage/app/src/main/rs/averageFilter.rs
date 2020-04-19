#pragma version(1)
#pragma rs java_package_name(com.example.myappimage.algorithm)

rs_allocation gIn;

float* kmatrix;
uchar4* kpixels;
uint32_t ksize;
float kdiv;

static uint32_t width;
static uint32_t height;

uchar4 RS_KERNEL root(uchar4 in, uint32_t x, uint32_t y)
{
    if(x < (ksize / 2) || y < (ksize / 2)) return in;
    if((x >= width - (ksize / 2)) || (y >= height - (ksize / 2))) return in;

    uint8_t kx, ky;
    float red, green, blue;
    red = green = blue = 0;

    for(kx = 0; kx < ksize; kx++)
    {
        for(ky = 0; ky < ksize; ky++)
        {
            uchar4 pixel = kpixels[x + (kx - ksize / 2) + width * (y + (ky - ksize / 2))];
            red += pixel.r * (kmatrix[kx + ksize * ky] / kdiv);
            green += pixel.g * (kmatrix[kx + ksize * ky] / kdiv);
            blue += pixel.b * (kmatrix[kx + ksize * ky] / kdiv);
        }
    }

    if (red < 0) red = 0;
    if (red > 255) red = 255;
    if (green < 0) green = 0;
    if (green > 255) green = 255;
    if (blue < 0) blue = 0;
    if (blue > 255) blue = 255;

    uchar4 out = in;
    out.r = red;
    out.g = green;
    out.b = blue;

    return out;
}

void setup()
{
    width = rsAllocationGetDimX(gIn);
    height = rsAllocationGetDimY(gIn);
}