package com.berfinilik.mynoteappkotlin.mvvm

import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.berfinilik.mynoteappkotlin.db.Notes
import com.berfinilik.mynoteappkotlin.db.NotesDao

class NotesRepository(val dao : NotesDao) {

    // display all notes

    fun allnotes() : LiveData<List<Notes>> {

        return dao.getAllNotes()

    }

    // insert note

    suspend fun insertNote(notes: Notes){
        dao.insertNotes(notes)
    }

    suspend fun updateNotes(notes: Notes) {
        dao.updateNotes(notes)
    }




    suspend fun deleteNote(notes: Notes){
        dao.deleteNotes(notes)
    }



    suspend fun deleteAll(){
        dao.deleteAll()
    }
}