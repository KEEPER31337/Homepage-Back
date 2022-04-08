package keeper.project.homepage.common.service;

import keeper.project.homepage.common.dto.result.CommonResult;
import keeper.project.homepage.common.dto.result.ListResult;
import keeper.project.homepage.common.dto.result.SingleResult;
import java.util.List;
import org.springframework.stereotype.Service;

@Service // 해당 Class가 Service임을 명시합니다.
public class ResponseService {

  // enum으로 api 요청 결과에 대한 code, message를 정의합니다.
  public enum CommonResponse {
    SUCCESS(0, "성공하였습니다."),
    FAIL(-1, "실패하였습니다.");

    int code;
    String msg;

    CommonResponse(int code, String msg) {
      this.code = code;
      this.msg = msg;
    }

    public int getCode() {
      return code;
    }

    public String getMsg() {
      return msg;
    }
  }

  // 단일건 결과를 처리하는 메소드
  public <T> SingleResult<T> getSuccessSingleResult(T data) {
    SingleResult<T> result = new SingleResult<>();
    result.setData(data);
    setSuccessResult(result);
    return result;
  }

  // 단일건 결과를 처리하는 메소드
  public <T> SingleResult<T> getFailSingleResult(T data, int code, String msg) {
    SingleResult<T> result = new SingleResult<>();
    result.setData(data);
    setFailResult(result, code, msg);
    return result;
  }

  // 다중건 결과를 처리하는 메소드
  public <T> ListResult<T> getSuccessListResult(List<T> list) {
    ListResult<T> result = new ListResult<>();
    result.setList(list);
    setSuccessResult(result);
    return result;
  }

  // 다중건 결과를 처리하는 메소드
  public <T> ListResult<T> getFailListResult(List<T> list, int code, String msg) {
    ListResult<T> result = new ListResult<>();
    result.setList(list);
    setFailResult(result, code, msg);
    return result;
  }

  // 성공 결과만 처리하는 메소드
  public CommonResult getSuccessResult() {
    CommonResult result = new CommonResult();
    setSuccessResult(result);
    return result;
  }

  public CommonResult getFailResult(int code, String msg) {
    CommonResult result = new CommonResult();
    result.setSuccess(false);
    result.setCode(code);
    result.setMsg(msg);
    return result;
  }

  // 실패 결과만 처리하는 메소드
  public CommonResult getFailResult() {
    CommonResult result = new CommonResult();
    result.setSuccess(false);
    result.setCode(CommonResponse.FAIL.getCode());
    result.setMsg(CommonResponse.FAIL.getMsg());
    return result;
  }

  private void setFailResult(CommonResult result, int code, String msg) {
    result.setSuccess(false);
    result.setCode(code);
    result.setMsg(msg);
  }

  private void setFailResult(CommonResult result) {
    result.setSuccess(false);
    result.setCode(CommonResponse.FAIL.getCode());
    result.setMsg(CommonResponse.FAIL.getMsg());
  }

  // 결과 모델에 api 요청 성공 데이터를 세팅해주는 메소드
  private void setSuccessResult(CommonResult result) {
    result.setSuccess(true);
    result.setCode(CommonResponse.SUCCESS.getCode());
    result.setMsg(CommonResponse.SUCCESS.getMsg());
  }
}