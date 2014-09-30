package at.ac.tuwien.dsg.salam.common.fuzzy;

public class InfinityTrapezoidalMembershipFunction implements MembershipFunction {

  private double a, b, c, c_half;
  
  public InfinityTrapezoidalMembershipFunction(double a, double b, double c, double c_half) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.c_half = c_half;
  }

  public double lowerBound() {
    return a;
  }

  public double upperBound() {
    return Double.POSITIVE_INFINITY;
  }
  
  public double center() {
    return c_half;
  }

  public double grade(double x) {
    double grade;
    if (x<a) {
      grade = 0;
    } else if (x>=a && x<b) {
      grade = (1 - (b-x)/(b-a));
    } else if (x>=b && x<=c) {
      grade = 1;
    } else {
      grade = c_half / (c_half + (x-c));
    }
    return grade;
  }

}
