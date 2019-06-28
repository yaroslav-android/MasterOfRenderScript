#pragma version(1)
#pragma rs java_package_name(yaroslav.ovdiienko.idivision.fragmentstest)
#pragma rs_fp_relaxed

#include "rs_debug.rsh"

const static float3 gMonoMult = {0.299f, 0.587f, 0.114f};

float saturationValue = 0.f;

// rsDebug does not work for some reason :(
uchar4 __attribute__((kernel)) saturation(uchar4 in) {
    float4 f4 = rsUnpackColor8888(in);
    float3 result = dot(f4.rgb, gMonoMult);
    rsDebug("dot before", result);
    result = mix(result, f4.rgb, saturationValue);
    rsDebug("dot after", result);

    return rsPackColorTo8888(result);
}