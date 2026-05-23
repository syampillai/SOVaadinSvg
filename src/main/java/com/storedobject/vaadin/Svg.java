package com.storedobject.vaadin;

import com.storedobject.svg.Document;
import com.storedobject.svg.Node;
import com.storedobject.svg.chart.Chart;
import com.storedobject.svg.chart.Values;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * An enhanced version of Vaadin's {@link com.vaadin.flow.component.Svg}.
 * This class provides functionality to manage tooltips, click popups, and event listeners
 * for the components within an SVG structure.
 * <p>The SVG structure's can be created/manipulated using the {@link com.storedobject.svg.Document}
 * and {@link com.storedobject.svg.Node}.</p>
 *
 * @author Syam
 */
@Tag("svg")
public class Svg extends com.vaadin.flow.component.Svg {

    private final Map<Long, Node> nodes = new HashMap<>();
    private TooltipProviders tooltipProviders;
    private ClickPopupProviders clickPopupProviders;
    private NodeClickListeners clickListeners;
    private Registration clickPopupRegistration, tooltipRegistration, mouseOutRegistration, clickRegistration;
    private final Pop pop = new Pop();

    /**
     * Constructs a new instance of the Svg component.
     */
    public Svg() {
        addAttachListener(e -> attached(e.getUI()));
        addDetachListener(e -> detached(e.getUI()));
    }

    /**
     * Creates a new instance of the Svg component and initializes it with the provided node.
     *
     * @param node the SVG node to associate with this component
     */
    public Svg(Node node) {
        this();
        setSvg(node);
    }

    /**
     * Creates a new instance of the Svg component and initializes it with the provided SVG document.
     *
     * @param document the SVG document to associate with this component
     */
    public Svg(Document document) {
        this();
        setSvg(document);
    }

    /**
     * Sets the SVG content of this component using the provided node.
     *
     * @param node the SVG node from which the SVG content will be set
     */
    public void setSvg(Node node) {
        setSvg(node.getSvg());
    }

    /**
     * Sets the SVG content of this component using the provided SVG document.
     *
     * @param document the SVG document from which the SVG content will be set
     */
    public void setSvg(Document document) {
        setSvg(document.getSvg());
    }

    /**
     * Clears all stored SVG-related data and deregisters any active event listeners or popups associated
     * with the Svg component. This method ensures that the component is reset to its initial state.
     * <p>Note: When you set a new SVG content, this method may be called to ensure a clean slate for the new content.</p>
     * <pre>
     * Functionality:
     * - Closes any active popovers.
     * - Clears the internal map of SVG nodes.
     * - Resets tooltip and click popup providers to null.
     * - Clears out any registered click listeners.
     * - Safely removes and nullifies popovers, tooltips, and mouse interactions' registrations,
     *   ensuring no memory leaks or unwanted event triggers remain.
     * </pre>
     */
    public void clear() {
        pop.close();
        nodes.clear();
        tooltipProviders = null;
        clickPopupProviders = null;
        clickListeners = null;
        if(clickPopupRegistration != null) {
            clickPopupRegistration.remove();
            clickPopupRegistration = null;
        }
        if(tooltipRegistration != null) {
            tooltipRegistration.remove();
            tooltipRegistration = null;
        }
        if(mouseOutRegistration != null) {
            mouseOutRegistration.remove();
            mouseOutRegistration = null;
        }
        if(clickRegistration != null) {
            clickRegistration.remove();
            clickRegistration = null;
        }
    }

    /**
     * Adds a click listener for the specified node. This listener will be triggered
     * when the associated node is clicked in the SVG component.
     *
     * @param node the SVG node for which the click listener is to be added
     * @param listener the listener to be executed when the node is clicked
     * @return a {@code Registration} object that can be used to remove the listener
     */
    public final Registration addClickListener(Node node, ComponentEventListener<ClickEvent> listener) {
        if(clickListeners == null) {
            clickListeners = new NodeClickListeners();
        }
        Registration r = clickListeners.set(node, listener);
        registerListeners();
        return r;
    }

    private void attached(UI ui) {
        pop.attach(ui);
    }

    private void detached(UI ui) {
        pop.detach(ui);
    }

    private void registerListeners() {
        if(clickRegistration != null) {
            if(clickListeners.empty()) {
                clickListeners = null;
                clickRegistration.remove();
                clickRegistration = null;
            }
            return;
        }
        clickRegistration = addListener(ClickEvent.class, this::processEvent);
    }

