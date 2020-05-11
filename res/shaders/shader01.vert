uniform float time;
varying vec4 vertColor;
varying out vec2 TexCoord;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    TexCoord = gl_MultiTexCoord0;
    float light = 1f - distance(vec2(0, 0), gl_Position.xy);
    vertColor = vec4(light, light, light, 1.0);
}