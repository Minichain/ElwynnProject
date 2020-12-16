#version 120

uniform float time; //Time elapsed since the Game has been executed (in milliseconds)
uniform float rainingIntensity;

varying vec4 vertColor;
varying vec2 TexCoord;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    TexCoord = gl_MultiTexCoord0.xy;
    vertColor = vec4(gl_Color.x - (0.4 * rainingIntensity), gl_Color.y - (0.4 * rainingIntensity), gl_Color.z - (0.15 * rainingIntensity), gl_Color.w);
}