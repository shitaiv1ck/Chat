package com.example.chatmessenger.chatroom;

import com.example.chatmessenger.friendship.FriendshipServiceImpl;
import com.example.chatmessenger.user.entity.Client;
import com.example.chatmessenger.user.repository.UserRepository;

public class ChatRoomServiceImpl implements ChatRoomService{
    private ChatRoomRepository chatRoomRepository;

    public ChatRoomServiceImpl(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    @Override
    public ChatRoom createChat(Client client1, Client client2) {
        ChatRoom chatRoom = new ChatRoom(client1, client2);

        if (chatRoomRepository.findChatRoomByClient(chatRoom) == 0) {
            chatRoomRepository.saveChatRoom(chatRoom);
        }

        chatRoom.setChatId(chatRoomRepository.findChatRoomByClient(chatRoom));

        return chatRoom;
    }
}
