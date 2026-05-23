# SO Vaadin SVG

An enhanced SVG component for Vaadin Flow that provides powerful SVG rendering capabilities, including tooltips, click popups, and event listeners for SVG elements.

## Features

- **Enhanced SVG Component**: Extends Vaadin's native `Svg` component.
- **Dynamic Tooltips**: Easily add tooltips to specific SVG nodes.
- **Click Popups**: Support for showing popovers when SVG elements are clicked.
- **Event Listeners**: Register click and mouse event listeners on individual SVG nodes.
- **Integration**: Seamlessly works with the `so-svg` library for SVG document manipulation.

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>org.vaadin.addons.so</groupId>
    <artifactId>so-vaadin-svg</artifactId>
    <version>0.0.1</version>
</dependency>
```

## Usage

Here is a simple example of how to use the `Svg` component in a Vaadin application:

```java
import com.storedobject.svg.Star;
import com.storedobject.vaadin.Svg;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;

@Route("")
public class MyView extends Div {

    public MyView() {
        // Create an SVG component
        Svg svg = new Svg();

        // Create an SVG node (e.g., a Star) using the so-svg library
        Star star = new Star(100, 100, 20, 40, 5);
        
        // Set the SVG content
        svg.setSvg(star.getSvg());
        add(svg);

        // Add a tooltip to the star
        svg.setTooltip(star, e -> new Div("This is a star!"));

        // Add a click listener to the star
        svg.addClickListener(star, e -> Notification.show("Star clicked!"));
    }
}
```

## License

This project is licensed under the Apache License 2.0. See the `LICENSE` (or `pom.xml`) for details.

## Author

Syam Pillai
