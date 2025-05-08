package com.boot.web.modules.web.vo;

import com.boot.web.modules.ums.model.UmsAdmin;
import com.boot.web.modules.web.model.Task;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TaskVo extends Task {
    private String categoryName;
    private String importanceName;
    private String exigencyName;
    private BigDecimal priorityScore;

    private List<UmsAdmin> userList;
    
    // 日程开始时间
    private LocalDateTime scheduleStartTime;
    // 日程结束时间
    private LocalDateTime scheduleEndTime;
    
    // 冲突类型：deadline表示截止时间冲突，schedule表示日程时间冲突
    private String conflictType;
}
