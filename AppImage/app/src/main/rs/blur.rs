#pragma version(1)
#pragma rs java_package_name(com.example.myappimage.algorithm)

rs_allocation gIn;

float* matrix;
uchar4* pixels;
uint32_t size;
float div;

static uint32_t width;
static uint32_t height;

uchar4 RS_KERNEL applyFilter(uchar4 in, uint32_t x, uint32_t y)
{
    if(x < (size / 2) || y < (size / 2)) return in;
    if((x >= width - (size / 2)) || (y >= height - (size / 2))) return in;

    uint8_t i, j;
    float red, green, blue;
    red = green = blue = 0;

    for(i = 0; i < size; i++)
    {
        for(j = 0; j < size; j++)
        {
            uchar4 pixel = pixels[x + (i - size / 2) + width * (y + (j - size / 2))];
            red += pixel.r * (matrix[i + size * j] / div);
            green += pixel.g * (matrix[i + size * j] / div);
            blue += pixel.b * (matrix[i + size * j] / div);
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