#version 120

uniform float time; //Time elapsed since the Game has been executed (in milliseconds)
uniform vec3 environmentLight;

varying vec4 vertColor;
varying vec2 TexCoord;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    TexCoord = gl_MultiTexCoord0.xy;
    vertColor = vec4(gl_Color.x, gl_Color.y, gl_Color.z, gl_Color.w);
}