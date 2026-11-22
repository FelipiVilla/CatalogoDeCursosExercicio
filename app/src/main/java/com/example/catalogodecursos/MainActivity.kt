package com.example.catalogodecursos

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.catalogodecursos.data.Curso
import com.example.catalogodecursos.data.CursoDatabase
import com.example.catalogodecursos.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.DialogInterface

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var db: CursoDatabase
    private lateinit var launcherCadastro: ActivityResultLauncher<Intent>

    private val cursos = mutableListOf<Curso>()
    private val nomes = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupLauncherCadastro()
        setupListeners()
        loadData()
        inicializarDados()
    }

    private fun setupViews() {
        db = CursoDatabase.getInstance(this)
        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            nomes
        )
        binding.listaCursos.adapter = adapter
    }

    private fun setupLauncherCadastro() {
        launcherCadastro = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadData()
            }
        }
    }

    private fun setupListeners() {
        binding.listaCursos.setOnItemClickListener { _, _, position, _ ->
            binding.txtDescricao.text = cursos[position].descricao
        }

        binding.listaCursos.setOnItemLongClickListener { _, _, position, _ ->
            mostrarOpcoes(cursos[position])
            true
        }

        binding.btnAdicionar.setOnClickListener {
            val intent = Intent(this, CadastroCursoActivity::class.java)
            launcherCadastro.launch(intent)
        }
    }

    private fun loadData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val lista = db.cursoDao().listarTodos()
            withContext(Dispatchers.Main) {
                cursos.clear()
                cursos.addAll(lista)
                nomes.clear()
                nomes.addAll(lista.map { it.nome })
                adapter.notifyDataSetChanged()
                binding.txtDescricao.text = ""
            }
        }
    }

    private fun mostrarOpcoes(curso: Curso) {
        AlertDialog.Builder(this)
            .setTitle(curso.nome)
            .setPositiveButton(R.string.editar, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    editarCurso(curso)
                }
            })
            .setNegativeButton(R.string.excluir, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    excluirCurso(curso)
                }
            })
            .setNeutralButton(R.string.cancelar, null)
            .show()
    }

    private fun editarCurso(curso: Curso) {
        val intent = Intent(this, CadastroCursoActivity::class.java)
        intent.putExtra("curso", curso)
        launcherCadastro.launch(intent)
    }


    private fun excluirCurso(curso: Curso) {
        lifecycleScope.launch(Dispatchers.IO) {
            db.cursoDao().deletar(curso)
            loadData()
        }
    }

    private fun inicializarDados() {
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = db.cursoDao()

            if (dao.contar() == 0) {
                dao.inserir(Curso(nome = getString(R.string.nome_tsi), descricao = getString(R.string.desc_tsi)))
                dao.inserir(Curso(nome = getString(R.string.nome_eng), descricao = getString(R.string.desc_eng)))
                dao.inserir(Curso(nome = getString(R.string.nome_ads), descricao = getString(R.string.desc_ads)))
                dao.inserir(Curso(nome = getString(R.string.nome_mat), descricao = getString(R.string.desc_mat)))
            }

            loadData()
        }
    }

}