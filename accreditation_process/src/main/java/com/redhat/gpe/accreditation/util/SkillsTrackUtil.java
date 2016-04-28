package com.redhat.gpe.accreditation.util;

import java.util.List;

public class SkillsTrackUtil {

    /**
     * Returns true if student's assessment ids meets all  qualification ids
     * 
     * @param courseCompletionIds
     * @param qualificationAssessmentIds
     * @return
     */
    public static boolean checkQualifications(List<Integer> courseCompletionIds, List<Integer> qualificationAssessmentIds) {
        
        boolean result = true;
        for (int temp : qualificationAssessmentIds) {
            
            if (!courseCompletionIds.contains(temp)) {
                result = false;
                break;
            }
        }
        return result;
    }
    
}
