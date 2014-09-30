package at.ac.tuwien.dsg.salam.common.fuzzy;

public interface MembershipFunction {

  public double lowerBound();
  public double upperBound();
  public double grade(double numericValue);
  
}
