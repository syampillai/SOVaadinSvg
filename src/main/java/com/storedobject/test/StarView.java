package com.storedobject.test;

import com.storedobject.svg.Star;
import com.storedobject.vaadin.Svg;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;

@Route("star")
public class StarView extends Div {

    public StarView() {
        // Create an SVG component
        Svg svg = new Svg();

        // Create an SVG node (e.g., a Star) using the so-svg library
        Star star = new Star(300, 300, 100, 200, 5);

        // Set the SVG content
        svg.setSvg(star);

        // Add a tooltip to the star
        svg.setTooltip(star, e -> new Div("This is a star!"));

        // Add a click listener to the star
        svg.addClickListener(star, e -> Notification.show("Star clicked!"));

        // Add the SVG component to the page
        add(svg);
    }
}
