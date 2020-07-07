package com.example.firebasepresentation.firebase

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.Observable

class FirebaseManager {

    private val firebaseDatabase: FirebaseFirestore = Firebase.firestore

    //Simple methods:

    fun set(value: Any, key: String, collectionPath: String) {
        firebaseDatabase.collection(collectionPath).document(key).set(value)
    }

    fun <T> get(firebaseKey: String, clazz: Class<out T>): List<T> {
        val documents = firebaseDatabase.collection(firebaseKey).get().result?.documents
        val values = mutableListOf<T>()
        if (documents != null) {
            for (document in documents) {
                val value = document.toObject(clazz)
                if (value != null) {
                    values.add(value)
                }
            }
        }
        return values
    }

    fun <T> getObservable(firebaseKey: String, clazz: Class<out T>): Observable<List<T>> {

        val observableDocuments = Observable.create<List<T>> { emitter ->
            val collection: CollectionReference? = firebaseDatabase.collection(firebaseKey)
            collection?.let {
                it.get()
                    .addOnSuccessListener { result ->
                        val values = mutableListOf<T>()
                        for (document in result.documents) {
                            val value = document.toObject(clazz)
                            if (value != null) {
                                values.add(value)
                            }
                        }
                        emitter.onNext(values)
                        emitter.onComplete()
                    }
                    .addOnFailureListener { error ->
                        emitter.onError(error)
                    }
            }
        }
        return observableDocuments
    }

    fun <T> getWhereEqualsToObservable(
        firebaseKey: String,
        field: String,
        equalsTo: Any,
        clazz: Class<out T>
    ): Observable<List<T>> {

        val observableDocuments = Observable.create<List<T>> { emitter ->
            var collection: CollectionReference? = firebaseDatabase.collection(firebaseKey)

            collection?.let {
                it.whereEqualTo(field, equalsTo).get()
                    .addOnSuccessListener { result ->
                        val values = mutableListOf<T>()
                        for (document in result.documents) {
                            val value = document.toObject(clazz)
                            if (value != null) {
                                values.add(value)
                            }
                        }
                        emitter.onNext(values)
                        emitter.onComplete()
                    }
                    .addOnFailureListener { error ->
                        emitter.onError(error)
                    }
            }
        }
        return observableDocuments
    }

    fun <T> changes(firebaseKey: String, clazz: Class<out T>): Observable<List<T>> {
        val observableDocuments = Observable.create<List<T>> { emitter ->
            var collection: CollectionReference? = firebaseDatabase.collection(firebaseKey)
            val listener = collection?.addSnapshotListener { snapshot, _ ->
                val values = mutableListOf<T>()

                if (snapshot?.documents != null) {
                    for (document in snapshot.documents) {
                        val value = document.toObject(clazz)
                        if (value != null) {
                            values.add(value)
                        }
                    }
                    emitter.onNext(values)
                }
            }
            emitter.setCancellable {
                listener?.remove()
            }
        }
        return observableDocuments
    }

    fun <T> changesWhereEqualsTo(
        firebaseKey: String,
        field: String,
        equalsTo: Any,
        clazz: Class<out T>
    ): Observable<List<T>> {

        val observableDocuments = Observable.create<List<T>> { emitter ->
            var collection: CollectionReference? = firebaseDatabase.collection(firebaseKey)
            val listener = collection?.addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    it.query.whereEqualTo(field, equalsTo)
                        .get()
                        .addOnSuccessListener { result ->
                            val values = mutableListOf<T>()
                            for (document in result.documents) {
                                val value = document.toObject(clazz)
                                if (value != null) {
                                    values.add(value)
                                }
                            }
                            emitter.onNext(values)
                        }
                        .addOnFailureListener { error ->
                            emitter.onError(error)
                        }
                }
            }
            emitter.setCancellable {
                listener?.remove()
            }
        }
        return observableDocuments
    }

    fun updateDocument(
        collectionPath: String,
        documentKey: String,
        field: String,
        value: Any
    ) {
        firebaseDatabase.collection(collectionPath).document(documentKey).update(field, value)
    }

    fun delete(collectionPath: String, documentKey: String): Observable<Unit> {
        return Observable.create { emitter ->
            firebaseDatabase.collection(collectionPath).document(documentKey).delete()
                .addOnSuccessListener {
                    emitter.onNext(Unit)
                    emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }

    fun set(value: Any, collectionPath: String) {
        firebaseDatabase.collection(collectionPath).add(value)
    }

    fun setObservable(
        value: Any,
        key: String,
        collectionPath: String
    ): Observable<Unit> {
        return Observable.create { emitter ->
            firebaseDatabase.collection(collectionPath).document(key).set(value)
                .addOnSuccessListener {
                    emitter.onNext(Unit)
                    emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }

    fun updateDocumentObservable(
        collectionPath: String,
        documentKey: String,
        field: String,
        value: Any
    ): Observable<Unit> {
        return Observable.create { emitter ->
            firebaseDatabase.collection(collectionPath).document(documentKey).update(field, value)
                .addOnSuccessListener {
                    emitter.onNext(Unit)
                    emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }
}
