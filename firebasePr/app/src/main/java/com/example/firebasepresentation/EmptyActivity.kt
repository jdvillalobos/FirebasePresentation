package com.example.firebasepresentation

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasepresentation.firebase.FirebaseManager
import com.example.firebasepresentation.model.Movie
import com.example.firebasepresentation.model.MoviesAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_empty.*
import java.util.*

class EmptyActivity : AppCompatActivity() {

    private val moviesAdapter = MoviesAdapter()

    private val firebaseManager = FirebaseManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty)
        initRecyclerView()
        setupObservables()

        movies_floating_action_button.setOnClickListener {
            showNewMovieTextInputAlertDialog()
        }
    }
    
    @SuppressLint("CheckResult")
    private fun setupObservables(){
        firebaseManager.changes("Movies", Movie::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                moviesAdapter.movies = it
            }

        moviesAdapter.getNameClickedObservable()
            .subscribe {
                showModifyRoleTextInputAlertDialog(it)
            }

        moviesAdapter.getDeleteButtonClickedObservable()
            .observeOn(Schedulers.io())
            .subscribe {
                firebaseManager.delete("Movies", it.id)
                    .subscribe()
            }
    }

    private fun initRecyclerView() {
        movies_recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = moviesAdapter
            addItemDecoration(
                DividerItemDecoration(
                    this.context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    @SuppressLint("CheckResult")
    private fun showNewMovieTextInputAlertDialog() {
        val alert = TextInputAlertDialog(this)
        alert.show(
            "Add movie",
            "Add",
            "Cancel"
        )

        alert.getPositiveButtonObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                val id = UUID.randomUUID().toString()
                firebaseManager.set(Movie(id, it.name), id, "Movies")
            }
    }

    @SuppressLint("CheckResult")
    private fun showModifyRoleTextInputAlertDialog(movie: Movie) {
        val alert = TextInputAlertDialog(this)
        alert.show(
            "Modify role",
            "Save",
            "Cancel",
            movie.name,
            movie.id
        )

        alert.getPositiveButtonObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                firebaseManager.updateDocument("Movies",it.id,"name", it.name)
            }
    }
}