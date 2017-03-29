package com.redhat.gpte.util;

import java.util.Comparator;

import com.redhat.gpe.domain.helper.CourseCompletion;
import com.redhat.gpe.domain.helper.GPTEBaseCondition;

public class CourseCompletionDateComparator implements Comparator<GPTEBaseCondition> {

    public int compare(GPTEBaseCondition cc0, GPTEBaseCondition cc1) {
        return Long.compare(cc0.getCompletionDate().getTime(), cc1.getCompletionDate().getTime());
    }

}
