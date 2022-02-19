package keeper.project.homepage.controller.member;

import static keeper.project.homepage.service.sign.SignUpService.HALF_GENERATION_MONTH;
import static keeper.project.homepage.service.sign.SignUpService.KEEPER_FOUNDING_YEAR;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.exception.ExceptionAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class MemberControllerTestSetup extends ApiControllerTestHelper {

  @Autowired
  public ExceptionAdvice exceptionAdvice;

}
