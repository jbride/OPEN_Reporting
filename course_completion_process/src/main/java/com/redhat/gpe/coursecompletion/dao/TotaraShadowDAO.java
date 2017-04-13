package com.redhat.gpe.coursecompletion.dao;

import java.util.List;

import com.redhat.gpe.domain.helper.CourseCompletion;
import com.redhat.gpe.domain.canonical.StudentCourse;

public interface TotaraShadowDAO {

    int testTotaraJDBCConnection();

    List<CourseCompletion> getLatestCourseCompletions(int lastCC, int totaraCCLimit);

}
