#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform sampler2D u_texture2;
uniform sampler2D u_texture3;
uniform vec2 u_camera;

varying vec4 v_color;
varying vec2 v_texCoord;
varying vec4 v_worldCoord;


void main() {
    vec2 textureCoord = v_texCoord + u_camera;
	vec4 grass = texture2D(u_texture2, (textureCoord) * 4.);
	vec4 road = texture2D(u_texture3, (textureCoord) * 8.);
	vec4 c = texture2D(u_texture, v_texCoord);
//	c.a = a;

	gl_FragColor = mix(grass, road, c.r);
}

