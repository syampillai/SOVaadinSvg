package com.storedobject.test;

import com.storedobject.svg.chart.Bars;
import com.storedobject.svg.chart.Values;
import com.storedobject.vaadin.Svg;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

@Route("bars")
public class BarsView extends Div {

    private final Bars bars = new Bars(400);
    private final Div tooltip = new Div();

    public BarsView() {
        bars.setLabelName("Products");
        bars.setValueName("Sales");
        bars.setUnit(" MT");
        bars.addValue("Sugar", 320);
        bars.addValue("Chocolate", 250);
        bars.addValue("Cardamom", 470);
        Svg svg = new Svg(bars);
        svg.setTooltip(bars, this::tooltip);
        add(svg);
    }


    private Component tooltip(Svg.MouseOverEvent e) {
        if(e.getChart() != bars) {
            return null;
        }
        Values.Value v = e.getValue();
        tooltip.setText(v == null ? "Bars" : (v.getLabel() + " = " + v.getValue()));
        return tooltip;
    }
}