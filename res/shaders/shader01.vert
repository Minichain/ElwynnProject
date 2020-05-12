uniform float time; //Time elapsed since the Game has been executed (in milliseconds)
uniform vec2 lightSource01;
uniform vec2 lightSource02;
uniform vec2 lightSource03;
uniform vec2 lightSources[10];
uniform float zoom;
uniform float widthHeightRatio;

varying vec4 vertColor;
varying out vec2 TexCoord;
void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    TexCoord = gl_MultiTexCoord0;

    //Light source 01
    float distanceFromLightSource01X = abs(lightSource01.x - gl_Position.x) * widthHeightRatio;
    float distanceFromLightSource01Y = abs(lightSource01.y - gl_Position.y);
    float distanceFromLightSource01 = length(vec2(distanceFromLightSource01X, distanceFromLightSource01Y));
    float lightReceivedFromLightSource01 = 1.0 - (distanceFromLightSource01 / zoom) * 1.0;

    //Light source 02
    float distanceFromLightSource02X = abs(lightSource02.x - gl_Position.x) * widthHeightRatio;
    float distanceFromLightSource02Y = abs(lightSource02.y - gl_Position.y);
    float distanceFromLightSource02 = length(vec2(distanceFromLightSource02X, distanceFromLightSource02Y));
    float lightReceivedFromLightSource02 = 1.0 - (distanceFromLightSource02 / zoom) * 1.0;

    //Light source 03
    float distanceFromLightSource03X = abs(lightSource03.x - gl_Position.x) * widthHeightRatio;
    float distanceFromLightSource03Y = abs(lightSource03.y - gl_Position.y);
    float distanceFromLightSource03 = length(vec2(distanceFromLightSource03X, distanceFromLightSource03Y));
    float lightReceivedFromLightSource03 = 1.0 - (distanceFromLightSource03 / zoom) * 1.0;

    float light = max(lightReceivedFromLightSource01, lightReceivedFromLightSource02);
    light = max(light, lightReceivedFromLightSource03);

    light = clamp(light, 0, 1);

    vertColor = vec4(light, light, light, 1.0);
}