    private void registerClicks() {
        if(clickPopupRegistration != null) {
            if(clickPopupProviders == null || clickPopupProviders.empty()) {
                clickPopupProviders = null;
                clickPopupRegistration.remove();
                clickPopupRegistration = null;
            }
            return;
        }
        clickPopupRegistration = addListener(ClickEvent.class, this::processEvent);
    }

    private void registerTooltips() {
        if(tooltipRegistration != null) {
            if(tooltipProviders == null || tooltipProviders.empty()) {
                tooltipProviders = null;
                tooltipRegistration.remove();
                tooltipRegistration = null;
                mouseOutRegistration.remove();
                mouseOutRegistration = null;
            }
            return;
        }
        tooltipRegistration = addListener(MouseOverEvent.class, this::processEvent);
        mouseOutRegistration = addListener(MouseOutEvent.class, this::processEvent);
    }

    /**
     * Associates a tooltip with the specified SVG node. The tooltip will be dynamically
     * generated based on the provided function when the user hovers over the node.
     *
     * @param node the SVG node to which the tooltip will be attached
     * @param tooltipProvider the function that generates the tooltip component
     *                        based on the {@link MouseOverEvent}
     */
    public final void setTooltip(Node node, Function<MouseOverEvent, Component> tooltipProvider) {
        if(tooltipProviders == null) {
            tooltipProviders = new TooltipProviders();
        }
        tooltipProviders.set(node, tooltipProvider);
        registerTooltips();
    }

    /**
     * Removes the tooltip associated with the specified SVG node.
     *
     * @param node the SVG node for which the tooltip should be removed
     */
    public final void removeTooltip(Node node) {
        if(tooltipProviders != null) {
            tooltipProviders.remove(node);
            registerTooltips();
        }
    }

    /**
     * Associates a click popup with the specified SVG node. The popup will be dynamically
     * generated based on the provided function when the node is clicked.
     *
     * @param node the SVG node for which the click popup should be associated
     * @param clickPopupProvider the function that generates the popup component
     *                           based on the {@link ClickEvent}
     */
    public final void setClickPopup(Node node, Function<ClickEvent, Component> clickPopupProvider) {
        if(clickPopupProviders == null) {
            clickPopupProviders = new ClickPopupProviders();
        }
        clickPopupProviders.set(node, clickPopupProvider);
        registerClicks();
    }

    /**
     * Removes the click popup associated with the specified SVG node.
     * This method ensures that the popup provider for the given node is
     * removed, and the click registration is updated as necessary.
     *
     * @param node the SVG node for which the associated click popup should be removed
     */
    public void removeClickPopup(Node node) {
        if(clickPopupProviders != null) {
            clickPopupProviders.remove(node);
            registerClicks();
        }
    }

    private void processEvent(Event event) {
        if(event instanceof MouseOutEvent) {
            pop.close();
            return;
        }
        if(event.nodeId == 0) {
            return;
        }
        Node node = nodes.get(event.nodeId);
        if(node == null) {
            return;
        }
        boolean click = event instanceof ClickEvent;
        PopupProviders<?> popupProviders = click ? clickPopupProviders : tooltipProviders;
        if(popupProviders != null && popupProviders.empty()) {
            popupProviders = null;
        }
        if(popupProviders == null) { // No popup provider
            if(!click || clickListeners == null || clickListeners.empty()) {
                return;
            }
        }
        event.node = node;
        if(event.valueId != 0 &&  node instanceof Chart chart) { // It might be a value
            chart.getValues().stream().filter(v -> v.getId() == event.valueId).findFirst()
                    .ifPresent(v -> event.value = v);
        }
        if(popupProviders != null) {
            pop.pop(event, popupProviders.getPopup(event));
        }
        if(click && clickListeners != null) {
            clickListeners.fire((ClickEvent)event);
        }
    }

    /**
     * Represents an abstract base class for events triggered by interactions
     * with an SVG component. This class stores details about the event such as
     * the coordinates of the mouse pointer during the event, the ID and tag name
     * of the targeted SVG element, and associated metadata related to the event.
     * <p>
     * Subclasses of this class should define specific event types.
     * </p>
     *
     * @author Syam
     */
    public static abstract class Event extends ComponentEvent<Svg> {

