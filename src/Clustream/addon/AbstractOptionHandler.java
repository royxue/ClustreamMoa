package Clustream.addon;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import Clustream.addon.ObjectRepository;
import Clustream.addon.NullMonitor;
import Clustream.addon.TaskMonitor;

public abstract class AbstractOptionHandler extends AbstractMOAObject implements
        OptionHandler {

    private static final long serialVersionUID = 1L;

    protected Options options;

    protected Map<String, Object> classOptionNamesToPreparedObjects;

    @Override
    public String getPurposeString() {
        return "Anonymous object: purpose undocumented.";
    }

    @Override
    public Options getOptions() {
        if (this.options == null) {
            this.options = new Options();
            Option[] myOptions = discoverOptionsViaReflection();
            for (Option option : myOptions) {
                this.options.addOption(option);
            }
        }
        return this.options;
    }

    @Override
    public void prepareForUse() {
        prepareForUse(new NullMonitor(), null);
    }

    @Override
    public void prepareForUse(TaskMonitor monitor, ObjectRepository repository) {
        prepareClassOptions(monitor, repository);
        prepareForUseImpl(monitor, repository);
    }

    protected abstract void prepareForUseImpl(TaskMonitor monitor,
            ObjectRepository repository);

    @Override
    public String getCLICreationString(Class<?> expectedType) {
        return ClassOption.stripPackagePrefix(this.getClass().getName(),
                expectedType)
                + " " + getOptions().getAsCLIString();
    }

    @Override
    public OptionHandler copy() {
        return (OptionHandler) super.copy();
    }

    protected Option[] discoverOptionsViaReflection() {
        Class<? extends AbstractOptionHandler> c = this.getClass();
        Field[] fields = c.getFields();
        List<Option> optList = new LinkedList<Option>();
        for (Field field : fields) {
            String fName = field.getName();
            Class<?> fType = field.getType();
            if (fName.endsWith("Option")) {
                if (Option.class.isAssignableFrom(fType)) {
                    Option oVal = null;
                    try {
                        field.setAccessible(true);
                        oVal = (Option) field.get(this);
                    } catch (IllegalAccessException ignored) {
                        // cannot access this field
                    }
                    if (oVal != null) {
                        optList.add(oVal);
                    }
                }
            }
        }
        return optList.toArray(new Option[optList.size()]);
    }

    protected void prepareClassOptions(TaskMonitor monitor,
            ObjectRepository repository) {
        this.classOptionNamesToPreparedObjects = null;
        Option[] optionArray = getOptions().getOptionArray();
        for (Option option : optionArray) {
            if (option instanceof ClassOption) {
                ClassOption classOption = (ClassOption) option;
                monitor.setCurrentActivity("Materializing option "
                        + classOption.getName() + "...", -1.0);
                Object optionObj = classOption.materializeObject(monitor,
                        repository);
                if (monitor.taskShouldAbort()) {
                    return;
                }
                if (optionObj instanceof OptionHandler) {
                    monitor.setCurrentActivity("Preparing option "
                            + classOption.getName() + "...", -1.0);
                    ((OptionHandler) optionObj).prepareForUse(monitor,
                            repository);
                    if (monitor.taskShouldAbort()) {
                        return;
                    }
                }
                if (this.classOptionNamesToPreparedObjects == null) {
                    this.classOptionNamesToPreparedObjects = new HashMap<String, Object>();
                }
                this.classOptionNamesToPreparedObjects.put(option.getName(),
                        optionObj);
            }
        }
    }

    protected Object getPreparedClassOption(ClassOption opt) {
        return this.classOptionNamesToPreparedObjects.get(opt.getName());
    }
}
