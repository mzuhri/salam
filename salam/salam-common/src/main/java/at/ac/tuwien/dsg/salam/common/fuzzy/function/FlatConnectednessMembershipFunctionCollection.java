package at.ac.tuwien.dsg.salam.common.fuzzy.function;

import at.ac.tuwien.dsg.salam.common.fuzzy.AbtractMembershipFunctionCollection;
import at.ac.tuwien.dsg.salam.common.fuzzy.FuzzyValue;
import at.ac.tuwien.dsg.salam.common.fuzzy.TrapezoidalMembershipFunction;

public class FlatConnectednessMembershipFunctionCollection extends AbtractMembershipFunctionCollection {

    private static FlatConnectednessMembershipFunctionCollection functions;

    public FlatConnectednessMembershipFunctionCollection() {
        addMembershipFunction(FuzzyValue.POOR, 
                new TrapezoidalMembershipFunction(Double.MIN_VALUE, Double.MIN_VALUE, 0.5, 1.0));         
        addMembershipFunction(FuzzyValue.FAIR, 
                new TrapezoidalMembershipFunction(0.50, 0.61, 0.69, 1.0));
        addMembershipFunction(FuzzyValue.GOOD, 
                new TrapezoidalMembershipFunction(0.69, 0.76, 0.84, 1.0));
        addMembershipFunction(FuzzyValue.VERY_GOOD, 
                new TrapezoidalMembershipFunction(0.84, 0.90, Double.MAX_VALUE, Double.MAX_VALUE));
    }

    public static FlatConnectednessMembershipFunctionCollection getInstance() {
        if (functions==null) functions = new FlatConnectednessMembershipFunctionCollection();
        return functions;
    }

}
