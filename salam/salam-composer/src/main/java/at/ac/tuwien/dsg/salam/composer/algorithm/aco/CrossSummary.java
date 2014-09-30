package at.ac.tuwien.dsg.salam.composer.algorithm.aco;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import at.ac.tuwien.dsg.salam.composer.Tracer;
import at.ac.tuwien.dsg.salam.util.Util;

public class CrossSummary {
  
  private static final String PROP_FILE = "aco.properties";

  private static ArrayList<SummaryStatistics> bestScore = new ArrayList<SummaryStatistics>();
  private static ArrayList<SummaryStatistics> avgScore = new ArrayList<SummaryStatistics>();
  private static ArrayList<SummaryStatistics> sdScore = new ArrayList<SummaryStatistics>();
  private static ArrayList<SummaryStatistics> minPheromone = new ArrayList<SummaryStatistics>();
  private static ArrayList<SummaryStatistics> maxPheromone = new ArrayList<SummaryStatistics>();
  
  public static void add(int iteration, double best, double avg, double sd, double minPhero, double maxPhero) {

    // add arraylist if necessary
    for (int i=bestScore.size(); i<=iteration; i++) {
      bestScore.add(new SummaryStatistics());
      avgScore.add(new SummaryStatistics());
      sdScore.add(new SummaryStatistics());
      minPheromone.add(new SummaryStatistics());
      maxPheromone.add(new SummaryStatistics());
    }
    
    bestScore.get(iteration).addValue(best);
    avgScore.get(iteration).addValue(avg);
    sdScore.get(iteration).addValue(sd);
    minPheromone.get(iteration).addValue(minPhero);
    maxPheromone.get(iteration).addValue(maxPhero);
    
  }
  
  public static void trace() {
    String traceFilePrefix = Util.getProperty(PROP_FILE, "trace_file_prefix");
    if (traceFilePrefix!=null && !traceFilePrefix.equals("")) {
    
      // init tracer
      SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
      Date now = new Date();
      String date = sdfDate.format(now);      
      Tracer tracer = new Tracer(traceFilePrefix + "cross-" + date + ".csv");
      tracer.traceln("iteration,best_score_average,avg_score_average,sd_score_average,min_pheromone_average,max_pheromone_average");
      
      // trace
      for (int i=0; i<bestScore.size(); i++) {
        tracer.traceln(i + "," + bestScore.get(i).getMean() + "," + avgScore.get(i).getMean() + "," + sdScore.get(i).getMean() + "," + minPheromone.get(i).getMean() + "," + maxPheromone.get(i).getMean());
      }
      
      // close
      tracer.close();

      // reset
      bestScore = new ArrayList<SummaryStatistics>();
      avgScore = new ArrayList<SummaryStatistics>();
      sdScore = new ArrayList<SummaryStatistics>();
      avgScore = new ArrayList<SummaryStatistics>();
      minPheromone = new ArrayList<SummaryStatistics>();
      maxPheromone = new ArrayList<SummaryStatistics>();
      
    }
    
  }
  
  public static long size() {
    return bestScore.get(0).getN();
  }

}
