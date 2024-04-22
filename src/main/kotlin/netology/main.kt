package ru.netology

import java.util.*

data class Post(
    val id: Int,
    val ownerId: Int,
    val createdBy: Int = 5,
    val date: Int,
    val text: String,
    val comments: Comment? = null,
    val canPin: Boolean? = null,
    val canDelete: Boolean? = null,
    val canEdit: Boolean = false,
    val postType: String = "post",
    val attachments: Array<Attachment>? = null,
)

data class Note(
    val id: Int,
    val ownerId: Int,
    val title: String,
    val text: String,
    val comments: MutableList<Comment> = mutableListOf(),
    var deleted: Boolean = false
)

class NoteNotFoundException(message: String) : Exception(message)
class PostNotFoundException(message: String) : Exception(message)

interface Attachment {
    val type: String
}

data class PhotoAttachment(
    val photo: Photo
) : Attachment {
    override val type: String = "photo"
}

data class VideoAttachment(
    val video: Video
) : Attachment {
    override val type: String = "video"
}

data class AudioAttachment(
    val audio: Audio
) : Attachment {
    override val type: String = "audio"
}

data class FileAttachment(
    val file: File
) : Attachment {
    override val type: String = "file"
}

data class GeoAttachment(
    val geo: Geo
) : Attachment {
    override val type: String = "geo"
}

data class Photo(
    val id: Int,
    val ownerId: Int,
    val userId: Int,
    val text: String,
)

data class Video(
    val id: Int,
    val ownerId: Int,
    val title: String,
    val duration: Int
)

data class Audio(
    val id: Int,
    val ownerId: Int,
    val artist: String,
    val duration: Int,
    val url: String
)

data class File(
    val id: Int,
    val ownerId: Int,
    val title: String,
    val size: Int,
    val url: String
)

data class Geo(
    val type: String,
    val coordinates: Objects,
    val place: Objects
)

data class Comment(
    val id: Int,
    val from_id: Int,
    val postId: Int,
    val date: Int,
    var text: String,
    val count: Int = 0,
    val canPost: Boolean = false,
    var deleted: Boolean = false,

    )

object WallService {

    private var nextId = 1
    private var posts = emptyArray<Post>()
    private var comments = emptyArray<Comment>()

    fun add(post: Post): Post {
        val newPost = post.copy(id = nextId++)
        posts += newPost
        return newPost
    }

    fun update(post: Post): Boolean {
        val index = posts.indexOfFirst { it.id == post.id }
        if (index != -1) {
            posts = posts.copyOf()
            posts[index] = post
            return true
        }
        return false
    }

    fun createComment(postId: Int, comment: Comment): Comment {
        try {
            val post = posts.first { it.id == postId }
            val newComment = comment.copy(id = comments.size + 1)
            comments += newComment
            return newComment
        } catch (e: RuntimeException) {
            throw PostNotFoundException("Поста с таким Id $postId не существует.")
        }
    }

    fun clear() {
        posts = emptyArray()
        nextId = 1
    }
}

object NoteService {

    private var nextId = 1
    private var notes = emptyArray<Note>()

    fun add(note: Note): Note {
        val newNote = note.copy(id = nextId++)
        notes += newNote
        return newNote
    }

    fun update(note: Note): Boolean {
        val index = notes.indexOfFirst { it.id == note.id }
        if (index != -1) {
            notes = notes.copyOf()
            notes[index] = note
            return true
        }
        return false
    }

    fun createComment(noteId: Int, comment: Comment): Comment {
        try {
            val note = notes.first { it.id == noteId }
            val newComment = comment.copy(id = note.comments.size + 1)
            note.comments.add(newComment)
            return newComment
        } catch (e: NoSuchElementException) {
            throw NoteNotFoundException("Заметки с таким Id $noteId не существует.")
        }
    }

    fun editComment(commentId: Int, newText: String): Boolean {
        val comment = notes.flatMap { it.comments }.find { it.id == commentId && !it.deleted }
        return if (comment != null) {
            comment.text = newText
            true
        } else {
            false
        }
    }

    fun deleteComment(commentId: Int): Boolean {
        val comment = notes.flatMap { it.comments }.find { it.id == commentId && !it.deleted }
        return if (comment != null) {
            comment.deleted = true
            true
        } else {
            false
        }
    }

    fun restoreComment(commentId: Int): Boolean {
        val comment = notes.flatMap { it.comments }.find { it.id == commentId && it.deleted }
        return if (comment != null) {
            comment.deleted = false
            true
        } else {
            false
        }
    }

    fun getCommentsForNote(noteId: Int): List<Comment> {
        val note = notes.find { it.id == noteId }
        return note?.comments?.filter { !it.deleted } ?: emptyList()
    }

    fun getUserNotes(userId: Int): List<Note> {
        return notes.filter { it.ownerId == userId && !it.deleted }
    }

    fun delete(noteId: Int): Boolean {
        val index = notes.indexOfFirst { it.id == noteId }
        if (index != -1) {
            notes[index].deleted = true
            return true
        }
        return false
    }

    fun restore(noteId: Int): Boolean {
        val index = notes.indexOfFirst { it.id == noteId }
        if (index != -1) {
            notes[index].deleted = false
            return true
        }
        return false
    }

    // Метод возвращает заметку по ее id при условии что она не была удалена, если заметка удалена то возвращается null
    fun getNoteById(id: Int): Note? {
        return notes.find { it.id == id && !it.deleted }
    }

    fun clear() {
        notes = emptyArray()
        nextId = 1
    }

}
