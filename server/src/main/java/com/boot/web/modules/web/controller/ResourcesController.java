package com.boot.web.modules.web.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boot.web.common.api.CommonPage;
import com.boot.web.common.api.CommonResult;
import com.boot.web.modules.web.model.Resources;
import com.boot.web.modules.web.service.ResourcesService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 资源表 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/web/resources")
public class ResourcesController {
    @Autowired
    private ResourcesService resourcesService;

    @ApiOperation("根据条件查询列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<Resources>> list(@RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                 @RequestParam(value = "type", required = false) Integer type,
                                                 @RequestParam(value = "searchKey", required = false) String searchKey) {
        Page<Resources> resourcesPage = resourcesService.search(pageSize, pageNum, type, searchKey);

        return CommonResult.success(CommonPage.restPage(resourcesPage));
    }

    @ApiOperation(value = "创建资源")
    @PostMapping("/create")
    public CommonResult<String> create(@RequestBody Resources resources) {
        boolean isSuccess = resourcesService.save(resources);
        if (!isSuccess) {
            return CommonResult.failed();
        }
        return CommonResult.success(null);
    }

    @ApiOperation("修改资源")
    @PostMapping("/update")
    public CommonResult<Boolean> update(@RequestBody Resources resources) {
        boolean success = resourcesService.updateById(resources);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @ApiOperation("移除资源")
    @PostMapping("/delete/{id}")
    public CommonResult<String> delete(@PathVariable Long id) {
        Resources resources = resourcesService.getById(id);
        boolean success = resourcesService.removeById(resources);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed("移除失败，请稍后重试");
    }

    @ApiOperation("查询所有列表")
    @GetMapping("/listAll")
    public CommonResult<List<Resources>> listAll() {
        List<Resources> resourcesVos = resourcesService.list();

        return CommonResult.success(resourcesVos);
    }
}

