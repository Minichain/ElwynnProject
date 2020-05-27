const int maxLightSources = 150;

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
    float distanceFromLightSource;
    vec3 light = vec3(0.0);
    float lightReceivedFromLightSource;

    for (int i = 0; i < maxLightSources; i++) {
        if (lightSourceIntensity[i] != -1.0) {
            distanceFromLightSource = length(vec2(abs(lightSourceCoordinates[i].x - gl_FragCoord.x), abs(lightSourceCoordinates[i].y - gl_FragCoord.y)));

            //Linear light attenuation
            lightReceivedFromLightSource = (distanceFromLightSource / zoom) / lightSourceIntensity[i];

            if (lightReceivedFromLightSource > 0.0) {
                //Inverse square (real light attenuation behaviour)
                lightReceivedFromLightSource = 1.0 / (pow(lightReceivedFromLightSource + 1.0, 2.0));

                light.x += (lightSourceColor[i].x * lightReceivedFromLightSource);
                light.y += (lightSourceColor[i].y * lightReceivedFromLightSource);
                light.z += (lightSourceColor[i].z * lightReceivedFromLightSource);
            }
        }
    }

    light += vec3(gameTimeLight);

    gl_FragColor = vertColor * texColor * vec4(light.x, light.y, light.z, 1.0);
}