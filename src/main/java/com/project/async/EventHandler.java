package com.project.async;

import java.util.List;

/**
 * Created by Sherl on 2017/12/25.
 */
public interface EventHandler {
    void doHandle(EventModel model);

    List<EventType> getSupportEventTypes();
}
