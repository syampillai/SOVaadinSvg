package com.storedobject.test;

import com.storedobject.svg.chart.DayPlot;
import com.storedobject.svg.chart.Values;
import com.storedobject.vaadin.Svg;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;

@Route("load")
public class ShipLoaded extends Div {

    private final DayPlot plot;
    private final Div tooltip = new Div();

    public ShipLoaded() {
        double[] data = { 102, 251, 151, 4024, 3534 };
        plot = new DayPlot(LocalDate.now().minusDays(4), data, "MT", 0, 10, 5);
        Svg svg = new Svg(plot);
        svg.setTooltip(plot, this::tooltip);
        add(svg);
    }


    private Component tooltip(Svg.MouseOverEvent e) {
        if(e.getChart() != plot) {
            return null;
        }
        Values.Value v = e.getValue();
        tooltip.setText(v == null ? "Loading status" : (v.getLabel() + " = " + v.getValue()));
        return tooltip;
    }
}