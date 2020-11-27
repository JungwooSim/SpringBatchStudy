package me.springbatchstudy.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryDto {
    public Long id;

    public String libraryNM;

    public List<BigLocal> bigLocal = new ArrayList<>();

    public List<SmallLocal> smallLocal = new ArrayList<>();

    public List<LibraryType> libraryType = new ArrayList<>();
}
