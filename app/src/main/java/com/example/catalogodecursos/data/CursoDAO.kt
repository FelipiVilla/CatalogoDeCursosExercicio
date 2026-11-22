package com.example.catalogodecursos.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CursoDAO {

    @Insert
    suspend fun inserir(curso: Curso)

    @Query("SELECT * FROM curso ORDER BY nome ASC")
    suspend fun listarTodos(): List<Curso>

    @Update
    suspend fun atualizar(curso: Curso)

    @Delete
    suspend fun deletar(curso: Curso)

    @Query("SELECT COUNT(*) FROM curso")
    suspend fun contar(): Int
}