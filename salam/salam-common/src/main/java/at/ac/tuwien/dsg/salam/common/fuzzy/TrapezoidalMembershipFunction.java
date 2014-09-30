package at.ac.tuwien.dsg.salam.common.fuzzy;

public class TrapezoidalMembershipFunction implements MembershipFunction {

  private double a, b, c, d;
  
  public TrapezoidalMembershipFunction(double a, double b, double c, double d) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
  }

  public double lowerBound() {
    return a;
  }

  public double upperBound() {
    return d;
  }

  public double grade(double x) {
    double grade;
    if (x<a) {
      grade = 0;
    } else if (x>=a && x<b) {
      grade = (1 - (b-x)/(b-a));
    } else if (x>=b && x<=c) {
      grade = 1;
    } else if (x>c && x<=d) {
      grade = (1 - (x-c)/(d-c));
    } else {
      grade = 0;
    }
    return grade;
  }

}
