package keeper.project.homepage.clerk.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import keeper.project.homepage.clerk.dto.request.MeritAddRequestDto;
import keeper.project.homepage.clerk.entity.MeritLogEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

  @Test
  @DisplayName("서로 다른 해에 상벌점 내역 추가시 연도 리스트에 추가되어야 한다")
  void addMeritLogTest() {
    // given
    LocalDate toDay = LocalDate.now();
    List<LocalDate> dates = List.of(toDay, toDay.minusYears(2L), toDay.plusYears(1L));
    List<MeritAddRequestDto> requests = dates.stream()
        .map(AdminMeritServiceTest::getVirtualMeritAddRequestDto).toList();

    // when
    requests.forEach(request -> adminMeritService.addMeritWithLog(request));
    List<Integer> years = adminMeritService.getYears();

    // then
    assertThat(years).containsAll(
        dates.stream().map(LocalDate::getYear).collect(Collectors.toList()));
  }

  private static MeritAddRequestDto getVirtualMeritAddRequestDto(LocalDate date) {
    return MeritAddRequestDto.builder()
        .date(date)
        .meritTypeId(1L)
        .memberId(1L)
        .build();
  }
}