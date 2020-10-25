package me.springbatchstudy.Listener;


import me.springbatchstudy.model.LibraryDTO;
import org.springframework.batch.core.ItemReadListener;

public class ReadListener implements ItemReadListener<LibraryDTO> {
    @Override
    public void beforeRead() {
        System.out.println("before");
    }

    @Override
    public void afterRead(LibraryDTO item) {
        System.out.println("after");
    }

    @Override
    public void onReadError(Exception ex) {
        System.out.println("Error");
    }
}
