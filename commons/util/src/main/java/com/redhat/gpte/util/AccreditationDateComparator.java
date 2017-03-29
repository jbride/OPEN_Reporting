package com.redhat.gpte.util;

import java.util.Comparator;

import com.redhat.gpe.domain.helper.Accreditation;

public class AccreditationDateComparator implements Comparator<Accreditation> {

    public int compare(Accreditation accred0, Accreditation accred1) {
        return Long.compare(accred0.getCompletionDate().getTime(), accred1.getCompletionDate().getTime());
    }

}
