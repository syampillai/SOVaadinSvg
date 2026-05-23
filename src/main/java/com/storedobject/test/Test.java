package com.storedobject.test;

import com.storedobject.svg.Star;
import com.storedobject.vaadin.Svg;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.BodySize;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route("")
@PageTitle("SVG Test")
public class Test extends Div {

    private final Svg svg = new Svg();

    public Test() {
        Star star = new Star(100, 100, 20, 40, 5);
        svg.setSvg(star.getSvg());
        add(svg);
        svg.setTooltip(star, e -> new Div("Your star"));
        svg.addClickListener(star, e -> Notification.show("Clicked"));
    }

    /**
     * App shell.
     */
    @Push(PushMode.MANUAL)
    @BodySize(height = "100vh", width = "100vw")
    @Theme(themeClass = Lumo.class, variant = Lumo.LIGHT)
    @PWA(name = "SO Application", shortName = "SO")
    public static class AppShell implements AppShellConfigurator {
    }
}
