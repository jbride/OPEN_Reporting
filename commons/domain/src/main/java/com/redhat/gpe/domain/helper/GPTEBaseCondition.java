package com.redhat.gpe.domain.helper;

import java.util.Date;

public class GPTEBaseCondition {
    
    protected String name;
    protected Date completionDate;
    
    public String getName() {
        return name;
    }
    public void setName(String x) {
        this.name = x;
    }
    
    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date assessmentDate) {
        this.completionDate = assessmentDate;
    }

}
