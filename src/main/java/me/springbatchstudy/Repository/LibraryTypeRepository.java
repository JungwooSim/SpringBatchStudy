package me.springbatchstudy.Repository;

import me.springbatchstudy.model.LibraryType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryTypeRepository extends JpaRepository<LibraryType, Long> {
}
