package keeper.project.homepage.clerk.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import keeper.project.homepage.clerk.dto.request.MeritAddRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class AdminMeritServiceTest extends AdminClerkServiceTestHelper {

  @Test
  @DisplayName("이미 상벌점 내역에 있는 타입이면 삭제 불가")
  void deleteMeritTypeIfExistLog() {
    // given
    MeritAddRequestDto request = getVirtualMeritAddRequestDto(LocalDate.now());

    // when
    adminMeritService.addMeritWithLog(request);

    // then
    assertThrows(IllegalArgumentException.class, () -> adminMeritService.deleteMeritType(1L));
  }

  private static MeritAddRequestDto getVirtualMeritAddRequestDto(LocalDate date) {
    return MeritAddRequestDto.builder()
        .date(date)
        .meritTypeId(1L)
        .memberId(1L)
        .build();
  }
}