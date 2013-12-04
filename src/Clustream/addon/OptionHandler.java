package Clustream.addon;

import Clustream.addon.MOAObject;
import Clustream.addon.ObjectRepository;
import Clustream.addon.TaskMonitor;

public interface OptionHandler extends MOAObject {

    public String getPurposeString();

    public Options getOptions();
    //public Options getOptions(); Options == java system

    public void prepareForUse();

    public void prepareForUse(TaskMonitor monitor, ObjectRepository repository);

    public OptionHandler copy();

    public String getCLICreationString(Class<?> expectedType);
}