package com.bank.authorization.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * <h1>Entity class Audit</h1>
 * <p>
 * Представляет сущность для аудита изменений в системе.
 * </p>
 * <p>
 * Эта сущность хранит информацию о типе сущности, операции, инициаторе изменений,
 * времени создания и модификации, а также о состоянии сущности до и после изменений.
 * </p>
 * <p>
 * Эта сущность соответствует таблице {@code audit} в схеме {@code authorizations}.
 * </p>
 *
 * @author  Илья Криуляк
 * @version v1
 */

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Table(name = "audit", schema = "authorizations")
public class Audit {

    /**
     * Уникальный идентификатор записи аудита.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    /**
     * Тип сущности, для которой создаётся запись аудита.
     * <p>
     * В данном микросервисе - {@link User}.
     * Максимальная длина — 40 символов.
     * </p>
     */
    @Column(name = "entity_type")
    @Size(max = 40)
    private String entityType;

    /**
     * Тип операции, которая была выполнена над сущностью.
     * <p>
     * Максимальная длина — 255 символов.
     * </p>
     */
    @Column(name = "operation_type")
    @Size(max = 255)
    private String operationType;

    /**
     * Пользователь или процесс, создавший эту запись аудита.
     * <p>
     * Максимальная длина — 255 символов.
     * </p>
     */
    @Column(name = "created_by")
    @Size(max = 255)
    private String createdBy;

    /**
     * Пользователь или процесс, изменивший запись.
     * <p>
     * Обязательное поле. Максимальная длина — 255 символов.
     * </p>
     */
    @Column(name = "modified_by")
    @Size(max = 255)
    private String modifiedBy;

    /**
     * Дата и время создания записи.
     * <p>
     * Обязательное поле.
     * </p>
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Дата и время последней модификации записи.
     */
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    /**
     * JSON-строка, представляющая новое состояние сущности после изменений.
     */
    @Column(name = "new_entity_json", columnDefinition = "TEXT")
    private String newEntityJson;

    /**
     * JSON-строка, представляющая предыдущее состояние сущности перед изменениями.
     * <p>
     * Обязательное поле.
     * </p>
     */
    @Column(name = "entity_json", nullable = false, columnDefinition = "TEXT")
    private String entityJson;

}

