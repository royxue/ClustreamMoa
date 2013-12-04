package Clustream.addon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import weka.core.Instance;

public abstract class Cluster extends AbstractMOAObject {
	 
    private static final long serialVersionUID = 1L;

    private double id = -1;
private double gtLabel = -1;

private HashMap<String, String> measure_values;


public Cluster(){
    this.measure_values = new HashMap<String, String>();
}
public abstract double[] getCenter();

public abstract double getWeight();

public abstract double getInclusionProbability(Instance instance);


//TODO: for non sphere cluster sample points, find out MIN MAX neighbours within cluster
//and return the relative distance
//public abstract double getRelativeHullDistance(Instance instance);

@Override
public void getDescription(StringBuilder sb, int i) {
    sb.append("Cluster Object");
}

public void setId(double id) {
    this.id = id;
}

public double getId() {
    return id;
}

public boolean isGroundTruth(){
    return gtLabel != -1;
}

public void setGroundTruth(double truth){
    gtLabel = truth;
}

public double getGroundTruth(){
    return gtLabel;
}


public abstract Instance sample(Random random);


public void setMeasureValue(String measureKey, String value){
    measure_values.put(measureKey, value);
}

public void setMeasureValue(String measureKey, double value){
    measure_values.put(measureKey, Double.toString(value));
}


public String getMeasureValue(String measureKey){
    if(measure_values.containsKey(measureKey))
        return measure_values.get(measureKey);
    else
        return "";
}


protected void getClusterSpecificInfo(ArrayList<String> infoTitle,ArrayList<String> infoValue){
    infoTitle.add("ClusterID");
    infoValue.add(Integer.toString((int)getId()));

    infoTitle.add("Type");
    infoValue.add(getClass().getSimpleName());

    double c[] = getCenter();
    if(c!=null)
    for (int i = 0; i < c.length; i++) {
        infoTitle.add("Dim"+i);
        infoValue.add(Double.toString(c[i]));
    }

    infoTitle.add("Weight");
    infoValue.add(Double.toString(getWeight()));
    
}

public String getInfo() {
    ArrayList<String> infoTitle = new ArrayList<String>();
    ArrayList<String> infoValue = new ArrayList<String>();
    getClusterSpecificInfo(infoTitle, infoValue);

    StringBuffer sb = new StringBuffer();

    //Cluster properties
    sb.append("<html>");
    sb.append("<table>");
    int i = 0;
    while(i < infoTitle.size() && i < infoValue.size()){
        sb.append("<tr><td>"+infoTitle.get(i)+"</td><td>"+infoValue.get(i)+"</td></tr>");
        i++;
    }
    sb.append("</table>");

    //Evaluation info
    sb.append("<br>");
    sb.append("<b>Evaluation</b><br>");
    sb.append("<table>");
    Iterator miterator = measure_values.entrySet().iterator();
    while(miterator.hasNext()) {
         Map.Entry e = (Map.Entry)miterator.next();
         sb.append("<tr><td>"+e.getKey()+"</td><td>"+e.getValue()+"</td></tr>");
    }
    sb.append("</table>");
    sb.append("</html>");
    return sb.toString();
}

}

