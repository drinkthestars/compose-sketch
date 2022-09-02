package com.drinkstars.composesketch.shaders

import android.graphics.RuntimeShader
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import com.drinkstars.composesketch.sketch.Sketch
import org.intellij.lang.annotations.Language

@Language("AGSL")
private const val BLOBS = """
    uniform float2 iResolution; // Viewport resolution (pixels)
    uniform float iTime; // Shader playback time (s)
    uniform vec4 iMouse; // Mouse drag pos=.xy Click pos=.zw (pixels)
    
    vec4 main( vec2 fragCoord )
    {
      float mx = max(iResolution.x, iResolution.y);
      vec2 uv = fragCoord.xy / mx;
      vec2 center = iResolution.xy / mx * 0.5;
        
      float cDist = distance(center, uv) * 5.0;
      float mDist = distance(iMouse.xy / mx, uv) * 10.0;
      float color = cDist * cDist * mDist;
        
      // Normalized pixel coordinates (from 0 to 1)
      vec2 uvColor = fragCoord/iResolution.xy;
        
      // Time varying pixel color
      vec3 col = 0.7 + 0.1*cos(iTime*2.0+uvColor.xxx*2.0+vec3(1.0,2.0,4.0));
        
      return vec4(color > 1.0, col);
    }
"""

@Language("AGSL")
private const val STARNEST = """
    const int iterations = 17;
    const float formuparam = 0.53;
    
    const int volsteps = 20;
    const float stepsize = 0.1;
    
    const float zoom =   0.800;
    const float tile  =  0.850;
    const float speed  = 0.010 ;
    
    const float brightness = 0.0015;
    const float darkmatter = 0.300;
    const float distfading = 0.730;
    const float saturation  =.850;
    
    uniform float2 iResolution;
    uniform float iTime;
    uniform vec4 iMouse;

    vec4 main(in float2 fragCoord) {
        //get coords and direction
        vec2 uv=fragCoord.xy/iResolution.xy-.5;
        uv.y*=iResolution.y/iResolution.x;
        vec3 dir=vec3(uv*zoom,1.);
        float time=iTime*speed+.25;
        
        //mouse rotation
	    float a1=.5+iMouse.x/iResolution.x*2.;
        float a2=.8+iMouse.y/iResolution.y*2.;
        mat2 rot1=mat2(cos(a1),sin(a1),-sin(a1),cos(a1));
        mat2 rot2=mat2(cos(a2),sin(a2),-sin(a2),cos(a2));
        dir.xz*=rot1;
        dir.xy*=rot2;
        vec3 from=vec3(1.,.5,0.5);
        from+=vec3(time*2.,time,-2.);
        from.xz*=rot1;
        from.xy*=rot2;
        
        //volumetric rendering
        float s=0.1,fade=1.;
        vec3 v=vec3(0.);
        for (int r=0; r<volsteps; r++) {
            vec3 p=from+s*dir*.5;
            p = abs(vec3(tile)-mod(p,vec3(tile*2.))); // tiling fold
            
            float pa,a=pa=0.; // [takahiroando] `pa` would be past `a`, `a` might be average???
            for (int i=0; i<iterations; i++) { 
                p=abs(p)/dot(p,p)-formuparam; // the magic formula
                a+=abs(length(p)-pa); // absolute sum of average change
                pa=length(p);
            }
            float dm=max(0.,darkmatter-a*a*.001); //dark matter
            a*=a*a; // add contrast
            if (r>6) fade*=1.-dm; // dark matter, don't render near
            //v+=vec3(dm,dm*.5,0.);
            v+=fade;
            v+=vec3(s,s*s,s*s*s*s)*a*brightness*fade; // coloring based on distance
            fade*=distfading; // distance fading
            s+=stepsize;
        }
        v=mix(vec3(length(v)),v,saturation); //color adjust
        return vec4(v*.01,1.);		
    }
"""

