package org.mlhypernymextractor.threading;

import gate.util.Out;

import java.util.ArrayList;
import java.util.List;

public class RemoveMotsVidesFromTermAnnotations extends Thread {
	private ArrayList<gate.Annotation> annotations;
	private ArrayList<gate.Annotation> motsvidesAnnotations;
	private int low, high;
	
	public RemoveMotsVidesFromTermAnnotations(ArrayList<gate.Annotation> a, int low, int high, ArrayList<gate.Annotation> c){
		this.low = low;
		this.high = Math.min(high, a.size());
		this.annotations = new ArrayList<>(a.subList(this.low, this.high));
		this.motsvidesAnnotations = c;
	}
	
	public ArrayList<gate.Annotation> getTreatedList(){
		return this.annotations;
	}
	public void run(){
		deleteMotsVidesFromTermAnnotations2(this.annotations, this.motsvidesAnnotations);
	}

	private static void deleteMotsVidesFromTermAnnotations(
			ArrayList<gate.Annotation> annotations, ArrayList<gate.Annotation> motvideAnnotations) {
		List<gate.Annotation> toRemove = new ArrayList<gate.Annotation>();
		for (int i = 0; i < annotations.size(); i++) {
			for (int j = 0; j < motvideAnnotations.size(); j++) {
					gate.Annotation termAnnotation = annotations.get(i);
					gate.Annotation motvideAnnotation = motvideAnnotations.get(j);
					if (termAnnotation.overlaps(motvideAnnotation)){
						toRemove.add(termAnnotation);
				}
			}
		}
		annotations.removeAll(toRemove);
	}
	
	private void deleteMotsVidesFromTermAnnotations2(
			ArrayList<gate.Annotation> annotations, ArrayList<gate.Annotation> motvideAnnotations) {
		for (int i = 0; i < annotations.size(); i++) {
			for (int j = 0; j < motvideAnnotations.size(); j++) {
					gate.Annotation termAnnotation = annotations.get(i);
					gate.Annotation motvideAnnotation = motvideAnnotations.get(j);
					if (termAnnotation.overlaps(motvideAnnotation)){
						annotations.remove(i);
						i--;
						j = motvideAnnotations.size();
					}
				}
			}
		Out.prln("----Mots vides supprimés du "+this.getName());
	}
	
	public static void parallelDeleteMotsVidesFromTermAnnotations(ArrayList<gate.Annotation> annotations, ArrayList<gate.Annotation> motsvides){
		parallelDeleteMotsVidesFromTermAnnotations(annotations, motsvides, Runtime.getRuntime().availableProcessors());
	}

	private static void parallelDeleteMotsVidesFromTermAnnotations(
			ArrayList<gate.Annotation> annotations, ArrayList<gate.Annotation> motsvides, int threads) {
		
		Out.prln("---Nombre de threads pour MotsVidesFromTerm : "+threads);
		int size = (int) Math.ceil(annotations.size() * 1.0 / threads);

        RemoveMotsVidesFromTermAnnotations[] tasks = new RemoveMotsVidesFromTermAnnotations[threads];
        for (int i = 0; i < threads; i++) {
            tasks[i] = new RemoveMotsVidesFromTermAnnotations(annotations, i * size, (i + 1) * size, motsvides);
            tasks[i].start();
        }

        try {
            for (RemoveMotsVidesFromTermAnnotations task : tasks) {
                task.join();
            }
        } catch (InterruptedException e) { }


        ArrayList<gate.Annotation> finalAnnotations = new ArrayList<>();
        for (RemoveMotsVidesFromTermAnnotations task : tasks) {
            finalAnnotations.addAll(task.getTreatedList());
        }
        
        annotations.clear();
        annotations.addAll(finalAnnotations);
        motsvides = null;
	}
	
}
