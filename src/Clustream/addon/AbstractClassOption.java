package Clustream.addon;



import java.io.File;

import javax.swing.JComponent;

import Clustream.addon.ObjectRepository;
import Clustream.addon.SerializeUtils;
import Clustream.addon.Task;
import Clustream.addon.TaskMonitor;

public abstract class AbstractClassOption extends AbstractOption {

    private static final long serialVersionUID = 1L;

    public static final String FILE_PREFIX_STRING = "file:";

    public static final String INMEM_PREFIX_STRING = "inmem:";

    protected Object currentValue;

    protected Class<?> requiredType;

    protected String defaultCLIString;

    protected String nullString;

    public AbstractClassOption(String name, char cliChar, String purpose,
            Class<?> requiredType, String defaultCLIString) {
        this(name, cliChar, purpose, requiredType, defaultCLIString, null);
    }

    public AbstractClassOption(String name, char cliChar, String purpose,
            Class<?> requiredType, String defaultCLIString, String nullString) {
        super(name, cliChar, purpose);
        this.requiredType = requiredType;
        this.defaultCLIString = defaultCLIString;
        this.nullString = nullString;
        resetToDefault();
    }

    public void setCurrentObject(Object obj) {
        if (((obj == null) && (this.nullString != null))
                || this.requiredType.isInstance(obj)
                || (obj instanceof String)
                || (obj instanceof File)
                || ((obj instanceof Task) && this.requiredType.isAssignableFrom(((Task) obj).getTaskResultType()))) {
            this.currentValue = obj;
        } else {
            throw new IllegalArgumentException("Object not of required type.");
        }
    }

    public Object getPreMaterializedObject() {
        return this.currentValue;
    }

    public Class<?> getRequiredType() {
        return this.requiredType;
    }

    public String getNullString() {
        return this.nullString;
    }

    public Object materializeObject(TaskMonitor monitor,
            ObjectRepository repository) {
        if ((this.currentValue == null)
                || this.requiredType.isInstance(this.currentValue)) {
            return this.currentValue;
        } else if (this.currentValue instanceof String) {
            if (repository != null) {
                Object inmemObj = repository.getObjectNamed((String) this.currentValue);
                if (inmemObj == null) {
                    throw new RuntimeException("No object named "
                            + this.currentValue + " found in repository.");
                }
                return inmemObj;
            }
            throw new RuntimeException("No object repository available.");
        } else if (this.currentValue instanceof Task) {
            Task task = (Task) this.currentValue;
            Object result = task.doTask(monitor, repository);
            return result;
        } else if (this.currentValue instanceof File) {
            File inputFile = (File) this.currentValue;
            Object result = null;
            try {
                result = SerializeUtils.readFromFile(inputFile);
            } catch (Exception ex) {
                throw new RuntimeException("Problem loading "
                        + this.requiredType.getName() + " object from file '"
                        + inputFile.getName() + "':\n" + ex.getMessage(), ex);
            }
            return result;
        } else {
            throw new RuntimeException(
                    "Could not materialize object of required type "
                    + this.requiredType.getName() + ", found "
                    + this.currentValue.getClass().getName()
                    + " instead.");
        }
    }

    @Override
    public String getDefaultCLIString() {
        return this.defaultCLIString;
    }

    public static String classToCLIString(Class<?> aClass, Class<?> requiredType) {
        String className = aClass.getName();
        String packageName = requiredType.getPackage().getName();
        if (className.startsWith(packageName)) {
            // cut off package name
            className = className.substring(packageName.length() + 1, className.length());
        } else if (Task.class.isAssignableFrom(aClass)) {
            packageName = Task.class.getPackage().getName();
            if (className.startsWith(packageName)) {
                // cut off task package name
                className = className.substring(packageName.length() + 1,
                        className.length());
            }
        }
        return className;
    }

    @Override
    public abstract String getValueAsCLIString();

    @Override
    public abstract void setValueViaCLIString(String s);

    @Override
    public abstract JComponent getEditComponent();

    public static String stripPackagePrefix(String className, Class<?> expectedType) {
        if (className.startsWith(expectedType.getPackage().getName())) {
            return className.substring(expectedType.getPackage().getName().length() + 1);
        }
        return className;
    }
}