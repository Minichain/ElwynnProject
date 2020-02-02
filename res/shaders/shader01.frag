varying vec4 vertColor;
uniform vec2 characterLocalCoordinates;

void main() {
    float mixValue;
    float distanceFromCharacter = distance(characterLocalCoordinates, gl_FragCoord.xy);
    float maxDistance = 500;
    float maxValueDistance = 200;

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