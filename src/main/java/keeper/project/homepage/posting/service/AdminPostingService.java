package keeper.project.homepage.posting.service;

import java.util.List;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.exception.CustomMemberNotFoundException;
import keeper.project.homepage.member.repository.MemberRepository;
import keeper.project.homepage.posting.entity.PostingEntity;
import keeper.project.homepage.posting.repository.PostingRepository;
import keeper.project.homepage.util.entity.FileEntity;
import keeper.project.homepage.util.repository.FileRepository;
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
