package me.springbatchstudy;

import me.springbatchstudy.model.LibraryDTO;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class LibraryFileRowMapper implements FieldSetMapper<LibraryDTO> {

    @Override
    public LibraryDTO mapFieldSet(FieldSet fieldSet) {
        LibraryDTO libraryDTO = new LibraryDTO();
        libraryDTO.setCol1(fieldSet.readString("col1"));
        libraryDTO.setCol2(fieldSet.readString("col2"));
        libraryDTO.setCol3(fieldSet.readString("col3"));

        return libraryDTO;
    }
}
