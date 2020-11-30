package me.springbatchstudy.model.Repository;

import me.springbatchstudy.model.SmallLocal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmallLocalRepository extends JpaRepository<SmallLocal, Long> {
    SmallLocal findBySmallLocal(String smallLocalValue);
}
