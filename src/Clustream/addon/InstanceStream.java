package Clustream.addon;

import Clustream.addon.MOAObject;
import Clustream.addon.InstancesHeader;
import weka.core.Instance;

public interface InstanceStream extends MOAObject {

	public InstancesHeader getHeader();

	public long estimatedRemainingInstances();

	public boolean hasMoreInstances();

	public Instance nextInstance();

	public boolean isRestartable();

	public void restart();

}
