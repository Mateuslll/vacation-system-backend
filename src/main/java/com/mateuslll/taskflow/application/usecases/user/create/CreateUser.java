package com.mateuslll.taskflow.application.usecases.user.create;

import com.mateuslll.taskflow.application.usecases.mappers.UserMapper;
import com.mateuslll.taskflow.application.usecases.user.UserResponseDTO;
import com.mateuslll.taskflow.common.exceptions.ResourceAlreadyExistsException;
import com.mateuslll.taskflow.common.messages.ResourceMessages;
import com.mateuslll.taskflow.domain.entities.user.User;
import com.mateuslll.taskflow.domain.repository.DomainUserRepository;
import com.mateuslll.taskflow.domain.valueobject.Email;
import com.mateuslll.taskflow.domain.valueobject.Password;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateUser implements CreateUserUseCase {

    private final DomainUserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDTO execute(CreateUserRequestDTO input) {
        Email email = new Email(input.email());
        if (userRepository.existsByEmail(email)) {
            throw new ResourceAlreadyExistsException(
                    ResourceMessages.EMAIL_ALREADY_REGISTERED.getMessage()
            );
        }

        Password password = Password.fromPlainText(input.password());
        User user = new User(email, password, input.firstName(), input.lastName());
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }
}
