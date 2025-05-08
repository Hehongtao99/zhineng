package com.boot.web.modules.web.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boot.web.modules.web.model.Schedule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boot.web.modules.web.vo.ScheduleVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 日程安排表 Mapper 接口
 * </p>
 */
public interface ScheduleMapper extends BaseMapper<Schedule> {

    Page<ScheduleVo> searchSchedule(Page<ScheduleVo> page, String searchKey, Integer status);

    /**
     * 用户日程专用搜索方法，支持通过用户ID关联查询
     * @param page 分页参数
     * @param userId 用户ID
     * @param searchKey 搜索关键字
     * @param status 状态
     * @return 分页结果
     */
    Page<ScheduleVo> searchUserSchedule(Page<ScheduleVo> page, Long userId, String searchKey, Integer status);

    List<ScheduleVo> findAllSchedules();

    List<ScheduleVo> findByIds(List<Long> ids);
    
    List<ScheduleVo> findSchedulesByMonth(String yearMonth);
    
    /**
     * 根据月份和用户ID查询与用户相关的日程
     * @param yearMonth 年月，格式为：yyyy-MM
     * @param userId 用户ID
     * @return 日程列表
     */
    List<ScheduleVo> findSchedulesByMonthAndUser(String yearMonth, Long userId);

    IPage<ScheduleVo> searchScheduleVoPage(Page<ScheduleVo> page, @Param("adminId") Long adminId, @Param("userId") Long userId,
                                          @Param("searchKey") String searchKey, @Param("status") Integer status);

    List<ScheduleVo> searchAllUsersSchedules(@Param("searchKey") String searchKey, @Param("status") Integer status);
}
