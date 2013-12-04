package Clustream.addon;

import javax.swing.JComponent;

import Clustream.addon.FlagOptionEditComponent;

public class FlagOption extends AbstractOption {

    private static final long serialVersionUID = 1L;

    protected boolean isSet = false;

    public FlagOption(String name, char cliChar, String purpose) {
        super(name, cliChar, purpose);
    }

    public void setValue(boolean v) {
        this.isSet = v;
    }

    public void set() {
        setValue(true);
    }

    public void unset() {
        setValue(false);
    }

    public boolean isSet() {
        return this.isSet;
    }

    @Override
    public String getDefaultCLIString() {
        return null;
    }

    @Override
    public String getValueAsCLIString() {
        return this.isSet ? "" : null;
    }

    @Override
    public void setValueViaCLIString(String s) {
        this.isSet = (s != null);
    }

    @Override
    public String getStateString() {
        return this.isSet ? "true" : "false";
    }

    @Override
    public JComponent getEditComponent() {
        return new FlagOptionEditComponent(this);
    }
}
