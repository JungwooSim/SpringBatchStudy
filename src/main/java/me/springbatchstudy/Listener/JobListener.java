package me.springbatchstudy.Listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;


public class JobListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("BeforeJob");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("afterJob");
    }
}
