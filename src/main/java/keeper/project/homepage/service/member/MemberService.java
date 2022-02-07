package keeper.project.homepage.service.member;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import keeper.project.homepage.dto.posting.PostingDto;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.exception.CustomMemberNotFoundException;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.service.posting.PostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;


  public MemberEntity findById(Long id) throws RuntimeException {
    Optional<MemberEntity> memberEntity = memberRepository.findById(id);
    return memberEntity.orElse(null);
  }

  public Page<PostingDto> findAllPostingByIsTemp(Long id, Pageable pageable, Integer isTemp) {
    MemberEntity memberEntity = memberRepository.findById(id).orElseThrow(
        () -> new CustomMemberNotFoundException(id.toString() + "인 id를 가진 member가 존재하지 않습니다."));

    List<PostingDto> postings = new ArrayList<>();
    memberEntity.getPosting().forEach(posting -> {
      if (posting.getIsTemp() == isTemp) {
        postings.add(PostingDto.create(posting));
      }
    });
    final int start = (int) pageable.getOffset();
    final int end = Math.min((start + pageable.getPageSize()), postings.size());
    final Page<PostingDto> page = new PageImpl<>(postings.subList(start, end), pageable,
        postings.size());

    return page;
  }

}
