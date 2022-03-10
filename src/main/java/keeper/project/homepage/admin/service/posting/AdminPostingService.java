package keeper.project.homepage.admin.service.posting;

import java.util.List;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.common.entity.posting.PostingEntity;
import keeper.project.homepage.exception.member.CustomMemberNotFoundException;
import keeper.project.homepage.repository.FileRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.common.repository.posting.PostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminPostingService {

  private final MemberRepository memberRepository;
  private final FileRepository fileRepository;
  private final PostingRepository postingRepository;

  @Transactional
  public void deleteByAdmin(PostingEntity postingEntity) {

    MemberEntity memberEntity = memberRepository.findById(
        postingEntity.getMemberId().getId()).orElseThrow(CustomMemberNotFoundException::new);

    // Foreign Key로 연결 된 file 제거
    List<FileEntity> fileEntities = fileRepository.findAllByPostingId(postingEntity);
    for (FileEntity fileEntity : fileEntities) {
      fileEntity.setPostingId(null);
      fileRepository.save(fileEntity);
    }

    memberEntity.getPosting().remove(postingEntity);
    postingRepository.delete(postingEntity);
  }
}
