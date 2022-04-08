package keeper.project.homepage.common.controller.util;

import keeper.project.homepage.common.dto.result.ListResult;
import keeper.project.homepage.common.service.ResponseService;
import keeper.project.homepage.common.service.util.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController {

  private final AuthService authService;
  private final ResponseService responseService;

  @GetMapping("")
  public ListResult<String> getAuth() {

    return responseService.getSuccessListResult(authService.getAuthByJWT());
  }
}
