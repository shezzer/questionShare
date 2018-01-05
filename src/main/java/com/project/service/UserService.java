package com.project.service;

import com.project.dao.LoginTicketDao;
import com.project.dao.UserDao;
import com.project.model.LoginTicket;
import com.project.model.User;
import com.project.util.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Sherl on 2017/12/12.
 */
@Service
public class UserService {
    @Autowired
    UserDao userDao;
    @Autowired
    LoginTicketDao loginTicketDao;

    public User getUser(int id){
        return userDao.selectById(id);
    }

    public User getUser(String name){
        return userDao.selectByUserName(name);
    }

    public Map<String,String> reg(String name,String password){
        Map<String,String> map = new HashMap<>();
        if(StringUtils.isBlank(name)){
            map.put("msg","用户名不能为空");
        }

        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
        }

        User user = userDao.selectByUserName(name);
        if(user!=null) {
            map.put("msg", "用户名已被注册");
            return map;
        }
        //设置用户各属性并添加用户，给密码加salt
        user = new User();
        user.setName(name);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setPassword(WendaUtil.MD5(password+user.getSalt()));
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        userDao.addUser(user);

        //给注册成功用户加T票
        String ticket = addTicket(user.getId());
        map.put("ticket",ticket);

        return map;
    }

    public Map<String,Object> login(String name,String password){
        Map<String,Object> map = new HashMap<>();
        if(StringUtils.isBlank(name)){
            map.put("msg","用户名不能为空");
        }

        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
        }

        User user = userDao.selectByUserName(name);
        if(user==null) {
            map.put("msg", "用户名不存在");
            return map;
        }

        if(!WendaUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msg","用户名密码不正确");
        }
        String ticket = addTicket(user.getId());
        map.put("ticket",ticket);
        map.put("userId", user.getId());
        return map;
    }

    public String addTicket(int userId){
        String ticket = UUID.randomUUID().toString().replace("-","");
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket(ticket);
        loginTicket.setStatus(0);
        loginTicket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        loginTicket.setExpired(date);
        loginTicketDao.addTicket(loginTicket);
        return loginTicket.getTicket();
    }

    public void logout(String ticket) {
        loginTicketDao.updateStatus(ticket, 1);
    }
}
