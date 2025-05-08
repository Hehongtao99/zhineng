package com.boot.web.modules.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boot.web.modules.web.model.Schedule;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boot.web.modules.web.vo.ScheduleVo;
import com.boot.web.modules.web.vo.TaskVo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 日程安排表 服务类
 * </p>
 */
public interface ScheduleService extends IService<Schedule> {

    Page<ScheduleVo> search(Integer pageSize, Integer pageNum, String searchKey, Integer status);

    Boolean create(Schedule schedule);

    boolean updateSchedule(Schedule schedule);

    List<TaskVo> conflictList(Long id, List<Long> scheduleIds);

    Page<ScheduleVo> searchUser(Integer pageSize, Integer pageNum, String searchKey, Long id, Integer status);

    Map<String, List<ScheduleVo>> getSchedulesByMonth(String yearMonth);

    /**
     * 根据月份和用户权限获取日程安排
     *
     * @param yearMonth 年月，格式为：yyyy-MM
     * @param userId 用户ID，如果是管理员则获取所有日程，否则只获取与用户相关的日程
     * @param isAdmin 是否为管理员
     * @return 日期为键，日程列表为值的映射
     */
    Map<String, List<ScheduleVo>> getSchedulesByMonthAndUser(String yearMonth, Long userId, boolean isAdmin);

    /**
     * 智能排序日程
     * 根据时间紧急性、任务重要性等因素进行智能排序
     *
     * @param pageSize 页面大小
     * @param pageNum 页码
     * @param searchKey 搜索关键词
     * @return 排序后的日程分页结果
     */
    Page<ScheduleVo> smartSort(Integer pageSize, Integer pageNum, String searchKey);

    /**
     * 用户智能排序日程
     * 根据时间紧急性、任务重要性等因素对当前用户的日程进行智能排序
     *
     * @param pageSize 页面大小
     * @param pageNum 页码
     * @param searchKey 搜索关键词
     * @param userId 用户ID
     * @return 排序后的日程分页结果
     */
    Page<ScheduleVo> smartSortUser(Integer pageSize, Integer pageNum, String searchKey, Long userId);

    /**
     * 调整日程顺序并更新时间
     * @param scheduleId 被拖动的日程ID
     * @param targetTime 目标时间
     * @param date 日期
     * @return 是否调整成功
     */
    boolean adjustScheduleOrder(Long scheduleId, LocalDateTime targetTime, LocalDate date);

    /**
     * 获取所有用户的日程，按权重排序
     * 管理员专用方法，用于查看所有用户的日程安排，不包含具体时间信息
     *
     * @param pageSize 页面大小
     * @param pageNum 页码
     * @param searchKey 搜索关键词
     * @param status 状态过滤
     * @return 排序后的日程分页结果
     */
    Page<ScheduleVo> getAllUsersSchedulesByWeight(Integer pageSize, Integer pageNum, String searchKey, Integer status);
}
