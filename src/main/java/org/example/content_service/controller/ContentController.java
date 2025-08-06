package org.example.content_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.content_service.dto.ContentResponse;
import org.example.content_service.dto.CreateContentRequest;
import org.example.content_service.dto.UpdateContentRequest;
import org.example.content_service.models.Content;
import org.example.content_service.service.ContentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@SecurityRequirement(name = "BearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/content")
@Tag(name = "Контент", description = "Управление контентом и файлами")
public class ContentController {

    private final ContentService contentService;

    @Operation(summary = "Создать контент")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Контент успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера при сохранении файла")
    })
    @PostMapping(value = "/createContent", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ContentResponse> createContent(
            @ModelAttribute CreateContentRequest request,
            @RequestPart(value = "file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(contentService.createContent(request, file));
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Обновить контент")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Контент обновлён"),
            @ApiResponse(responseCode = "404", description = "Контент не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PutMapping(value = "/updateContent/{contentId}")
    public ResponseEntity<ContentResponse> updateContent(@PathVariable long contentId,
                                                         @RequestBody UpdateContentRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(contentService.updateContent(contentId, request));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Удалить контент")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Контент удалён"),
            @ApiResponse(responseCode = "404", description = "Контент не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @DeleteMapping("/deleteContent/{contentId}")
    public ResponseEntity<Void> deleteContent(@PathVariable long contentId) {
        try {
            contentService.deleteContent(contentId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Получить контент по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Контент найден"),
            @ApiResponse(responseCode = "404", description = "Контент не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @GetMapping("/getContent/{contentId}")
    public ResponseEntity<ContentResponse> getContent(@PathVariable long contentId) {
        try {
            return ResponseEntity.ok(contentService.getContent(contentId));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Скачать файл контента")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Файл скачан"),
            @ApiResponse(responseCode = "404", description = "Контент не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @GetMapping("/{contentId}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long contentId) {
        try {
            Content content = contentService.getContentById(contentId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; " +
                            "fileName=\"" + content.getFileName() + "\"")
                    .body(content.getFileData());
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Получить список контента с фильтрацией")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список получен"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @GetMapping("/getContent")
    public ResponseEntity<List<ContentResponse>> getContent(
            @Parameter(description = "ID темы") @RequestParam(required = false) Long topicId,
            @Parameter(description = "ID предмета") @RequestParam(required = false) Long subjectId,
            @Parameter(description = "ID урока") @RequestParam(required = false) Long lessonId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(contentService.getContent(topicId, subjectId, lessonId));
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
