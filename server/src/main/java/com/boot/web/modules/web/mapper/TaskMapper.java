package com.boot.web.modules.web.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boot.web.modules.web.model.Task;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boot.web.modules.web.vo.TaskVo;
import com.boot.web.modules.web.vo.Trend;
import com.boot.web.modules.web.vo.TrendPie;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 任务表 Mapper 接口
 * </p>
 */
public interface TaskMapper extends BaseMapper<Task> {

    Page<TaskVo> searchTask(Page<TaskVo> page, String searchKey, Long categoryId);

    List<TaskVo> listAll(Integer status);

    List<TrendPie> trendPie(Long userId);

    List<Trend> trend(String startDate, String endDate, Long userId);

    List<Trend> userCompletedTaskTrend(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("userId") Long userId);

    /**
     * 查找在指定时间范围内已分配会议室但尚未创建日程的任务的会议室ID列表
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 会议室ID列表
     */
    List<Long> findPendingMeetingRoomIds(LocalDateTime startTime, LocalDateTime endTime);
}
