package com.bank.authorization.service;

import com.bank.authorization.entity.Audit;
import com.bank.authorization.repository.AuditRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class AuditServiceImpl {

    private final AuditRepository auditRepository;

    public AuditServiceImpl(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Transactional
    public void saveAudit(Audit audit) {
        if (audit == null) {
            throw  new IllegalArgumentException("Ошибка при сохранении Audit: в параметрах передан null");
        }
        auditRepository.save(audit);
    }
}
