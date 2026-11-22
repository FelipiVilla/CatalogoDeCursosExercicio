package com.example.catalogodecursos

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.catalogodecursos.data.Curso
import com.example.catalogodecursos.data.CursoDatabase
import com.example.catalogodecursos.databinding.ActivityCadastroCursoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CadastroCursoActivity : AppCompatActivity() {

    companion object {
        const val TAG = "CadastroCursoActivity"
    }

    private lateinit var binding: ActivityCadastroCursoBinding
    private lateinit var db: CursoDatabase
    private var cursoEdicao: Curso? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroCursoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = CursoDatabase.getInstance(this)
        cursoEdicao = intent.getSerializableExtra("curso") as? Curso

        setupViews()
        setupListeners()
    }

    private fun setupViews() {
        cursoEdicao?.let { curso ->
            binding.txtTitulo.setText(R.string.titulo_editar_curso)
            binding.edtNome.setText(curso.nome)
            binding.edtDescricao.setText(curso.descricao)
        }
    }

    private fun setupListeners() {
        binding.btnSalvar.setOnClickListener {
            val nome = binding.edtNome.text.toString().trim()
            val descricao = binding.edtDescricao.text.toString().trim()

            if (nome.isNotBlank() && descricao.isNotBlank()) {
                salvarNoBanco(nome, descricao)
            } else {
                Toast.makeText(this, R.string.preencha_campos, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnVoltar.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun salvarNoBanco(nome: String, descricao: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val atual = cursoEdicao
            if (atual == null) {
                db.cursoDao().inserir(Curso(nome = nome, descricao = descricao))
            } else {
                db.cursoDao().atualizar(atual.copy(nome = nome, descricao = descricao))
            }
            withContext(Dispatchers.Main) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }
}