        private final double clientX;
        private final double clientY;
        private final String tagName;
        private final long nodeId, valueId;
        private Node node;
        private Values.Value value;

        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source     the source component
         * @param fromClient <code>true</code> if the event originated from the client
         *                   side, <code>false</code> otherwise
         * @param clientX    the x coordinate of the mouse pointer
         * @param clientY    the y coordinate of the mouse pointer
         * @param elementId the id of the element that was clicked
         * @param tagName    the tag name of the element that was clicked
         */
        public Event(Svg source, boolean fromClient,
                     @EventData("event.clientX") double clientX,
                     @EventData("event.clientY") double clientY,
                     @EventData("event.target.id") String elementId,
                     @EventData("event.target.tagName") String tagName) {
            super(source, fromClient);
            this.clientX = clientX;
            this.clientY = clientY;
            this.tagName = tagName;
            if(elementId != null && elementId.startsWith("so-svg-")) {
                elementId = elementId.substring(7);
                int i = elementId.indexOf('-');
                if(i < 0) {
                    nodeId = Long.parseLong(elementId);
                    valueId = 0;
                } else {
                    long id;
                    try {
                        id = Long.parseLong(elementId.substring(0, i));
                    } catch (NumberFormatException e) {
                        id = 0;
                    }
                    nodeId = id;
                    try {
                        id = Long.parseLong(elementId.substring(i + 1));
                    } catch (NumberFormatException e) {
                        id = 0;
                    }
                    valueId = id;
                }
            } else {
                nodeId = valueId = 0;
            }
        }

        /**
         * Returns the X-coordinate of the mouse pointer relative to the client area
         * at the time the event was generated.
         *
         * @return the X-coordinate of the mouse pointer in pixels
         */
        public double getClientX() { return clientX; }

        /**
         * Returns the Y-coordinate of the mouse pointer relative to the client area
         * at the time the event was generated.
         *
         * @return the Y-coordinate of the mouse pointer in pixels
         */
        public double getClientY() { return clientY; }

        /**
         * Returns the tag name of the HTML or SVG element associated with the event.
         *
         * @return the tag name of the element as a string
         */
        public String getTagName() { return tagName; }

        /**
         * Returns the node identifier associated with this event. The identifier
         * corresponds to a unique ID assigned to the element or node that triggered
         * the event.
         *
         * @return the node ID as a long value
         */
        public long getNodeId() { return nodeId; }

        /**
         * Returns the value identifier associated with this event. The identifier
         * corresponds to a unique ID assigned to the value within the node that triggered
         * the event. Typically, a value represents a specific data point of the {@link Chart} when the node is
         * a {@link Chart}.
         *
         * @return the value ID as a long value
         */
        public long getValueId() { return valueId; }

        /**
         * Returns the node associated with this event. The node represents a specific
         * element or object within the context of the generated event.
         *
         * @return the associated Node object
         */
        public Node getNode() {
            return node;
        }

        /**
         * Retrieves the value associated with the event. The value is typically
         * an encapsulation of data specific to the event context, such as user interaction
         * or chart data points.
         *
         * @return the associated {@code Values.Value} object
         */
        public Values.Value getValue() {
            return value;
        }

        /**
         * Retrieves the chart associated with this event, if the underlying node
         * is of type {@code Chart}.
         *
         * @return the associated {@code Chart} object if the node is an instance of {@code Chart},
         *         or {@code null} otherwise
         */
        public Chart getChart() {
            return node instanceof Chart ? (Chart)node : null;
        }
    }

    /**
     * Represents a click event triggered by user interaction with an SVG component.
     * This class provides metadata about the click event, including the coordinates
     * of the mouse pointer, the id of the clicked element, and the tag name of the
     * element that was clicked.
     * <p>
     * The event is generated in the context of an SVG component and is designed to
     * handle client-side interactions such as mouse clicks.
     * </p>
     *
     * @author Syam
     */
    @DomEvent("click")
    public static class ClickEvent extends Event {

        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source     the source component
         * @param fromClient <code>true</code> if the event originated from the client
         *                   side, <code>false</code> otherwise
         * @param clientX    the x coordinate of the mouse pointer
         * @param clientY    the y coordinate of the mouse pointer
         * @param elementId the id of the element that was clicked
         * @param tagName    the tag name of the element that was clicked
         */
        public ClickEvent(Svg source, boolean fromClient,
                          @EventData("event.clientX") double clientX,
                          @EventData("event.clientY") double clientY,
                          @EventData("event.target.id") String elementId,
                          @EventData("event.target.tagName") String tagName) {
            super(source, fromClient, clientX, clientY, elementId, tagName);
        }
    }

