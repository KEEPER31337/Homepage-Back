package keeper.project.homepage.controller.util;

import keeper.project.homepage.dto.result.ListResult;
import keeper.project.homepage.service.ResponseService;
import keeper.project.homepage.service.util.AuthService;
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
