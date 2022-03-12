package keeper.project.homepage.common.service.member;

import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.common.dto.member.CommonMemberDto;
import keeper.project.homepage.common.mapper.CommonMemberMapper;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommonMemberService {

  private final MemberRepository memberRepository;
  private final CommonMemberMapper memberMapper = Mappers.getMapper(CommonMemberMapper.class);

  public List<CommonMemberDto> getAllCommonMemberInfo() {
    List<CommonMemberDto> result = new ArrayList<>();

    List<MemberEntity> memberEntities = memberRepository.findAll();
    for (MemberEntity member : memberEntities) {
      result.add(memberMapper.toDto(member));
    }

    return result;
  }
}
