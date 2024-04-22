package netology

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import ru.netology.Comment
import ru.netology.Note
import ru.netology.NoteNotFoundException
import ru.netology.NoteService


class NoteServiceTest {

    // Объявление переменнрой для создания экземпляра класса с объявлением непосредственно в тесте
    // lateinit ключевое слово для объявления переменной позже
    private lateinit var noteService: NoteService

    @Before
    fun clearBeforeTest() {
        noteService = NoteService
        NoteService.clear()
    }

    @Test
    fun add() {
        val note = Note(
            id = 1,
            ownerId = 1,
            title = "Тестовая заметка",
            text = "Это текст тестовой заметки"
        )
        val addedNote = noteService.add(note)
        assertEquals(note, addedNote)
    }

    @Test
    fun update() {
        val note = Note(
            id = 1,
            ownerId = 1,
            title = "Тестовая заметка",
            text = "Это текст тестовой заметки"
        )
        noteService.add(note)

        val updatedNote = note.copy(text = "Обновлениетестовой заметки")
        assertTrue(noteService.update(updatedNote))

        val retrievedNote = noteService.getNoteById(1)
        assertEquals(updatedNote, retrievedNote)
    }

    @Test(expected = NoteNotFoundException::class)
    fun createCommentOnNonExistentNote() {
        val comment = Comment(
            id = 1,
            from_id = 1,
            postId = 1,
            date = 123456,
            text = "Тестовый комментарий"
        )
        noteService.createComment(1, comment)
    }

    @Test
    fun deleteNote() {
        val note = Note(
            id = 1,
            ownerId = 1,
            title = "Тестовая заметка",
            text = "Это текст тестовой заметки"
        )
        noteService.add(note)

        assertTrue(noteService.delete(1))

        assertNull(noteService.getNoteById(1))
    }

    @Test
    fun restoreNote() {
        val note = Note(
            id = 1,
            ownerId = 1,
            title = "Тестовая заметка",
            text = "Это текст тестовой заметки"
        )
        noteService.add(note)
        noteService.delete(1)

        assertTrue(noteService.restore(1))

        val restoredNote = noteService.getNoteById(1)
        assertNotNull(restoredNote)
        assertFalse(restoredNote!!.deleted)
    }

    @Test
    fun editComment() {
        val note = Note(
            id = 1,
            ownerId = 1,
            title = "Тестовая заметка",
            text = "Это текст тестовой заметки"
        )
        noteService.add(note)

        val comment = Comment(
            id = 1,
            from_id = 1,
            postId = 1,
            date = 123456,
            text = "Тестовый комментарий"
        )
        noteService.createComment(1, comment)

        assertTrue(noteService.editComment(1, "Обновленный комментарий"))

        val editedComment = noteService.getNoteById(1)?.comments?.find { it.id == 1 }
        assertEquals("Обновленный комментарий", editedComment?.text)
    }

    @Test
    fun deleteComment() {
        val note = Note(
            id = 1,
            ownerId = 1,
            title = "Тестовая заметка",
            text = "Это тестовая заметка"
        )
        noteService.add(note)

        val comment = Comment(
            id = 1,
            from_id = 1,
            postId = 1,
            date = 123456,
            text = "Тестовый комментарий"
        )
        noteService.createComment(1, comment)

        assertTrue(noteService.deleteComment(1))

        val deletedComment = noteService.getNoteById(1)?.comments?.find { it.id == 1 }
        assertTrue(deletedComment?.deleted ?: false)
    }

    @Test
    fun restoreComment() {
        val note = Note(
            id = 1,
            ownerId = 1,
            title = "Тестовая заметка",
            text = "Это тестовая заметка"
        )
        noteService.add(note)

        val comment = Comment(
            id = 1,
            from_id = 1,
            postId = 1,
            date = 123456,
            text = "Тестовая заметка"
        )
        noteService.createComment(1, comment)
        noteService.deleteComment(1)

        assertTrue(noteService.restoreComment(1))

        val restoredComment = noteService.getNoteById(1)?.comments?.find { it.id == 1 }
        assertFalse(restoredComment?.deleted ?: true)
    }

    @Test
    fun getCommentsForNote() {
        val note = Note(
            id = 1,
            ownerId = 1,
            title = "Тестовая заметка",
            text = "Это тестовая заметка"
        )
        noteService.add(note)

        val comment1 = Comment(
            id = 1,
            from_id = 1,
            postId = 1,
            date = 123456,
            text = "Тестовый комментарий 1"
        )
        val comment2 = Comment(
            id = 2,
            from_id = 2,
            postId = 1,
            date = 123457,
            text = "Тестовый комментарий 2"
        )

        noteService.createComment(1, comment1)
        noteService.createComment(1, comment2)

        val comments = noteService.getCommentsForNote(1)
        assertEquals(2, comments.size)
    }

    @Test
    fun getUserNotes() {
        val note1 = Note(
            id = 1,
            ownerId = 1,
            title = "Тестовая заметка 1",
            text = "Это тестовая заметка 1"
        )
        val note2 = Note(
            id = 2,
            ownerId = 2,
            title = "Тестовая заметка 2",
            text = "Это тестовая заметка 2"
        )

        noteService.add(note1)
        noteService.add(note2)

        val user1Notes = noteService.getUserNotes(1)
        assertEquals(1, user1Notes.size)
        assertEquals(1, user1Notes.first().id)
    }
}