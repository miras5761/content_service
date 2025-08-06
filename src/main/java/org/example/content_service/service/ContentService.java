package org.example.content_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.content_service.dto.ContentResponse;
import org.example.content_service.dto.CreateContentRequest;
import org.example.content_service.dto.UpdateContentRequest;
import org.example.content_service.mapper.ContentMapper;
import org.example.content_service.models.Content;
import org.example.content_service.models.Lesson;
import org.example.content_service.models.Subject;
import org.example.content_service.models.Topic;
import org.example.content_service.repository.ContentRepository;
import org.example.content_service.repository.LessonRepository;
import org.example.content_service.repository.SubjectRepository;
import org.example.content_service.repository.TopicRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;
    private final LessonRepository lessonRepository;
    private final TopicRepository topicRepository;
    private final SubjectRepository subjectRepository;
    private final ContentMapper contentMapper;

    public ContentResponse createContent(CreateContentRequest request, MultipartFile file) throws IOException {
        Content content = contentMapper.fromCreateRequest(request);
        List<Lesson> lessons = lessonRepository.findAllById(request.getLessonIds());
        content.setLessons(new HashSet<>(lessons));
        content.setFileData(file.getBytes());
        content.setFileName(file.getOriginalFilename());
        contentRepository.save(content);
        return contentMapper.toResponse(content);
    }

    public ContentResponse updateContent(Long contentId,
                                         UpdateContentRequest request) {
        Content content = getContentById(contentId);
        contentMapper.updateFromRequest(request, content);
        if (request.getLessonIds() != null && !request.getLessonIds().isEmpty()) {
            List<Lesson> lessons = lessonRepository.findAllById(request.getLessonIds());
            content.setLessons(new HashSet<>(lessons));
        }
        contentRepository.save(content);
        return contentMapper.toResponse(content);
    }

    public void deleteContent(long contentId) {
        getContentById(contentId);
        contentRepository.deleteById(contentId);
    }

    public ContentResponse getContent(long contentId) {
        Content content = getContentById(contentId);
        return contentMapper.toResponse(content);
    }

    public List<ContentResponse> getContent(Long topicId, Long subjectId, Long lessonId) {
        Lesson lesson = getOrEmptyIfIdInvalid(lessonId, lessonRepository);
        Topic topic = getOrEmptyIfIdInvalid(topicId, topicRepository);
        Subject subject = getOrEmptyIfIdInvalid(subjectId, subjectRepository);
        if (isInvalid(lessonId, lesson) || isInvalid(topicId, topic) || isInvalid(subjectId, subject)) {
            return List.of();
        }
        return contentRepository.findAll()
                .stream()
                .filter(content -> {
                    if (lesson != null) {
                        return content.getLessons().stream()
                                .anyMatch(l -> l.equals(lesson));
                    }
                    return true;
                })
                .filter(content -> {
                    if (topic != null) {
                        return content.getLessons().stream()
                                .anyMatch(l -> l.getTopic().equals(topic));
                    }
                    return true;
                })
                .filter(content -> {
                    if (subject != null) {
                        return content.getLessons().stream()
                                .anyMatch(l -> l.getTopic().getSubject().equals(subject));
                    }
                    return true;
                })
                .map(contentMapper::toResponse)
                .sorted(Comparator.comparing(ContentResponse::getId))
                .toList();
    }

    public Content getContentById(long contentId) {
        return contentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("Content with id: " + contentId + " not found"));

    }

    private <T, ID> T getOrEmptyIfIdInvalid(ID id, JpaRepository<T, ID> repository) {
        return (id != null) ? repository.findById(id).orElse(null) : null;
    }


    private boolean isInvalid(Object id, Object entity) {
        return id != null && entity == null;
    }

}
