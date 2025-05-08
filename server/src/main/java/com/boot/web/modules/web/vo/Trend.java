package com.boot.web.modules.web.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Trend {
    private String date;
    private Long countPending;
    private Long countLeft;
    private Long countRight;
}
