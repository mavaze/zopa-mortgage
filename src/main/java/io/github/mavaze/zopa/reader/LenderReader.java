package io.github.mavaze.zopa.reader;

import io.github.mavaze.zopa.dto.LenderDto;
import lombok.NonNull;

import java.util.List;

public interface LenderReader<T> {

    List<LenderDto> parse(@NonNull T input);
}
