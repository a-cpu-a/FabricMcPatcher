#version 150

uniform float sun;
uniform float torch;
uniform float nightVisionStrength;
uniform float gamma;
uniform int customNightvision;
uniform float heightR;

uniform sampler2D origMap;

in vec2 texCoord;

out vec4 fragColor;

vec3 notGamma(vec3 x) {
    vec3 nx = 1.0 - x;
	nx = 1.0 - nx * nx * nx * nx;
	return mix(x,nx,gamma);//gamma * nx + (1.0 - gamma) * x;
}

float getSampleY(float yC,float _heightR) {
	return yC*_heightR;
}


void main() {
	
	vec3 sunrgb = texture(origMap,vec2(sun,getSampleY(texCoord.y,heightR))).rgb;
	vec3 torchrgb = texture(origMap,vec2(torch,getSampleY(texCoord.x+1.0,heightR))).rgb;
	
	
	//S=X,T=Y
	vec3 color = min(vec3(1),sunrgb+torchrgb);
	
	
	if (nightVisionStrength > 0.0) {
		if(customNightvision==1) {
			vec3 sunrgbnv = texture(origMap,vec2(sun,getSampleY(texCoord.y+2.0,0.25))).rgb;
			vec3 torchrgbnv = texture(origMap,vec2(torch,getSampleY(texCoord.x+3.0,0.25))).rgb;
			color = min(vec3(1),mix(color,sunrgbnv+torchrgbnv,nightVisionStrength));
			//color = min(vec3(1),(1.0-nightVisionStrength)*color+nightVisionStrength*(sunrgbnv+torchrgbnv));
		} else {
			float nightVisionMultiplier = max(max(color.x, color.y), color.z);
			if(nightVisionMultiplier>0.0) {
				color *= 1.0-nightVisionStrength + nightVisionStrength/nightVisionMultiplier;
			}
		}
	}
	
	if (gamma != 0.0) {
		color = notGamma(color);
	}

	fragColor = vec4(color, 1.0);
}
