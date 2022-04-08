package keeper.project.homepage.service.member;

import java.io.File;
import java.util.Optional;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.posting.PostingEntity;
import keeper.project.homepage.exception.member.CustomAccountDeleteFailedException;
import keeper.project.homepage.user.service.member.MemberService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class MemberServiceTest extends MemberServiceTestSetup {

  @BeforeEach
  public void setup() throws Exception {
    virtualMember = memberRepository.findById(MemberService.VIRTUAL_MEMBER_ID).orElseThrow(null);
    Assertions.assertNotNull(virtualMember, "id=1인 가상 멤버 레코드가 존재하지 않습니다.");
  }

  @Test
  @DisplayName("Member id로 조회")
  public void findByIdTest() {
    MemberEntity findTest = generateMemberEntity(1);

    Long findId = findTest.getId();
    Optional<MemberEntity> findMember = memberRepository.findById(findId);
    Assertions.assertTrue(findMember.isPresent());
  }

  @Test
  @DisplayName("Member와 MemberHasMemberJob의 연관 관계 확인")
  public void jobRelationTest() {
    generateJobCascadeRemoveTestcase();

    // 자식 필드가 제대로 설정이 됐는지 확인
    Assertions.assertTrue(
        memberHasMemberJobRepository.findById(hasMemberJobEntity.getId()).isPresent());
    Assertions.assertTrue(
        hasMemberJobEntity.getMemberEntity().getId().equals(deletedMember.getId()));
    Assertions.assertTrue(deletedMember.getMemberJobs().contains(hasMemberJobEntity));
  }

  @Test
  @DisplayName("회원 삭제 시, MemberHasMemberJob 하위 필드 자동 삭제 테스트")
  public void jobCascadeRemoveTest() {
    generateJobCascadeRemoveTestcase();

    // 삭제 전 연관관계 확인
    Assertions.assertTrue(deletedMember.getMemberJobs().contains(hasMemberJobEntity));

    memberDeleteService.deleteMember(deletedMember);

    // CascadeType.REMOVE - 삭제 후 하위 레코드까지 삭제 됐는지 확인
    Assertions.assertTrue(memberRepository.findById(deletedMember.getId()).isEmpty());
    Assertions.assertTrue(
        memberHasMemberJobRepository.findById(hasMemberJobEntity.getId()).isEmpty());
  }

  @Test
  @DisplayName("회원 삭제 시, FriendEntity 하위 필드 자동 삭제 테스트")
  public void friendCascadeRemoveTest() {
    generateFriendCascadeRemoveTestcase();

    // 삭제 전 연관관계 확인
    Assertions.assertTrue(followee.getFollower().contains(follow));
    Assertions.assertTrue(follower.getFollowee().contains(follow));

    Integer befFolloweeCnt = follower.getFollowee().size();
    memberDeleteService.deleteMember(followee);

    // CascadeType.REMOVE - 삭제 후 하위 레코드까지 삭제 됐는지 확인
    Assertions.assertTrue(memberRepository.findById(followee.getId()).isEmpty());
    Assertions.assertTrue(friendRepository.findById(follow.getId()).isEmpty());

    // follower의 list에서도 FriendEntity 삭제가 반영되었는지 확인
    Assertions.assertFalse(follower.getFollower().contains(follow));
    Assertions.assertTrue(follower.getFollower().size() == befFolloweeCnt - 1);
  }

  @Test
  @DisplayName("회원 삭제 시, MemberRankEntity & MemberTypeEntity에서 삭제 되었는지 확인")
  public void rankAndTypeRemoveTest() {
    generateRankAndTypeRemoveTestcase();

    memberDeleteService.deleteMember(deletedMember);

    Assertions.assertFalse(rank.getMembers().contains(deletedMember));
    Assertions.assertFalse(type.getMembers().contains(deletedMember));
  }

  @Test
  @DisplayName("회원 삭제 시, 댓글 좋아요 삭제 확인")
  public void commentLikeRemoveTest() {
    generateCommentLikeRemoveTestcase();
    int likeCount = commentLikeTest.getLikeCount();

    memberDeleteService.decreaseCommentsLike(deletedMember);
    memberDeleteService.deleteMember(deletedMember);

    Assertions.assertTrue(
        memberHasCommentLikeRepository.findById(mhcLike.getMemberHasCommentEntityPK())
            .isEmpty());
    // 이건 자동으로 안 됨. comment 돌면서 수동으로 감해줘야 함.
    Assertions.assertTrue(commentLikeTest.getLikeCount().equals(likeCount - 1));
  }

  @Test
  @DisplayName("회원 삭제 시, 댓글 싫어요 삭제 확인")
  public void commentDislikeRemoveTest() {
    generateCommentDislikeRemoveTestcase();
    int dislikeCount = commentDislikeTest.getDislikeCount();

    memberDeleteService.decreaseCommentsDislike(deletedMember);
    memberDeleteService.deleteMember(deletedMember);

    Assertions.assertTrue(
        memberHasCommentDislikeRepository.findById(
            mhcDislike.getMemberHasCommentEntityPK()).isEmpty());
    // 이건 자동으로 안 됨. comment 돌면서 수동으로 감해줘야 함.
    Assertions.assertTrue(commentDislikeTest.getDislikeCount().equals(dislikeCount - 1));
  }

  @Test
  @DisplayName("댓글의 작성자가 virtual member로 변경되었는지 확인")
  public void commentChangeToVirtualMemberTest() {
    generateCommentChangeToVirtualMemberTestcase();

    memberDeleteService.commentChangeToVirtualMember(virtualMember, deletedMember);
    memberDeleteService.deleteMember(deletedMember);

    Assertions.assertTrue(updatedComment.getMember().equals(virtualMember));
  }

  @Test
  @DisplayName("게시글의 작성자가 virtual member로 변경되었는지 확인")
  public void postingChangeToVirtualMemberTest() {
    generatePostingChangeToVirtualMemberTestcase();

    memberDeleteService.postingChangeToVirtualMember(virtualMember, deletedMember);
    memberDeleteService.deleteMember(deletedMember);

    for (PostingEntity post : virtualTestPosts) {
      Assertions.assertTrue(post.getMemberId().equals(virtualMember));
    }
  }

  @Test
  @DisplayName("임시저장 게시글이 삭제되었는지 확인")
  public void tempPostingRemovedTest() {
    generateTempPostingRemovedTestcase();

    memberDeleteService.postingChangeToVirtualMember(virtualMember, deletedMember);
    memberDeleteService.deleteMember(deletedMember);

    for (PostingEntity post : removeTestTempPosts) {
      Assertions.assertTrue(postingRepository.findById(post.getId()).isEmpty());
    }
  }

  @Test
  @DisplayName("회원 삭제 시, 게시글 좋아요 삭제 확인")
  public void postingLikeRemoveTest() {
    generatePostingLikeRemoveTestcase();
    int befLikeCount = postLikeTest.getLikeCount();

    memberDeleteService.decreasePostingsLike(deletedMember);
    memberDeleteService.deleteMember(deletedMember);

    Assertions.assertFalse(
        postingRepository.existsByMemberHasPostingLikeEntitiesContaining(mhpLike));
    Assertions.assertTrue(postLikeTest.getLikeCount().equals(befLikeCount - 1));
  }

  @Test
  @DisplayName("회원 삭제 시, 게시글 싫어요 삭제 확인")
  public void postingDislikeRemoveTest() {
    generatePostingDislikeRemoveTestcase();
    int befDislikeCount = postDislikeTest.getDislikeCount();

    memberDeleteService.decreasePostingsDislike(deletedMember);
    memberDeleteService.deleteMember(deletedMember);

    Assertions.assertFalse(
        postingRepository.existsByMemberHasPostingDislikeEntitiesContaining(mhpDislike));
    Assertions.assertTrue(postDislikeTest.getDislikeCount().equals(befDislikeCount - 1));
  }

  @Test
  @DisplayName("회원 삭제 시, 썸네일 삭제 확인")
  public void thumbnailRemoveTest() {
    generateThumbnailRemoveTestcase();

    memberDeleteService.deleteThumbnail(deletedMember);
    memberDeleteService.deleteMember(deletedMember);

    Assertions.assertTrue(thumbnailRepository.findById(thumbnailRemoveTest.getId()).isEmpty());
    Assertions.assertTrue(fileRepository.findById(thumbnailRemoveTest.getFile().getId()).isEmpty());
    File thumbnail = new File(
        System.getProperty("user.dir") + File.separator + thumbnailRemoveTest.getPath());
    File image = new File(
        System.getProperty("user.dir") + File.separator + thumbnailRemoveTest.getFile()
            .getFilePath());
    Assertions.assertFalse(thumbnail.exists());
    Assertions.assertFalse(image.exists());
  }

  @Test
  @DisplayName("회원 탈퇴 시, 출석 기록 삭제 테스트")
  public void attendanceRemoveTest() {
    generateAttendanceRemoveTestcase();

    memberDeleteService.deleteAttendance(deletedMember);
    memberDeleteService.deleteMember(deletedMember);

    Assertions.assertTrue(memberRepository.findById(deletedMember.getId()).isEmpty());
    Assertions.assertTrue(attendanceRepository.findById(attendance.getId()).isEmpty());
    // deleteAll()이 다른 출석에 영향을 안 끼치는지 확인
    Assertions.assertFalse(attendanceRepository.findAll().isEmpty());
  }

  @Test
  @DisplayName("미납한 기록이 있으면 회원 탈퇴 실패 예외처리")
  public void remainBookBorrowInfoTest() {
    generateCheckRemainBorrowInfoTestcase();

    Assertions.assertThrows(CustomAccountDeleteFailedException.class, () -> {
      memberDeleteService.checkRemainBorrowInfo(deletedMember);
    });
  }

  @Test
  @DisplayName("미납한 기록이 없으면 정상")
  public void noBookBorrowInfoTest() {
    generateCheckRemainBorrowInfoTestcase();

    bookBorrowRepository.delete(borrow);
    Assertions.assertDoesNotThrow(() -> {
      memberDeleteService.checkRemainBorrowInfo(deletedMember);
    });

    Assertions.assertTrue(bookBorrowRepository.findByMember(deletedMember).isEmpty());
  }

  @Test
  @DisplayName("비밀번호가 일치하지 않으면 회원 탈퇴 실패 예외처리")
  public void incorrectPasswordTest() {
    generateCheckCorrectPasswordTestcase();

    final String wrongPassword = "wrongpw";
    Assertions.assertThrows(CustomAccountDeleteFailedException.class, () -> {
      memberDeleteService.checkCorrectPassword(deletedMember, wrongPassword);
    });
  }

  @Test
  @DisplayName("비밀번호가 일치하면 정상")
  public void correctPasswordTest() {
    generateCheckCorrectPasswordTestcase();

    final String correctPassword = "keeper1";
    Assertions.assertDoesNotThrow(() -> {
      memberDeleteService.checkCorrectPassword(deletedMember, correctPassword);
    });
  }

  @Test
  @DisplayName("회원 탈퇴 시, 관련된 point log 삭제")
  public void pointLogRemoveTest() {
    generatePointLogRemoveTestcase();

    memberDeleteService.deletePointLog(deletedMember);
    memberDeleteService.deleteMember(deletedMember);

    Assertions.assertTrue(pointLogRepository.findById(pointLogTest.getId()).isEmpty());
    Assertions.assertFalse(pointLogRepository.findAll().isEmpty());
  }
}
