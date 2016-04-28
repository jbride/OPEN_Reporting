package com.redhat.gpte.util;

import java.util.Comparator;

import com.redhat.gpe.domain.helper.CourseCompletion;

public class CourseCompletionDateComparator implements Comparator<CourseCompletion> {

    public int compare(CourseCompletion cc0, CourseCompletion cc1) {
        return Long.compare(cc0.getAssessmentDate().getTime(), cc1.getAssessmentDate().getTime());
    }

}
