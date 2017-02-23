package org.mlhypernymextractor.utils;

import gate.Annotation;

import java.util.Comparator;

public class GateAnnotationComparator implements Comparator<gate.Annotation>{

	@Override
	public int compare(Annotation o1, Annotation o2) {
		if(o1.withinSpanOf(o2))
            return 0;
		else return 0;
	}

}
