uniform float time; //Time elapsed since the Game has been executed (in milliseconds)
uniform float rainingIntensity;

varying vec4 vertColor;
varying vec2 TexCoord;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    TexCoord = gl_MultiTexCoord0.xy;
    vertColor = vec4(1.0 - (0.4 * rainingIntensity), 1.0 - (0.4 * rainingIntensity), 1.0 - (0.15 * rainingIntensity), 1.0);
}