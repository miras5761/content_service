package org.example.content_service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.content_service.models.ContentType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentResponse {

    private Long id;
    private String title;
    private String description;
    private ContentType contentType;
    private Long authorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Long> lessonIds;
}
