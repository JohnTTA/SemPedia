package org.mlhypernymextractor.threading;

import gate.Annotation;
import gate.util.OffsetComparator;
import gate.util.Out;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class RemoveOverlappedTerms extends Thread {
	private ArrayList<gate.Annotation> annotations;
	private int low, high;
	
	public RemoveOverlappedTerms(ArrayList<gate.Annotation> a, int low, int high){
		this.low = low;
		this.high = Math.min(high, a.size());
		this.annotations = new ArrayList<>(a.subList(this.low, this.high));
	}
	
	public ArrayList<gate.Annotation> getTreatedList(){
		return this.annotations;
	}
	public void run(){
		deleteOverlappedTermsAnnotations(this.annotations);
	}
	
//	public static void deleteOverlappedTermsAnnotations(ArrayList<Annotation> annotations){
//		List<gate.Annotation> toRemove = new ArrayList<gate.Annotation>();
//		for (int i = 0; i < annotations.size(); i++) {
//			for (int j = 0; j < annotations.size(); j++) {
//				if (i != j) {
//					gate.Annotation annotation1 = annotations.get(i);
//					gate.Annotation annotation2 = annotations.get(j);
//					if (annotation2.withinSpanOf(annotation1))
//						toRemove.add(annotation1);
//					else if (annotation1.withinSpanOf(annotation2))
//						toRemove.add(annotation2);
//				}
//			}
//		}
//		annotations.removeAll(toRemove);
//	}
	
	public void deleteOverlappedTermsAnnotations(
			ArrayList<Annotation> annotations) {
		for (int i = 0; i < annotations.size()-1; i++) {
			gate.Annotation annotation1 = annotations.get(i);
			gate.Annotation annotation2 = annotations.get(i + 1);
			if (annotation2.withinSpanOf(annotation1)) {
				annotations.remove(i);
				i--;
			} else if (annotation1.withinSpanOf(annotation2)) {
				annotations.remove(i + 1);
				i--;
			}
		}
		Out.prln("----Overlapped terms supprimés du "+this.getName());
	}
	
//	public static void deleteOverlappedTermsAnnotations2(ArrayList<Annotation> annotations){
////		int size = annotations.size();
//		for (int i = 0; i < annotations.size(); i++) {
//			for (int j = i+1; j < annotations.size(); j++) {
//					gate.Annotation annotation1 = annotations.get(i);
//					gate.Annotation annotation2 = annotations.get(j);
//					if (annotation2.withinSpanOf(annotation1)){
//						annotations.remove(i);
//						i--;
//					}
//					else if (annotation1.withinSpanOf(annotation2)){
//						annotations.remove(j);
//						i--;
//					}
//			}
//		}
//		
//	}
	
	public static void parallelDeleteOverlappedTermsAnnotations(ArrayList<gate.Annotation> annotations) throws InterruptedException{
		parallelDeleteOverlappedTermsAnnotations(annotations, Runtime.getRuntime().availableProcessors());
	}

	private static void parallelDeleteOverlappedTermsAnnotations(
			ArrayList<gate.Annotation> annotations, int threads) throws InterruptedException {
		Long start = System.currentTimeMillis();
		Out.prln("---Nombre de threads pour RemoveOverlappedTerms : "+threads);
		int size = (int) Math.ceil(annotations.size() * 1.0 / threads);

        RemoveOverlappedTerms[] tasks = new RemoveOverlappedTerms[threads];

        for (int i = 0; i < threads; i++) {
            tasks[i] = new RemoveOverlappedTerms(annotations, i * size, (i + 1) * size);
            tasks[i].start();
        }

        try {
            for (RemoveOverlappedTerms task : tasks) {
                task.join();
            }
        } catch (InterruptedException e) { }


        ArrayList<gate.Annotation> finalAnnotations = new ArrayList<>();
        for (RemoveOverlappedTerms task : tasks) {
            finalAnnotations.addAll(task.getTreatedList());
        }
        annotations.clear();
        annotations.addAll(finalAnnotations);
        
        RemoveOverlappedTerms master = new RemoveOverlappedTerms(annotations, 0, annotations.size());
        master.start();
        master.join();
        
        Long end = System.currentTimeMillis();
		System.out.println(end-start);
	}
}
