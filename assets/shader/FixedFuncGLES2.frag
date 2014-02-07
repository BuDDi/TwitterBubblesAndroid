#ifdef SHADER_API_OGLES20
precision mediump float;
#endif
uniform vec4 vColor;
void main() {
  gl_FragColor = vColor;
}