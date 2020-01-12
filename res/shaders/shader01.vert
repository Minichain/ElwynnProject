varying vec4 vertColor;
uniform float time;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    vertColor = vec4(0.0, (0.5 * sin(time / 1000.0)) + 0.5, 0.0, 0.5);
}