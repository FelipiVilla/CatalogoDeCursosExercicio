package com.example.catalogodecursos.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.catalogodecursos.R

@Database(entities = [Curso::class], version = 1)
abstract class CursoDatabase : RoomDatabase() {

    abstract fun cursoDao(): CursoDAO

    companion object {
        @Volatile
        private var INSTANCE: CursoDatabase? = null

        fun getInstance(context: Context): CursoDatabase {
            return INSTANCE ?: synchronized(this) {
                val appContext = context.applicationContext

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CursoDatabase::class.java,
                    "curso_database"
                )
                .build()

                INSTANCE = instance
                instance
            }
        }
    }
}