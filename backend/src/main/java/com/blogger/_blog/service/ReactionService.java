package com.blogger._blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blogger._blog.Repository.ReactionRepository;
import com.blogger._blog.model.Post;
import com.blogger._blog.model.Reaction;
import com.blogger._blog.model.User;

@Service
public class ReactionService {
    @Autowired
    private ReactionRepository reactionRepository;

    public void add(User user,Post post) {
        Reaction reaction = new Reaction(post, user);
        this.reactionRepository.save(reaction);
    }

    public void remove(Long id) {
        this.reactionRepository.deleteById(id);
    }

    public void remove(User user,Post post) {
        this.reactionRepository.deleteByPostandUserIds(post.getId(), user.getId());
    }

    public Reaction get(Long user,Long post) {
        return this.reactionRepository.findByPostAndUsersIds(post, user).orElse(null);
    }

    public Long countsByPostId(Long post) {
        return this.reactionRepository.countByPostId(post);
    }
}
