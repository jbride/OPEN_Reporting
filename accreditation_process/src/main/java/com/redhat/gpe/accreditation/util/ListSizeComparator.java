package com.redhat.gpe.accreditation.util;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

public class ListSizeComparator implements Comparator<Entry<String, List<?>>> {

    public int compare(Entry<String, List<?>> e1, Entry<String, List<?>> e2) {
        List l1 = e1.getValue();
        List l2 = e2.getValue();

        if(l1.size() > l2.size())
            return -1;
        else if (l1.size() == l2.size())
            return 0;
        else
            return 1;
    }

}
