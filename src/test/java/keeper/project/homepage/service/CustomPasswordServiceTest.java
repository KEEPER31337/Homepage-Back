package keeper.project.homepage.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import keeper.project.homepage.service.sign.CustomPasswordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CustomPasswordServiceTest {

  final private String password = "test123";

  @Autowired
  CustomPasswordService customPasswordService;

  @Test
  @DisplayName("구DB pbkdf2_sha256 비밀번호 검증 성공")
  public void passwordMatchWithSHA256() throws Exception {
    String exceptString = customPasswordService.encodeWithPBKDF2SHA256(password, "2ygQ3QEXOkbX",
        512);
    // then
    assertTrue(customPasswordService.checkPasswordWithPBKDF2SHA256(password,
        "sha256:0000512:2ygQ3QEXOkbX:NTJZJFeOsNOmmCAQ3bKhqFX2BwQzXtIs"));
  }

  @Test
  @DisplayName("구DB pbkdf2_sha256 비밀번호 검증 실패")
  public void passwordDisMatchWithSHA256() throws Exception {
    String exceptString = customPasswordService.encodeWithPBKDF2SHA256(password, "2ygQ3QEXOkbX",
        512);
    // then
    assertFalse(customPasswordService.checkPasswordWithPBKDF2SHA256(password,
        "sha256:0000512:2ygQ3QEXOkbX:KeeperMo"));
    assertFalse(customPasswordService.checkPasswordWithPBKDF2SHA256(password,
        "sha256:0000512:KeeperMo:NTJZJFeOsNOmmCAQ3bKhqFX2BwQzXtIs"));
    assertFalse(customPasswordService.checkPasswordWithPBKDF2SHA256(password,
        "sha256:KeeperMo:2ygQ3QEXOkbX:NTJZJFeOsNOmmCAQ3bKhqFX2BwQzXtIs"));
    /*
     *  SHA1이 따로 존재 할 경우 처리할 Code
     *  assertFalse(customPasswordService.checkPasswordWithPBKDF2SHA256(password,
     *    "KeeperMo:0000512:2ygQ3QEXOkbX:NTJZJFeOsNOmmCAQ3bKhqFX2BwQzXtIs"));
     */
    assertFalse(customPasswordService.checkPasswordWithPBKDF2SHA256(password,
        "1:2:하나가 없을 때"));
  }


  @Test
  @DisplayName("구DB md5 비밀번호 검증 성공")
  public void passwordMatchWithMD5() throws Exception {
    String exceptString = customPasswordService.encodeWithMD5(password);
    // then
    assertTrue(customPasswordService.checkPasswordWithMD5(password,
        "cc03e747a6afbbcbf8be7668acfebee5"));
  }

  @Test
  @DisplayName("구DB md5 비밀번호 검증 실패")
  public void passwordDisMatchWithMD5() throws Exception {
    String exceptString = customPasswordService.encodeWithMD5(password);
    // then
    assertFalse(customPasswordService.checkPasswordWithMD5(password,
        "keeperMo"));
  }
}