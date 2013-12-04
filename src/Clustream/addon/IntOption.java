package Clustream.addon;

import javax.swing.JComponent;

import Clustream.addon.IntOptionEditComponent;

public class IntOption extends AbstractOption {
	 
    private static final long serialVersionUID = 1L;

    protected int currentVal;

    protected int defaultVal;

    protected int minVal;

    protected int maxVal;

    public IntOption(String name, char cliChar, String purpose, int defaultVal) {    //IntOption��  name,�ַ��� cliChar??? ,ע������Ŀ��,Ĭ����ֵ
        this(name, cliChar, purpose, defaultVal, Integer.MIN_VALUE,
                Integer.MAX_VALUE);
    }

    public IntOption(String name, char cliChar, String purpose, int defaultVal,
            int minVal, int maxVal) {
        super(name, cliChar, purpose);
        this.defaultVal = defaultVal;
        this.minVal = minVal;
        this.maxVal = maxVal;
        resetToDefault();
    }

    public void setValue(int v) {
        if (v < this.minVal) {
            throw new IllegalArgumentException("Option " + getName()
                    + " cannot be less than " + this.minVal
                    + ", out of range: " + v);
        }
        if (v > this.maxVal) {
            throw new IllegalArgumentException("Option " + getName()
                    + " cannot be greater than " + this.maxVal
                    + ", out of range: " + v);
        }
        this.currentVal = v;
    }

    public int getValue() {
        return this.currentVal;
    }

    public int getMinValue() {
        return this.minVal;
    }

    public int getMaxValue() {
        return this.maxVal;
    }

    @Override
    public String getDefaultCLIString() {
        return intToCLIString(this.defaultVal);
    }

    @Override
    public String getValueAsCLIString() {
        return intToCLIString(this.currentVal);
    }

    @Override
    public void setValueViaCLIString(String s) {
        setValue(cliStringToInt(s));
    }

    public static int cliStringToInt(String s) {
        return Integer.parseInt(s.trim());
    }

    public static String intToCLIString(int i) {
        return Integer.toString(i);
    }

    @Override
    public JComponent getEditComponent() {
        return new IntOptionEditComponent(this);
    }
}