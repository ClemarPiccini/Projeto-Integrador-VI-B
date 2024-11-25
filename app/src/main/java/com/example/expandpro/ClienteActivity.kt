package com.example.expandpro

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.expandpro.database.AppDatabaseHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClienteScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Painel do Cliente",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = { navController.navigate("ver_questionarios") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Responder Questionários")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Sair")
        }

    }
}

@Composable
fun VerQuestionariosScreen(navController: NavController) {
    val context = LocalContext.current
    val dbHelper = AppDatabaseHelper(context)
    val questionarios = remember {
        dbHelper.buscarQuestionarios()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Escolha um Questionário",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (questionarios.isEmpty()) {
            Text(
                text = "Nenhum questionário disponível.",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(questionarios) { questionario ->
                    Button(
                        onClick = { navController.navigate("resposta_cliente/${questionario.id}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(questionario.titulo)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Voltar")
        }
    }
}

@Composable
fun ResponderQuestionarioClienteScreen(navController: NavController, questionarioId: Int) {
    val context = LocalContext.current
    val dbHelper = AppDatabaseHelper(context)
    val perguntasComOpcoes = remember {
        dbHelper.buscarPerguntasComOpcoes(questionarioId)
    }
    var respostas by remember { mutableStateOf(mutableMapOf<Int, String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Responder Questionário",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (perguntasComOpcoes.isEmpty()) {
            Text(
                text = "Nenhuma pergunta encontrada para este questionário.",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(perguntasComOpcoes) { (pergunta, opcoes) ->
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(
                            text = pergunta.texto,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        opcoes.forEach { (valor, opcao) ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = respostas[pergunta.id] == valor,
                                    onClick = { respostas[pergunta.id] = valor }
                                )
                                Text(text = opcao, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (respostas.size == perguntasComOpcoes.size) {
                        val idCliente = 1
                        val sucesso = salvarRespostasCliente(idCliente, questionarioId, respostas, dbHelper)
                        if (sucesso) {
                            Toast.makeText(context, "Respostas enviadas com sucesso!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, "Erro ao salvar respostas.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Responda todas as perguntas!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar Respostas")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Voltar")
        }
    }
}

fun salvarRespostasCliente(
    idCliente: Int,
    questionarioId: Int,
    respostas: Map<Int, String>,
    dbHelper: AppDatabaseHelper
): Boolean {
    return try {
        val dataResposta = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        respostas.forEach { (idPergunta, valorResposta) ->
            val resultado = dbHelper.inserirResposta(
                idCliente,
                idPergunta.toString(),
                valorResposta,
                dataResposta
            )
            if (resultado == -1L) {
                throw Exception("Erro ao inserir resposta para a pergunta $idPergunta")
            }
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

@Preview(showBackground = true)
@Composable
fun ClienteScreenPreview() {
    ClienteScreen(navController = rememberNavController())
}
