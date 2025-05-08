package com.boot.web.modules.web.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boot.web.common.api.CommonPage;
import com.boot.web.common.api.CommonResult;
import com.boot.web.modules.web.model.Category;
import com.boot.web.modules.web.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 类别表 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/web/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation("根据条件查询列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<Category>> list(@RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                                                     @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        Page<Category> categoryPage = categoryService.search(pageSize, pageNum);

        return CommonResult.success(CommonPage.restPage(categoryPage));
    }

    @ApiOperation(value = "创建分类")
    @PostMapping("/create")
    public CommonResult<String> create(@RequestBody Category category) {
        boolean isSuccess = categoryService.save(category);
        if (!isSuccess) {
            return CommonResult.failed();
        }
        return CommonResult.success(null);
    }

    @ApiOperation("修改分类")
    @PostMapping("/update")
    public CommonResult<Boolean> update(@RequestBody Category category) {
        boolean success = categoryService.updateById(category);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @ApiOperation("移除分类")
    @PostMapping("/delete/{id}")
    public CommonResult<String> delete(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        boolean success = categoryService.removeById(category);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed("移除失败，请稍后重试");
    }

    @ApiOperation("查询所有列表")
    @GetMapping("/listAll")
    public CommonResult<List<Category>> listAll() {
        List<Category> categoryVos = categoryService.list();

        return CommonResult.success(categoryVos);
    }

}

