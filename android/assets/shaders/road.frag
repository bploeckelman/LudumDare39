#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform sampler2D u_texture2;
uniform sampler2D u_texture3;
uniform sampler2D u_texture4;
uniform vec2 u_camera;

varying vec4 v_color;
varying vec2 v_texCoord;
varying vec4 v_worldCoord;


void main() {
    vec2 textureCoord = v_texCoord + u_camera;
	vec4 grass = texture2D(u_texture2, (textureCoord) * 4.);
	vec4 road = texture2D(u_texture3, (textureCoord) * 8.);
	vec4 gravel = texture2D(u_texture4, (textureCoord) * 4.);

	vec4 c = texture2D(u_texture, v_texCoord);
//	c.a = a;
    float roadWeight = max((c.r - .75), 0.) * 4.;
    float grassWeight = 1.0 - c.r;

    float gravelWeight = c.r - roadWeight;
	gl_FragColor = grass * grassWeight + road * roadWeight + gravel * gravelWeight;
}

