package keeper.project.homepage.controller.member;

import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.exception.ExceptionAdvice;
import org.springframework.beans.factory.annotation.Autowired;

public class MemberControllerTestSetup extends ApiControllerTestHelper {

  @Autowired
  public ExceptionAdvice exceptionAdvice;

}
