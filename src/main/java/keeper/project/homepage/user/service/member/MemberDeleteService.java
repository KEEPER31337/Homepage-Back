package keeper.project.homepage.user.service.member;

import java.util.ArrayList;
import java.util.List;
import keeper.project.homepage.util.entity.ThumbnailEntity;
import keeper.project.homepage.attendance.entity.AttendanceEntity;
import keeper.project.homepage.library.entity.BookBorrowEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberHasCommentDislikeEntity;
import keeper.project.homepage.member.entity.MemberHasCommentLikeEntity;
import keeper.project.homepage.member.entity.MemberHasMemberJobEntity;
import keeper.project.homepage.member.entity.MemberHasPostingDislikeEntity;
import keeper.project.homepage.member.entity.MemberHasPostingLikeEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import keeper.project.homepage.member.entity.MemberRankEntity;
import keeper.project.homepage.member.entity.MemberTypeEntity;
import keeper.project.homepage.posting.entity.CommentEntity;
import keeper.project.homepage.posting.entity.PostingEntity;
import keeper.project.homepage.member.exception.CustomAccountDeleteFailedException;
import keeper.project.homepage.attendance.repository.AttendanceRepository;
import keeper.project.homepage.attendance.repository.GameRepository;
import keeper.project.homepage.library.repository.BookBorrowRepository;
import keeper.project.homepage.member.repository.MemberHasCommentDislikeRepository;
import keeper.project.homepage.member.repository.MemberHasCommentLikeRepository;
import keeper.project.homepage.member.repository.MemberHasPostingDislikeRepository;
import keeper.project.homepage.member.repository.MemberHasPostingLikeRepository;
import keeper.project.homepage.member.repository.MemberRepository;
import keeper.project.homepage.point.repository.PointLogRepository;
import keeper.project.homepage.posting.repository.CommentRepository;
import keeper.project.homepage.posting.repository.PostingRepository;
import keeper.project.homepage.util.service.FileService;
import keeper.project.homepage.util.service.ThumbnailService;
import keeper.project.homepage.user.service.posting.PostingService;
import keeper.project.homepage.sign.service.CustomPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberDeleteService {

  private final MemberRepository memberRepository;
  private final CommentRepository commentRepository;
  private final MemberHasCommentLikeRepository memberHasCommentLikeRepository;
  private final MemberHasCommentDislikeRepository memberHasCommentDislikeRepository;
  private final PostingRepository postingRepository;
  private final MemberHasPostingLikeRepository memberHasPostingLikeRepository;
  private final MemberHasPostingDislikeRepository memberHasPostingDislikeRepository;
  private final AttendanceRepository attendanceRepository;
  private final BookBorrowRepository bookBorrowRepository;
  private final PointLogRepository pointLogRepository;
  private final GameRepository gameRepository;
  private final PasswordEncoder passwordEncoder;
  private final CustomPasswordService customPasswordService;

  private final MemberUtilService memberUtilService;
  private final ThumbnailService thumbnailService;
  private final FileService fileService;

  public void deleteMember(MemberEntity member) {
    memberRepository.delete(member);
  }

  public void decreaseCommentsLike(MemberEntity member) {
    List<MemberHasCommentLikeEntity> likes = memberHasCommentLikeRepository.findByMemberHasCommentEntityPK_MemberEntity(
        member);
    List<CommentEntity> updateAll = new ArrayList<>();
    for (MemberHasCommentLikeEntity like : likes) {
      CommentEntity comment = like.getMemberHasCommentEntityPK().getCommentEntity();
      comment.decreaseLikeCount();
      updateAll.add(comment);
    }
    memberHasCommentLikeRepository.deleteAll(likes);
    commentRepository.saveAll(updateAll);
    // save vs saveAll 성능 차이 : https://sas-study.tistory.com/388
  }

  public void decreaseCommentsDislike(MemberEntity member) {
    List<MemberHasCommentDislikeEntity> dislikes = memberHasCommentDislikeRepository.findByMemberHasCommentEntityPK_MemberEntity(
        member);
    List<CommentEntity> updateAll = new ArrayList<>();
    for (MemberHasCommentDislikeEntity dislike : dislikes) {
      CommentEntity comment = dislike.getMemberHasCommentEntityPK().getCommentEntity();
      comment.decreaseDislikeCount();
      updateAll.add(comment);
    }
    memberHasCommentDislikeRepository.deleteAll(dislikes);
    commentRepository.saveAll(updateAll);
  }

  public void decreasePostingsLike(MemberEntity member) {
    List<MemberHasPostingLikeEntity> likes = memberHasPostingLikeRepository.findByMemberId(member);
    List<PostingEntity> updateAll = new ArrayList<>();
    for (MemberHasPostingLikeEntity like : likes) {
      PostingEntity posting = like.getPostingId();
      memberHasPostingLikeRepository.deleteByMemberIdAndPostingId(member, posting);
      // FIXME : PostingService 로직과는 달리 .remove()가 없으면 에러가 발생. 아직 이유는 잘 모르겠음.
      posting.getMemberHasPostingLikeEntities().remove(like);
      posting.decreaseLikeCount();
      updateAll.add(posting);
    }
    postingRepository.saveAll(updateAll);
  }

  public void decreasePostingsDislike(MemberEntity member) {
    List<MemberHasPostingDislikeEntity> dislikes = memberHasPostingDislikeRepository.findByMemberId(
        member);
    List<PostingEntity> updateAll = new ArrayList<>();
    for (MemberHasPostingDislikeEntity dislike : dislikes) {
      PostingEntity posting = dislike.getPostingId();
      memberHasPostingDislikeRepository.deleteByMemberIdAndPostingId(member, posting);
      posting.getMemberHasPostingDislikeEntities().remove(dislike);
      posting.decreaseDislikeCount();
      updateAll.add(posting);
    }
    postingRepository.saveAll(updateAll);
  }

  public void commentChangeToVirtualMember(MemberEntity virtual, MemberEntity deleted) {
    List<CommentEntity> changedComments = commentRepository.findAllByMember(deleted);
    List<CommentEntity> updateAll = new ArrayList<>();
    for (CommentEntity comment : changedComments) {
      comment.changeMemberId(virtual);
      updateAll.add(comment);
    }
    commentRepository.saveAll(updateAll);
  }

  public void postingChangeToVirtualMember(MemberEntity virtual, MemberEntity deleted) {
    List<PostingEntity> changedPostings = postingRepository.findAllByMemberId(deleted);
    List<PostingEntity> updateAll = new ArrayList<>();
    for (PostingEntity posting : changedPostings) {
      if (posting.getIsTemp().equals(PostingService.isTempPosting)) {
        postingRepository.delete(posting);
      } else {
        posting.updateMemberId(virtual);
        updateAll.add(posting);
      }
    }
  }

  public void deleteThumbnail(MemberEntity member) {
    if (member.getThumbnail() != null) {
      ThumbnailEntity deleteThumbnail = thumbnailService.find(member.getThumbnail().getId());
      thumbnailService.delete(deleteThumbnail.getId());
    }
  }

  public void deleteAttendance(MemberEntity member) {
    List<AttendanceEntity> attendances = attendanceRepository.findAllByMember(member);
    attendanceRepository.deleteAll(attendances);
  }

  public void checkRemainBorrowInfo(MemberEntity member) {
    List<BookBorrowEntity> bookBorrow = bookBorrowRepository.findByMember(member);
    boolean remainBorrowInfo = false;
    if (bookBorrow.isEmpty() == false) {
      remainBorrowInfo = true;
    }
    // TODO : equipment_borrow_info 추가하기
    if (remainBorrowInfo) {
      throw new CustomAccountDeleteFailedException("미납한 대여 기록이 남아있어 회원 탈퇴에 실패했습니다.");
    }
  }

  // FIXME : SignInService와 겹치는 메소드
  private boolean passwordMatches(String password, String hashedPassword) {
    return passwordEncoder.matches(password, hashedPassword)
        || customPasswordService.checkPasswordWithPBKDF2SHA256(password, hashedPassword)
        || customPasswordService.checkPasswordWithMD5(password, hashedPassword);
  }

  public void checkCorrectPassword(MemberEntity member, String password) {
    String hashedPassword = member.getPassword();
    if (!passwordMatches(password, hashedPassword)) {
      throw new CustomAccountDeleteFailedException("비밀번호가 일치하지 않습니다.");
    }
  }

  public void deletePointLog(MemberEntity member) {
    pointLogRepository.deleteByMember(member);
  }

  public void deleteGameInfo(MemberEntity member) {
    gameRepository.deleteByMember(member);
  }

  @Transactional
  public void deleteAccount(Long memberId, String password) {
    MemberEntity deleted = memberUtilService.getById(memberId);
    // 동아리 물품, 책 미납한 기록 있으면 불가능
    checkRemainBorrowInfo(deleted);

    // 비밀번호 인증
    checkCorrectPassword(deleted, password);

    decreaseCommentsLike(deleted);
    decreaseCommentsDislike(deleted);
    decreasePostingsLike(deleted);
    decreasePostingsDislike(deleted);
    deleteAttendance(deleted);
    deletePointLog(deleted);
    deleteGameInfo(deleted);
    deleteMemberRank(deleted);
    deleteMemberType(deleted);
    deleteMemberJob(deleted);

    MemberEntity virtualMember = memberUtilService.getById(MemberUtilService.VIRTUAL_MEMBER_ID);
    commentChangeToVirtualMember(virtualMember, deleted);
    postingChangeToVirtualMember(virtualMember, deleted);

    deleteMember(deleted);
    deleteThumbnail(deleted);
  }

  public void deleteMemberRank(MemberEntity memberEntity) {
    MemberRankEntity rank = memberEntity.getMemberRank();
    if (rank != null) {
      rank.getMembers().remove(memberEntity);
    }
  }

  public void deleteMemberType(MemberEntity memberEntity) {
    MemberTypeEntity type = memberEntity.getMemberType();
    if (type != null) {
      type.getMembers().remove(memberEntity);
    }
  }

  public void deleteMemberJob(MemberEntity memberEntity) {
    List<MemberHasMemberJobEntity> memberHasMemberJobEntities = memberEntity.getMemberJobs();
    for (MemberHasMemberJobEntity hasMemberJobEntity : memberHasMemberJobEntities) {
      MemberJobEntity memberJobEntity = hasMemberJobEntity.getMemberJobEntity();
      memberJobEntity.getMembers().remove(hasMemberJobEntity);
    }
    // memberEntity가 삭제 시, MemberHasMemberJobEntity는 자동으로 삭제
  }
}