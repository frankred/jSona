package de.roth.jsona.view;

import de.roth.jsona.config.Config;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.DefaultPropertyEditorFactory;
import org.controlsfx.property.editor.Editors;
import org.controlsfx.property.editor.PropertyEditor;

import java.io.File;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigPropertySheet extends VBox {

    private static Map<String, Object> customDataMap = new LinkedHashMap();
    private static String[] CHOICE = {"DU", "HIRENSOH"};

    static {
        customDataMap.put("Group 1#My Text", "Same text"); // Creates a TextField in property sheet
        customDataMap.put("Group 1#My Date", LocalDate.of(2000, Month.JANUARY, 1)); // Creates a DatePicker
        customDataMap.put("Group 2#My Boolean", false); // Creates a CheckBox
        customDataMap.put("Group 2#My Number", 500); // Creates a NumericField
        customDataMap.put("Group 2#My asd", Color.RED); // Creates a NumericField
    }

    class CustomPropertyItem implements PropertySheet.Item {
        private String key;
        private String category, name;

        public CustomPropertyItem(String key) {
            this.key = key;
            String[] skey = key.split("#");
            category = skey[0];
            name = skey[1];
        }

        @Override
        public Class<?> getType() {
            return customDataMap.get(key).getClass();
        }

        @Override
        public String getCategory() {
            return category;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public Object getValue() {
            return customDataMap.get(key);
        }

        @Override
        public void setValue(Object value) {
            customDataMap.put(key, value);
        }
    }

    public ConfigPropertySheet() {
        ObservableList<PropertySheet.Item> list = FXCollections.observableArrayList();
        for (String key : customDataMap.keySet()) {
            list.add(new CustomPropertyItem(key));
        }

        PropertySheet propertySheet = new PropertySheet(list);

        propertySheet.setPropertyEditorFactory(new Callback<PropertySheet.Item, PropertyEditor<?>>() {
            @Override
            public PropertyEditor<?> call(PropertySheet.Item param) {
                if (param.getValue() instanceof String[]) {
                    return Editors.createChoiceEditor(param, new ArrayList<String>(Arrays.<String>asList((String[]) param.getValue())));
                } else if (param.getValue() instanceof Boolean) {
                    return Editors.createCheckEditor(param);
                } else if (param.getValue() instanceof Integer) {
                    return Editors.createNumericEditor(param);
                } else {
                    // return Editors.createTextEditor(param);
                }
                return null;
            }
        });

        propertySheet.setPropertyEditorFactory(new DefaultPropertyEditorFactory());

        VBox.setVgrow(propertySheet, Priority.ALWAYS);
        getChildren().add(propertySheet);
    }
}
