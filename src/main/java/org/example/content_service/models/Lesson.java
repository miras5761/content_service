package org.example.content_service.models;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"topic", "contents"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @ManyToMany(mappedBy = "lessons")
    private Set<Content> contents;
}
