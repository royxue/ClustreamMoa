package Clustream.addon;


import javax.swing.JCheckBox;

import Clustream.addon.FlagOption;
import Clustream.addon.Option;

public class FlagOptionEditComponent extends JCheckBox implements
        OptionEditComponent {

    private static final long serialVersionUID = 1L;

    protected FlagOption editedOption;

    public FlagOptionEditComponent(FlagOption option) {
        this.editedOption = option;
        setEditState(this.editedOption.getValueAsCLIString());
    }

    @Override
    public Option getEditedOption() {
        return this.editedOption;
    }

    @Override
    public void setEditState(String cliString) {
        setSelected(cliString != null);
    }

    @Override
    public void applyState() {
        this.editedOption.setValue(isSelected());
    }
}
