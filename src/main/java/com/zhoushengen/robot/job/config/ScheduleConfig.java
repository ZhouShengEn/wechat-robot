package com.zhoushengen.robot.job.config;

import org.quartz.Scheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.annotation.Resource;

/**
 * @author zhoushengen
 * @version 1.0
 * @date 2023/9/2 22:54
 */
@Configuration
public class ScheduleConfig {

    @Resource
    private SchedulerFactoryBean schedulerFactoryBean;

    @Bean
    public Scheduler scheduler() {
        return schedulerFactoryBean.getScheduler();
    }
}

