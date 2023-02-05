package com.example.jt_loginvideoui

import android.content.Context
import android.media.AudioFocusRequest
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.jt_loginvideoui.ui.theme.JT_LoginVideoUITheme
import com.example.jt_loginvideoui.ui.theme.Shapes
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
import com.google.android.exoplayer2.ui.StyledPlayerView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JT_LoginVideoUITheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.DarkGray
                ) {
                    Login(getVideoUri())
                }
            }
        }
    }

    private fun getVideoUri(): Uri {
        val rawId = resources.getIdentifier("clouds", "raw", packageName)
        val videoUri = "android.resource://$packageName/$rawId"

        return Uri.parse(videoUri)
    }
}

private fun Context.buildExoPlayer(uri: Uri) = ExoPlayer.Builder(this).build().apply {
    setMediaItem(MediaItem.fromUri(uri))
    repeatMode = Player.REPEAT_MODE_ALL
    playWhenReady = true
    prepare()
}


private fun Context.buildPlayerView(exoPlayer: ExoPlayer) = StyledPlayerView(this).apply {
    player = exoPlayer
    layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
    useController = false
    resizeMode = RESIZE_MODE_ZOOM
}

private fun Context.doLogin(){
    Toast.makeText(this, "Something went wrong, wry again later!", Toast.LENGTH_SHORT).show()
}



@Composable
fun Login(videoUri: Uri) {
    val context = LocalContext.current
    val passwordFocusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current
    val exoPlayer = remember {
        context.buildExoPlayer(videoUri)
    }

    DisposableEffect(AndroidView(factory = {it.buildPlayerView(exoPlayer)}, modifier = Modifier.fillMaxSize())){
        onDispose { exoPlayer.release() }
    }

    ProvideWindowInsets {
        Column(
            Modifier
                .navigationBarsWithImePadding()
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Bottom),
            Alignment.CenterHorizontally
        ) {
            Icon(painterResource(id = R.drawable.logo), null, Modifier.size(80.dp), Color.White)
            TextInput(InputType.Name, keyboardActions = KeyboardActions(onNext = {
                passwordFocusRequester.requestFocus()
            }))
            TextInput(InputType.Password, keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                context.doLogin()
            }), focusRequester = passwordFocusRequester)
            Button(
                onClick = { context.doLogin() }, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(
                    Color.Gray
                ), shape = CircleShape
            ) {
                Text(text = "SING IN", Modifier.padding(vertical = 8.dp))
            }
            Divider(
                color = Color.White.copy(0.3f),
                thickness = 1.dp,
                modifier = Modifier.padding(top = 48.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Don't you have an account?", color = Color.White)
                TextButton(onClick = { /*TODO*/ }) {
                    Text(text = "SING UP", color = Color(0xFF4281A4))
                }
            }
        }
    }

}

sealed class InputType(
    val label: String,
    val icon: ImageVector,
    val keyboardOptions: KeyboardOptions,
    val visualTransformation: VisualTransformation
) {
    object Name : InputType(
        label = "username",
        icon = Icons.Default.Person,
        KeyboardOptions(imeAction = ImeAction.Next),
        visualTransformation = VisualTransformation.None
    )

    object Password : InputType(
        label = "password",
        icon = Icons.Default.Lock,
        KeyboardOptions(imeAction = ImeAction.Done),
        visualTransformation = PasswordVisualTransformation()
    )
}

@Composable
fun TextInput(inputType: InputType, focusRequester: FocusRequester? = null, keyboardActions: KeyboardActions) {
    var value by remember {
        mutableStateOf("")
    }

    TextField(
        value = value,
        onValueChange = { value = it },
        Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester ?: FocusRequester()),
        leadingIcon = {
            Icon(
                imageVector = inputType.icon,
                contentDescription = null
            )
        },
        label = { Text(text = inputType.label) },
        shape = CircleShape,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        singleLine = true,
        keyboardOptions = inputType.keyboardOptions,
        visualTransformation = inputType.visualTransformation,
        keyboardActions = keyboardActions
    )
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JT_LoginVideoUITheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.DarkGray
        ) {
        }
    }
}