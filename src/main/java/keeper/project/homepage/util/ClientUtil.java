package keeper.project.homepage.util;

import javax.servlet.http.HttpServletRequest;

public class ClientUtil {

  public static String getUserIP(HttpServletRequest request) {
    String ip = request.getHeader("X-FORWARDED-FOR");
    if (ip == null) {
      ip = request.getHeader("Proxy-Client-IP");
    }

    if (ip == null) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }

    if (ip == null) {
      ip = request.getHeader("HTTP_CLIENT_IP");
    }

    if (ip == null) {
      ip = request.getHeader("HTTP_X_FORWARDED_FOR");
    }

    if (ip == null) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }
}
