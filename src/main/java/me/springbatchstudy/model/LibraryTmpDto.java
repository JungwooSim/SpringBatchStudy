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

    public LibraryTmp toEntity() {
        return LibraryTmp.builder()
                .libraryNM(libraryNM)
                .libraryType(libraryType)
                .bigLocal(bigLocal)
                .smallLocal(smallLocal)
                .build();
    }
}
