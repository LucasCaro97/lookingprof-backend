package com.lookingprof.lookingProf.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class CommentDTORequest {

    String description;
    Integer qualification;
    Integer userOrigin;
    Integer userDestination;


}
