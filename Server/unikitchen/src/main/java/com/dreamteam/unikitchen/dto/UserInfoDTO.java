package com.dreamteam.unikitchen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private Long id;
    private String username;
    private String bio;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

