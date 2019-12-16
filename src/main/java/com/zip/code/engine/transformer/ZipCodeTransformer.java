package com.zip.code.engine.transformer;

import com.zip.code.engine.domain.ZipCodeMessage;
import com.zip.code.engine.domain.ZipCodeRange;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by sanemdeepak on 12/15/19.
 */
@Component
public class ZipCodeTransformer implements Transformer<ZipCodeMessage, ZipCodeMessage> {

    @Override
    public ZipCodeMessage transform(ZipCodeMessage from) {
        if (Objects.isNull(from)) {
            throw new IllegalArgumentException("ZipCodeMessage to transform from cannot be null");
        }
        List<ZipCodeRange> transformedRanges = this.mergeOverlapping(from.getZipCodeRanges());
        return ZipCodeMessage.builder().zipCodeRanges(transformedRanges).build();
    }

    private List<ZipCodeRange> mergeOverlapping(List<ZipCodeRange> zipCodeRanges) {
        final Comparator<ZipCodeRange> startValueComparator = Comparator.comparingInt(ZipCodeRange::getStart);

        if (CollectionUtils.isEmpty(zipCodeRanges)) {
            return Collections.emptyList();
        }
        if (zipCodeRanges.size() < 2) {
            return zipCodeRanges;
        }

        zipCodeRanges.sort(startValueComparator);

        List<ZipCodeRange> merged = new ArrayList<>();
        int start = zipCodeRanges.get(0).getStart();
        int end = zipCodeRanges.get(0).getEnd();

        for (int idx = 1; idx < zipCodeRanges.size(); idx++) {
            ZipCodeRange curr = zipCodeRanges.get(idx);
            if (curr.getStart() <= end) {
                end = Math.max(curr.getEnd(), end);
            } else {
                merged.add(ZipCodeRange.builder().start(start).end(end).build());
                start = curr.getStart();
                end = curr.getEnd();
            }
        }

        merged.add(ZipCodeRange.builder().start(start).end(end).build());
        return merged;
    }
}
