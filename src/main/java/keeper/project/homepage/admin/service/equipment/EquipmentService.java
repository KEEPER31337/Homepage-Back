package keeper.project.homepage.admin.service.equipment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.admin.dto.equipment.EquipmentDto;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.equipment.EquipmentBorrowEntity;
import keeper.project.homepage.entity.equipment.EquipmentEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.equipment.CustomEquipmentOverTheMaxException;
import keeper.project.homepage.exception.equipment.CustomEquipmentEntityNotFoundException;
import keeper.project.homepage.exception.file.CustomThumbnailEntityNotFoundException;
import keeper.project.homepage.exception.member.CustomMemberNotFoundException;
import keeper.project.homepage.repository.ThumbnailRepository;
import keeper.project.homepage.repository.equipment.EquipmentBorrowRepository;
import keeper.project.homepage.repository.equipment.EquipmentRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EquipmentService {

  private final EquipmentRepository equipmentRepository;
  private final ThumbnailRepository thumbnailRepository;
  private final MemberRepository memberRepository;
  private final EquipmentBorrowRepository equipmentBorrowRepository;

  public String transferFormat(Date date) {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    String transferDate = format.format(date);

    return transferDate;
  }

  private String getExpireDate(int date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.DATE, date);

    return transferFormat(calendar.getTime());
  }

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
    List<EquipmentEntity> equipmentEntities = equipmentRepository.findByNameOrderByRegisterDateDesc(
        name);
    if (equipmentEntities.size() == 0) {
      throw new CustomEquipmentEntityNotFoundException();
    }
    for (int i = 0; i < quantity; i++) {
      equipmentRepository.delete(equipmentEntities.get(i));
    }
  }

  public void borrowEquipment(String name, Long quantity, Long borrowMemberId) throws Exception {
    List<EquipmentEntity> equipmentEntities = equipmentRepository.findByNameOrderByRegisterDateDesc(
        name);
    if (equipmentEntities.size() == 0) {
      throw new CustomEquipmentEntityNotFoundException();
    }
    Long enableQuantity = 0L;
    for (EquipmentEntity equipmentEntity : equipmentEntities) {
      if (equipmentEntity.getEnable() == 1) {
        enableQuantity++;
      }
    }

    if (enableQuantity < quantity) {
      throw new CustomEquipmentOverTheMaxException();
    }

    MemberEntity memberEntity = memberRepository.findById(borrowMemberId)
        .orElseThrow(() -> new CustomMemberNotFoundException());

    int i = 0;
    for (EquipmentEntity equipmentEntity : equipmentEntities) {
      if (i >= quantity) {
        break;
      }
      if (equipmentEntity.getEnable() != 1) {
        equipmentEntity.setEnable(0L);
        equipmentEntity.setBorrow(1L);
        equipmentRepository.save(equipmentEntity);
        saveBorrowRepository(memberEntity, equipmentEntity);
      }
      i++;
    }

  }

  public void saveBorrowRepository(MemberEntity memberEntity, EquipmentEntity equipmentEntity) {
    String borrowDate = getExpireDate(0);
    String expireDate = getExpireDate(14);
    equipmentBorrowRepository.save(
        EquipmentBorrowEntity.builder().quantity(1L).borrowDate(java.sql.Date.valueOf(borrowDate))
            .expireDate(java.sql.Date.valueOf(expireDate))
            .memberId(memberEntity).equipmentId(equipmentEntity).build());
  }

  public void returnEquipment(String name, Long quantity, Long borrowMemberId) throws Exception {
    List<EquipmentBorrowEntity> equipmentBorrowEntities = equipmentBorrowRepository.findByNameOrOrderByBorrowDateDesc(
        name);
    List<EquipmentEntity> equipmentEntities = equipmentRepository.findByNameOrderByRegisterDateDesc(
        name);

    if (equipmentBorrowEntities.size() == 0) {
      throw new CustomEquipmentEntityNotFoundException();
    }
    if (equipmentEntities.size() == 0) {
      throw new CustomEquipmentEntityNotFoundException();
    }

    if (equipmentBorrowEntities.size() < quantity) {
      throw new CustomEquipmentOverTheMaxException();
    }

    int i = 0;
    for (EquipmentBorrowEntity equipmentBorrowEntity : equipmentBorrowEntities) {
      if (i >= quantity) {
        break;
      }

      int j = 0;
      for (EquipmentEntity equipmentEntity = equipmentEntities.get(j);
          equipmentEntity.getBorrow() != 1; j++) {
        equipmentEntity.setEnable(1L);
        equipmentEntity.setBorrow(0L);
        equipmentRepository.save(equipmentEntity);
      }

      equipmentBorrowRepository.delete(equipmentBorrowEntity);
      i++;
    }
  }

  public List<EquipmentBorrowEntity> getOverdueEquipments(Pageable pageable) {
    String nowDate = getExpireDate(0);

    return equipmentBorrowRepository.findByExpireDateAfter(pageable,
        java.sql.Date.valueOf(nowDate));
    ;
  }
}
