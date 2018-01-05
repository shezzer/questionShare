package com.project.model;

import org.springframework.stereotype.Component;

/**
 * Created by Sherl on 2017/12/15.
 */
@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<>();

    public void setUsers(User user){
        users.set(user);
    }
    public User getUsers(){
        return users.get();
    }
    public void clear(){
        users.remove();
    }
}
