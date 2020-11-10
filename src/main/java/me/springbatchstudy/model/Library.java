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

    @Column(name = "library_type")
    public String libraryType;


    public List<BigLocal> bigLocal = new ArrayList<>();



//    @Column(name = "big_local")
//    public String bigLocal;
//
//    @Column(name = "small_local")
//    public String smallLocal;

//    CREATE TABLE `library` (
//            `id` int unsigned NOT NULL AUTO_INCREMENT,
//            `library_nm` int DEFAULT NULL,
//            `big_local_id` int DEFAULT NULL,
//            `libaray_type_id` int DEFAULT NULL,
//            `small_local_id` int DEFAULT NULL,
//    PRIMARY KEY (`id`),
//    UNIQUE KEY `id_UNIQUE` (`id`)
//            ) ENGINE=InnoDB DEFAULT CHARSET=utf8
//

//    @Column(name="col_4")
//    public String col4;
//    @Column(name="col_5")
//    public String col5;
//    @Column(name="col_6")
//    public String col6;
//    @Column(name="col_7")
//    public String col7;
//    @Column(name="col_8")
//    public String col8;
//    @Column(name="col_9")
//    public String col9;
//    @Column(name="col_10")
//    public String col10;
//    @Column(name="col_11")
//    public String col11;
//    @Column(name="col_12")
//    public String col12;
//    @Column(name="col_13")
//    public String col13;
//    @Column(name="col_14")
//    public Long col14;
//    @Column(name="col_15")
//    public Long col15;
//    @Column(name="col_16")
//    public Long col16;
//    @Column(name="col_17")
//    public Long col17;
//    @Column(name="col_18")
//    public String col18;
//    @Column(name="col_19")
//    public String col19;
//    @Column(name="col_20")
//    public String col20;
//    @Column(name="col_21")
//    public Long col21;
//    @Column(name="col_22")
//    public Long col22;
//    @Column(name="col_23")
//    public Long col23;
//    @Column(name="col_24")
//    public Float col24;
//    @Column(name="col_25")
//    public Float col25;
//    @Column(name="col_26")
//    public LocalDate col26;
//    @Column(name="col_27")
//    public Long col27;
//    @Column(name="col_28")
//    public String col28;
}
