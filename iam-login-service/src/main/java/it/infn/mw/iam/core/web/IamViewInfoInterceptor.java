package it.infn.mw.iam.core.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class IamViewInfoInterceptor extends HandlerInterceptorAdapter {

  @Value("${iam.version}")
  String iamVersion;

  @Value("${git.commit.id.abbrev}")
  String gitCommitId;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    request.setAttribute("iamVersion", iamVersion);
    request.setAttribute("gitCommitId", gitCommitId);

    return true;
  }

}
