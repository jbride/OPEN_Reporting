package com.redhat.gpe.coursecompletion.dao;

import java.util.List;

import com.redhat.gpe.coursecompletion.domain.TotaraCourseCompletion;

public interface TotaraShadowDAO {

    int testTotaraJDBCConnection();

    List<TotaraCourseCompletion> getLatestCourseCompletions(long lastCCDate, int totaraCCLimit);
    List<TotaraCourseCompletion> getCourseCompletionsByRange(long lowCCDate, long highCCDate);

}
