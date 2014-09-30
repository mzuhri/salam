package at.ac.tuwien.dsg.salam.composer;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import at.ac.tuwien.dsg.salam.composer.model.Solution;

public class Summary {

    SummaryStatistics statObjValue = new SummaryStatistics();
    SummaryStatistics statCost = new SummaryStatistics();
    SummaryStatistics statNormCost = new SummaryStatistics();
    SummaryStatistics statCompetency = new SummaryStatistics();
    SummaryStatistics statConn = new SummaryStatistics();
    SummaryStatistics statMuCost = new SummaryStatistics();
    SummaryStatistics statRT = new SummaryStatistics();
    SummaryStatistics statNormRT = new SummaryStatistics();

    public static String header() {
        return "avg_objective_value,sd_objective_value,avg_cost,sd_cost,avg_norm_cost,sd_norm_cost,avg_competency,sd_competency,avg_connnectedness,sd_connnectedness,avg_mu_connnectedness,sd_mu_connnectedness,avg_response_time,sd_response_time,avg_norm_response_time,sd_norm_response_time";
    }

    public void add(Solution s) {
        statObjValue.addValue(s.getAggregateScore());
        statCost.addValue(s.getCost());
        statNormCost.addValue(s.getNormCost());
        statCompetency.addValue(s.getSkillScore());
        statConn.addValue(s.getConnectednessScore());
        statMuCost.addValue(s.getFuzzyConnectedness());
        statRT.addValue(s.getTime());
        statNormRT.addValue(s.getNormTime());
    }

    public String getSummary() {
        String s = "";
        s += statObjValue.getMean() + ",";
        s += statObjValue.getStandardDeviation() + ",";
        s += statCost.getMean() + ",";
        s += statCost.getStandardDeviation() + ",";
        s += statNormCost.getMean() + ",";
        s += statNormCost.getStandardDeviation() + ",";
        s += statCompetency.getMean() + ",";
        s += statCompetency.getStandardDeviation() + ",";
        s += statConn.getMean() + ",";
        s += statConn.getStandardDeviation() + ",";
        s += statMuCost.getMean() + ",";
        s += statMuCost.getStandardDeviation() + ",";
        s += statRT.getMean() + ",";
        s += statRT.getStandardDeviation() + ",";
        s += statNormRT.getMean() + ",";
        s += statNormRT.getStandardDeviation() + ",";
        return s;
    }

    public double getObjevtiveAverage() {
        return statObjValue.getMean();
    }

    public double getObjevtiveSD() {
        return statObjValue.getStandardDeviation();
    }
}
