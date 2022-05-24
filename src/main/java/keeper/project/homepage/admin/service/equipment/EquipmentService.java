package keeper.project.homepage.admin.service.equipment;

import keeper.project.homepage.admin.dto.equipment.EquipmentDto;
import keeper.project.homepage.common.dto.result.CommonResult;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.equipment.EquipmentEntity;
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
        .orElseThrow();

    Long addTotal = equipmentDto.getTotal();

    for (int i = 0; i < addTotal; i++) {
      equipmentRepository.save(EquipmentEntity.builder().name(equipmentDto.getName())
          .information(equipmentDto.getInformation()).total(1L).borrow(0L).enable(1L)
          .registerDate(equipmentDto.getRegisterDate()).thumbnailId(thumbnailId).build());
    }
  }

}
