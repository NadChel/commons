package com.seryozha.commons;

import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class UIUtil {
    private static Rectangle maximumWindowBounds;
    public static final int RGB_MID = 127;
    public static final int RGB_MAX = 255;

    public static UIBuilder getUIBuilder() {
        return new UIBuilder();
    }

    public static UIBuilder getUIBuilderWithLayout(@NotNull Supplier<LayoutManager> layoutSupplier) {
        Objects.requireNonNull(layoutSupplier);
        return new UIBuilder()
                .withLayout(layoutSupplier);
    }

    public static UIBuilder getUIBuilderWithContentPane(@NotNull Supplier<Container> contentPaneSupplier) {
        Objects.requireNonNull(contentPaneSupplier);
        return new UIBuilder()
                .withContentPane(contentPaneSupplier);
    }

    public static UIBuilder getUIBuilderWithLayoutAndContentPane(@NotNull Supplier<LayoutManager> layoutSupplier,
                                                                 @NotNull Supplier<Container> contentPaneSupplier) {
        Stream.of(layoutSupplier, contentPaneSupplier).forEach(Objects::requireNonNull);
        return new UIBuilder()
                .withLayout(layoutSupplier)
                .withContentPane(contentPaneSupplier);
    }

    public static ComponentBuilder getComponentBuilder(@NotNull Supplier<JComponent> componentSupplier) {
        Objects.requireNonNull(componentSupplier);
        return new ComponentBuilder(componentSupplier);
    }

    public static MenuBarBuilder getMenuBarBuilder() {
        return new MenuBarBuilder();
    }

    public static MenuBuilder getMenuBuilder(@NotNull String menuName) {
        Objects.requireNonNull(menuName);
        return new MenuBuilder(menuName);
    }

    public static Color getRandomColor() {
        int red = Util.randomInt(RGB_MAX);
        int green = Util.randomInt(RGB_MAX);
        int blue = Util.randomInt(RGB_MAX);
        return new Color(red, green, blue);
    }

    public static Color getRandomColor(float brightnessFloor) {
        if (brightnessFloor > 1 || brightnessFloor < 0) {
            throw new IllegalArgumentException("brightnessFloor should be between 0 and 1");
        }
        float hue = Util.randomFloat(0, 1);
        float brightness = Util.randomFloat(brightnessFloor, 1);
        float saturation = 1 - brightness;
        return Color.getHSBColor(hue, saturation, brightness);
    }

    public static Color getContrastingColor(Color color) {
        return getContrastingColor(color, 100);
    }

    public static Color getContrastingColor(Color color, int gap) {
        int oldRed = color.getRed();
        int oldGreen = color.getGreen();
        int oldBlue = color.getBlue();

        int newRed = (oldRed < RGB_MID) ? (Math.min(RGB_MAX, oldRed + gap)) : (Math.max(0, oldRed - gap));
        int newGreen = (oldGreen < RGB_MID) ? (Math.min(RGB_MAX, oldGreen + gap)) : (Math.max(0, oldGreen - gap));
        int newBlue = (oldBlue < RGB_MID) ? (Math.min(RGB_MAX, oldBlue + gap)) : (Math.max(0, oldBlue - gap));

        return new Color(newRed, newGreen, newBlue);
    }

    public static SimpleRectangle getRandomRectangle(int parentWidth, int parentHeight) {
        return getRandomRectangle(parentWidth, parentHeight, 0.5F);
    }

    public static SimpleRectangle getRandomRectangle(int parentWidth, int parentHeight, float recDimensionToParentDimensionRatio) {
        int x = Util.randomInt(parentWidth);
        int y = Util.randomInt(parentHeight);
        int width = Util.randomInt((int) (parentWidth * recDimensionToParentDimensionRatio));
        int height = Util.randomInt((int) (parentHeight * recDimensionToParentDimensionRatio));
        return new SimpleRectangle(x, y, width, height);
    }

    public static void requestFocusIfBlank(JTextArea textArea, JTextArea... otherTextAreas) {
        Stream.of(ArrayUtils.add(otherTextAreas, textArea))
                .filter(area -> area.getText().isBlank())
                .forEach(JComponent::requestFocus);
    }

    public static Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    public static Rectangle getMaximumWindowBounds() {
        if (maximumWindowBounds == null) {
            maximumWindowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        }
        return maximumWindowBounds;
    }

    private static void checkBounds(int width, int height) {
        Stream.of(width, height).forEach(Util::requirePositive);
        checkAgainstScreenSize(width, height);
    }

    private static void checkAgainstScreenSize(int widthOrX, int heightOrY) {
        var bounds = getMaximumWindowBounds();
        if (bounds.width < widthOrX) {
            throw new IllegalArgumentException("Values like width or X coordinate cannot be greater than screen width");
        } else if (bounds.height < heightOrY) {
            throw new IllegalArgumentException("Values like height or Y coordinate cannot be greater than screen height");
        }
    }

    public record SimpleRectangle(int x, int y, int width, int height) {
    }

    @NoArgsConstructor
    public static class UIBuilder {
        private final JFrame frame;
        private final Rectangle maximumWindowBounds = getMaximumWindowBounds();

        {
            frame = new JFrame();
            this.withFrameSize(300, 300)
                    .withDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }

        private UIBuilder withLayout(@NotNull Supplier<LayoutManager> layoutSupplier) {
            Objects.requireNonNull(layoutSupplier);
            frame.setLayout(layoutSupplier.get());
            return this;
        }

        private UIBuilder withContentPane(@NotNull Supplier<Container> contentPaneSupplier) {
            Objects.requireNonNull(contentPaneSupplier);
            frame.setContentPane(contentPaneSupplier.get());
            return this;
        }

        public UIBuilder withTitle(@NotNull String title) {
            Objects.requireNonNull(title);
            frame.setTitle(title);
            return this;
        }

        public UIBuilder withComponent(@NotNull Supplier<JComponent> componentSupplier) {
            Objects.requireNonNull(componentSupplier);
            getAndAddToPane(componentSupplier);
            return this;
        }

        public UIBuilder withComponent(@NotNull String position, @NotNull Supplier<JComponent> componentSupplier) {
            Stream.of(position, componentSupplier).forEach(Objects::requireNonNull);
            getAndAddToPane(position, componentSupplier);
            return this;
        }

        public UIBuilder withBackgroundColor(@NotNull Color color) {
            Objects.requireNonNull(color);
            frame.getContentPane().setBackground(color);
            return this;
        }

        public UIBuilder withFrameSize(int width, int height) {
            checkBounds(width, height);
            int x = (maximumWindowBounds.width - width) / 2;
            int y = (maximumWindowBounds.height - height) / 2;
            frame.setBounds(x, y, width, height); // implicit frame centering
            return this;
        }

        private void getAndAddToPane(@NotNull Supplier<JComponent> componentSupplier) {
            JComponent textField = componentSupplier.get();
            frame.getContentPane().add(textField);
        }

        private void getAndAddToPane(@NotNull String position, @NotNull Supplier<JComponent> componentSupplier) {
            JComponent textField = componentSupplier.get();
            frame.getContentPane().add(position, textField);
        }

        public UIBuilder withDefaultCloseOperation(int windowConstant) {
            frame.setDefaultCloseOperation(windowConstant);
            return this;
        }

        public UIBuilder withMenuBar(@NotNull Supplier<JMenuBar> menuBarSupplier) {
            Objects.requireNonNull(menuBarSupplier);
            frame.setJMenuBar(menuBarSupplier.get());
            return this;
        }

        public UIBuilder withResizable(boolean isResizable) {
            frame.setResizable(isResizable);
            return this;
        }

        public UIBuilder withLocationRelativeTo(JComponent component) {
            frame.setLocationRelativeTo(component);
            return this;
        }

        public void visualize() {
            frame.setVisible(true);
        }
    }

    public static class ComponentBuilder {
        private JComponent component;

        public ComponentBuilder(@NotNull Supplier<JComponent> panelSupplier) {
            Objects.requireNonNull(panelSupplier);
            this.component = panelSupplier.get();
        }

        public ComponentBuilder withBorder(@NotNull Supplier<Border> borderSupplier) {
            Objects.requireNonNull(borderSupplier);
            component.setBorder(borderSupplier.get());
            return this;
        }

        public ComponentBuilder withSize(int width, int height) {
            checkBounds(width, height);
            component.setSize(width, height);
            return this;
        }

        public ComponentBuilder withComponent(@NotNull String position, @NotNull Supplier<JComponent> componentSupplier) {
            Stream.of(position, componentSupplier).forEach(Objects::requireNonNull);
            component.add(position, componentSupplier.get());
            return this;
        }

        public ComponentBuilder withComponent(@NotNull Supplier<JComponent> componentSupplier) {
            Objects.requireNonNull(componentSupplier);
            component.add(componentSupplier.get());
            return this;
        }

        public ComponentBuilder withVerticalScrollBarPolicy(int verticalScrollbarPolicy) {
            makeComponentScrollableAsNeeded();
            ((JScrollPane) component).setVerticalScrollBarPolicy(verticalScrollbarPolicy);
            return this;
        }

        public ComponentBuilder withHorizontalScrollBarPolicy(int horizontalScrollbarPolicy) {
            makeComponentScrollableAsNeeded();
            ((JScrollPane) component).setHorizontalScrollBarPolicy(horizontalScrollbarPolicy);
            return this;
        }

        public ComponentBuilder withPreferredSize(@NotNull Dimension preferredSize) {
            Objects.requireNonNull(preferredSize);
            component.setPreferredSize(preferredSize);
            return this;
        }

        public ComponentBuilder withLocation(int x, int y) {
            checkBounds(x, y);
            component.setLocation(x, y);
            return this;
        }

        private void makeComponentScrollableAsNeeded() {
            if (!(component instanceof JScrollPane)) {
                component = new JScrollPane(component);
            }
        }

        public JComponent build() {
            return component;
        }
    }

    public static class MenuBarBuilder {
        private final JMenuBar menuBar = new JMenuBar();

        public MenuBarBuilder withMenu(@NotNull Supplier<JMenu> menuSupplier) {
            Objects.requireNonNull(menuSupplier);
            menuBar.add(menuSupplier.get());
            return this;
        }

        public JMenuBar build() {
            return menuBar;
        }
    }

    public static class MenuBuilder {
        private final JMenu menu;

        public MenuBuilder(String menuName) {
            menu = new JMenu(menuName);
        }

        public MenuBuilder withMenuItem(@NotNull Supplier<JMenuItem> menuItemSupplier) {
            Objects.requireNonNull(menuItemSupplier);
            menu.add(menuItemSupplier.get());
            return this;
        }

        public JMenu build() {
            return menu;
        }
    }
}
