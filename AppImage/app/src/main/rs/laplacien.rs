#pragma version(1)
#pragma rs java_package_name(com.example.myappimage.algorithm)

rs_allocation gIn;

float* laplacien;
uchar4* pixels;
uint32_t size;

static uint32_t width;
static uint32_t height;

static const float4 weight = {0.299f, 0.587f, 0.114f, 0.0f};

uchar4 RS_KERNEL applyLaplacien(uchar4 in, uint32_t x, uint32_t y)
{
    if(x < (size / 2) || y < (size / 2)) return in;
    if((x >= width - (size / 2)) || (y >= height - (size / 2))) return in;

    uint8_t i, j;
    float newPixel = 0.0f;

    for(i = 0; i < size; i++)
    {
        for(j = 0; j < size; j++)
        {
            uchar4 pixel = pixels[x + (i - size / 2) + width * (y + (j - size / 2))];
            int gray = (0.3 * pixel.r + 0.59 * pixel.g + 0.11 * pixel.b);

            newPixel += gray * laplacien[i + size * j];
        }
    }

    if (newPixel > 255) newPixel = 255;
    if (newPixel < 0) newPixel = 0;

    uchar4 out = in;
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