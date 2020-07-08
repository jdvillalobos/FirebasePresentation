package com.example.firebasepresentation

import android.content.Context
import android.view.View.inflate
import androidx.appcompat.app.AlertDialog
import com.example.firebasepresentation.model.Movie
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.text_input_alertdialog.view.*
import io.reactivex.Observable

class TextInputAlertDialog(
    context: Context
) : AlertDialog(context) {

    private val positiveButtonPublishSubject = PublishSubject.create<Movie>()

    fun show(
        title: String,
        positiveButtonText: String,
        negativeButtonText: String,
        placeHolder: String = "",
        movieId: String = ""
    ) {
        val builder = Builder(this.context)
        builder.setTitle(title)

        val customLayout = inflate(this.context, R.layout.text_input_alertdialog, null)
        customLayout.text_input_alert_edit_text.setText(placeHolder)
        builder.setView(customLayout)

        builder.setPositiveButton(positiveButtonText) { _, _ ->
            customLayout.let {
                if (it.text_input_alert_edit_text?.text.toString().isNotEmpty()) {
                    positiveButtonPublishSubject.onNext(
                        Movie(
                            movieId,
                            it.text_input_alert_edit_text.text.toString()
                        )
                    )
                }
            }
        }
        builder.setNegativeButton(negativeButtonText) { dialog, _ -> dialog.cancel() }
        builder.setOnDismissListener { positiveButtonPublishSubject.onComplete() }
        builder.show()
    }

    fun getPositiveButtonObservable(): Observable<Movie> {
        return positiveButtonPublishSubject.hide()
    }

}