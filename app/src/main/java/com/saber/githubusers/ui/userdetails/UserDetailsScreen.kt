package com.saber.githubusers.ui.userdetails


import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.saber.githubusers.R
import com.saber.githubusers.compose.ErrorItem
import com.saber.githubusers.compose.LoadingView
import com.saber.githubusers.compose.OurAppTheme
import com.saber.githubusers.data.User
import com.saber.githubusers.utils.checkCachedAvatarExists
import com.saber.githubusers.utils.getCachedAvatarPath

@ExperimentalMaterial3Api
@Composable
fun UserDetailsScreen(
    viewModel: UserDetailsViewModel = viewModel(),
    userName: String,
    navigationController: () -> (Unit)
) {

    val viewState by viewModel.viewStateFlow.collectAsState()

    OurAppTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            userName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigationController.invoke() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Localized description",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
                )
            },
            modifier = Modifier.statusBarsPadding(),
        ) {
            val modifier = Modifier.padding(it)
            Crossfade(viewState.uiState) { uiState ->
                when (uiState) {
                    UIState.LOADING -> LoadingView(modifier = modifier.fillMaxSize())
                    UIState.CONTENT -> viewState.user?.let { user ->
                        UserDetailsContent(
                            user,
                            modifier = modifier
                        ) { note -> viewModel.saveNote(note) }
                    }
                    UIState.ERROR -> {
                        ErrorItem(
                            message = "Error occurred",
                            onClickRetry = { viewModel.fetchUserDetails(userName) }
                        )
                    }
                    UIState.IDLE -> {}
                }
            }
        }
    }

}

@Composable
fun UserDetailsContent(user: User, modifier: Modifier = Modifier, saveAction: (String) -> Unit) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        UserDetailsImage(
            imageUrl = if (user.checkCachedAvatarExists(LocalContext.current))
                user.getCachedAvatarPath(LocalContext.current) else user.avatar.toString(),
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        )
        UserDetailsDescription(description = "Followers: ${user.followers ?: 0} - Following: ${user.following ?: 0}")
        NoteSection(user.note.orEmpty(), saveAction)
    }
}

@Composable
fun UserDetailsImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    val painter = rememberAsyncImagePainter(imageUrl)
    Image(
        painter = painter,
        modifier = modifier,
        contentDescription = "User Details Poster",
        contentScale = ContentScale.Crop
    )
}

@Composable
fun UserDetailsDescription(description: String) {
    Text(
        modifier = Modifier.padding(all = 16.dp),
        text = description,
        letterSpacing = 2.sp,
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
fun NoteSection(currentNote: String, saveAction: (String) -> Unit) {
    val noteValue = remember { mutableStateOf(currentNote) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    )
    {
        Text(
            text = stringResource(id = R.string.notes),
            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
            maxLines = 1,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )
        TextField(
            value = noteValue.value,
            onValueChange = { noteValue.value = it },
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth()
                .height(120.dp),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = true,
                keyboardType = KeyboardType.Text,
            ),
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.secondary,
                fontFamily = FontFamily.SansSerif
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedButton(onClick = { saveAction.invoke(noteValue.value) }) {
                Text(
                    text = stringResource(R.string.save),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    UserDetailsContent(
        User(
            1,
            "Ahmed",
            "User",
            "https://avatars.githubusercontent.com/u/9743939?v=4",
            100,
            50,
            null
        )
    )
}*/