@Language("AGSL")
private const val FRACTAL = """
    uniform float2 iResolution;
    uniform float iTime;

    vec3 palette(float d){
        return mix(vec3(0.2,0.7,0.9),vec3(1.,0.,1.),d);
    }
    
    vec2 rotate(vec2 p,float a){
        float c = cos(a);
        float s = sin(a);
        return p*mat2(c,s,-s,c);
    }
    
    float map(vec3 p){
        for( int i = 0; i<8; ++i){
            float t = iTime*0.2;
            p.xz =rotate(p.xz,t);
            p.xy =rotate(p.xy,t*1.89);
            p.xz = abs(p.xz);
            p.xz-=.5;
        }
        return dot(sign(p),p)/5.;
    }
    
    vec4 rm (vec3 ro, vec3 rd){
        float t = 0.;
        vec3 col = vec3(0.);
        float d;
        for(float i =0.; i<64.; i++){
            vec3 p = ro + rd*t;
            d = map(p)*.5;
            if(d<0.02){
                break;
            }
            if(d>100.){
                break;
            }
            //col+=vec3(0.6,0.8,0.8)/(400.*(d));
            col+=palette(length(p)*.1)/(400.*(d));
            t+=d;
        }
        return vec4(col,1./(d*100.));
    }
    vec4 main( in float2 fragCoord )
    {
        vec2 uv = (fragCoord-(iResolution.xy/2.))/iResolution.x;
        vec3 ro = vec3(0.,0.,-50.);
        ro.xz = rotate(ro.xz,iTime);
        vec3 cf = normalize(-ro);
        vec3 cs = normalize(cross(cf,vec3(0.,1.,0.)));
        vec3 cu = normalize(cross(cf,cs));
        
        vec3 uuv = ro+cf*3. + uv.x*cs + uv.y*cu;
        
        vec3 rd = normalize(uuv-ro);
        
        vec4 col = rm(ro,rd);
        
        
        return col;
    }
    
    /** SHADERDATA
    {
        "title": "fractal pyramid",
        "description": "",
        "model": "car"
    }
    */
"""

