package org.example.omictincrud;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Reminder {
    private final SimpleIntegerProperty reminderId;
    private final SimpleStringProperty description;

    public Reminder(int reminderId, String description) {
        this.reminderId = new SimpleIntegerProperty(reminderId);
        this.description = new SimpleStringProperty(description);
    }

    public int getReminderId() {
        return reminderId.get();
    }

    public SimpleIntegerProperty reminderIdProperty() {
        return reminderId;
    }

    public void setReminderId(int reminderId) {
        this.reminderId.set(reminderId);
    }

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }
}

