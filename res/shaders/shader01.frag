varying vec4 vertColor;
uniform vec2 characterCameraCoordinates;
uniform float cameraZoom;

void main() {
    float mixValue;
    float distanceFromCharacter = distance(characterCameraCoordinates, gl_FragCoord.xy) / cameraZoom;
    float maxDistance = 100;
    float maxValueDistance = 100;

    if (distanceFromCharacter > maxValueDistance) {
        mixValue = (maxDistance - (distanceFromCharacter - maxValueDistance)) / maxDistance;
        if (mixValue < 0) {
            mixValue = 0;
        }
    } else {
        mixValue = distanceFromCharacter / maxValueDistance;
    }

    gl_FragColor = vec4(vertColor.xyz, mixValue);
}