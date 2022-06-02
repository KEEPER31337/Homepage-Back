package keeper.project.homepage.user.service.about;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import keeper.project.homepage.admin.dto.about.response.StaticWriteTitleResponseDto;
import keeper.project.homepage.entity.about.StaticWriteTitleEntity;
import keeper.project.homepage.repository.about.StaticWriteTitleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StaticWriteTitleServiceTest {

  @Mock
  private StaticWriteTitleRepository staticWriteTitleRepository;

  @InjectMocks
  private StaticWriteTitleService staticWriteTitleService;

  private StaticWriteTitleEntity staticWriteTitleEntity = StaticWriteTitleEntity.builder()
      .title("키퍼(Keeper) 소개글2")
      .type("intro")
      .build();

  @Test
  @DisplayName("페이지 블럭 타이틀 조회")
  public void getTitles() {
    // given
    String type = "intro";

    // when
    when(staticWriteTitleRepository.findAllByType(any(String.class))).thenReturn(
        new ArrayList<>(Arrays.asList(staticWriteTitleEntity)));
    List<StaticWriteTitleResponseDto> result = staticWriteTitleService.findAllByType(type);

    // then
    assertThat(result.size()).isEqualTo(1);
    assertThat(result.get(0).getId()).isEqualTo(staticWriteTitleEntity.getId());
  }

}
