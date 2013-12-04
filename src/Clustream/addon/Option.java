package Clustream.addon;

import javax.swing.JComponent;

import Clustream.addon.MOAObject;

public interface Option extends MOAObject {
	 
    public String getName();

    public char getCLIChar();

    public String getPurpose();

    public String getDefaultCLIString();

    public void setValueViaCLIString(String s);

    public String getValueAsCLIString();

    public void resetToDefault();

    public String getStateString();

    public Option copy();

    public JComponent getEditComponent();
}
