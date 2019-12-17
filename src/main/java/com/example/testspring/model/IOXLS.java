package com.example.testspring.model;

import javax.persistence.*;

@Entity
@Table(name = "fileupload")
public class IOXLS extends AuditModel {
    @Id
    @GeneratedValue(generator = "fileupload_generator")
    @SequenceGenerator(
            name = "fileupload_generator",
            sequenceName = "fileupload_sequence",
            initialValue = 1000
    )
    private Long id;

    @Column(columnDefinition = "text")
    private String path;

    @Column(columnDefinition = "text")
    private String status;

//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "question_id", nullable = false)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JsonIgnore
//    private Question question;

    // Getters and Setters (Omitted for brevity)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public IOXLS setPath(String path) {
        this.path = path;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public IOXLS setStatus(String status) {
        this.status = status;
        return this;
    }

//    public Question getQuestion() {
//        return question;
//    }
//
//    public void setQuestion(Question question) {
//        this.question = question;
//    }
}