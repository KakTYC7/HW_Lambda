package netology

import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import ru.netology.ChatService


class ChatServiceTest {

    private lateinit var chatService: ChatService

    @Before
    fun setUp() {
        chatService = ChatService()
    }

    @Test
    fun testCreateChat() {
        val chat = chatService.createChat(1)
        assertNotNull(chat)
    }

    @Test
    fun testDeleteChat() {
        val chat = chatService.createChat(1)
        val chatId = chat.id
        chatService.deleteChat(chatId)
        assertEquals(0, chatService.getChats().size)
    }

    @Test
    fun testGetChats() {
        chatService.createChat(1)
        chatService.createChat(2)
        assertEquals(2, chatService.getChats().size)
    }

    @Test
    fun testGetUnreadChatsCount() {
        chatService.createChat(1)
        chatService.createChat(1)
        chatService.createMessage(1, 1, "Тестовое сообщение")
        assertEquals(1, chatService.getUnreadChatsCount(1))
    }

    @Test
    fun testGetLatestMessages() {
        chatService.createChat(1)
        chatService.createMessage(1, 1, "Первое сообщение")
        chatService.createMessage(1, 1, "Второе сообщение")
        val latestMessages = chatService.getLatestMessages(1)
        assertEquals("Пользователь 1: Второе сообщение", latestMessages[0])
    }


    @Test
    fun testCreateMessage() {
        val message = chatService.createMessage(1, 1, "Тестовое сообщение")
        assertNotNull(message)
    }

    @Test
    fun testDeleteMessage() {
        val message = chatService.createMessage(1, 1, "Тестовое сообщение")
        val messageId = message.id
        chatService.deleteMessage(messageId)
        assertEquals(0, chatService.getChats().size)
    }

    @Test
    fun testMarkAllMessagesAsRead() {
        chatService.createChat(1)
        chatService.createMessage(1, 1, "Тестовое сообщение")
        chatService.markAllMessagesAsRead(1)
        assertEquals(0, chatService.getUnreadChatsCount(1))
    }

    @Test
    fun testGetMessagesFromChat() {
        val chatId = 1
        val userId = 1
        val count = 2

        chatService.createChat(chatId)
        val message1 = chatService.createMessage(chatId, 2, "Сообщение 1")
        val message2 = chatService.createMessage(chatId, 2, "Сообщение 2")
        val message3 = chatService.createMessage(chatId, 3, "Сообщение 3")

        val resultMessages = chatService.getMessagesFromChat(chatId, userId, count)

        assertEquals(2, resultMessages.size)
        assertTrue(resultMessages.contains(message1))
        assertTrue(resultMessages.contains(message2))
        assertFalse(resultMessages.contains(message3))
        assertTrue(resultMessages.all { it.isRead })
    }

    @Test(expected = IllegalArgumentException::class)
    fun testDeleteChat_exception() {
        chatService.deleteChat(1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testDeleteMessage_exception() {
        chatService.deleteMessage(1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testMarkAllMessagesAsRead_exception() {
        chatService.markAllMessagesAsRead(1)
    }
}
