package com.quickbite.buildingblocks.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.time.Instant;

@MappedSuperclass
public abstract class AggregateRoot<TId> extends Entity<TId> {

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant lastModifiedAt;

    @Version
    private Long version;

    protected AggregateRoot() {
        this.createdAt = Instant.now();
        this.lastModifiedAt = Instant.now();
    }

    protected AggregateRoot(TId id) {
        super(id);
        this.createdAt = Instant.now();
        this.lastModifiedAt = Instant.now();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(Instant lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}