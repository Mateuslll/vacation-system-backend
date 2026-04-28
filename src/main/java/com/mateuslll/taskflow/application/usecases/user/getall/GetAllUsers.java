package com.mateuslll.taskflow.application.usecases.user.getall;

import com.mateuslll.taskflow.application.usecases.mappers.UserMapper;
import com.mateuslll.taskflow.application.usecases.user.UserResponseDTO;
import com.mateuslll.taskflow.domain.entities.user.User;
import com.mateuslll.taskflow.domain.enums.UserStatus;
import com.mateuslll.taskflow.domain.repository.DomainUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAllUsers implements GetAllUsersUseCase {

    private final DomainUserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> execute(GetAllUsersRequestDTO request) {
        String filtro = request.isAll() ? "ALL" : request.isInactive() ? "INACTIVE" : "ACTIVE";
        log.info("Listando usuários filtro={}", filtro);

        List<User> users;

        if (request.isAll()) {
            users = userRepository.findAll();
        } else if (request.isInactive()) {
            users = userRepository.findByStatus(UserStatus.INACTIVE);
        } else {
            users = userRepository.findByStatus(UserStatus.ACTIVE);
        }

        log.info("Usuários retornados total={}", users.size());

        return users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }
}
