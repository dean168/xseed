package org.learning.portal.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.learning.basic.shiro.domain.ShiroPermission;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

@JsonAutoDetect(creatorVisibility = NONE, fieldVisibility = NONE, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
public class StaffPermission extends ShiroPermission {
}
