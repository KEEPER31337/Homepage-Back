package keeper.project.homepage.repository.equipment;

import java.util.List;
import keeper.project.homepage.entity.equipment.EquipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository extends JpaRepository<EquipmentEntity, Long> {
  public List<EquipmentEntity> findByNameOrderByRegisterDate();

}
