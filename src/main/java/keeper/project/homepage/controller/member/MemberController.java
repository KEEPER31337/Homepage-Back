package keeper.project.homepage.controller.member;

import keeper.project.homepage.dto.member.MemberDto;
import keeper.project.homepage.dto.result.ListResult;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.exception.CustomMemberNotFoundException;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.service.FileService;
import keeper.project.homepage.service.ResponseService;
import keeper.project.homepage.service.ThumbnailService;
import keeper.project.homepage.service.image.ImageCenterCrop;
import keeper.project.homepage.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

//@Secured("ROLE_USER") // 모든 url에 공통 설정
@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1")
public class MemberController {

  private final MemberRepository memberRepository;
  private final ResponseService responseService;
  private final MemberService memberService;
  private final ThumbnailService thumbnailService;
  private final FileService fileService;

  @Secured("ROLE_ADMIN") // 각 리소스별 권한 설정
  @GetMapping(value = "/members")
  public ListResult<MemberEntity> findAllMember() {
    // 결과데이터가 여러건인경우 getListResult를 이용해서 결과를 출력한다.
    return responseService.getListResult(memberRepository.findAll());
  }

  @Secured("ROLE_USER") // 각 리소스별 권한 설정
  @GetMapping(value = "/member")
  public SingleResult<MemberEntity> findMember() {
    // SecurityContext에서 인증받은 회원의 정보를 얻어온다.
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Long id = Long.valueOf(authentication.getName());
    // 결과데이터가 단일건인경우 getSingleResult를 이용해서 결과를 출력한다.
    return responseService.getSingleResult(
        memberRepository.findById(id).orElseThrow(CustomMemberNotFoundException::new));
  }

  @Secured("ROLE_USER")
  @PutMapping(value = "/member/update/names")
  public SingleResult<MemberDto> updateNames(@RequestBody MemberDto memberDto) {
    // SecurityContext에서 인증받은 회원의 정보를 얻어온다.
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Long id = Long.valueOf(authentication.getName());

    MemberDto updated = memberService.updateNames(memberDto, id);

    return responseService.getSingleResult(updated);
  }

//  @Secured("ROLE_USER")
//  @PutMapping(value = "/member/update/thumbnail")
//  public SingleResult<MemberDto> updateThumbnail(MultipartFile thumbnail) {
//    // SecurityContext에서 인증받은 회원의 정보를 얻어온다.
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    Long id = Long.valueOf(authentication.getName());
//
//    MemberEntity memberEntity = memberService.findById(id);
//    ThumbnailEntity prevThumbnail = thumbnailService.findById(memberEntity.getThumbnail().getId());
//    fileService.deleteById(prevThumbnail.getFile().getId());
//    thumbnailService.deleteById(prevThumbnail.getId());
//
//    ThumbnailEntity thumbnailEntity = null;
//    try {
//      FileEntity fileEntity = fileService.saveOriginalImage(thumbnail,
//          memberEntity.getEmailAddress());
//      thumbnailEntity = thumbnailService.saveThumbnail(new ImageCenterCrop(),
//          thumbnail, fileEntity, 100, 100);
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
////    fileService.saveOriginalImage() // 인자로 ipAddress(posting의 필드)를 받고 있음.
//    return responseService.getSingleResult(updated);
//  }
}