    /**
     * Represents a mouseover event triggered when the user moves the mouse pointer
     * over an SVG element. This event is specific to interactions involving SVG components
     * and captures details such as the coordinates of the mouse pointer and information
     * about the target element.
     * <p>
     * This event class extends the base {@code Event} class and provides additional
     * metadata required for handling mouseover interactions.
     * </p>
     *
     * @author Syam
     */
    @DomEvent("mouseover")
    public static class MouseOverEvent extends Event {

        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source     the source component
         * @param fromClient <code>true</code> if the event originated from the client
         *                   side, <code>false</code> otherwise
         * @param clientX    the x coordinate of the mouse pointer
         * @param clientY    the y coordinate of the mouse pointer
         * @param elementId the id of the element that was clicked
         * @param tagName    the tag name of the element that was clicked
         */
        public MouseOverEvent(Svg source, boolean fromClient,
                              @EventData("event.clientX") double clientX,
                              @EventData("event.clientY") double clientY,
                              @EventData("event.target.id") String elementId,
                              @EventData("event.target.tagName") String tagName) {
            super(source, fromClient, clientX, clientY, elementId, tagName);
        }
    }

    /**
     * Represents an event triggered when the mouse pointer exits
     * the boundaries of an SVG element.
     * <p>
     * This event is associated with the "mouseout" event at the DOM level
     * and provides details about the source component and the targeted element.
     * It is primarily used to handle mouse-out interactions on SVG elements
     * within the application.
     * </p>
     *
     * @author Syam
     */
    @DomEvent("mouseout")
    public static class MouseOutEvent extends Event {

        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source     the source component
         * @param fromClient <code>true</code> if the event originated from the client
         *                   side, <code>false</code> otherwise
         * @param elementId the id of the element that was clicked
         */
        public MouseOutEvent(Svg source, boolean fromClient,
                              @EventData("event.target.id") String elementId) {
            super(source, fromClient, 0, 0, elementId, "");
        }
    }

    private static class Pop extends Popover {

        private final Pos pos = new Pos();

        Pop() {
            setTarget(pos);
        }

        void attach(UI ui) {
            ui.add(pos);
        }

        void detach(UI ui) {
            ui.remove(pos);
        }

        void pop(Event event, Component component) {
            if(component == null) {
                return;
            }
            close();
            removeAll();
            add(component);
            pos.pos(event.clientX, event.clientY);
            open();
        }

        private static class Pos extends Div {

            Pos() {
                getStyle().set("position", "fixed").set("width", "1px").set("height", "1px").set("overflow", "visible")
                        .set("pointer-events", "none");
            }

            void pos(double x, double y) {
                getStyle().set("left", x + "px").set("top", y + "px");
            }
        }
    }

    private abstract class PopupProviders<E extends Event> {

        private final Map<Long, Function<E, Component>> popupProviders = new HashMap<>();

        void set(Node node, Function<E, Component> provider) {
            popupProviders.put(node.getId(), provider);
            nodes.put(node.getId(), node);
        }

        void remove(Node node) {
            popupProviders.remove(node.getId());
        }

        Component getPopup(Event event) {
            Function<E, Component> provider = popupProviders.get(event.getNodeId());
            //noinspection unchecked
            return provider == null ? null : provider.apply((E)event);
        }

        boolean empty() {
            return popupProviders.isEmpty();
        }
    }

    private class TooltipProviders extends PopupProviders<MouseOverEvent> {
    }

    private class ClickPopupProviders extends PopupProviders<ClickEvent> {
    }

    private class NodeClickListeners {

        private final Map<Long, List<ComponentEventListener<ClickEvent>>> listeners = new HashMap<>();

        Registration set(Node node, ComponentEventListener<ClickEvent> listener) {
            List<ComponentEventListener<ClickEvent>> list = listeners.computeIfAbsent(node.getId(), k -> new ArrayList<>());
            list.add(listener);
            nodes.put(node.getId(), node);
            return () -> list.remove(listener);
        }

        boolean empty() {
            if(listeners.isEmpty()) {
                return true;
            }
            return listeners.values().stream().allMatch(List::isEmpty);
        }

        void fire(ClickEvent event) {
            List<ComponentEventListener<ClickEvent>> list = listeners.get(event.getNodeId());
            if(list != null) {
                for(ComponentEventListener<ClickEvent> listener : list) {
                    listener.onComponentEvent(event);
                }
            }
        }
    }
}