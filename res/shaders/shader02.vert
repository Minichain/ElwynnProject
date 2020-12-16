#version 120

uniform float time;

varying vec4 vertColor;
varying vec2 TexCoord;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    TexCoord = gl_MultiTexCoord0.xy;
    vertColor = gl_Color;
}