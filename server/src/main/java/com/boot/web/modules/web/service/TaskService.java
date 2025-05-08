package com.boot.web.modules.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boot.web.modules.web.model.Task;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boot.web.modules.web.vo.TaskVo;
import com.boot.web.modules.web.vo.Trend;
import com.boot.web.modules.web.vo.TrendPie;
import com.boot.web.modules.web.mapper.PriorityMapper;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 任务表 服务类
 * </p>
 */
public interface TaskService extends IService<Task> {

    Page<TaskVo> search(Integer pageSize, Integer pageNum, Long categoryId, String searchKey);

    List<TaskVo> listAll(Integer status);

    TaskVo getInfo(Long id);

    List<TrendPie> trendPie();

    List<TrendPie> trendPie(Long userId);

    List<Trend> trend(String startDate, String endDate);

    List<Trend> trend(String startDate, String endDate, Long userId);

    /**
     * 获取指定用户在时间段内完成的任务统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param userId 用户ID
     * @return 统计数据列表
     */
    List<Trend> getUserCompletedTaskTrend(String startDate, String endDate, Long userId);

    Boolean create(Task task);

    Boolean updateTask(Task task);
    
    /**
     * 获取优先级Mapper
     * 用于智能排序功能中查询优先级信息
     * 
     * @return PriorityMapper实例
     */
    PriorityMapper getPriorityMapper();
    
    /**
     * 查找指定时间范围内已分配会议室但尚未创建日程的任务的会议室ID列表
     * 用于准确计算可用会议室数量
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 已分配但尚未创建日程的会议室ID列表
     */
    List<Long> findPendingMeetingRoomIds(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取当前已使用的会议室数量
     * 注意：已完成和待办状态的任务不会占用会议室资源
     * 
     * @return 已占用的会议室数量
     */
    int getUsedMeetingRoomCount();
    
    /**
     * chuang状态到数据库
     * 用于确保状态更新立即生效
     *
     * @param taskId 任务ID
     * @param status 新状态
     * @return 更新是否成功
     */
    boolean updateTaskStatusInDB(Long taskId, Integer status);
    
    /**
     * 删除任务及其关联的日程
     *
     * @param taskId 任务ID
     * @return 操作是否成功
     */
    boolean deleteTaskWithSchedules(Long taskId);

    /**
     * 自动安排指定日期的所有任务日程
     * 按照任务权重从高到低排序，从早上8点开始安排
     * 
     * @param date 需要安排的日期
     * @return 是否安排成功
     */
    boolean autoArrangeSchedules(LocalDate date);
    
    /**
     * 获取指定日期剩余可安排时间(分钟)
     * @param date 指定日期
     * @return 剩余可安排分钟数
     */
    int getRemainingTimeForDate(LocalDate date);
    
    /**
     * 获取指定用户在指定日期的剩余可安排时间(分钟)
     * @param date 指定日期
     * @param userId 用户ID
     * @return 用户在该日期的剩余可安排分钟数
     */
    int getRemainingTimeForUserAndDate(LocalDate date, Long userId);

    /**
     * 检查任务时间是否与现有任务冲突
     * @param task 要检查的任务
     * @return 冲突的任务列表
     */
    List<TaskVo> checkTimeConflict(Task task);
}
