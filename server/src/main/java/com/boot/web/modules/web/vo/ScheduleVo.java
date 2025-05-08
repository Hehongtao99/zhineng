package com.boot.web.modules.web.vo;

import com.boot.web.modules.web.model.Schedule;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ScheduleVo extends Schedule {
    private String taskName;
    private String taskCode;
    private Integer status;
    private LocalDateTime deadline;

    private Boolean hasConflict;
    private String conflictScheduleIds;
    private String userData;
    private String resourcesData;

    private LocalDateTime endTime;
    
    // 用于优先级排序
    private Double priorityScore;
    
    // 用户信息
    private String username;
    private String nickname;
}
