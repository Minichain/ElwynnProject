uniform float time; //Time elapsed since the Game has been executed (in milliseconds)
uniform vec3 lightSources[64];
uniform float zoom;
uniform float widthHeightRatio;
uniform float gameTimeLight;

varying vec4 vertColor;
varying out vec2 TexCoord;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    TexCoord = gl_MultiTexCoord0;

    float distanceFromLightSourceX;
    float distanceFromLightSourceY;
    float distanceFromLightSource;
    float lightReceivedFromLightSource;
    float light = 0.0;
    for (int i = 0; i < 64; i++) {
        if (lightSources[i].x != -10000) {
            distanceFromLightSourceX = abs(lightSources[i].x - gl_Position.x) * widthHeightRatio;
            distanceFromLightSourceY = abs(lightSources[i].y - gl_Position.y);
            distanceFromLightSource = length(vec2(distanceFromLightSourceX, distanceFromLightSourceY));
            lightReceivedFromLightSource = 1.0 - (distanceFromLightSource / zoom) / lightSources[i].z;
            light += clamp(lightReceivedFromLightSource, gameTimeLight, 1);
        }
    }

    light = clamp(light, 0, 1);

    vertColor = vec4(light, light, light, 1.0);
}