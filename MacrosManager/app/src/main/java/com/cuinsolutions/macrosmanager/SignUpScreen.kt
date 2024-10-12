package com.cuinsolutions.macrosmanager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController

@Composable
fun SignUpScreen(modifier: Modifier, navController: NavHostController, signUpViewModel: SignUpViewModel = hiltViewModel()) {

    Scaffold(modifier = modifier.navigationBarsPadding(), topBar = { MacrosManagerOptionsMenuAppBar(modifier, R.string.sign_up, navController) },
        bottomBar = { /*BannerAdview()*/ }) {

        Column(modifier = Modifier.fillMaxSize().padding(it)) {
            Text(text = stringResource(id = R.string.sign_up_description))
        }
    }
}

@Composable
fun EmailTextField(modifier: Modifier, signUpViewModel: SignUpViewModel) {

    val validEmail by signUpViewModel.validEmail.collectAsStateWithLifecycle(false)

    TextField(modifier = modifier, label = { Text(text = stringResource(id = R.string.email)) },
        value = signUpViewModel.email, onValueChange = { updatedEmail -> signUpViewModel.updateEmail(updatedEmail) },
        isError = validEmail,
        supportingText = {
            if (validEmail) {
                Text(text = stringResource(id = R.string.valid_email_error))
            }
        },
        trailingIcon = { if (validEmail) Icon(Icons.Filled.Warning, contentDescription = stringResource(R.string.valid_email_error)) }
    )
}