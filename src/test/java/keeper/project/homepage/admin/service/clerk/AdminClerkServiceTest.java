package keeper.project.homepage.admin.service.clerk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import keeper.project.homepage.clerk.dto.response.TypeResponseDto;
import keeper.project.homepage.util.entity.ThumbnailEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import keeper.project.homepage.member.entity.MemberTypeEntity;
import keeper.project.homepage.repository.member.MemberTypeRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class AdminClerkServiceTest {

  private static long memberSequence = 1L;
  private static long typeSequence = 1L;
  private static final List<MemberTypeEntity> types = new ArrayList<>();

  @InjectMocks
  private AdminClerkService adminClerkService;
  @Mock
  private MemberTypeRepository memberTypeRepository;


  @BeforeAll
  static void initTypes() {
    Stream.of("활동", "휴면", "졸업")
        .forEach(typeName -> types.add(
            MemberTypeEntity.builder()
                .id(typeSequence++)
                .name(typeName)
                .badge(ThumbnailEntity.builder()
                    .path("testTypePath_" + typeSequence)
                    .build())
                .build())
        );
  }

  @BeforeEach
  void setUp() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
  }

  @Test
  @DisplayName("[SUCCESS] 회원 타입 리스트 불러오기")
  void getTypeList() {
    // mocking
    given(memberTypeRepository.findAll()).willReturn(types);

    // when
    List<TypeResponseDto> result = adminClerkService.getTypeList();

    // then
    List<TypeResponseDto> actual = types.stream().map(TypeResponseDto::toDto).toList();
    assertThat(actual).containsAll(result);
  }

  private static MemberEntity generateMemberEntity(MemberJobEntity... memberJobEntities) {
    MemberEntity member = MemberEntity.builder()
        .id(memberSequence++)
        .generation(10.0f)
        .nickName("nickName" + memberSequence)
        .realName("realName" + memberSequence)
        .memberType(types.get(0))
        .build();
    for (MemberJobEntity memberJobEntity : memberJobEntities) {
      member.addMemberJob(memberJobEntity);
    }
    return member;
  }
}