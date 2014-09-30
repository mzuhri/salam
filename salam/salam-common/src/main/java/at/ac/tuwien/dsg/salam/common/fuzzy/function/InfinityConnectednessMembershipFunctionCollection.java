package at.ac.tuwien.dsg.salam.common.fuzzy.function;

import at.ac.tuwien.dsg.salam.common.fuzzy.AbtractMembershipFunctionCollection;
import at.ac.tuwien.dsg.salam.common.fuzzy.FuzzyValue;
import at.ac.tuwien.dsg.salam.common.fuzzy.InfinityTrapezoidalMembershipFunction;

public class InfinityConnectednessMembershipFunctionCollection extends AbtractMembershipFunctionCollection {

    private static InfinityConnectednessMembershipFunctionCollection functions;

    public InfinityConnectednessMembershipFunctionCollection() {
        addMembershipFunction(FuzzyValue.POOR, 
                new InfinityTrapezoidalMembershipFunction(Double.MIN_VALUE, Double.MIN_VALUE, 2, 4));         
        addMembershipFunction(FuzzyValue.FAIR, 
                new InfinityTrapezoidalMembershipFunction(2, 4, 8, 4));
        addMembershipFunction(FuzzyValue.GOOD, 
                new InfinityTrapezoidalMembershipFunction(8, 10, 14, 4));
        addMembershipFunction(FuzzyValue.VERY_GOOD, 
                new InfinityTrapezoidalMembershipFunction(14, 16, Double.MAX_VALUE, Double.MAX_VALUE));
    }

    public static InfinityConnectednessMembershipFunctionCollection getInstance() {
        if (functions==null) functions = new InfinityConnectednessMembershipFunctionCollection();
        return functions;
    }

}
