package com.bank.authorization.service;

import com.bank.authorization.entity.Audit;
import com.bank.authorization.repository.AuditRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuditServiceTest {
    @Mock
    private AuditRepository auditRepository;

    @InjectMocks
    private AuditServiceImpl auditService;

    private Audit audit;

    @BeforeEach
    void setUp() {
        audit = new Audit();
        audit.setCreatedAt(LocalDateTime.now());
        audit.setCreatedBy("Test system");
        audit.setModifiedAt(LocalDateTime.now());
        audit.setModifiedBy("Test system");
        audit.setEntityJson("entityJson");
        audit.setOperationType("Test");
        audit.setEntityType("User");
    }

    @Test
    @DisplayName("Тест метода saveAudit: проверка вызова save у репозитория")
    void saveAudit_ShouldCallRepositorySave() {
        auditService.saveAudit(audit);
        verify(auditRepository, times(1)).save(audit);
    }

    @Test
    @DisplayName("Тест метода saveAudit: проверка выброса исключения при ошибке сохранения")
    void saveAudit_ShouldThrowExceptionWhenSaveFails() {
        doThrow(new RuntimeException("Ошибка сохранения Audit")).when(auditRepository).save(any(Audit.class));

        assertThrows(RuntimeException.class, () -> auditService.saveAudit(audit));
        verify(auditRepository, times(1)).save(audit);
    }

    @Test
    @DisplayName("Тест метода saveAudit: проверка передачи null в метод save")
    void saveAudit_ShouldHandleNullAudit() {
        assertThrows(IllegalArgumentException.class, () -> auditService.saveAudit(null));
        verify(auditRepository, never()).save(any(Audit.class));
    }
}
