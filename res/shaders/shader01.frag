varying vec4 vertColor;
uniform vec2 playerCameraCoordinates;
uniform vec2 cameraWindowRatio;
uniform float cameraZoom;

void main() {
    float mixValue;
    vec2 distanceFromPlayerVector = vec2((playerCameraCoordinates.x - gl_FragCoord.x) / cameraWindowRatio.x, (playerCameraCoordinates.y - gl_FragCoord.y) / cameraWindowRatio.y);
    float distanceFromPlayer = length(distanceFromPlayerVector) / cameraZoom;
    float maxDistance = 100;
    float maxValueDistance = 100;

    if (distanceFromPlayer > maxValueDistance) {
        mixValue = (maxDistance - (distanceFromPlayer - maxValueDistance)) / maxDistance;
        if (mixValue < 0) {
            mixValue = 0;
        }
    } else {
        mixValue = distanceFromPlayer / maxValueDistance;
    }

    gl_FragColor = vec4(vertColor.xyz, mixValue);
}