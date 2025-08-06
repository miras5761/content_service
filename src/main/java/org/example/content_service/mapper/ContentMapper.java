package org.example.content_service.mapper;

import org.example.content_service.dto.ContentResponse;
import org.example.content_service.dto.CreateContentRequest;
import org.example.content_service.dto.UpdateContentRequest;
import org.example.content_service.models.Content;
import org.example.content_service.models.Lesson;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ContentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fileData", ignore = true)
    @Mapping(target = "lessons", ignore = true)
    Content fromCreateRequest(CreateContentRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "fileData", ignore = true)
    @Mapping(target = "lessons", ignore = true)
    void updateFromRequest(UpdateContentRequest request, @MappingTarget Content content);

    @Mapping(target = "lessonIds", expression = "java(mapLessonIds(content.getLessons()))")
    ContentResponse toResponse(Content content);

    default List<Long> mapLessonIds(Set<Lesson> lessons) {
        if (lessons == null) return null;
        return lessons.stream()
                .map(Lesson::getId)
                .collect(Collectors.toList());
    }
}
