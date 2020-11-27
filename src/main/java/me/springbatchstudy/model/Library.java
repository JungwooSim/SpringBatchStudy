package me.springbatchstudy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "library")
public class Library {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "library_nm")
    public String libraryNM;

    @OneToOne
    @JoinColumn(name = "big_local_id", referencedColumnName = "id")
    public BigLocal bigLocal;

    @OneToOne
    @JoinColumn(name = "small_local_id", referencedColumnName = "id")
    public SmallLocal smallLocal;

    @OneToOne
    @JoinColumn(name = "libaray_type_id", referencedColumnName = "id")
    public LibraryType libraryType;
}
