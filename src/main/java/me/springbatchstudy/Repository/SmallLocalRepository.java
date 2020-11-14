package me.springbatchstudy.Repository;

import me.springbatchstudy.model.SmallLocal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmallLocalRepository extends JpaRepository<SmallLocal, Long> {
}
