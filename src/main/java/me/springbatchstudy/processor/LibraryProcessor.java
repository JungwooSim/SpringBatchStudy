package me.springbatchstudy.processor;

import me.springbatchstudy.model.Library;
import me.springbatchstudy.model.LibraryDTO;
import org.springframework.batch.item.ItemProcessor;

public class LibraryProcessor implements ItemProcessor<Library, Library> {

    @Override
    public Library process(Library library) {
        System.out.println("-----------");
//        System.out.println(library.col1);
//        final LibraryDTO libraryDTO = new LibraryDTO();
        return library;
    }
}
