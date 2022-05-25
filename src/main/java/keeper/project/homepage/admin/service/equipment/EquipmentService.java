package keeper.project.homepage.admin.service.equipment;

import java.util.List;
import keeper.project.homepage.admin.dto.equipment.EquipmentDto;
import keeper.project.homepage.common.dto.result.CommonResult;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.equipment.EquipmentEntity;
import keeper.project.homepage.exception.equipment.CustomEquipmentEntityNotFoundException;
import keeper.project.homepage.exception.file.CustomThumbnailEntityNotFoundException;
import keeper.project.homepage.repository.ThumbnailRepository;
import keeper.project.homepage.repository.equipment.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EquipmentService {

  private final EquipmentRepository equipmentRepository;
  private final ThumbnailRepository thumbnailRepository;

  public void addEquipment(EquipmentDto equipmentDto) {
    ThumbnailEntity thumbnailId = thumbnailRepository.findById(equipmentDto.getThumbnailId())
        .orElseThrow(() -> new CustomThumbnailEntityNotFoundException());

    Long addTotal = equipmentDto.getTotal();

    for (int i = 0; i < addTotal; i++) {
      equipmentRepository.save(EquipmentEntity.builder().name(equipmentDto.getName())
          .information(equipmentDto.getInformation()).total(1L).borrow(0L).enable(1L)
          .registerDate(equipmentDto.getRegisterDate()).thumbnailId(thumbnailId).build());
    }
  }

  public void deleteEquipment(String name, Long quantity) throws Exception {
    List<EquipmentEntity> equipmentEntities = equipmentRepository.findByNameOrderByRegisterDate();
    if (equipmentEntities.size() == 0) {
      throw new CustomEquipmentEntityNotFoundException();
    }
    for (int i = 0; i < quantity; i++) {
      equipmentRepository.delete(equipmentEntities.get(i));
    }
  }

}
