package me.springbatchstudy.processor;

import me.springbatchstudy.model.LibraryTmp;
import me.springbatchstudy.model.LibraryTmpDto;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LibraryProcessor implements ItemProcessor<LibraryTmpDto, LibraryTmp> {

    @Override
    public LibraryTmp process(LibraryTmpDto libraryTmpDto) {
        LibraryTmp libraryTmp = LibraryTmp.builder()
                .libraryNM(libraryTmpDto.getLibraryNM())
                .libraryType(libraryTmpDto.getLibraryType())
                .bigLocal(libraryTmpDto.getBigLocal())
                .smallLocal(libraryTmpDto.smallLocal)
                .build();
        return libraryTmp;
    }
}
