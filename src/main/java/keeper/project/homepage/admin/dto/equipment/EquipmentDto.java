package keeper.project.homepage.admin.dto.equipment;

import java.time.LocalDate;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.equipment.EquipmentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentDto {

  private Long id;
  private String name;
  private String information;
  private Long total;
  private Long borrow;
  private Long enable;
  private LocalDate registerDate;
  private String ipAddress;
  private Long thumbnailId;

  public EquipmentEntity toEntity(ThumbnailEntity thumbnailEntity) {

    return EquipmentEntity.builder().name(name).information(information).total(total).borrow(borrow)
        .enable(enable).registerDate(registerDate).thumbnailId(thumbnailEntity).build();
  }
}