@Language("AGSL")
private val NEBULA = """
    const int iterations = 4;
    const float formuparam2 = 0.89;
     
    const int  volsteps= 10;
    const float  stepsize = 0.190;
     
    const float zoom = 3.900;
    const float  tile =   0.450;
    const float speed2 =  0.010;
     
    const float  brightness = 0.2;
    const float  darkmatter = 0.400;
    const float  distfading = 0.560;
    const float  saturation = 0.400;
    
    const float  transverseSpeed = 1.1;
    const float  cloud = 0.2;
    
    uniform float2 iResolution;
    uniform float iTime;
     
    float triangle(float x, float a) {
        float output2 = 2.0 * abs(3.0 * ((x / a) - floor((x / a) + 0.5))) - 1.0;
        return output2;
    }
     
    
    float field(in vec3 p) {
        
        float strength = 7.0 + 0.03 * log(1.e-6 + fract(sin(iTime) * 4373.11));
        float accum = 0.;
        float prev = 0.;
        float tw = 0.;
        
    
        for (int i = 0; i < 6; ++i) {
            float mag = dot(p, p);
            p = abs(p) / mag + vec3(-.5, -.8 + 0.1 * sin(iTime * 0.2 + 2.0), -1.1 + 0.3 * cos(iTime * 0.15));
            float w = exp(-float(i) / 7.);
            accum += w * exp(-strength * pow(abs(mag - prev), 2.3));
            tw += w;
            prev = mag;
        }
        return max(0., 5. * accum / tw - .7);
    }
    
    
    vec4 main( vec2 fragCoord )
    {
       
      vec2 uv2 = 2. * fragCoord.xy / iResolution.xy - 1.;
      vec2 uvs = uv2 * iResolution.xy / max(iResolution.x, iResolution.y);
        
        float time2 = iTime;
                   
      float speed = speed2;
      speed = 0.005 * cos(time2*0.02 + 3.1415926/4.0);
        float formuparam = formuparam2;
        //get coords and direction
        vec2 uv = uvs;
        //mouse rotation
        float a_xz = 0.9;
        float a_yz = -.6;
        float a_xy = 0.9 + iTime*0.04;
        
        
        mat2 rot_xz = mat2(cos(a_xz),sin(a_xz),-sin(a_xz),cos(a_xz));
        
        mat2 rot_yz = mat2(cos(a_yz),sin(a_yz),-sin(a_yz),cos(a_yz));
            
        mat2 rot_xy = mat2(cos(a_xy),sin(a_xy),-sin(a_xy),cos(a_xy));
        
    
        float v2 =1.0;
        
        vec3 dir=vec3(uv*zoom,1.);
     
        vec3 from=vec3(0.0, 0.0,0.0);
     
                                   
            from.x -= 5.0* (0.5);
            from.y -= 5.0* (0.5);
                   
                   
        vec3 forward = vec3(0.,0.,1.);
                   
    
        from.x += transverseSpeed*(1.0)*cos(0.01*iTime) + 0.001*iTime;
            from.y += transverseSpeed*(1.0)*sin(0.01*iTime) +0.001*iTime;
        
        from.z += 0.003*iTime;
        
        
        dir.xy*=rot_xy;
        forward.xy *= rot_xy;
    
        dir.xz*=rot_xz;
        forward.xz *= rot_xz;
            
        
        dir.yz*= rot_yz;
        forward.yz *= rot_yz;
         
    
        
        from.xy*=-rot_xy;
        from.xz*=rot_xz;
        from.yz*= rot_yz;
         
        
        //zoom
        float zooom = (time2-3311.)*speed;
        from += forward* zooom;
        float sampleShift = mod( zooom, stepsize );
         
        float zoffset = -sampleShift;
        sampleShift /= stepsize; // make from 0 to 1
    
    
        
        //volumetric rendering
        float s=0.24;
        float s3 = s + stepsize/2.0;
        vec3 v=vec3(0.);
        float t3 = 0.0;
        
        
        vec3 backCol2 = vec3(0.);
        for (int r=0; r<volsteps; r++) {
            vec3 p2=from+(s+zoffset)*dir;// + vec3(0.,0.,zoffset);
            vec3 p3=(from+(s3+zoffset)*dir )* (1.9/zoom);// + vec3(0.,0.,zoffset);
            
            p2 = abs(vec3(tile)-mod(p2,vec3(tile*2.))); // tiling fold
            p3 = abs(vec3(tile)-mod(p3,vec3(tile*2.))); // tiling fold
            
        t3 = field(p3);
            float pa,a=pa=0.;
            for (int i=0; i<iterations; i++) {
                p2=abs(p2)/dot(p2,p2)-formuparam; // the magic formula
                //p=abs(p)/max(dot(p,p),0.005)-formuparam; // another interesting way to reduce noise
                float D = abs(length(p2)-pa); // absolute sum of average change
                
                if (i > 2)
                {
                a += i > 7 ? min( 12., D) : D;
                }
                    pa=length(p2);
            }
            
            //float dm=max(0.,darkmatter-a*a*.001); //dark matter
            a*=a*a; // add contrast
            //if (r>3) fade*=1.-dm; // dark matter, don't render near
            // brightens stuff up a bit
            float s1 = s+zoffset;
            // need closed form expression for this, now that we shift samples
            float fade = pow(distfading,max(0.,float(r)-sampleShift));
                    
            v+=fade;
    
            // fade out samples as they approach the camera
            if( r == 0 )
                fade *= (1. - (sampleShift));
            // fade in samples as they approach from the distance
            if( r == volsteps-1 )
                fade *= sampleShift;
            v+=vec3(s1,s1*s1,s1*s1*s1*s1)*a*brightness*fade; // coloring based on distance
            
            backCol2 += mix(.4, 1., v2) * vec3(0.20 * t3 * t3 * t3, 0.4 * t3 * t3, t3 * 0.7) * fade;
    
            
            s+=stepsize;
            s3 += stepsize;
            
        }
                   
        v=mix(vec3(length(v)),v,saturation);
        vec4 forCol2 = vec4(v*.01,1.);
        
        
        backCol2 *= cloud;
    
        
        return forCol2 + vec4(backCol2, 1.0);
    }
    
    
    /*void mainImage( out vec4 fragColor, in vec2 fragCoord )
    {
        vec2 uv = fragCoord.xy / iResolution.xy;
        fragColor = vec4(uv,0.5+0.5*sin(iTime),1.0);
    }*/
""".trimIndent()

@Language("AGSL")
private val FIB_SPHERE =  """
    // Source: @XorDev https://twitter.com/XorDev/status/1475524322785640455
    
   uniform float2 iResolution;
   uniform float iTime;

   vec4 main(vec2 FC) {
      vec4 o = vec4(0);
      vec2 p = vec2(0), c=p, u=FC.xy*2.-iResolution.xy;
      float a;
      for (float i=0; i<4e2; i++) {
        a = i/2e2-1.;
        p = cos(i*2.4+iTime+vec2(0,11))*sqrt(1.-a*a);
        c = u/iResolution.y+vec2(p.x,a)/(p.y+2.);
        o += (cos(i+vec4(0,2,4,0))+1.)/dot(c,c)*(1.-p.y)/3e4;
      }
      return o;
    }
"""

