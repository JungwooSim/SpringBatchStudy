package me.springbatchstudy.processor;

import me.springbatchstudy.model.Library;
import me.springbatchstudy.model.LibraryDTO;
import org.springframework.batch.item.ItemProcessor;

public class LibraryProcessor implements ItemProcessor<LibraryDTO, Library> {

    @Override
    public Library process(LibraryDTO libraryDto) {
        Library library = Library.builder()
                .col1(libraryDto.col1)
                .col2(libraryDto.col2)
                .col3(libraryDto.col3)
                .build();

        return library;
    }
}
