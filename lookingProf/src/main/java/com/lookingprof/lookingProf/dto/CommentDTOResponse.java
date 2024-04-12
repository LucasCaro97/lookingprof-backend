package com.lookingprof.lookingProf.dto;

import com.lookingprof.lookingProf.model.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class CommentDTOResponse {

    String description;
    Integer qualification;
    String userOrigin;
    String userDestination;
    LocalDate createdAt;

    public CommentDTOResponse(Comment comment) {
        this.description = comment.getDescription();
        this.qualification = comment.getQualification();
        this.userOrigin = comment.getUserOrigin().getFirstName() + " " + comment.getUserOrigin().getLastName();
        this.userDestination = comment.getUserDestination().getFirstName() + " " + comment.getUserDestination().getLastName();
        this.createdAt = comment.getCreatedAt().toLocalDate();
    }
}
