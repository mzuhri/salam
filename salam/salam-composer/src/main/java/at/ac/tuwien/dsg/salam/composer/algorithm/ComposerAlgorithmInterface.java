package at.ac.tuwien.dsg.salam.composer.algorithm;

import at.ac.tuwien.dsg.salam.composer.Composer;
import at.ac.tuwien.dsg.salam.composer.model.ConstructionGraph;
import at.ac.tuwien.dsg.salam.composer.model.Solution;

public interface ComposerAlgorithmInterface {
    public void init(String configFile, ConstructionGraph cons, Composer composer);
    public Solution solve();
}
