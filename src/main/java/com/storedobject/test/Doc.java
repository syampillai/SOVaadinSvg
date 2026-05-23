package com.storedobject.test;

import com.storedobject.svg.Document;
import com.storedobject.svg.Node;
import com.storedobject.svg.chart.DayPlot;
import com.storedobject.svg.chart.Pie;
import com.storedobject.svg.chart.Values;
import com.storedobject.vaadin.Svg;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;

@Route("doc")
public class Doc extends Div {

    private final Pie pie = new Pie();
    private final DayPlot plot;
    private final Div tooltip = new Div();

    public Doc() {
        pie.addValue("Apples", 450);
        pie.addValue("Oranges", 270);
        pie.addValue("Bananas", 130);
        pie.setDonutHoleRadius(50); // Make it a donut chart
        double[] data = { 102, 251, 151, 4024, 3534 };
        plot = new DayPlot(LocalDate.now().minusDays(4), data, "MT", 0, 10, 5);
        Node node = plot.moveTo(500, 500); // Move it away
        Document doc = new Document();
        doc.add(pie);
        Node doublePie = pie.duplicate().scale(2); // Duplicate it and scale it up
        doc.add(node);
        doc.add(doublePie);
        System.err.println(doc);
        Svg svg = new Svg(doc);
        svg.setTooltip(pie, this::tooltip);
        svg.setTooltip(plot, this::tooltip);
        add(svg);
    }


    private Component tooltip(Svg.MouseOverEvent e) {
        if(e.getChart() == pie) {
            Values.Value v = e.getValue();
            tooltip.setText(v == null ? "Pie chart" : (v.getLabel() + " = " + v.getValue()));
            return tooltip;
        }
        if(e.getChart() == plot) {
            Values.Value v = e.getValue();
            tooltip.setText(v == null ? "Plot chart" : (v.getLabel() + " = " + v.getValue()));
            return tooltip;
        }
        return null;
    }
}