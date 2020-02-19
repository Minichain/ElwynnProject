varying vec4 vertColor;
uniform vec2 characterCameraCoordinates;
uniform vec2 cameraWindowRatio;
uniform float cameraZoom;

void main() {
    float mixValue;
    vec2 distanceFromCharacterVector = vec2((characterCameraCoordinates.x - gl_FragCoord.x) / cameraWindowRatio.x, (characterCameraCoordinates.y - gl_FragCoord.y) / cameraWindowRatio.y);
    float distanceFromCharacter = length(distanceFromCharacterVector) / cameraZoom;
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