package com.bank.authorization.aop;

import com.bank.authorization.entity.Audit;
import com.bank.authorization.service.AuditServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class AuditAspect {

    private final Logger logger = LoggerFactory.getLogger(AuditAspect.class);
    private final AuditServiceImpl auditService;

    @Autowired
    public AuditAspect(AuditServiceImpl auditService) {
        this.auditService = auditService;
    }

    @Around("execution(* com.bank.authorization.service.UserService.saveUser(..))")
    public void auditLog(ProceedingJoinPoint joinPoint) throws Throwable {

        Audit audit = new Audit();

        Object result = joinPoint.proceed();
        String entityType = joinPoint.getSignature().getDeclaringType().getSimpleName();

        audit.setEntityType(entityType);
        audit.setOperationType("CREATE");
        audit.setCreatedBy("system");
        audit.setCreatedAt(LocalDateTime.now());
        audit.setModifiedBy("system");
        audit.setModifiedAt(LocalDateTime.now());
        audit.setEntityJson(result.toString());

        try {
            auditService.saveAudit(audit);
        } catch (Throwable e) {
            logger.warn("Error when save audit", e.getMessage());
        }
    }
}
