#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;

uniform vec4 fillColor;
uniform float amount;

varying vec4 v_color;
varying vec2 v_texCoord;

void main() {
	vec4 colors = texture2D(u_texture, v_texCoord);

	vec4 finalColor = v_color * colors.r;
	finalColor += fillColor * (1.0 - smoothstep(amount-.05, amount + .05, colors.g));
	finalColor.a = colors.a;
	gl_FragColor = finalColor;
}
