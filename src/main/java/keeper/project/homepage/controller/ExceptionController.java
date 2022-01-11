package keeper.project.homepage.controller;

import keeper.project.homepage.exception.CustomAuthenticationEntryPointException;
import keeper.project.homepage.dto.CommonResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/exception")
public class ExceptionController {

  @GetMapping(value = "/entrypoint")
  public CommonResult entrypointException() {
    throw new CustomAuthenticationEntryPointException();
  }

  @GetMapping(value = "/accessdenied")
  public CommonResult accessdeniedException() {
    throw new AccessDeniedException("");
  }
}