package com.zhoushengen.robot.job;

import com.zhoushengen.robot.job.task.SendMorningJob;
import com.zhoushengen.robot.job.task.SendNewsJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @author: jiachaozhou
 * @date: 2021年08月01日 20:02:39
 */
@Configuration
@Slf4j
public class SysJobRunner implements CommandLineRunner {

    @Autowired
    private Scheduler scheduler;


    @Override
    public void run(String... args) {

        JobDetail sendNewsJob = JobBuilder.newJob(SendNewsJob.class).build();
        JobDetail sendMorningJob = JobBuilder.newJob(SendMorningJob.class).build();
        try {
            execude(sendMorningJob, "0 0 8 * * ?", "SendMorningJob");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            execude(sendNewsJob, "0 30 7 * * ?", "SendNewsJob");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void execude(JobDetail jobDetail, String cron, String identity) throws SchedulerException {
        // 触发器
        TriggerKey triggerKey = TriggerKey.triggerKey(identity, Scheduler.DEFAULT_GROUP);
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .withSchedule(CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionDoNothing()).build();
        scheduler.scheduleJob(jobDetail, trigger);
        // 启动
        if (!scheduler.isShutdown()) {
            scheduler.start();
            log.info("***************任务[" + triggerKey.getName() + "]启动成功***************");
        } else {
            log.info("***************任务[" + triggerKey.getName() + "]已经运行，请勿再次启动***************");
        }
    }
}
