package keeper.project.homepage.config.security;

// import 생략

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Principal;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider { // JWT 토큰을 생성 및 검증 모듈

  @Value("spring.jwt.secret")
  private String secretKey;

  // FIXME: CTF 기간동안 5일의 세션을 가짐!! CTF 종료 후 꼭 수정 필요
  private final long tokenValidMilisecond = 1000L * 60 * 60 * 24 * 5;

  @PostConstruct
  protected void init() {
    secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
  }

  // Jwt 토큰 생성
  public String createToken(String userPk, List<String> roles) {
    Claims claims = Jwts.claims().setSubject(userPk);
    claims.put("roles", roles);
    Date now = new Date();
    return Jwts.builder()
        .setClaims(claims) // 데이터
        .setIssuedAt(now) // 토큰 발행일자
        .setExpiration(new Date(now.getTime() + tokenValidMilisecond)) // set Expire Time
        .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
        .compact();
  }

  // Jwt 토큰으로 인증 정보를 조회
  public Authentication getAuthentication(String token) {
    Claims claims = getClaim(token);
    String rolesString = claims.get("roles").toString();
    List<String> roles = Arrays.stream(
        rolesString.substring(1, rolesString.length() - 1).split(", ")).toList();
    UserDetails userDetails = new JwtMemberEntity(claims.getSubject(), roles);
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  // Jwt 토큰을 Claim 으로 변경
  private Claims getClaim(String token) {
    return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
  }

  // Request의 Header에서 token 파싱 : "Authorization: jwt토큰"
  public String resolveToken(HttpServletRequest req) {
    String requestHeader = req.getHeader("Authorization");
    if (requestHeader == null || requestHeader.isEmpty()) {
      return null;
    }
    String[] parts = requestHeader.split(" ");
    String type = parts[0];
    if (parts.length != 2 || !type.equals("Bearer")) {
      return null;
    }
    String token = parts[1];
    return token;
  }

  // Jwt 토큰의 유효성 + 만료일자 확인
  public boolean validateToken(String jwtToken) {
    try {
      Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
      return !claims.getBody().getExpiration().before(new Date());
    } catch (Exception e) {
      return false;
    }
  }
}