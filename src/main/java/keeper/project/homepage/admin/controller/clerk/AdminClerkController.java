package keeper.project.homepage.admin.controller.clerk;

import javax.validation.constraints.NotNull;
import keeper.project.homepage.admin.dto.clerk.response.ClerkMemberJobTypeResponseDto;
import keeper.project.homepage.admin.dto.clerk.response.TypeResponseDto;
import keeper.project.homepage.admin.service.clerk.AdminClerkService;
import keeper.project.homepage.common.dto.result.ListResult;
import keeper.project.homepage.common.dto.result.SingleResult;
import keeper.project.homepage.common.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Secured({"ROLE_회장", "ROLE_부회장", "ROLE_서기"})
@RequestMapping("/v1/admin/clerk")
public class AdminClerkController {

  private final ResponseService responseService;
  private final AdminClerkService adminClerkService;

  @GetMapping("/types")
  public ListResult<TypeResponseDto> getTypeList() {
    return responseService.getSuccessListResult(adminClerkService.getTypeList());
  }

  @GetMapping("/members/types/{typeId}")
  public ListResult<ClerkMemberJobTypeResponseDto> getClerkMemberListByType(
      @PathVariable @NotNull Long typeId) {
    return responseService.getSuccessListResult(adminClerkService.getClerkMemberListByType(typeId));
  }

  @PutMapping("/members/{memberId}/types/{typeId}")
  public SingleResult<ClerkMemberJobTypeResponseDto> updateMemberType(
      @PathVariable @NotNull Long memberId,
      @PathVariable @NotNull Long typeId) {
    return responseService.getSuccessSingleResult(
        adminClerkService.updateMemberType(memberId, typeId));
  }
}
