package com.example.firebasepresentation.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasepresentation.R
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_movie.view.*

class MoviesAdapter : RecyclerView.Adapter<RoleViewHolder>() {

    var movies: List<Movie> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val nameClicked = PublishSubject.create<Movie>()

    private val deleteButtonClicked = PublishSubject.create<Movie>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return RoleViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoleViewHolder, position: Int) {
        holder.bind(movies[position], nameClicked, deleteButtonClicked)
    }

    override fun getItemCount(): Int = movies.size

    fun getNameClickedObservable(): Observable<Movie> {
        return nameClicked
    }

    fun getDeleteButtonClickedObservable(): Observable<Movie> {
        return deleteButtonClicked
    }

}

class RoleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(
        movie: Movie,
        nameClicked: Observer<Movie>,
        deleteButtonClicked: Observer<Movie>
    ) =
        with(itemView) {
            item_movie_name.text = movie.name

            item_movie_delete_button.setOnClickListener {
                deleteButtonClicked.onNext(movie)
            }

            item_movie_layout.setOnClickListener {
                nameClicked.onNext(movie)
            }
        }
}