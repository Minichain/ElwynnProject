varying vec4 vertColor;
uniform float time;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    float intensity = (0.10 * sin(time / 100.0) * sin(time / 25.0)) + 0.90;
    float r = intensity * 1.0;
    float g = intensity * 1.0;
    float b = intensity * 0.8;
    vertColor = vec4(r, g, b, 0.5);
}