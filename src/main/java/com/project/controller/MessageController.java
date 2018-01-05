package com.project.controller;

import com.project.model.HostHolder;
import com.project.model.Message;
import com.project.model.User;
import com.project.model.ViewObject;
import com.project.service.MessageService;
import com.project.service.UserService;
import com.project.util.WendaUtil;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Sherl on 2017/12/19.
 */
@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    @Autowired
    MessageService messageService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;

    @RequestMapping(path = "/msg/list",method = RequestMethod.GET)
    public String messageDetail(Model model){
        try {
            int localId = hostHolder.getUsers().getId();
            List<Message> conversationList = messageService.getConversationList(localId, 0, 10);
            List<ViewObject> conversations = new ArrayList<>();
            for (Message message : conversationList) {
                ViewObject vo = new ViewObject();
                vo.setObs("message", message);
                int targetId = message.getFromId() == localId ? message.getToId() : message.getFromId();
                User user = userService.getUser(targetId);
                vo.setObs("user", user);
                vo.setObs("unread",messageService.getConvesationUnreadCount(localId,message.getConversationId()));
                conversations.add(vo);
                model.addAttribute("conversations",conversations);
            }
        }catch (Exception e){
            logger.error("显示站内信列表失败"+e.getMessage());
        }
        return "letter";
    }

    @RequestMapping(path = {"/msg/detail"},method = RequestMethod.GET)
    public String conversationDetail(Model model, @Param("conversationId") String conversationId){
        try{
            List<Message> conversationDetail = messageService.getConversationDetail(conversationId, 0, 10);
            List<ViewObject> messages = new ArrayList<>();
            for(Message message : conversationDetail){
                ViewObject vo = new ViewObject();
                vo.setObs("message", message);
                User user = userService.getUser(message.getFromId());
                if (user == null) {
                    continue;
                }
                vo.setObs("headUrl", user.getHeadUrl());
                vo.setObs("userId", user.getId());
                messages.add(vo);
            }
            model.addAttribute("messages",messages);
        }catch (Exception e){
            logger.error("显示站内信详情页出错"+e.getMessage());
        }
        return "letterdetail";
    }

    @RequestMapping(path = "/msg/addMessage",method = RequestMethod.POST)
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,@RequestParam("content") String content){
        try{
            if (hostHolder.getUsers() == null) {
                return WendaUtil.getJSONString(999, "未登录");
            }
            User user = userService.getUser(toName);
            if(user == null){
                return WendaUtil.getJSONString(1,"该用户未登录");
            }
            Message message = new Message();
            message.setContent(content);
            message.setFromId(hostHolder.getUsers().getId());
            message.setToId(user.getId());
            message.setCreatedDate(new Date());
            messageService.addMessage(message);
            return WendaUtil.getJSONString(0);
        }catch (Exception e){
            logger.error("增加站内信失败"+e.getMessage());
            return WendaUtil.getJSONString(1,"添加消息失败");
        }
    }
}
