package com.example.expandpro

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.expandpro.database.Resposta
import androidx.compose.material3.MaterialTheme.colorScheme as colorScheme1
import androidx.compose.ui.graphics.Color

val CustomPrimary = Color(0xFF6200EE)
val CustomSecondary = Color(0xFF03DAC5)

@Composable
fun EmpresarioScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Painel do Empresário",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = { navController.navigate("criar_questionarios") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Criar Questionários")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate("ver_respostas") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Respostas dos Clientes")
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
fun CriarQuestionariosScreen(navController: NavController) {
    val context = LocalContext.current
    val dbHelper = AppDatabaseHelper(context)
    var tituloQuestionario by remember { mutableStateOf("") }
    var descricaoQuestionario by remember { mutableStateOf("") }
    var textoPergunta by remember { mutableStateOf("") }
    val opcoes = remember { mutableStateMapOf("A" to "", "B" to "", "C" to "", "D" to "") }
    val perguntas = remember { mutableListOf<Pair<String, Map<String, String>>>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Criar Questionário",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        TextField(
            value = tituloQuestionario,
            onValueChange = { tituloQuestionario = it },
            label = { Text("Título do Questionário") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = descricaoQuestionario,
            onValueChange = { descricaoQuestionario = it },
            label = { Text("Descrição do Questionário") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Criar Pergunta",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TextField(
            value = textoPergunta,
            onValueChange = { textoPergunta = it },
            label = { Text("Texto da Pergunta") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        opcoes.forEach { (valor, texto) ->
            TextField(
                value = texto,
                onValueChange = { opcoes[valor] = it },
                label = { Text("Opção $valor") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (textoPergunta.isNotEmpty() && opcoes.values.all { it.isNotEmpty() }) {
                    perguntas.add(textoPergunta to opcoes.toMap())
                    textoPergunta = ""
                    opcoes.keys.forEach { opcoes[it] = "" }

                    Toast.makeText(context, "Pergunta adicionada com sucesso!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adicionar Pergunta")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (tituloQuestionario.isNotEmpty() && descricaoQuestionario.isNotEmpty()) {
                    val questionarioId = dbHelper.inserirQuestionario(tituloQuestionario, descricaoQuestionario)

                    perguntas.forEach { (perguntaTexto, opcoesPergunta) ->
                        val perguntaId = dbHelper.inserirPerguntaComOpcoes(questionarioId.toInt(), perguntaTexto, opcoesPergunta)

                    }
                    tituloQuestionario = ""
                    descricaoQuestionario = ""
                    perguntas.clear()

                    Toast.makeText(context, "Questionário criado com sucesso!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Preencha título e descrição!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Finalizar Questionário")
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

@Composable
fun VerRespostasScreen(navController: NavController) {
    val context = LocalContext.current
    val dbHelper = AppDatabaseHelper(context)
    val respostasClientes = remember { mutableStateListOf<Resposta>() }

    LaunchedEffect(Unit) {
        val respostas = dbHelper.buscarRespostas()
        respostasClientes.clear()
        respostasClientes.addAll(respostas)
    }

    val respostasAgrupadas = respostasClientes
        .groupingBy { it.texto }
        .eachCount()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Resultados das Respostas",
            style = MaterialTheme.typography.headlineMedium,
            color = colorScheme1.primary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (respostasClientes.isEmpty()) {
            Text(
                text = "Nenhuma resposta encontrada.",
                style = MaterialTheme.typography.bodyLarge,
                color = colorScheme1.secondary
            )
        } else {
            GraficoRespostas(respostasAgrupadas = respostasAgrupadas)
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

@Composable
fun GraficoRespostas(respostasAgrupadas: Map<String, Int>) {
    val respostas = respostasAgrupadas.keys.toList()
    val valores = respostasAgrupadas.values.toList()

    Canvas(modifier = Modifier.fillMaxWidth().height(300.dp)) {
        val barWidth = size.width / (valores.size * 2)
        val maxValue = valores.maxOrNull() ?: 1

        valores.forEachIndexed { index, valor ->
            val barHeight = (valor.toFloat() / maxValue) * size.height
            drawRect(
                color = CustomPrimary,
                topLeft = androidx.compose.ui.geometry.Offset(
                    x = index * 2 * barWidth + barWidth / 2,
                    y = size.height - barHeight
                ),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
            )
        }
    }

    Column(modifier = Modifier.padding(top = 16.dp)) {
        respostas.forEachIndexed { index, resposta ->
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(CustomPrimary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$resposta: ${valores[index]}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun VerRespostasScreenPreview() {
    VerRespostasScreen(navController = rememberNavController())
}
