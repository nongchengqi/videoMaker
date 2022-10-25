package com.evan.videomaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.evan.aac.AACEncoder
import com.evan.lame.Lame
import com.evan.img2mp4.MuxAudioVideo
import com.evan.videomaker.ui.theme.VideoMakerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoMakerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }

    fun testLame() {
        lifecycleScope.launch(Dispatchers.IO) {
            Lame.execute(
                "./lame -f ${File(filesDir, "test.wav").absolutePath} ${
                    File(
                        filesDir,
                        "test123.mp3"
                    ).absolutePath
                }"
            )
        }
    }

    fun testAac() {
        lifecycleScope.launch(Dispatchers.IO) {
            AACEncoder.encodePcmToAAC(
                File(filesDir, "test.pcm").absolutePath,
                File(filesDir, "test.aac").absolutePath,
                16,
                32000,
                2
            )
        }
    }
    fun testMux(){
        lifecycleScope.launch(Dispatchers.IO) {
            // 依赖MP4parser
            MuxAudioVideo.muxAccToMp4("","","")
            MuxAudioVideo.muxMp3ToMp4("","","")
            // 不需要依赖任何库
            MuxAudioVideo.muxByMediaMuxer("","","")
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    VideoMakerTheme {
        Greeting("Android")
    }
}