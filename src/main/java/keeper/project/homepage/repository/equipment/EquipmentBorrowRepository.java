package keeper.project.homepage.repository.equipment;

import java.sql.Date;
import java.util.List;
import keeper.project.homepage.entity.equipment.EquipmentBorrowEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentBorrowRepository extends JpaRepository<EquipmentBorrowEntity, Long> {

  List<EquipmentBorrowEntity> findByNameOrOrderByBorrowDateDesc(String name);
  List<EquipmentBorrowEntity> findByExpireDateAfter(Pageable pageable, Date date);
}
