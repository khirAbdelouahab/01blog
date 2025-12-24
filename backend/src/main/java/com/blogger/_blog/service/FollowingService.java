package com.blogger._blog.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.blogger._blog.Repository.SubscribeRepository;
import com.blogger._blog.Repository.UserRepository;
import com.blogger._blog.details.OtherUserData;
import com.blogger._blog.details.ProfileFollowStats;
import com.blogger._blog.details.SubscribeData;
import com.blogger._blog.details.UserDataResponse;
import com.blogger._blog.model.Subscribe;
import com.blogger._blog.model.User;

@Service
public class FollowingService {
    @Autowired
    private SubscribeRepository subscribeRepository;
    @Autowired
    private UserRepository userRepository;
    public List<OtherUserData> findOthers(String username) {
        List<OtherUserData> users = this.userRepository.findOthers(username);
        return users;
    }
    public SubscribeData Subsrcibe(String senderName,String recieverName) {
        User sender = userRepository.findByUsername(senderName).orElse(null);
        User reciever = userRepository.findByUsername(recieverName).orElse(null);
        if (reciever == null || sender == null) {
            return null;
        }
        Subscribe checkExistSubscribe = this.subscribeRepository.findBySenderAndReciever(sender.getId(),reciever.getId());
        if (checkExistSubscribe == null) {
            Subscribe subscribe = new Subscribe(sender, reciever);
            this.subscribeRepository.save(subscribe);
            SubscribeData subscribeData = new SubscribeData();
            subscribeData.setSenderName(sender.getUsername());
            subscribeData.setReceiverName(reciever.getUsername());
            subscribeData.setIsFollower(true);
            return subscribeData;
        } else {
            this.subscribeRepository.delete(checkExistSubscribe);
            SubscribeData subscribeData = new SubscribeData();
            subscribeData.setSenderName(sender.getUsername());
            subscribeData.setReceiverName(reciever.getUsername());
            subscribeData.setIsFollower(false);
            return subscribeData;
        }
    }

    public SubscribeData UnSubsrcibe(String senderName,String recieverName) {
        User sender = userRepository.findByUsername(senderName).orElse(null);
        User reciever = userRepository.findByUsername(recieverName).orElse(null);
        if (reciever == null || sender == null) {
            return null;
        }
        Subscribe checkExistSubscribe = this.subscribeRepository.findBySenderAndReciever(sender.getId(),reciever.getId());
        if (checkExistSubscribe == null) {
            Subscribe subscribe = new Subscribe(sender, reciever);
            this.subscribeRepository.save(subscribe);
            SubscribeData subscribeData = new SubscribeData();
            subscribeData.setSenderName(sender.getUsername());
            subscribeData.setReceiverName(reciever.getUsername());
            subscribeData.setIsFollower(true);
            return subscribeData;
        } else {
            this.subscribeRepository.delete(checkExistSubscribe);
           SubscribeData subscribeData = new SubscribeData();
            subscribeData.setSenderName(sender.getUsername());
            subscribeData.setReceiverName(reciever.getUsername());
            subscribeData.setIsFollower(false);
            return subscribeData;
        }
    }
    
    public boolean IsFollower(Long followerName, Long FollowingName) {
        Subscribe s = this.subscribeRepository.findBySenderAndReciever(followerName, FollowingName);
        return s != null;
    }
    private List<UserDataResponse> convert(List<User> users) {
        List<UserDataResponse> result = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            result.add(new UserDataResponse(user.getId(), user.getUsername(), user.getFullname(), user.getAvatar(),user.getAbout(),user.getState()));
        }
        return result;
    }
    public List<User> getAllSubscribersByUser(String username) {
        List<Subscribe> subscribs = this.subscribeRepository.findSubscribersByUser(username);
        List<User> users = new ArrayList<>();
        for (int i = 0; i < subscribs.size(); i++) {
            User user = subscribs.get(i).getSender();
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }

    public ProfileFollowStats getUserProfileStats(String username) {
        return this.subscribeRepository.getUserState(username);
    }

    public boolean isSubscribedByMe(String MyName, String OtherName) {
        return this.subscribeRepository.isSubscribedByMe(MyName, OtherName);
    }
}
