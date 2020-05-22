const int maxLightSources = 100;

uniform vec2 lightSourceCoordinates[maxLightSources];
uniform float lightSourceIntensity[maxLightSources];
uniform vec3 lightSourceColor[maxLightSources];
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
    vec3 light = vec3(0.0);

    for (int i = 0; i < maxLightSources; i++) {
        if (lightSourceIntensity[i] != -1.0) {
            distanceFromLightSourceX = abs(lightSourceCoordinates[i].x - gl_FragCoord.x);
            distanceFromLightSourceY = abs(lightSourceCoordinates[i].y - gl_FragCoord.y);
            distanceFromLightSource = length(vec2(distanceFromLightSourceX, distanceFromLightSourceY)) / 500.0;
            lightReceivedFromLightSource = 1.0 - (distanceFromLightSource / zoom) / lightSourceIntensity[i];
            if (lightReceivedFromLightSource < 0.0) lightReceivedFromLightSource = 0.0;
            light.x += (lightSourceColor[i].x * lightReceivedFromLightSource);
            light.y += (lightSourceColor[i].y * lightReceivedFromLightSource);
            light.z += (lightSourceColor[i].z * lightReceivedFromLightSource);
        }
    }

    light.x = clamp(light.x, gameTimeLight, 1.0);
    light.y = clamp(light.y, gameTimeLight, 1.0);
    light.z = clamp(light.z, gameTimeLight, 1.0);

    gl_FragColor = vertColor * texColor * vec4(light.x, light.y, light.z, 1.0);
}