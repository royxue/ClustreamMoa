package Clustream.addon;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Clustream.addon.AutoClassDiscovery;
import Clustream.addon.AutoExpandVector;
import Clustream.addon.ClassOption;
import Clustream.addon.OptionHandler;
import Clustream.addon.Task;

public class ClassOptionSelectionPanel extends JPanel {

    // TODO: idea - why not retain matching options between classes when the
    // class type is changed
    // e.g. if user switches from LearnModel to EvaluateLearner, retain whatever
    // the 'stream' option was set to
    private static final long serialVersionUID = 1L;

    protected JComboBox classChoiceBox;

    protected JComponent chosenObjectEditor;

    protected Object chosenObject;

    public ClassOptionSelectionPanel(Class<?> requiredType,
            String initialCLIString, String nullString) {
        // Class<?>[] classesFound = AutoClassDiscovery.findClassesOfType("moa",
        // requiredType);
        Class<?>[] classesFound = findSuitableClasses(requiredType);
        this.classChoiceBox = new JComboBox(classesFound);
        setLayout(new BorderLayout());
        add(this.classChoiceBox, BorderLayout.NORTH);
        Object initialObject = null;
        try {
            initialObject = ClassOption.cliStringToObject(initialCLIString,
                    requiredType, null);
        } catch (Exception ignored) {
            // ignore exception
        }
        if (initialObject != null) {
            this.classChoiceBox.setSelectedItem(initialObject.getClass());
            classChoiceChanged(initialObject);
        } else {
            try {
                Object chosen = ((Class<?>) ClassOptionSelectionPanel.this.classChoiceBox.getSelectedItem()).newInstance();
                classChoiceChanged(chosen);
            } catch (Exception ex) {
                GUIUtils.showExceptionDialog(ClassOptionSelectionPanel.this,
                        "Problem", ex);
            }
        }
        this.classChoiceBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    Object chosen = ((Class<?>) ClassOptionSelectionPanel.this.classChoiceBox.getSelectedItem()).newInstance();
                    classChoiceChanged(chosen);
                } catch (Exception ex) {
                    GUIUtils.showExceptionDialog(
                            ClassOptionSelectionPanel.this, "Problem", ex);
                }
            }
        });
    }

    public Class<?>[] findSuitableClasses(Class<?> requiredType) {
        AutoExpandVector<Class<?>> finalClasses = new AutoExpandVector<Class<?>>();
        Class<?>[] classesFound = AutoClassDiscovery.findClassesOfType("moa",
                requiredType);
        for (Class<?> foundClass : classesFound) {
            finalClasses.add(foundClass);
        }
        Class<?>[] tasksFound = AutoClassDiscovery.findClassesOfType("moa",
                Task.class);
        for (Class<?> foundTask : tasksFound) {
            try {
                Task task = (Task) foundTask.newInstance();
                if (requiredType.isAssignableFrom(task.getTaskResultType())) {
                    finalClasses.add(foundTask);
                }
            } catch (Exception e) {
                // ignore
            }
        }
        return finalClasses.toArray(new Class<?>[finalClasses.size()]);
    }

    public static String showSelectClassDialog(Component parent, String title,
            Class<?> requiredType, String initialCLIString, String nullString) {
        ClassOptionSelectionPanel panel = new ClassOptionSelectionPanel(
                requiredType, initialCLIString, nullString);
        if (JOptionPane.showOptionDialog(parent, panel, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                null, null) == JOptionPane.OK_OPTION) {
            return panel.getChosenObjectCLIString(requiredType);
        }
        return initialCLIString;
    }

    public String getChosenObjectCLIString(Class<?> requiredType) {
        if (this.chosenObjectEditor instanceof OptionsConfigurationPanel) {
            ((OptionsConfigurationPanel) this.chosenObjectEditor).applyChanges();
        }
        return ClassOption.objectToCLIString(this.chosenObject, requiredType);
    }

    public void classChoiceChanged(Object chosen) {
        this.chosenObject = chosen;
        JComponent newChosenObjectEditor = null;
        if (this.chosenObject instanceof OptionHandler) {
            OptionHandler chosenOptionHandler = (OptionHandler) this.chosenObject;
            newChosenObjectEditor = new OptionsConfigurationPanel(
                    chosenOptionHandler.getPurposeString(), chosenOptionHandler.getOptions());
        }
        if (this.chosenObjectEditor != null) {
            remove(this.chosenObjectEditor);
        }
        this.chosenObjectEditor = newChosenObjectEditor;
        if (this.chosenObjectEditor != null) {
            add(this.chosenObjectEditor, BorderLayout.CENTER);
        }
        Component component = this;
        while ((component != null) && !(component instanceof JDialog)) {
            component = component.getParent();
        }
        if (component != null) {
            Window window = (Window) component;
            window.pack();
        }
    }
}