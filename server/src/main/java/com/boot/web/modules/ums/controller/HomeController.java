package com.boot.web.modules.ums.controller;

import com.boot.web.common.api.CommonResult;
import com.boot.web.modules.ums.model.UmsAdmin;
import com.boot.web.modules.ums.service.UmsAdminService;
import com.boot.web.modules.ums.vo.HomeData;
import com.boot.web.modules.web.service.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/web/home")
public class HomeController {

    @Autowired
    private UmsAdminService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ResourcesService resourcesService;

    @ApiOperation("首页信息")
    @GetMapping
    public CommonResult<HomeData> homeData() {
        HomeData homeData = new HomeData();
        long userCount = userService.count();
        long taskCount = taskService.count();
        long scheduleCount = scheduleService.count();
        long resourceCount = resourcesService.count();
        homeData.setUserCount(userCount);
        homeData.setTaskCount(taskCount);
        homeData.setScheduleCount(scheduleCount);
        homeData.setResourceCount(resourceCount);

        return CommonResult.success(homeData);
    }
}
