package Clustream.addon;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import Clustream.addon.ClassOption;
import Clustream.addon.Option;

public class ClassOptionEditComponent extends JPanel implements
        OptionEditComponent {

    private static final long serialVersionUID = 1L;

    protected ClassOption editedOption;

    protected JTextField textField = new JTextField();

    protected JButton editButton = new JButton("Edit");

    protected HashSet<ChangeListener> changeListeners = new HashSet<ChangeListener>();

    public ClassOptionEditComponent(ClassOption option) {
        this.editedOption = option;
        this.textField.setEditable(false);
        this.textField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                notifyChangeListeners();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                notifyChangeListeners();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                notifyChangeListeners();
            }
        });
        setLayout(new BorderLayout());
        add(this.textField, BorderLayout.CENTER);
        add(this.editButton, BorderLayout.EAST);
        this.editButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                editObject();
            }
        });
        setEditState(this.editedOption.getValueAsCLIString());
    }

    @Override
    public void applyState() {
        this.editedOption.setValueViaCLIString(this.textField.getText());
    }

    @Override
    public Option getEditedOption() {
        return this.editedOption;
    }

    @Override
    public void setEditState(String cliString) {
        this.textField.setText(cliString);
    }

    public void editObject() {
        setEditState(ClassOptionSelectionPanel.showSelectClassDialog(this,
                "Editing option: " + this.editedOption.getName(),
                this.editedOption.getRequiredType(), this.textField.getText(),
                this.editedOption.getNullString()));
    }

    public void addChangeListener(ChangeListener l) {
        changeListeners.add(l);
    }

    public void removeChangeListener(ChangeListener l) {
        changeListeners.remove(l);
    }

    protected void notifyChangeListeners() {
        ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener l : changeListeners) {
            l.stateChanged(e);
        }
    }
}
