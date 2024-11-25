package com.example.expandpro.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

data class Questionario(
    val id: Int,
    val idEmpresario: Int,
    var titulo: String,
    val descricao: String
)

data class Pergunta(
    val id: Int,
    val idQuestionario: Int,
    val texto: String,
    val tipo: String?,
    val opcoes: List<String>?
)

data class Resposta(
    val id: Int,
    val idCliente: Int,
    val idPergunta: String,
    val resposta: String,
    val dataResposta: String,
    val texto: String,
    val perguntaTexto: String,
    val respostaTexto: String
)

class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_EMPRESARIOS)
        db.execSQL(CREATE_TABLE_CLIENTES)
        db.execSQL(CREATE_TABLE_QUESTIONARIOS)
        db.execSQL(CREATE_TABLE_PERGUNTAS)
        db.execSQL(CREATE_TABLE_RESPOSTAS)
        db.execSQL(CREATE_TABLE_OPCOES_RESPOSTAS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Empresarios")
        db.execSQL("DROP TABLE IF EXISTS Clientes")
        db.execSQL("DROP TABLE IF EXISTS Questionario")
        db.execSQL("DROP TABLE IF EXISTS Pergunta")
        db.execSQL("DROP TABLE IF EXISTS Resposta")
        db.execSQL("DROP TABLE IF EXISTS OpcaoResposta")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "ExpandPro.db"
        private const val DATABASE_VERSION = 1
        private const val CREATE_TABLE_CLIENTES = """
            CREATE TABLE Cliente (
                id_cliente INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                senha TEXT NOT NULL,
                DataCadastro TEXT NOT NULL
            );
        """

        private const val CREATE_TABLE_EMPRESARIOS = """
            CREATE TABLE Empresario (
                id_empresario INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                senha TEXT NOT NULL
            );
        """

        private const val CREATE_TABLE_QUESTIONARIOS = """
            CREATE TABLE Questionario (
                id_questionario INTEGER PRIMARY KEY AUTOINCREMENT,
                id_empresario INTEGER,
                titulo TEXT NOT NULL,
                descricao TEXT,
                data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (id_empresario) REFERENCES Empresario(id_empresario)
            );
        """

        private const val CREATE_TABLE_PERGUNTAS = """
            CREATE TABLE Pergunta (
                id_pergunta INTEGER PRIMARY KEY AUTOINCREMENT,
                id_questionario INTEGER,
                texto TEXT NOT NULL,
                FOREIGN KEY (id_questionario) REFERENCES Questionario(id_questionario)
    );
"""

        private const val CREATE_TABLE_OPCOES_RESPOSTAS = """
            CREATE TABLE OpcaoResposta (
                id_opcao INTEGER PRIMARY KEY AUTOINCREMENT,
                id_pergunta INTEGER,
                opcao TEXT NOT NULL,
                valor TEXT NOT NULL, -- Armazena o valor associado, como "A", "B", "C", "D"
                FOREIGN KEY (id_pergunta) REFERENCES Pergunta(id_pergunta)
    );
"""

        private const val CREATE_TABLE_RESPOSTAS = """
            CREATE TABLE Resposta (
                id_resposta INTEGER PRIMARY KEY AUTOINCREMENT,
                id_cliente INTEGER,
                id_pergunta INTEGER,
                valor_resposta INTEGER NOT NULL,  -- Agora é o valor associado à resposta
                data_resposta DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (id_cliente) REFERENCES Cliente(id_cliente),
                FOREIGN KEY (id_pergunta) REFERENCES Pergunta(id_pergunta)
            );
"""

    }

    fun inserirEmpresario(nome: String, email: String, senha: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nome", nome)
            put("email", email)
            put("senha", senha)
        }
        val id = db.insert("Empresario", null, values)
        db.close()
        return id
    }

    fun inserirCliente(nome: String, email: String, senha: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nome", nome)
            put("email", email)
            put("senha", senha)
            put("DataCadastro", System.currentTimeMillis().toString())
        }
        val id = db.insert("Cliente", null, values)
        db.close()
        return id
    }

    fun buscarQuestionarios(): List<Questionario> {
        val db = readableDatabase
        val cursor = db.query("Questionario", null, null, null, null, null, null)
        val questionarios = mutableListOf<Questionario>()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow("id_questionario"))
                val idEmpresario = getInt(getColumnIndexOrThrow("id_empresario"))
                val titulo = getString(getColumnIndexOrThrow("titulo"))
                val descricao = getString(getColumnIndexOrThrow("descricao"))
                questionarios.add(Questionario(id, idEmpresario, titulo, descricao))
            }
        }
        cursor.close()
        db.close()
        return questionarios
    }

    fun inserirResposta(
        idCliente: Int,
        idPergunta: String,
        valorResposta: String,
        dataResposta: String
    ): Long {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put("id_cliente", idCliente)
                put("id_pergunta", idPergunta)
                put("valor_resposta", valorResposta)
                put("data_resposta", dataResposta)
            }
            val id = db.insert("Resposta", null, values)
            db.close()
            id
        } catch (e: Exception) {
            e.printStackTrace()
            -1L
        }
    }

    fun verificarLoginEmpresario(email: String, senha: String): Boolean {
        return try {
            val db = readableDatabase
            val cursor = db.rawQuery(
                "SELECT * FROM Empresario WHERE email = ? AND senha = ?",
                arrayOf(email, senha)
            )
            val loginValido = cursor.moveToFirst()
            cursor.close()
            db.close()
            loginValido
        } catch (e: Exception) {
            Log.e("DatabaseError", "Erro ao verificar login do empresário: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    fun verificarLoginCliente(email: String, senha: String): Boolean {
        return try {
            val db = readableDatabase
            val cursor = db.rawQuery(
                "SELECT * FROM Cliente WHERE email = ? AND senha = ?",
                arrayOf(email, senha)
            )
            val loginValido = cursor.moveToFirst()
            cursor.close()
            db.close()
            loginValido
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun inserirQuestionario(titulo: String, descricao: String): Long {
        val db = writableDatabase
        val questionarioValues = ContentValues().apply {
            put("titulo", titulo)
            put("descricao", descricao)
        }
        val questionarioId = db.insert("Questionario", null, questionarioValues)
        db.close()
        return questionarioId
    }

    fun inserirPerguntaComOpcoes(idQuestionario: Int, texto: String, opcoes: Map<String, String>): Long {
        val db = writableDatabase
        val perguntaValues = ContentValues().apply {
            put("id_questionario", idQuestionario)
            put("texto", texto)
        }
        val perguntaId = db.insert("Pergunta", null, perguntaValues)

        if (perguntaId != -1L) {
            for ((valor, opcao) in opcoes) {
                val opcaoValues = ContentValues().apply {
                    put("id_pergunta", perguntaId)
                    put("opcao", opcao)
                    put("valor", valor)
                }
                db.insert("OpcaoResposta", null, opcaoValues)
            }
        }

        db.close()
        return perguntaId
    }

    fun buscarPerguntasComOpcoes(idQuestionario: Int): List<Pair<Pergunta, List<Pair<String, String>>>> {
        val db = readableDatabase
        val perguntas = mutableListOf<Pair<Pergunta, List<Pair<String, String>>>>()

        val perguntaCursor = db.rawQuery(
            "SELECT id_pergunta, texto FROM Pergunta WHERE id_questionario = ?",
            arrayOf(idQuestionario.toString())
        )

        while (perguntaCursor.moveToNext()) {
            val idPergunta = perguntaCursor.getInt(perguntaCursor.getColumnIndexOrThrow("id_pergunta"))
            val texto = perguntaCursor.getString(perguntaCursor.getColumnIndexOrThrow("texto"))

            // Busca as opções associadas à pergunta
            val opcoesCursor = db.rawQuery(
                "SELECT valor, opcao FROM OpcaoResposta WHERE id_pergunta = ?",
                arrayOf(idPergunta.toString())
            )
            val opcoes = mutableListOf<Pair<String, String>>()
            while (opcoesCursor.moveToNext()) {
                val valor = opcoesCursor.getString(opcoesCursor.getColumnIndexOrThrow("valor"))
                val opcao = opcoesCursor.getString(opcoesCursor.getColumnIndexOrThrow("opcao"))
                opcoes.add(valor to opcao)
            }
            opcoesCursor.close()

            perguntas.add(Pergunta(idPergunta, idQuestionario, texto, null, null) to opcoes)
        }

        perguntaCursor.close()
        db.close()
        return perguntas
    }

    @SuppressLint("Range")
    fun buscarRespostas(): List<Resposta> {
        val respostas = mutableListOf<Resposta>()
        val db = readableDatabase
        var cursor: Cursor? = null

        try {

            val query = """
            SELECT 
                r.id_resposta AS id,
                r.id_cliente AS idCliente,
                r.id_pergunta AS idPergunta,
                r.valor_resposta AS resposta,
                r.data_resposta AS dataResposta,
                p.texto AS perguntaTexto,
                o.opcao AS respostaTexto
            FROM 
                Resposta r
            INNER JOIN 
                Pergunta p ON r.id_pergunta = p.id_pergunta
            LEFT JOIN 
                OpcaoResposta o ON r.valor_resposta = o.valor AND o.id_pergunta = p.id_pergunta
        """

            cursor = db.rawQuery(query, null)

            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val idCliente = cursor.getInt(cursor.getColumnIndexOrThrow("idCliente"))
                    val idPergunta = cursor.getInt(cursor.getColumnIndexOrThrow("idPergunta")).toString()
                    val resposta = cursor.getString(cursor.getColumnIndexOrThrow("resposta"))
                    val dataResposta = cursor.getString(cursor.getColumnIndexOrThrow("dataResposta"))
                    val perguntaTexto = cursor.getString(cursor.getColumnIndexOrThrow("perguntaTexto"))
                    val respostaTexto = cursor.getString(cursor.getColumnIndexOrThrow("respostaTexto"))

                    respostas.add(
                        Resposta(
                            id = id,
                            idCliente = idCliente,
                            idPergunta = idPergunta,
                            resposta = resposta,
                            dataResposta = dataResposta,
                            texto = respostaTexto ?: "",
                            perguntaTexto = perguntaTexto,
                            respostaTexto = respostaTexto ?: ""
                        )
                    )
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }

        return respostas
    }
}