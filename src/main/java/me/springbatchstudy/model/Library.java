package me.springbatchstudy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Library {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "library_nm")
    public String libraryNM;

    public List<BigLocal> bigLocal = new ArrayList<>();

    public List<SmallLocal> smallLocal = new ArrayList<>();

    public List<LibraryType> libraryType = new ArrayList<>();
}
