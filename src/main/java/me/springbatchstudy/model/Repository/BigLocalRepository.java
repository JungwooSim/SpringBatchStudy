package me.springbatchstudy.model.Repository;

import me.springbatchstudy.model.BigLocal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BigLocalRepository extends JpaRepository<BigLocal, Long> {
    BigLocal findByBigLocal(String bigLocalValue);
}
