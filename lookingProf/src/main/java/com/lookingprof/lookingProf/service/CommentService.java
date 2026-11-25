package com.lookingprof.lookingProf.service;

import com.lookingprof.lookingProf.dto.CommentDTORequest;
import com.lookingprof.lookingProf.dto.CommentDTOResponse;
import com.lookingprof.lookingProf.model.Comment;
import com.lookingprof.lookingProf.model.Enum.Role;
import com.lookingprof.lookingProf.model.User;
import com.lookingprof.lookingProf.repository.ICommentRepository;
import com.lookingprof.lookingProf.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {

    private final ICommentRepository iCommentRepository;
    private final IUserRepository userRepository;


    @Override
    @Transactional(readOnly = true)
    public List<CommentDTOResponse> findByUserOrigin(Integer user) {
        User userOrigin = userRepository.findById(user).get();
        List<Comment> comments = iCommentRepository.findByUserOrigin(userOrigin);
        List<CommentDTOResponse> commentDTOResponseList = new ArrayList<>();
        comments.forEach(comment -> {
            CommentDTOResponse commentDTOResponse = new CommentDTOResponse(comment);
            commentDTOResponseList.add(commentDTOResponse);
        });
        return commentDTOResponseList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDTOResponse> findByUserDestination(Integer userId) {
        User userDestination = userRepository.findById(userId).get();
        List<Comment> comments = iCommentRepository.findByUserDestination(userDestination);
        List<CommentDTOResponse> commentDTOResponseList = new ArrayList<>();
        comments.forEach(comment -> {
            CommentDTOResponse commentDTOResponse = new CommentDTOResponse(comment);
            commentDTOResponseList.add(commentDTOResponse);
        });
        Collections.sort(commentDTOResponseList, (c1,c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()));
        return commentDTOResponseList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDTOResponse> findByUserDestinationAndQualification(Integer idUser, Integer qualify) {
        User userDestination = userRepository.findById(idUser).get();
        List<Comment> comments = iCommentRepository.findByUserDestinationAndQualification(userDestination,qualify);
        List<CommentDTOResponse> commentDTOResponseList = new ArrayList<>();
        comments.forEach(comment -> {
            CommentDTOResponse commentDTOResponse = new CommentDTOResponse(comment);
            commentDTOResponseList.add(commentDTOResponse);
        });
        return commentDTOResponseList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDTOResponse> listAll() {
        try{
            List<Comment> commentList = iCommentRepository.findAll();
            List<CommentDTOResponse> commentDTOResponseList = new ArrayList<>();
            commentList.forEach(comment -> {
                CommentDTOResponse commentDTOResponse = new CommentDTOResponse(comment);
                commentDTOResponseList.add(commentDTOResponse);
            });

            return commentDTOResponseList;
        }catch (Exception e){
            throw new RuntimeException("Error");
        }
    }

     @Override
    @Transactional
    public CommentDTOResponse createComment(CommentDTORequest dto) {
        return createComment(dto, null);
    }

    @Transactional
    public CommentDTOResponse createComment(CommentDTORequest dto, User currentUser) {
        User origin = currentUser != null
                ? currentUser
                : userRepository.findById(dto.getUserOrigin()).orElseThrow(() -> new IllegalArgumentException("Usuario origen no encontrado"));
        User destination = userRepository.findById(dto.getUserDestination())
                .orElseThrow(() -> new IllegalArgumentException("Profesional no encontrado"));

        if (origin.getIdUser().equals(destination.getIdUser())) {
            throw new IllegalArgumentException("No podés calificarte a vos mismo");
        }
        validateQualification(dto.getQualification());

        Comment comment = new Comment();
        comment.setDescription(dto.getDescription());
        comment.setQualification(dto.getQualification());
        comment.setUserOrigin(origin);
        comment.setUserDestination(destination);
        comment.setCreatedAt(LocalDateTime.now());
        iCommentRepository.save(comment);
        refreshQualification(destination);
        return new CommentDTOResponse(comment);
    }

    @Override
    @Transactional
    public CommentDTOResponse updateComment(Integer id, CommentDTORequest dto) {
        return updateComment(id, dto, null);
    }

    @Transactional
    public CommentDTOResponse updateComment(Integer id, CommentDTORequest dto, User currentUser) {
        Comment comment = iCommentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comentario no encontrado"));
        assertCanMutate(comment, currentUser);
        validateQualification(dto.getQualification());

        comment.setDescription(dto.getDescription());
        comment.setQualification(dto.getQualification());
        comment.setUpdatedAt(LocalDateTime.now());
        iCommentRepository.save(comment);
        refreshQualification(comment.getUserDestination());
        return new CommentDTOResponse(comment);
    }

    @Override
    @Transactional
    public HashMap<String, String> deleteComment(Integer id) {
        return deleteComment(id, null);
    }

    @Transactional
    public HashMap<String, String> deleteComment(Integer id, User currentUser) {
        Comment comment = iCommentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comentario no encontrado"));
        assertCanMutate(comment, currentUser);
        User destination = comment.getUserDestination();
        iCommentRepository.deleteById(id);
        refreshQualification(destination);
        HashMap<String, String> response = new HashMap<>();
        response.put("message", "Comment was deleted succesfully");
        return response;
    }

    private void assertCanMutate(Comment comment, User currentUser) {
        if (currentUser == null) {
            return;
        }
        boolean owner = comment.getUserOrigin() != null && comment.getUserOrigin().getIdUser().equals(currentUser.getIdUser());
        boolean admin = currentUser.getRole() == Role.ADMIN;
        if (!owner && !admin) {
            throw new AccessDeniedException("No autorizado");
        }
    }

    private void validateQualification(Integer qualification) {
        if (qualification == null || qualification < 1 || qualification > 5) {
            throw new IllegalArgumentException("La calificación debe ser un valor entre 1 y 5");
        }
    }

    private void refreshQualification(User destination) {
        if (destination == null) {
            return;
        }
        List<Comment> comments = iCommentRepository.findByUserDestination(destination);
        if (comments.isEmpty()) {
            destination.setQualification(null);
        } else {
            int average = (int) Math.round(comments.stream()
                    .map(Comment::getQualification)
                    .filter(Objects::nonNull)
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0));
            destination.setQualification(average);
        }
        userRepository.save(destination);
    }

    @Override
    public Optional<CommentDTOResponse> findById(Integer id) {
        try{
            Comment comment = iCommentRepository.findById(id).get();
            CommentDTOResponse commentDTOResponse = new CommentDTOResponse(comment);
            return Optional.of(commentDTOResponse);
        }catch (Exception e){
            throw new RuntimeException("Comment not found");
        }

    }



}
