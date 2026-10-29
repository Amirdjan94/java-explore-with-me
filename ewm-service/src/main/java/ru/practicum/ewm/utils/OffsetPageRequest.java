package ru.practicum.ewm.utils;

import org.springframework.data.domain.*;

import java.util.Optional;

public class OffsetPageRequest extends PageRequest {

    private final int from;

    public OffsetPageRequest(int from, int size, Sort sort) {
        super(from / size, size, sort);
        this.from = from;
    }

    public static OffsetPageRequest of(int from, int size) {
        return of(from, size, Sort.unsorted());
    }

    public static OffsetPageRequest of(int from, int size, Sort sort) {
        return new OffsetPageRequest(from, size, sort);
    }

    @Override
    public boolean isPaged() {
        return super.isPaged();
    }

    @Override
    public boolean isUnpaged() {
        return super.isUnpaged();
    }

    @Override
    public Limit toLimit() {
        return super.toLimit();
    }

    @Override
    public Optional<Pageable> toOptional() {
        return super.toOptional();
    }

    @Override
    public OffsetScrollPosition toScrollPosition() {
        return super.toScrollPosition();
    }

    @Override
    public Sort getSortOr(Sort sort) {
        return super.getSortOr(sort);
    }
}
