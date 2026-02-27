package com.pvp.travelmatch.service;

import com.pvp.travelmatch.entity.*;
import com.pvp.travelmatch.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final TravelPartnerRepository travelPartnerRepository;

    // 🔥 Send Message
    public Message sendMessage(Long receiverId, String content) {

        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User sender = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // 🔥 Check if they are travel partners
        boolean isPartner = travelPartnerRepository.existsByUserOneAndUserTwoOrUserTwoAndUserOne(
                sender, receiver,
                sender, receiver
        );

        if (!isPartner) {
            throw new RuntimeException("You can only chat with accepted travel partners");
        }

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();

        return messageRepository.save(message);
    }

    // 🔥 Get Conversation
    public List<Message> getConversation(Long otherUserId) {

        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return messageRepository
                .findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(
                        currentUser, otherUser,
                        currentUser, otherUser
                );
    }
}