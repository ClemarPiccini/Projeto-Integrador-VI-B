package com.example.expandpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.expandpro.ui.theme.ExpandProTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpandProTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    content = { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "mainScreen",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("mainScreen") {
                                MainScreen(
                                    navigateToLogin = { navController.navigate("login") },
                                    navigateToCadastro = { navController.navigate("cadastro") },
                                    navigateToNomes = { navController.navigate("nomes") }
                                )
                            }
                            composable("login") {
                                // Tela de login já existente
                                LoginScreen(navController)
                            }
                            composable("cadastro") {
                                // Tela de cadastro já existente
                                CadastroScreen(navController)
                            }
                            composable("nomes") {
                                // Tela de nomes já existente
                                NomesScreen(
                                    onBackPressed = { navController.popBackStack() }

                                )
                            }
                            composable("empresario") {
                                // Tela para o Empresário
                                EmpresarioScreen(navController)
                            }
                            composable("cliente") {
                                // Tela para o Cliente
                                ClienteScreen(navController)}
                            composable("ver_respostas") {
                                VerRespostasScreen(navController = rememberNavController())
                            }
                            composable("criar_questionarios") { CriarQuestionariosScreen(navController) }
                            composable("cliente_screen") { ClienteScreen(navController = navController) }
                            composable("ver_questionarios") { VerQuestionariosScreen(navController = navController) }
                            composable("resposta_cliente/{questionarioId}") { backStackEntry ->
                                val questionarioId = backStackEntry.arguments?.getString("questionarioId")?.toInt() ?: 0
                                ResponderQuestionarioClienteScreen(navController = navController, questionarioId = questionarioId)
                            }





                        }
                    }
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navigateToLogin: () -> Unit,
    navigateToCadastro: () -> Unit,
    navigateToNomes: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo do ExpandPro",
            modifier = Modifier
                .size(128.dp)
                .padding(bottom = 32.dp)
        )
        Text(text = "Bem-vindo ao ExpandPro", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = navigateToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = navigateToCadastro,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cadastrar-se")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = navigateToNomes,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("NOMES")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    ExpandProTheme {
        MainScreen(
            navigateToLogin = {},
            navigateToCadastro = {},
            navigateToNomes = {}
        )
    }
}
