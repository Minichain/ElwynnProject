const int maxLightSources = 256;

uniform vec3 lightSources[maxLightSources];
uniform float zoom;
uniform float gameTimeLight;
uniform float time; //Time elapsed since the Game has been executed (in milliseconds)
uniform float windowWidth;
uniform float windowHeight;
uniform sampler2D texture01;

varying vec4 vertColor;
varying in vec2 TexCoord;

void main() {
    vec4 texColor = texture2D(texture01, TexCoord);
    float distanceFromLightSourceX;
    float distanceFromLightSourceY;
    float distanceFromLightSource;
    float lightReceivedFromLightSource;
    float light = 0.0;
    for (int i = 0; i < maxLightSources; i++) {
        if (lightSources[i].x != -10000) {
            distanceFromLightSourceX = abs(lightSources[i].x - gl_FragCoord.x);
            distanceFromLightSourceY = abs(lightSources[i].y - gl_FragCoord.y);
            distanceFromLightSource = length(vec2(distanceFromLightSourceX, distanceFromLightSourceY)) / 500;
            lightReceivedFromLightSource = 1.0 - (distanceFromLightSource / zoom) / lightSources[i].z;
            if (lightReceivedFromLightSource < 0.0) lightReceivedFromLightSource = 0.0;
            light += lightReceivedFromLightSource;
        }
    }

    light = clamp(light, gameTimeLight, 1);

    gl_FragColor = vertColor * texColor * vec4(light, light, light, 1.0);
}