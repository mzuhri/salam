package at.ac.tuwien.dsg.salam.common.fuzzy;

import java.util.Hashtable;

public abstract class AbtractMembershipFunctionCollection {

    Hashtable<String, MembershipFunction> functions;
    
    public AbtractMembershipFunctionCollection() {
        functions = new Hashtable<String, MembershipFunction>();
    }
    
    public MembershipFunction getMembershipFunction(String fuzzyValue) {
        return functions.get(fuzzyValue);
    }

    public AbtractMembershipFunctionCollection addMembershipFunction(
            String fuzzyValue, MembershipFunction mf) {
        functions.put(fuzzyValue, mf);
        return this;
    }

    public Hashtable<String, MembershipFunction> getMembershipFunctions() {
        return functions;
    }

    public void setMembershipFunctions(Hashtable<String, MembershipFunction> mfs) {
        this.functions = mfs;
    }
    
    
}
