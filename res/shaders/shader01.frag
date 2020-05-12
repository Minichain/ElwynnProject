uniform float time;
uniform sampler2D texture01;

varying vec4 vertColor;
varying in vec2 TexCoord;

void main() {
    vec4 texColor = texture2D(texture01, TexCoord);
    gl_FragColor = vertColor * texColor;
}