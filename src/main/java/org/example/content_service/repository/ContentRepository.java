package org.example.content_service.repository;

import org.example.content_service.models.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long>{

}
