package org.example.content_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Запрос на вход или регистрацию")
public class AuthRequest {

    @Schema(description = "Имя пользователя", example = "miras")
    private String username;

    @Schema(description = "Пароль", example = "qwerty123")
    private String password;
}