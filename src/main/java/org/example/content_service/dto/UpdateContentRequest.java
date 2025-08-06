package org.example.content_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.content_service.models.ContentType;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateContentRequest {

    private String title;
    private String description;
    private ContentType contentType;
    private List<Long> lessonIds;

}
