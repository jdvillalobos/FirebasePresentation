package com.example.firebasepresentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.firebasepresentation.firebase.FirebaseManager
import com.example.firebasepresentation.model.Movie
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class EmptyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty)
        FirestoreTest()
    }
    
    private fun FirestoreTest(){
        val firebaseManager = FirebaseManager()
        val moviesObservable = firebaseManager.changes("Movies", Movie::class.java)
        moviesObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { movies ->
                movies.forEach {
                    Log.d("prueba", "Movie name: ${it.name}")
                }
            }
    }
}