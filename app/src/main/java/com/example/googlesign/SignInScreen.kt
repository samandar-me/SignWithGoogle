package com.example.googlesign

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.googlesign.component.MyButton
import com.example.googlesign.model.User
import com.example.googlesign.util.AuthResult
import com.example.googlesign.viewmodel.MainViewModel
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    viewModel: MainViewModel
) {
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf<String?>(null) }
    val user by remember(viewModel) { viewModel.user }.collectAsState()
    val signInRequestCode = 1

    val authResultLauncher = rememberLauncherForActivityResult(
        contract = AuthResult()
    ) { result ->
        try {
            val account = result?.getResult(ApiException::class.java)
            if (account == null) {
                text = "Google Sign in Failed"
            } else {
                scope.launch {
                    viewModel.setSignInValue(
                        email = account.email!!,
                        displayName = account.displayName!!
                    )
                }
            }
        } catch (e: ApiException) {
            text = e.localizedMessage
        }
    }
    AutView(errorText = text) {
        text = null
        authResultLauncher.launch(signInRequestCode)
    }
    user?.let {
        SignSignInScreen(it)
    }
}

@Composable
fun AutView(
    errorText: String?,
    onClick: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Google Sign In",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MyButton(
                text = "Sign In with Google",
                icon = painterResource(id = R.drawable.ic_launcher_background),
                loadingText = "Signing In...",
                isLoading = isLoading,
                onClick = {
                    isLoading = true
                    onClick()
                }
            )
            errorText?.let {
                isLoading = false

                Spacer(modifier = Modifier.height(30.dp))

                Text(text = it)
            }
        }
    }
}

@Composable
fun SignSignInScreen(
    user: User
) {
    Scaffold(
        topBar = {
            TopAppBar(
            title = {
                Text(
                    text = "Sign In Successful",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        )}
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome! ${user.displayName}",
                fontSize = 30.sp,
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = user.email,
                color = Color.Gray,
                fontSize = 20.sp
            )
        }
    }
}