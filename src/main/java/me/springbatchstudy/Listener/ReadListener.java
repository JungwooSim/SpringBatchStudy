package me.springbatchstudy.Listener;


import org.springframework.batch.core.ItemReadListener;

public class ReadListener implements ItemReadListener<String> {
    @Override
    public void beforeRead() {
        System.out.println("before");
    }

    @Override
    public void afterRead(String item) {
        System.out.println("after");
    }

    @Override
    public void onReadError(Exception ex) {
        System.out.println("Error");
    }
}
