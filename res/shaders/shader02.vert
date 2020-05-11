uniform float time;
varying vec4 vertColor;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    vertColor = vec4(1f, 1f, 1f, 1f);
}