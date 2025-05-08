package com.boot.web.modules.ums.vo;

import lombok.Data;

@Data
public class HomeData {
    private Long userCount;
    private Long taskCount;
    private Long scheduleCount;
    private Long resourceCount;
}
