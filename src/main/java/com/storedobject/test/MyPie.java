package com.storedobject.test;

import com.storedobject.svg.chart.Pie;
import com.storedobject.svg.chart.Values;
import com.storedobject.vaadin.Svg;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

@Route("pie")
public class MyPie extends Div {

    private final Pie pie = new Pie();
    private final Div tooltip = new Div();

    public MyPie() {
        pie.addValue("Apples", 450);
        pie.addValue("Oranges", 270);
        pie.addValue("Bananas", 130);
        pie.setDonutHoleRadius(50); // Make it a donut chart
        Svg svg = new Svg(pie);
        svg.setTooltip(pie, this::tooltip);
        add(svg);
    }


    private Component tooltip(Svg.MouseOverEvent e) {
        if(e.getChart() != pie) {
            return null;
        }
        Values.Value v = e.getValue();
        tooltip.setText(v == null ? "Pie chart" : (v.getLabel() + " = " + v.getValue()));
        return tooltip;
    }
}