package com.sunnysuni.common.exception;

public class EntityNotFoundException extends BusinessException {
  public EntityNotFoundException(String entityName, Object id) {
    super(ErrorCode.ENTITY_NOT_FOUND, entityName + "을(를) 찾을 수 없습니다. id=" + id);
  }
}
