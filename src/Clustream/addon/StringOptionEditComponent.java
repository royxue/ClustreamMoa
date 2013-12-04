package Clustream.addon;

import javax.swing.JTextField;

import Clustream.addon.Option;

public class StringOptionEditComponent extends JTextField implements
        OptionEditComponent {

    private static final long serialVersionUID = 1L;

    protected Option editedOption;

    public StringOptionEditComponent(Option option) {
        this.editedOption = option;
        setEditState(this.editedOption.getValueAsCLIString());
    }

    @Override
    public Option getEditedOption() {
        return this.editedOption;
    }

    @Override
    public void setEditState(String cliString) {
        setText(cliString);
    }

    @Override
    public void applyState() {
        this.editedOption.setValueViaCLIString(getText().length() > 0 ? getText() : null);
    }
}