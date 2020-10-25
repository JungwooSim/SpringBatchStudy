package me.springbatchstudy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "tmp_library")
public class LibraryTmp {
    @Id
    public Long id;

    @Column(name = "libary_nm")
    public String libraryNM;

    @Column(name = "library_type")
    public String libraryType;

    @Column(name = "big_local")
    public String bigLocal;

    @Column(name = "small_local")
    public String smallLocal;
}
