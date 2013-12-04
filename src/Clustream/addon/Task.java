package Clustream.addon;

import Clustream.addon.MOAObject;
import Clustream.addon.ObjectRepository;

public interface Task extends MOAObject {

    public Class<?> getTaskResultType();

    public Object doTask();

    public Object doTask(TaskMonitor monitor, ObjectRepository repository);
}