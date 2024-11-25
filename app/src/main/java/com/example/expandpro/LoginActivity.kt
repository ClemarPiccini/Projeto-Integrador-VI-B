package com.example.expandpro

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.expandpro.database.AppDatabaseHelper
import com.example.expandpro.ui.theme.ExpandProTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpandProTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var senhaVisivel by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val dbHelper = AppDatabaseHelper(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                    Image(
                        painter = painterResource(
                            id = if (senhaVisivel) R.drawable.eye else R.drawable.eyeoff
                        ),
                        contentDescription = if (senhaVisivel) "Ocultar senha" else "Mostrar senha",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (email.isNotEmpty() && senha.isNotEmpty()) {
                    val isEmpresario = dbHelper.verificarLoginEmpresario(email, senha)
                    val isCliente = dbHelper.verificarLoginCliente(email, senha)

                    when {
                        isEmpresario -> {
                            navController.navigate("empresario")
                        }
                        isCliente -> {
                            navController.navigate("cliente")
                        }
                        else -> {
                            Toast.makeText(context, "Credenciais inválidas", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Entrar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate("cadastro") }) {
            Text("Criar uma conta")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    ExpandProTheme {
        LoginScreen(navController = rememberNavController())
    }
}
