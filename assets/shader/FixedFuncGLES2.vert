uniform mat4 mvpMatrix; // constant model view projection matrix
attribute vec4 vPosition; // per vertex normal
attribute vec4 vNormal; // per vertex position
void main() {
    gl_Position = vPosition * mvpMatrix;
}