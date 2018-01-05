package com.project.async.handler;

import com.project.async.EventHandler;
import com.project.async.EventModel;
import com.project.async.EventType;
import com.project.model.EntityType;
import com.project.model.Feed;
import com.project.model.Message;
import com.project.service.MessageService;
import com.project.service.UserService;
import com.project.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Sherl on 2017/12/27.
 */
@Component
public class FeedHandler implements EventHandler{
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel model) {
        Feed feed = new Feed();
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setToId(model.getEntityOwnerId());
        message.setCreatedDate(new Date());
        if (model.getEntityType() == EntityType.ENTITY_QUESTION)
            message.setContent("用户"+userService.getUser(model.getActorId()).getName()
                +"关注了你的问题，http://localhost:8080/question/"+model.getEntityId());
        else if(model.getEntityType() == EntityType.ENTITY_USER)
            message.setContent("用户"+userService.getUser(model.getActorId()).getName()
                    +"关注了你，http://localhost:8080/user/"+model.getActorId());
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
