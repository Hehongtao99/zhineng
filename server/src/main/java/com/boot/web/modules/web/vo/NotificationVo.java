package com.boot.web.modules.web.vo;

import com.boot.web.modules.web.model.Notification;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationVo extends Notification {
    private String userName;
    private String taskName;
    private Long taskId;
}
