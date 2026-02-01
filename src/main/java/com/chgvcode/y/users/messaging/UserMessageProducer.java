package com.chgvcode.y.users.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.chgvcode.y.users.dto.UserMessage;
import com.chgvcode.y.users.model.User;
import com.chgvcode.y.users.model.UserEntity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserMessageProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendUserCreated(User user) {
        UserMessage userMessage = new UserMessage(
            user.getUuid(),
            user.getUsername()
        );
        rabbitTemplate.convertAndSend("userCreationQueue", userMessage);
    }

    public void sendUserDeleted(UserEntity userEntity) {
        UserMessage userMessage = new UserMessage(
            userEntity.getUuid(),
            userEntity.getUsername()
        );
        rabbitTemplate.convertAndSend("userDeletionQueue", userMessage);
    }
}
