package com.example.noteapp.data

import android.util.Log
import com.example.noteapp.data.errors.NoAuthException
import com.example.noteapp.data.model.Note
import com.example.noteapp.data.model.NoteResult
import com.example.noteapp.data.model.User
import com.example.noteapp.extensions.DEBUG_MODE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val NOTES_COLLECTION = "notes"
private const val USERS_COLLECTION = "users"
private val TAG = "${FireStoreProvider::class.java.simpleName} :"

class FireStoreProvider(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : RemoteDataProvider {

    private val currentUser
        get() = firebaseAuth.currentUser

    private fun getUserNotesCollection() = currentUser?.let { user ->
        db.collection(USERS_COLLECTION).document(user.uid).collection(NOTES_COLLECTION)
    } ?: throw NoAuthException()

    @ExperimentalCoroutinesApi
    override suspend fun subscribeToAllNotes(): ReceiveChannel<NoteResult> =
        Channel<NoteResult>(Channel.CONFLATED).apply {
            var registration: ListenerRegistration? = null
            try {
                registration = getUserNotesCollection().addSnapshotListener { snapshot, e ->
                    val value = e?.let { NoteResult.Error(it) }
                        ?: snapshot?.let { query ->
                            val notes =
                                query.documents.map { document -> document.toObject(Note::class.java) }
                            NoteResult.Success(notes)
                        }

                    value?.let { offer(it) }
                }
            } catch (e: Throwable) {
                offer(NoteResult.Error(e))
            }

            invokeOnClose { registration?.remove() }
        }

    override suspend fun saveNote(note: Note): Note =
        suspendCoroutine { continuation ->
            try {
                getUserNotesCollection().document(note.id)
                    .set(note).addOnSuccessListener {
                        if (DEBUG_MODE) Log.d(TAG, "Note $note is saved")
                        continuation.resume(note)
                    }.addOnFailureListener { error ->
                        if (DEBUG_MODE) Log.d(
                            TAG,
                            "Error saving note $note, message: ${error.message}"
                        )
                        continuation.resumeWithException(error)
                    }
            } catch (e: Throwable) {
                continuation.resumeWithException(e)
            }
        }

    override suspend fun getNoteById(id: String): Note =
        suspendCoroutine { continuation ->
            try {
                getUserNotesCollection().document(id).get()
                    .addOnSuccessListener { document ->
                        continuation.resume(document.toObject(Note::class.java)!!)
                    }.addOnFailureListener { error ->
                        continuation.resumeWithException(error)
                    }
            } catch (e: Throwable) {
                continuation.resumeWithException(e)
            }
        }

    override suspend fun getCurrentUser(): User? =
        suspendCoroutine { continuation ->
            currentUser?.let { user ->
                continuation.resume(
                    User(
                        user.displayName ?: "",
                        user.email ?: ""
                    )
                )
            } ?: continuation.resume(null)
        }

    override suspend fun deleteNote(noteId: String): Note? =
        suspendCoroutine { continuation ->
            try {
                getUserNotesCollection()
                    .document(noteId)
                    .delete()
                    .addOnSuccessListener {
                        continuation.resume(null)
                    }
                    .addOnFailureListener { error ->
                        continuation.resumeWithException(error)
                    }
            } catch (e: Throwable) {
                continuation.resumeWithException(e)
            }
        }
}