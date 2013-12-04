package Clustream.addon;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Clustream.addon.IntOption;
import Clustream.addon.Option;

public class IntOptionEditComponent extends JPanel implements
OptionEditComponent {
	 
private static final long serialVersionUID = 1L;

protected IntOption editedOption;

protected JSpinner spinner;

protected JSlider slider;

public IntOptionEditComponent(IntOption option) {
this.editedOption = option;
int minVal = option.getMinValue();
int maxVal = option.getMaxValue();
setLayout(new GridLayout(1, 0));
this.spinner = new JSpinner(new SpinnerNumberModel(option.getValue(),
        minVal, maxVal, 1));
add(this.spinner);
if ((minVal > Integer.MIN_VALUE) && (maxVal < Integer.MAX_VALUE)) {
    this.slider = new JSlider(minVal, maxVal, option.getValue());
    add(this.slider);
    this.slider.addChangeListener(new ChangeListener() {

        @Override
        public void stateChanged(ChangeEvent e) {
            IntOptionEditComponent.this.spinner.setValue(IntOptionEditComponent.this.slider.getValue());
        }
    });
    this.spinner.addChangeListener(new ChangeListener() {

        @Override
        public void stateChanged(ChangeEvent e) {
            IntOptionEditComponent.this.slider.setValue(((Integer) IntOptionEditComponent.this.spinner.getValue()).intValue());
        }
    });
}
}

@Override
public void applyState() {
this.editedOption.setValue(((Integer) this.spinner.getValue()).intValue());
}

@Override
public Option getEditedOption() {
return this.editedOption;
}

@Override
public void setEditState(String cliString) {
this.spinner.setValue(IntOption.cliStringToInt(cliString));
}
}
