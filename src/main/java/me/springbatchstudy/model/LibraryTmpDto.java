package me.springbatchstudy.model;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class LibraryTmpDto {
    public String libraryNM;
    public String libraryType;
    public String bigLocal;
    public String smallLocal;
}
