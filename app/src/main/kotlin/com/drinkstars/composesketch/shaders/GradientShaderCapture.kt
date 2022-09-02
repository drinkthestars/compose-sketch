package com.drinkstars.composesketch.shaders

import android.graphics.RuntimeShader
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.toAndroidRect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.drinkstars.composesketch.capture.captureAndShare
import com.drinkstars.composesketch.sketch.Sketch

val colorShader = RuntimeShader(
    """
        uniform float2 iResolution;
        uniform float iTime;
        
        vec4 main(in float2 fragCoord) {
       
        // Normalized pixel coordinates (from 0 to 1)
        vec2 uv = fragCoord/iResolution.xy;
    
        // Time varying pixel color
        vec3 col = 0.8 + 0.2*cos(iTime*2.0+uv.xxx*2.0+vec3(1,2,4));
    
        // Output to screen
        return vec4(col,1.0);
       
    //    float uvY = fragCoord.y / iResolution.y;
    //    float progress = (uvY + sin((iTime) * 2.0*3.14)) / 3.0;
    //    vec3 color1 = vec3(46.0/255.0, 148.0/255.0, 250.0/255.0);
    //    vec3 color2 = vec3(1, 143.0/255.0, 216.0/255.0);
    //    return vec4(mix(color1, color2, progress), 1);
        }
    """
)
val colorBrush = ShaderBrush(colorShader)

@Preview
@Composable
fun GradientShader() {
    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        var size by remember { mutableStateOf(IntSize.Zero) }
        var boundsInWindow by remember { mutableStateOf(Rect.Zero) }
        val context = LocalContext.current

//        Spacer(modifier = Modifier.height(100.dp))
//        Button(onClick = {
//            captureAndShare(
//                width = size.width,
//                height = size.height,
//                rect = boundsInWindow.toAndroidRect(),
//                context = context
//            )
//        }) {
//            Text("Capture")
//        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Sketch(
                modifier = Modifier
                    .onGloballyPositioned {
                        size = it.size
                        boundsInWindow = it.boundsInWindow()
                    }
                    .fillMaxSize(),
                speed = 4f,
                showControls = false,
                onDraw = { value ->
                    colorShader.setFloatUniform(
                        "iResolution",
                        this.size.width, this.size.height
                    )
                    colorShader.setFloatUniform("iTime", value)
                    drawRect(brush = colorBrush)
                }
            )
        }
    }
}
