package com.lookingprof.lookingProf.controller;

import com.lookingprof.lookingProf.config.CurrentUserService;
import com.lookingprof.lookingProf.dto.CommentDTORequest;
import com.lookingprof.lookingProf.dto.CommentDTOResponse;
import com.lookingprof.lookingProf.model.User;
import com.lookingprof.lookingProf.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CurrentUserService currentUserService;

    @GetMapping()
    public ResponseEntity<List<CommentDTOResponse>> getAllComments() {
        return ResponseEntity.ok(commentService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDTOResponse> getById(@PathVariable Integer id) {
        Optional<CommentDTOResponse> comment = commentService.findById(id);
        return comment.map(value -> ResponseEntity.ok().body(value)).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/getByUserDestination/{idUser}")
    public ResponseEntity<List<CommentDTOResponse>> getByDestination(@PathVariable Integer idUser) {
        return ResponseEntity.ok(commentService.findByUserDestination(idUser));
    }

    @GetMapping("/getByUserOrigin/{idUser}")
    public ResponseEntity<List<CommentDTOResponse>> getByOrigin(@PathVariable Integer idUser) {
        return ResponseEntity.ok(commentService.findByUserOrigin(idUser));
    }

    @PostMapping()
    public ResponseEntity<CommentDTOResponse> createComment(@RequestBody CommentDTORequest dto) {
        User current = currentUserService.requireUser();
        CommentDTOResponse createdComment = commentService.createComment(dto, current);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDTOResponse> updateComment(@PathVariable Integer id, @RequestBody CommentDTORequest dto) {
        User current = currentUserService.requireUser();
        CommentDTOResponse updatedComment = commentService.updateComment(id, dto, current);
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HashMap<String, String>> deleteComment(@PathVariable Integer id) {
        User current = currentUserService.requireUser();
        HashMap<String, String> response = commentService.deleteComment(id, current);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getByUserDestinationAndQualification/{idUser}/{qualification}")
    public ResponseEntity<List<CommentDTOResponse>> getByDestination(@PathVariable Integer idUser, @PathVariable Integer qualification) {
        return ResponseEntity.ok(commentService.findByUserDestinationAndQualification(idUser, qualification));
    }
}
