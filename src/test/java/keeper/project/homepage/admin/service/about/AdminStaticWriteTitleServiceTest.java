package keeper.project.homepage.admin.service.about;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Optional;
import keeper.project.homepage.about.dto.request.StaticWriteTitleDto;
import keeper.project.homepage.about.dto.response.StaticWriteTitleResponseDto;
import keeper.project.homepage.about.entity.StaticWriteTitleEntity;
import keeper.project.homepage.repository.about.StaticWriteTitleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class AdminStaticWriteTitleServiceTest {

  @Mock
  private StaticWriteTitleRepository staticWriteTitleRepository;

  @InjectMocks
  private AdminStaticWriteTitleService adminStaticWriteTitleService;

  @Test
  @DisplayName("페이지 블럭 타이틀 수정")
  void updateTitle() {
    // given
    String type = "intro";
    String newTitle = "KEEPER 소개글";
    Optional<StaticWriteTitleEntity> before = Optional.of(StaticWriteTitleEntity.builder()
        .title("키퍼(KEEPER) 소개글")
        .type(type)
        .build());
    StaticWriteTitleEntity after = StaticWriteTitleEntity.builder()
        .title(newTitle)
        .type(type)
        .build();
    StaticWriteTitleDto staticWriteTitleDto = StaticWriteTitleDto.builder()
        .title(newTitle)
        .type(type)
        .build();

    // when
    when(staticWriteTitleRepository.findById(any(Long.class))).thenReturn(before);
    when(staticWriteTitleRepository.save(before.get())).thenReturn(after);
    StaticWriteTitleResponseDto result = adminStaticWriteTitleService.updateTitleById(
        staticWriteTitleDto, 1L);

    // then
    assertThat(result.getTitle()).isEqualTo(newTitle);
    assertThat(result.getType()).isEqualTo(type);
  }

}
