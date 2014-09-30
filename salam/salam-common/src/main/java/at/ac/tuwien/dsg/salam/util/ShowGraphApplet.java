package at.ac.tuwien.dsg.salam.util;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JApplet;
import javax.swing.JFrame;

import org.jgraph.JGraph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphModelAdapter;

public class ShowGraphApplet<V, W> extends JApplet {

    private static final long serialVersionUID = 3877521306902446441L;
    private static final Color DEFAULT_BG_COLOR = Color.decode("#FAFBFF");
    private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);

    public void showGraph(ListenableGraph<V, W> graph) {

        JGraphModelAdapter<V, W> jgAdapter = new JGraphModelAdapter<V, W>(graph);
        JGraph jgraph = new JGraph(jgAdapter);

        adjustDisplaySettings(jgraph);
        getContentPane().add(jgraph);
        resize(DEFAULT_SIZE);

        JFrame frame = new JFrame();
        frame.getContentPane().add(this);
        frame.setTitle("Show Graph Applet");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void adjustDisplaySettings(JGraph jg)  {
        jg.setPreferredSize(DEFAULT_SIZE);
        Color c = DEFAULT_BG_COLOR;
        jg.setBackground(c);
    }

}
