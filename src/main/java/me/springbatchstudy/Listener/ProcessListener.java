package me.springbatchstudy.Listener;

import lombok.extern.slf4j.Slf4j;

import javax.batch.api.chunk.listener.ItemProcessListener;

@Slf4j
public class ProcessListener implements ItemProcessListener {
    @Override
    public void beforeProcess(Object item) throws Exception {
        log.info("ProcessListener Start");
    }

    @Override
    public void afterProcess(Object item, Object result) throws Exception {

    }

    @Override
    public void onProcessError(Object item, Exception ex) throws Exception {
        log.info("ProcessListener Error");
    }
}
