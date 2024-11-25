package com.example.expandpro

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.expandpro.database.AppDatabaseHelper
import com.example.expandpro.ui.theme.ExpandProTheme

class CadastroActivity : ComponentActivity() {
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
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login")
     {
        composable("cadastro_screen") { CadastroScreen(navController) }
        composable("login_screen") { LoginScreen(navController) }
        composable("empresario") { EmpresarioScreen(navController) }
        composable("criar_questionarios") { CriarQuestionariosScreen(navController) }
         composable("cliente") { ClienteScreen(navController)}
         composable("ver_respostas") {
             VerRespostasScreen(navController = rememberNavController())
         }
         composable("cliente_screen") { ClienteScreen(navController = navController) }
         composable("ver_questionarios") { VerQuestionariosScreen(navController = navController) }
         composable("resposta_cliente/{questionarioId}") { backStackEntry ->
             val questionarioId = backStackEntry.arguments?.getString("questionarioId")?.toInt() ?: 0
             ResponderQuestionarioClienteScreen(navController = navController, questionarioId = questionarioId)
         }

     }

}

@Composable
fun CadastroScreen(navController: NavController) {
    val dbHelper = AppDatabaseHelper(LocalContext.current)
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var senhaVisivel by remember { mutableStateOf(false) }
    var tipoUsuario by remember { mutableStateOf("Empresário") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Cadastrar-se",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default
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

        Spacer(modifier = Modifier.height(16.dp))

        Text("Selecione o tipo de usuário", modifier = Modifier.padding(bottom = 8.dp))
        Row {
            RadioButton(
                selected = tipoUsuario == "Cliente",
                onClick = { tipoUsuario = "Cliente" }
            )
            Text("Cliente", modifier = Modifier.align(Alignment.CenterVertically))

            Spacer(modifier = Modifier.width(16.dp))

            RadioButton(
                selected = tipoUsuario == "Empresário",
                onClick = { tipoUsuario = "Empresário" }
            )
            Text("Empresário", modifier = Modifier.align(Alignment.CenterVertically))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (nome.isNotEmpty() && email.isNotEmpty() && senha.isNotEmpty()) {
                    val result = if (tipoUsuario == "Cliente") {
                        dbHelper.inserirCliente(nome, email, senha)
                    } else {
                        dbHelper.inserirEmpresario(nome, email, senha)
                    }

                    if (result > 0) {
                        Toast.makeText(context, "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show()
                        navController.navigate("login")
                    } else {
                        Toast.makeText(context, "Erro ao realizar o cadastro", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Cadastrar")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Voltar")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CadastroScreenPreview() {
    ExpandProTheme {
        CadastroScreen(navController = rememberNavController())
    }
}
