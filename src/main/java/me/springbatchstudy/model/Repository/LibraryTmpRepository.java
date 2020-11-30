package me.springbatchstudy.model.Repository;

import me.springbatchstudy.model.LibraryTmp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryTmpRepository extends JpaRepository<LibraryTmp, Long> {
}
