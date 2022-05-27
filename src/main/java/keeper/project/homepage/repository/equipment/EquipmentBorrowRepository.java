package keeper.project.homepage.repository.equipment;

import java.util.List;
import keeper.project.homepage.entity.equipment.EquipmentBorrowEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentBorrowRepository extends JpaRepository<EquipmentBorrowEntity, Long> {

  List<EquipmentBorrowEntity> findByNameOrOrderByBorrowDateDesc(String name);
}
