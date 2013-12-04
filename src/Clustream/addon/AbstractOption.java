package Clustream.addon;
import javax.swing.JComponent;

import Clustream.addon.AbstractMOAObject;
import Clustream.addon.StringOptionEditComponent;

public abstract class AbstractOption extends AbstractMOAObject implements
        Option {

    public static final char[] illegalNameCharacters = new char[]{' ', '-',
        '(', ')'};

    protected String name;

    protected char cliChar;

    protected String purpose;

    public static boolean nameIsLegal(String optionName) {
        for (char illegalChar : illegalNameCharacters) {
            if (optionName.indexOf(illegalChar) >= 0) {
                return false;
            }
        }
        return true;
    }

    public AbstractOption(String name, char cliChar, String purpose) {
        if (!nameIsLegal(name)) {
            throw new IllegalArgumentException("Illegal option name: " + name);
        }
        this.name = name;
        this.cliChar = cliChar;
        this.purpose = purpose;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public char getCLIChar() {
        return this.cliChar;
    }

    @Override
    public String getPurpose() {
        return this.purpose;
    }

    @Override
    public void resetToDefault() {
        setValueViaCLIString(getDefaultCLIString());
    }

    @Override
    public String getStateString() {
        return getValueAsCLIString();
    }

    @Override
    public Option copy() {
        return (Option) super.copy();
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {
        // TODO Auto-generated method stub
    }

    @Override
    public JComponent getEditComponent() {
        return new StringOptionEditComponent(this);
    }
}
