package br.com.dio.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;
import static java.util.Objects.nonNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OffSetDateTimeConverter {

    public static OffsetDateTime of(final Timestamp timestamp) {
        return nonNull(timestamp) ?
                OffsetDateTime.ofInstant(timestamp.toInstant(), UTC) : null;
    }

    public static Timestamp toTimestamp(final OffsetDateTime value) {
        return nonNull(value) ?
                Timestamp.valueOf(value.atZoneSameInstant(UTC).toLocalDateTime()) : null;
    }
}
