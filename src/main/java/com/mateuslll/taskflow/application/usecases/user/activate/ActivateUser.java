package com.mateuslll.taskflow.application.usecases.user.activate;

import com.mateuslll.taskflow.application.usecases.mappers.UserMapper;
import com.mateuslll.taskflow.application.usecases.user.UserResponseDTO;
import com.mateuslll.taskflow.common.exceptions.UserNotFoundException;
import com.mateuslll.taskflow.common.messages.ResourceMessages;
import com.mateuslll.taskflow.domain.entities.user.User;
import com.mateuslll.taskflow.domain.repository.DomainUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivateUser implements ActivateUserUseCase {

    private final DomainUserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDTO execute(ActivateUserRequestDTO input) {
        User user = userRepository.findById(input.userId())
                .orElseThrow(() -> new UserNotFoundException(
                        ResourceMessages.USER_NOT_FOUND_BY_ID.format(input.userId())
                ));
        user.activate();

        User activatedUser = userRepository.save(user);

        return userMapper.toResponse(activatedUser);
    }
}
