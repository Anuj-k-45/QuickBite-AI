package com.quickbite.buildingblocks.domain;

import java.util.Objects;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Entity<TId> {

    @Id
    private TId id;

    protected Entity() {
    }

    protected Entity(TId id) {
        this.id = id;
    }

    public TId getId() {
        return id;
    }

    public void setId(TId id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}