package keeper.project.homepage.common.service.sign;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import keeper.project.homepage.exception.sign.CustomLoginIdSigninFailedException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class CustomPasswordService {

  public final Integer DEFAULT_ITERATIONS = 512;
  public final String DEFAULT_ALGORITHM = "pbkdf2_sha256";


  private String getEncodedHashWithPBKDF2SHA256(String password, String salt, int iterations) {
    // Returns only the last part of whole encoded password
    SecretKeyFactory keyFactory = null;
    try {
      keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    } catch (NoSuchAlgorithmException e) {
      throw new CustomLoginIdSigninFailedException("존재하지 않는 패스워드 인증 알고리즘입니다. 관리자에게 문의하세요.");
    }
    KeySpec keySpec = new PBEKeySpec(password.toCharArray(),
        salt.getBytes(StandardCharsets.UTF_8), iterations, 256);
    SecretKey secret = null;
    try {
      secret = keyFactory.generateSecret(keySpec);
    } catch (InvalidKeySpecException e) {
      System.out.println("Could NOT generate secret key");
      e.printStackTrace();
    }

    assert secret != null;
    byte[] rawHash = secret.getEncoded();
    byte[] hashBase64 = Base64.getEncoder().encode(rawHash);

    return new String(hashBase64);
  }

  public String encodeWithPBKDF2SHA256(String password, String salt, int iterations) {
    // returns hashed password, along with algorithm, number of iterations and salt
    String hash = getEncodedHashWithPBKDF2SHA256(password, salt, iterations);
    return String.format("%s:%d:%s:%s", DEFAULT_ALGORITHM, iterations, salt, hash);
  }

  public String encodeWithPBKDF2SHA256(String password, String salt) {
    return this.encodeWithPBKDF2SHA256(password, salt, this.DEFAULT_ITERATIONS);
  }

  public boolean checkPasswordWithPBKDF2SHA256(String password, String hashedPassword) {
    // hashedPassword consist of: ALGORITHM, ITERATIONS_NUMBER, SALT and
    // HASH; parts are joined with dollar character ("$")
    String[] parts = hashedPassword.split(":");
    if (parts.length != 4) {
      // wrong hash format
      return false;
    }

    int iterations;
    try {
      iterations = Integer.parseInt(parts[1]);
    } catch (NumberFormatException e) {
      return false;
    }
    String salt = parts[2];
    String hash = encodeWithPBKDF2SHA256(password, salt, iterations);
    String[] passwordParts = hash.split(":");
    return passwordParts[3].substring(0, 32).equals(parts[3]);
  }


  private String getEncodedHashWithMD5(String pwd) {
    String hash = "";
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(pwd.getBytes());
      byte[] byteData = md.digest();
      StringBuilder sb = new StringBuilder();
      for (byte byteDatum : byteData) {
        sb.append(Integer.toString((byteDatum & 0xff) + 0x100, 16).substring(1));
      }
      hash = sb.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new CustomLoginIdSigninFailedException("존재하지 않는 패스워드 인증 알고리즘입니다. 관리자에게 문의하세요.");
    }
    return hash;
  }

  public String encodeWithMD5(String password) {
    return getEncodedHashWithMD5(password);
  }

  public boolean checkPasswordWithMD5(String password, String hashedPassword) {
    return hashedPassword.equals(encodeWithMD5(password));
  }
}
