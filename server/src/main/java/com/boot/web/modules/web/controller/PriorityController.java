package com.boot.web.modules.web.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boot.web.common.api.CommonPage;
import com.boot.web.common.api.CommonResult;
import com.boot.web.modules.web.model.Priority;
import com.boot.web.modules.web.service.PriorityService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.management.Query;
import java.util.List;

/**
 * <p>
 * 优先级表 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/web/priority")
public class PriorityController {
    @Autowired
    private PriorityService priorityService;

    @ApiOperation("根据条件查询列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<Priority>> list(@RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                 @RequestParam(value = "type", required = false) Integer type,
                                                 @RequestParam(value = "searchKey", required = false) String searchKey) {
        Page<Priority> priorityPage = priorityService.search(pageSize, pageNum, type, searchKey);

        return CommonResult.success(CommonPage.restPage(priorityPage));
    }

    @ApiOperation(value = "创建权重")
    @PostMapping("/create")
    public CommonResult<String> create(@RequestBody Priority priority) {
        boolean isSuccess = priorityService.save(priority);
        if (!isSuccess) {
            return CommonResult.failed();
        }
        return CommonResult.success(null);
    }

    @ApiOperation("修改权重")
    @PostMapping("/update")
    public CommonResult<Boolean> update(@RequestBody Priority priority) {
        boolean success = priorityService.updateById(priority);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @ApiOperation("移除权重")
    @PostMapping("/delete/{id}")
    public CommonResult<String> delete(@PathVariable Long id) {
        Priority priority = priorityService.getById(id);
        boolean success = priorityService.removeById(priority);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed("移除失败，请稍后重试");
    }

    @ApiOperation("查询所有列表")
    @GetMapping("/listAll")
    public CommonResult<List<Priority>> listAll(@RequestParam(value = "type", required = false) Integer type) {
        QueryWrapper<Priority> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(type)) {
            queryWrapper.lambda().eq(Priority::getType, type);
        }
        List<Priority> priorityVos = priorityService.list(queryWrapper);

        return CommonResult.success(priorityVos);
    }
}