@Language("AGSL")
private val GRADIENT_SLIDER =  """    
   uniform float2 iResolution;
   uniform float iTime;
   uniform vec4 iMouse;

    vec4 main( vec2 fragCoord )
    {
        // Normalized pixel coordinates (from 0 to 1)
        vec2 uv = fragCoord/iResolution.xy;
        float mouseY = iMouse.x/iResolution.x * 0.5;
    
    
        // Time varying pixel color
        vec3 col = 0.7 + 0.2*cos(iTime+uv.xxx+vec3(1,2,4)) + mouseY;
    
        // Output to screen
        return vec4(col,1.0);
    }
""".trimIndent()

val gradientSliderShader = RuntimeShader(GRADIENT_SLIDER)
val gradientSliderBrush = ShaderBrush(gradientSliderShader)
@Composable
fun GradientSliderShader() {
    Box(modifier = Modifier
        .fillMaxSize(), contentAlignment = Alignment.Center) {
        RuntimeShaderSketch(
            shader = gradientSliderShader,
            brush = gradientSliderBrush,
            modifier = Modifier
                .fillMaxSize(0.8f)
        )
    }
}

val fibSpShader = RuntimeShader(FIB_SPHERE)
val fibSpBrush = ShaderBrush(fibSpShader)
@Composable
fun FibSphereShader() {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.DarkGray), contentAlignment = Alignment.Center) {
        RuntimeShaderSketch(
            shader = fibSpShader,
            brush = fibSpBrush,
            modifier = Modifier
                .fillMaxSize(0.7f)
                .aspectRatio(1f)
        )
    }
}

val nebulaShader = RuntimeShader(NEBULA)
val nebulaBrush = ShaderBrush(nebulaShader)
@Composable
fun NebulaShader() {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.DarkGray), contentAlignment = Alignment.Center) {
        RuntimeShaderSketch(
            shader = nebulaShader,
            brush = nebulaBrush,
            modifier = Modifier
                .fillMaxSize(0.7f)
        )
    }
}

val blobsShader = RuntimeShader(BLOBS)
val blobsBrush = ShaderBrush(blobsShader)
@Composable
fun BlobsShader() {
    RuntimeShaderSketch(
        shader = blobsShader,
        brush = blobsBrush,
        modifier = Modifier.fillMaxSize()
    )
}

val starNest = RuntimeShader(STARNEST)
val starNestBrush = ShaderBrush(starNest)
@Composable
fun StarNestShader() {
    RuntimeShaderSketch(
        shader = starNest,
        brush = starNestBrush,
        modifier = Modifier.fillMaxSize()
    )
}

val fractalShader = RuntimeShader(FRACTAL)
val fractalBrush = ShaderBrush(fractalShader)
@Composable
fun FractalShader() {
    RuntimeShaderSketch(
        shader = fractalShader,
        brush = fractalBrush,
        modifier = Modifier
            .fillMaxSize(0.9f)
            .background(Color.DarkGray)
    )
}

@Composable
private fun RuntimeShaderSketch(
    modifier: Modifier = Modifier,
    shader: RuntimeShader,
    brush: ShaderBrush,
    speed: Float = 1f
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var size by remember { mutableStateOf(Size.Zero) }

    Sketch(
        modifier = modifier
//            .background(Color.DarkGray)
            .onSizeChanged {
                size = Size(it.width.toFloat(), it.height.toFloat())
            }
            .pointerInput(Unit) {
                detectTapGestures {
                    offset = Offset(it.x, it.y)
                }
                detectDragGestures(
                    onDrag = { _, dragAmount ->
                        val summedX = offset.x + dragAmount.x
                        val summedY = offset.y + dragAmount.y
                        val newValueY = summedY.coerceIn(0f, size.height)
                        val newValueX = summedX.coerceIn(0f, size.width)
                        offset = Offset(newValueX, newValueY)
                    }
                )
            },
        speed = speed,
        showControls = false,
        onDraw = { value ->
            shader.setFloatUniform(
                "iResolution",
                size.width, size.height
            )

            shader.setFloatUniform("iTime", value)
            shader.setFloatUniform("iMouse", offset.x, offset.y, 0f, 0f)
            drawRect(brush)
        }
    )
}
