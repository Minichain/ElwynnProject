varying vec4 vertColor;
uniform float time;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    float r = (0.5 * sin(time / 3000.0)) + 0.5;
    float g = (0.5 * sin(time / 1000.0)) + 0.5;
    float b = (0.5 * sin(time / 500.0)) + 0.5;
    vertColor = vec4(r, g, b, 0.5);
}