package me.springbatchstudy.model.Repository;

import me.springbatchstudy.model.LibraryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryTypeRepository extends JpaRepository<LibraryType, Long> {
    LibraryType findByLibarayType(String libraryTypeValue);
}
