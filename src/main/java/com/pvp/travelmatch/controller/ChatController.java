package com.pvp.travelmatch.controller;

import com.pvp.travelmatch.entity.Message;
import com.pvp.travelmatch.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/send/{receiverId}")
    public Message sendMessage(
            @PathVariable Long receiverId,
            @RequestBody String content
    ) {
        return chatService.sendMessage(receiverId, content);
    }

    @GetMapping("/{userId}")
    public List<Message> getConversation(@PathVariable Long userId) {
        return chatService.getConversation(userId);
    }
}