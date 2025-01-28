package com.education.mypaymentservice.exception;

import lombok.Getter;

@Getter
public class ForbiddenException extends SecurityException {
  private final String requiredPermission;
  private final String resource;

  public ForbiddenException(String message, String requiredPermission, String resource) {
    super(message);
    this.requiredPermission = requiredPermission;
    this.resource = resource;
  }
}
