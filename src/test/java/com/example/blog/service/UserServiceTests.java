package com.example.blog.service;

import com.example.blog.dto.UserRegisterDTO;
import com.example.blog.entity.Users;
import com.example.blog.exception.BusinessException;
import com.example.blog.mapper.UserMapper;
import com.example.blog.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTests {

    @Test
    void convertsUniqueConstraintViolationAfterPrecheckIntoConflict() {
        UserMapper mapper = mock(UserMapper.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        when(mapper.selectCount(any())).thenReturn(0L);
        when(encoder.encode("Password123!")).thenReturn("encoded-password");
        doThrow(new DuplicateKeyException("uk_users_username"))
                .when(mapper).insert(any(Users.class));
        UserRegisterDTO request = new UserRegisterDTO();
        request.setUsername("alice");
        request.setPassword("Password123!");

        BusinessException exception = catchThrowableOfType(BusinessException.class,
                () -> new UserServiceImpl(mapper, encoder, mock(TokenService.class)).register(request));

        assertThat(exception.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(exception.getMessage()).doesNotContain("uk_users_username");
        assertThat(exception.getCause()).isInstanceOf(DuplicateKeyException.class);
    }
}
