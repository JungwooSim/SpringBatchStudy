package me.springbatchstudy.Listener;

import org.springframework.batch.core.ItemWriteListener;

import java.util.List;

public class WriterListener implements ItemWriteListener {
    @Override
    public void beforeWrite(List items) {
        System.out.println("beforeWriter");
    }

    @Override
    public void afterWrite(List items) {
        System.out.println("afterWriter");
    }

    @Override
    public void onWriteError(Exception exception, List items) {
        System.out.println("onWriterError");
    }
}
