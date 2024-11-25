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

class NomesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpandProTheme {
                NomesScreen(onBackPressed = { finish() })
            }
        }
    }
}

@Composable
fun NomesScreen(onBackPressed: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Clemar Junior de Mattos Piccini", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Guilherme Abel", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Leonardo Picolotto", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Matheus M. Dutra", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Thiago Fideles G. Andrade", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(88.dp))

        Button(onClick = onBackPressed,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Voltar")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NomesScreenPreview() {
    ExpandProTheme {
        NomesScreen(onBackPressed = {})
    }
}
