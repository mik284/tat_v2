package com.company.tathminiv2.security;

import com.company.tathminiv2.entity.OTP;
import com.company.tathminiv2.entity.TathminiUser;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.security.role.annotation.SpecificPolicy;

@ResourceRole(name = "anonymous", code = AnonymousRole.CODE)
public interface AnonymousRole {
    String CODE = "anonymous";

    @EntityAttributePolicy(entityClass = TathminiUser.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = TathminiUser.class, actions = EntityPolicyAction.ALL)
    void tathminiUser();

    @EntityAttributePolicy(entityClass = OTP.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = OTP.class, actions = EntityPolicyAction.ALL)
    void oTP();

    @SpecificPolicy(resources = {"rest.enabled", "rest.fileDownload.enabled", "rest.fileUpload.enabled"})
    void specific();
}