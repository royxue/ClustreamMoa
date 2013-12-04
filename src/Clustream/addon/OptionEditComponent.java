package Clustream.addon;

import Clustream.addon.Option;

public interface OptionEditComponent {

    public Option getEditedOption();

    public void setEditState(String cliString);

    public void applyState();
}